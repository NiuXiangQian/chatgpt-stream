package org.chatgptstream.openai.listener;

import org.chatgptstream.openai.service.dto.Message;

/**
 * @author niuxiangqian
 * @version 1.0
 * @date 2023/3/23 15:14
 **/
public interface CompletedCallBack {

    /**
     * 成功完成回调用
     *
     * @param questions
     * @param sessionId
     * @param response
     */
    void completed(Message questions, String sessionId, String response);

    /**
     * 失败回调
     *
     * @param questions
     * @param sessionId
     * @param response
     */
    void fail(Message questions, String sessionId, String response);

    /**
     * 清除历史
     * @param sessionId
     */
    void clearHistory(String sessionId);
}
