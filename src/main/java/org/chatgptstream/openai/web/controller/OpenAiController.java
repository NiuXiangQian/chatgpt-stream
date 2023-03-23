package org.chatgptstream.openai.web.controller;

import org.chatgptstream.openai.util.api.OpenAiWebClient;
import org.chatgptstream.openai.listener.OpenAISubscriber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @author niuxiangqian
 * @version 1.0
 * @date 2023/3/21 16:18
 **/
@Slf4j
@RestController
@RequestMapping({"/openai"})
@RequiredArgsConstructor
public class OpenAiController {
    private final OpenAiWebClient openAiWebClient;

    @Value("${authorization}")
    private String authorization;

    /**
     * 流式返回
     *
     * @param prompt 提示词
     * @param user   用户
     * @return
     */
    @GetMapping(value = "/completions/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamCompletions(String prompt, String user) {
        Assert.hasLength(user, "user不能为空");
        Assert.hasLength(prompt, "prompt不能为空");
        checkContent(prompt);

        return Flux.create(emitter -> {
            OpenAISubscriber subscriber = new OpenAISubscriber(emitter);
            Flux<String> openAiResponse =
                openAiWebClient.getChatResponse(authorization, user, prompt, null, null, null);
            openAiResponse.subscribe(subscriber);
            emitter.onDispose(subscriber);
        });
    }


    public void checkContent(String prompt) {
        Assert.isTrue(Boolean.FALSE.equals(openAiWebClient.checkContent(authorization, prompt).block()), "您输入的内容违规");
    }

}
