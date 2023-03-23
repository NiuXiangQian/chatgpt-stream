package org.chatgptstream.openai.util.api;

/**
 * @author niuxiangqian
 * @version 1.0
 * @date 2023/3/22 9:53
 **/
public interface ApiConstant {


    String HOST = "https://api.openai.com";

    String CHAT_API = HOST + "/v1/chat/completions";

    String CONTENT_AUDIT = HOST + "/v1/moderations";
}
