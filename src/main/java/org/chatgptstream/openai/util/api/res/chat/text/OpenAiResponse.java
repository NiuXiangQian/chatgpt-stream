package org.chatgptstream.openai.util.api.res.chat.text;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author niuxiangqian
 * @version 1.0
 * @date 2023/3/21 17:20
 **/
@Data
public class OpenAiResponse implements Serializable {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<ChatChoice> choices;
    private Usage usage;
}
