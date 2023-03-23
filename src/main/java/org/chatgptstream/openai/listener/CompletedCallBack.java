package org.chatgptstream.openai.listener;

import org.chatgptstream.openai.service.dto.Message;

/**
 * @author niuxiangqian
 * @version 1.0
 * @date 2023/3/23 15:14
 **/
public interface CompletedCallBack {

    /**
     * 完成回掉
     *
     * @param questions
     * @param sessionId
     * @param response
     */
    void completed(Message questions, String sessionId, String response);

    void fail(String sessionId);

}
