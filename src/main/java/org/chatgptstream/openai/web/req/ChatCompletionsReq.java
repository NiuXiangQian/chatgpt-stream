package org.chatgptstream.openai.web.req;

import lombok.Data;

/**
 * @author niuxiangqian
 * @version 1.0
 * @date 2023/3/21 16:19
 **/
@Data
public class ChatCompletionsReq {
    private String prompt;
    private Integer maxTokens;
    private Double temperature;
    private Double topP;
    private String user;

}
