package org.chatgptstream.openai.listener;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.chatgptstream.openai.enmus.MessageType;
import org.chatgptstream.openai.service.dto.Message;
import org.chatgptstream.openai.service.dto.MessageRes;
import org.chatgptstream.openai.util.R;
import org.chatgptstream.openai.util.api.OpenAiWebClient;
import org.chatgptstream.openai.util.api.res.chat.image.DataRes;
import org.chatgptstream.openai.util.api.res.chat.image.OpenAiImageResponse;
import org.chatgptstream.openai.util.api.res.chat.text.OpenAiResponse;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.publisher.FluxSink;

import java.util.stream.Collectors;

/**
 * @author niuxiangqian
 * @version 1.0
 * @date 2023/3/21 20:15
 **/
@Slf4j
public class OpenAISubscriber implements Subscriber<String>, Disposable {
    private final FluxSink<String> emitter;
    private Subscription subscription;
    private final String sessionId;
    private final CompletedCallBack completedCallBack;
    private final StringBuilder sb;
    private final Message questions;
    private final MessageType messageType;

    public OpenAISubscriber(FluxSink<String> emitter, String sessionId, CompletedCallBack completedCallBack, Message questions) {
        this.emitter = emitter;
        this.sessionId = sessionId;
        this.completedCallBack = completedCallBack;
        this.questions = questions;
        this.sb = new StringBuilder();
        this.messageType = questions.getMessageType();
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(String data) {
        log.info("OpenAI返回数据：{}", data);
        if (messageType == MessageType.IMAGE) {
            subscription.request(1);
            sb.append(data);
            return;
        }
        MessageRes res = MessageRes.builder().message("")
                .end(Boolean.FALSE)
                .messageType(messageType).build();
        if ("[DONE]".equals(data)) {
            log.info("OpenAI返回数据结束了");
            subscription.request(1);
            res.setEnd(Boolean.TRUE);
            emitter.next(JSON.toJSONString(R.success(res)));
            completedCallBack.completed(questions, sessionId, sb.toString());
            emitter.complete();
        } else {
            OpenAiResponse openAiResponse = JSON.parseObject(data, OpenAiResponse.class);
            String content = openAiResponse.getChoices().get(0).getDelta().getContent();
            content = content == null ? "" : content;
            res.setMessage(content);
            emitter.next(JSON.toJSONString(R.success(res)));
            sb.append(content);
            subscription.request(1);
        }

    }

    @Override
    public void onError(Throwable t) {
        log.error("OpenAI返回数据异常：{}", t.getMessage());
        if (t.getMessage().contains(OpenAiWebClient.CONTEXT_LENGTH_EXCEEDED)){
            emitter.next(JSON.toJSONString(R.fail("内容超出了限制长度，已经清理历史记录，请重新进行提问")));
            completedCallBack.clearHistory(sessionId);
        }else {
            emitter.next(JSON.toJSONString(R.fail(t.getMessage())));
        }
        emitter.complete();
        completedCallBack.fail(questions, sessionId, t.getMessage());
    }

    @Override
    public void onComplete() {
        log.info("OpenAI返回数据完成");
        if (messageType == MessageType.IMAGE) {
            OpenAiImageResponse aiImageResponse = JSON.parseObject(sb.toString(), OpenAiImageResponse.class);
            String url = aiImageResponse.getData().stream().map(DataRes::getUrl).collect(Collectors.joining(","));
            MessageRes res = MessageRes.builder().message(url).end(true).build();
            emitter.next(JSON.toJSONString(R.success(res)));
        }
        emitter.complete();
    }

    @Override
    public void dispose() {
        log.warn("OpenAI返回数据关闭");
        emitter.complete();
    }
}