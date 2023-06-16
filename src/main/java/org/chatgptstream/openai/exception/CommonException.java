package org.chatgptstream.openai.exception;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author niuxiangqian
 * @version 1.0
 * @date 2023/6/16 18:50
 **/
@Data
@RequiredArgsConstructor
public class CommonException extends RuntimeException{
    public CommonException(String message) {
        super(message);
    }
}
