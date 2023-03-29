package org.chatgptstream.openai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;


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
    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.build();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return objectMapper;
    }
}
