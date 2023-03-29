package org.chatgptstream.openai.util.api;

import com.alibaba.fastjson.JSONObject;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.ProxyProvider;


import javax.annotation.PostConstruct;
import javax.net.ssl.SSLException;

import java.util.Collections;

/**
 * @author niuxiangqian
 * @version 1.0
 * @date 2023/3/21 17:18
 **/
@Slf4j
@Component
public class OpenAiWebClient {
    private WebClient webClient;
    @Value("${env:test}")
    private String env;
    @Value("${authorization}")
    private String authorization;

    /**
     * dev采用代理访问
     */
    @PostConstruct
    public void init() {
        log.info("init:{}", env);
        if (env.contains("test")) {
            initDev();
        } else {
            initProd();
        }
    }

    public void initDev() {
        log.info("initDev");
        SslContext sslContext = null;
        try {
            sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
        } catch (SSLException e) {
            throw new RuntimeException(e);
        }
        // 创建HttpClient对象，并设置代理
        SslContext finalSslContext = sslContext;
        HttpClient httpClient = HttpClient.create()
            .secure(sslContextSpec -> sslContextSpec.sslContext(finalSslContext))
            .tcpConfiguration(tcpClient -> tcpClient.proxy(proxy ->
                proxy.type(ProxyProvider.Proxy.HTTP).host("127.0.0.1").port(7890)));
        //海外正式不需要代理
        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        this.webClient = WebClient.builder().clientConnector(connector)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .build();
    }


    public void initProd() {
        log.info("initProd");
        this.webClient = WebClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .build();
    }


    public Flux<String> getChatResponse(String user, String prompt, Integer maxTokens, Double temperature, Double topP) {
        JSONObject params = new JSONObject();

        params.put("model", "gpt-3.5-turbo");
        params.put("max_tokens", maxTokens);
        params.put("stream", true);
        params.put("temperature", temperature);
        params.put("top_p", topP);
        params.put("user", user);
        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", prompt);
        params.put("messages", Collections.singleton(message));

        return webClient.post()
            .uri(ApiConstant.CHAT_API)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + authorization)
            .bodyValue(params.toJSONString())
            .retrieve()
            .bodyToFlux(String.class)
            .onErrorResume(WebClientResponseException.class, ex -> {
                HttpStatus status = ex.getStatusCode();
                String res = ex.getResponseBodyAsString();
                log.error("OpenAI API error: {} {}", status, res);
                return Mono.error(new RuntimeException(res));
            });

    }

    public Flux<String> getImage(String user, String prompt) {
        JSONObject params = new JSONObject();

        params.put("size", "512x512");
        params.put("prompt", prompt);
        params.put("user", user);
        params.put("n", 2);

        return webClient.post()
            .uri(ApiConstant.IMAGE_API)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + authorization)
            .bodyValue(params.toJSONString())
            .retrieve().bodyToFlux(String.class)
            .onErrorResume(WebClientResponseException.class, ex -> {
                HttpStatus status = ex.getStatusCode();
                String res = ex.getResponseBodyAsString();
                log.error("OpenAI API error: {} {}", status, res);
                return Mono.error(new RuntimeException(res));
            });

    }

    /**
     * 内容检查
     * 频繁输入违规的内容，会导致账号被封禁
     *
     * @param prompt
     * @return
     */
    public Mono<ServerResponse> checkContent(String prompt) {
        JSONObject params = new JSONObject();
        params.put("input", prompt);
        return webClient.post()
            .uri(ApiConstant.CONTENT_AUDIT)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + authorization)
            .bodyValue(params.toJSONString())
            .retrieve()
            .bodyToMono(JSONObject.class)
            .flatMap(jsonObject -> {
                // 在这里处理 JSON 对象，例如将其转换为其他类型
                // 并将结果包装为响应体返回
                Boolean aBoolean = jsonObject.getJSONArray("results").getJSONObject(0).getBoolean("flagged");
                return ServerResponse.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(BodyInserters.fromValue(aBoolean));
            });
    }
}
