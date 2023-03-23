package org.chatgptstream.openai.enmus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author niuxiangqian
 * @version 1.0
 * @date 2023/3/23 14:49
 **/
@Getter
@RequiredArgsConstructor
public enum UserType {
    USER("Q:%s\n"), BOT("A:%s\n");
    private final String code;
}
