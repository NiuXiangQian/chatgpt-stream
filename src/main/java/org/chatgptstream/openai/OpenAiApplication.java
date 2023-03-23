package org.chatgptstream.openai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * @author niuxiangqian
 * @version 1.0
 * @date 2023/3/21 16:14
 **/
@Slf4j
@SpringBootApplication
public class OpenAiApplication {


    public static void main(String[] args) {
        log.info("OpenAiApplication start");
        SpringApplication.run(OpenAiApplication.class, args);
    }
}
