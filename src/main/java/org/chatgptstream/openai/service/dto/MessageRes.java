package org.chatgptstream.openai.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.chatgptstream.openai.enmus.MessageType;
import org.chatgptstream.openai.enmus.UserType;

import java.util.Date;

/**
 * @author niuxiangqian
 * @version 1.0
 * @date 2023/3/23 14:48
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageRes {
    private MessageType messageType;
    private String message;
    private Boolean end;

}
