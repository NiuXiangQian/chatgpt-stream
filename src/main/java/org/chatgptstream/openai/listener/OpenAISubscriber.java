package org.chatgptstream.openai.listener;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.chatgptstream.openai.service.dto.Message;
import org.chatgptstream.openai.util.api.res.OpenAiResponse;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.publisher.FluxSink;

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

    public OpenAISubscriber(FluxSink<String> emitter, String sessionId, CompletedCallBack completedCallBack, Message questions) {
        this.emitter = emitter;
        this.sessionId = sessionId;
        this.completedCallBack = completedCallBack;
        this.questions = questions;
        this.sb = new StringBuilder();
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(String data) {
        log.info("OpenAI返回数据：{}", data);
        if ("[DONE]".equals(data)) {
            log.info("OpenAI返回数据结束了");
            emitter.next("[DONE]");
            subscription.request(1);
            completedCallBack.completed(questions, sessionId, sb.toString());
            emitter.complete();
        } else {
            OpenAiResponse openAiResponse = JSON.parseObject(data, OpenAiResponse.class);
            String content = openAiResponse.getChoices().get(0).getDelta().getContent();
            content = content == null ? "" : content;
            emitter.next(content);
            sb.append(content);
            subscription.request(1);
        }

    }

    @Override
    public void onError(Throwable t) {
        log.error("OpenAI返回数据异常：{}", t.getMessage());
        emitter.error(t);
        completedCallBack.fail(sessionId);
    }

    @Override
    public void onComplete() {
        log.info("OpenAI返回数据完成");
        emitter.complete();
    }

    @Override
    public void dispose() {
        log.warn("OpenAI返回数据关闭");
        emitter.complete();
    }
}