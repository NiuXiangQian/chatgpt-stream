package org.chatgptstream.openai.web.controller;

import com.alibaba.fastjson.JSON;
import org.chatgptstream.openai.enmus.MessageType;
import org.chatgptstream.openai.exception.CommonException;
import org.chatgptstream.openai.service.UserChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chatgptstream.openai.service.dto.Message;
import org.chatgptstream.openai.util.R;
import org.chatgptstream.openai.util.api.OpenAiWebClient;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
    private final UserChatService userChatService;
    private final OpenAiWebClient openAiWebClient;
    private static final String ERROR_MSG = "使用的人太多啦！等下再用吧！";
    /**
     * 建议更换为自己业务的线程池
     */
    private static final Executor EXECUTOR = Executors.newFixedThreadPool(10);
    private static final Random RANDOM = new Random();


    /**
     * 发信息
     *
     * @param prompt 提示词
     * @param user   用户
     * @return
     */
    @GetMapping(value = "/completions/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamCompletions(String prompt, String user) {
        Assert.hasLength(user, "user不能为空");
        Assert.hasLength(prompt, "prompt不能为空");
        try {
            return userChatService.send(MessageType.TEXT, prompt, user);
        } catch (CommonException e) {
            log.warn("e:{}", e.getMessage());
            e.printStackTrace();
            return getErrorRes(e.getMessage());
        } catch (Exception e) {
            log.error("e:{}", e.getMessage(), e);
            e.printStackTrace();
            return getErrorRes(ERROR_MSG);
        }
    }

    /**
     * post方式，可以解决特殊符号，过长的文本等问题
     *
     * @return
     */
    @PostMapping(value = "/completions/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamCompletionsPost(@RequestBody Map<String, String> param) {
        String user = param.get("user");
        String prompt = param.get("prompt");

        Assert.hasLength(user, "user不能为空");
        Assert.hasLength(prompt, "prompt不能为空");
        try {
            return userChatService.send(MessageType.TEXT, prompt, user);
        } catch (CommonException e) {
            log.warn("e:{}", e.getMessage());
            e.printStackTrace();
            return getErrorRes(e.getMessage());
        } catch (Exception e) {
            log.error("e:{}", e.getMessage(), e);
            e.printStackTrace();
            return getErrorRes(ERROR_MSG);
        }
    }


    /**
     * 内容检测
     *
     * @param content
     * @return
     */
    @GetMapping("/checkContent")
    public Mono<ServerResponse> checkContent(@RequestParam String content) {
        log.info("req:{}", content);
        return openAiWebClient.checkContent(content);
    }

    /**
     * 获取历史记录
     *
     * @param user
     */
    @GetMapping(value = "/history")
    public Mono<List<Message>> history(String user) {
        Assert.hasLength(user, "user不能为空");
        return Mono.just(userChatService.getHistory(user));
    }

    /**
     * 对sse接口的异常处理
     * 我这里的建议是不要直接抛出异常中断sse链接，因为这样前端无法获取错误信息，只能获取到链接断开了
     * 所以建议正常返回数据，把返回的数据中的code设置为非0的值，前端根据code来判断是否是错误信息，参考 @see org.chatgptstream.openai.util.R
     *
     * @param msg
     * @return
     */
    private Flux<String> getErrorRes(String msg) {
        return Flux.create(emitter -> {
            emitter.next(" ");
            emitter.next(" ");
            EXECUTOR.execute(() -> {
                try {
                    int time = RANDOM.nextInt(200);
                    // 请注意！这里加线程池休眠是为了解决一个问题，如果你不需要则删除掉这里线程池就行
                    // 问题：假如系统使用了nginx负载均衡，然后后端这个接口遇到异常立即断开sse会导致nginx重连，进而重复请求后端
                    // 所以休眠一下再断开让nginx知道正常连接了，不要重连

                    //不延迟的话nginx会重连sse，导致nginx重复请求后端
                    Thread.sleep(Math.max(time, 100));
                } catch (InterruptedException e) {
                    log.info("e:", e);
                }
                emitter.next(JSON.toJSONString(R.fail(msg)));
                emitter.complete();
            });
        });
    }

}
