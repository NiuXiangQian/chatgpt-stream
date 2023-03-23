package org.chatgptstream.openai.util.api.res.chat.text;

import lombok.Data;

/**
 * @author niuxiangqian
 * @version 1.0
 * @date 2023/3/21 20:25
 **/
@Data
public class Message {
    private String role;
    private String content;
}
