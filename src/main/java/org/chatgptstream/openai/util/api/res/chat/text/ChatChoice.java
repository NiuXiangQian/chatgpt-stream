package org.chatgptstream.openai.util.api.res.chat.text;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author niuxiangqian
 * @version 1.0
 * @date 2023/3/21 17:19
 **/
@Data
public class ChatChoice implements Serializable {
    private long index;
    @JsonProperty("delta")
    private Message delta;
    @JsonProperty("message")
    private Message message;
    @JsonProperty("finish_reason")
    private String finishReason;
}
