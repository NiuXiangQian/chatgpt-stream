package org.chatgptstream.openai.util.api.res;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author niuxiangqian
 * @version 1.0
 * @date 2023/3/21 17:20
 **/
@Data
public class Usage  implements Serializable {
    @JsonProperty("prompt_tokens")
    private long promptTokens;
    @JsonProperty("completion_tokens")
    private long completionTokens;
    @JsonProperty("total_tokens")
    private long totalTokens;
}
