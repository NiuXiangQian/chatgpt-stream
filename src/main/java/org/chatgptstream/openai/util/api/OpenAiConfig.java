package org.chatgptstream.openai.util.api;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author niuxiangqian
 * @version 1.0
 * @date 2023/7/28 14:50
 **/
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "openai")
public class OpenAiConfig {
    private List<String> keyList;
    private static final AtomicInteger ATOMIC_LONG = new AtomicInteger(0);

    /**
     * 先采用轮询的方式获取key
     * @return
     */
    public String getAuthorization() {
        int count = ATOMIC_LONG.incrementAndGet();
        if (count >= 10000) {
            ATOMIC_LONG.set(0);
        }
        String s = this.getKeyList().get(count % this.getKeyList().size());
//        log.info("getAuthorization:{}", s);
        return s;
    }
}
