package org.chatgptstream.openai.util.api.res.chat.image;

import lombok.Data;

import java.util.List;

/**
 * @author niuxiangqian
 * @version 1.0
 * @date 2023/3/23 22:28
 **/
@Data
public class OpenAiImageResponse {
    private Long created;
    private List<DataRes> data;
}
