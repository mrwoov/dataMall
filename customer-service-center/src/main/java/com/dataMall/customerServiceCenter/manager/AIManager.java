package com.dataMall.customerServiceCenter.manager;

import com.dataMall.common.common.ErrorCode;
import com.dataMall.common.exception.BusinessException;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
/**
 * 智谱AI通用调用能力
 */
@Slf4j
@Component
public class AIManager {
    @Resource
    private ClientV4 client;
    
    /**
     * 通用异步请求方法
     *
     * @param systemMessage 系统消息
     * @param userMessage   用户消息
     * @return 返回结果
     */
    public String doAsyncRequest(String systemMessage, String userMessage) {
        return doRequest(systemMessage, userMessage, Boolean.TRUE);
    }

    /**
     * 通用同步请求方法
     *
     * @param systemMessage 系统消息
     * @param userMessage   用户消息
     * @return 返回结果
     */
    public String doSyncRequest(String systemMessage, String userMessage) {
        return doRequest(systemMessage, userMessage, Boolean.FALSE);
    }

    /**
     * 通用请求方法(简化消息传递)
     *
     * @param systemMessage 系统消息
     * @param userMessage   用户消息
     * @param stream        是否流式
     * @return 返回结果
     */
    public String doRequest(String systemMessage, String userMessage, Boolean stream) {
        //构建请求
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemChatMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage);
        messages.add(systemChatMessage);
        ChatMessage userChatMessage = new ChatMessage(ChatMessageRole.USER.value(), userMessage);
        messages.add(userChatMessage);
        return doRequest(messages, stream);
    }

    /**
     * 通用请求方法
     *
     * @param messages 消息列表
     * @param stream   是否流式
     * @return 返回结果
     */
    public String doRequest(List<ChatMessage> messages, Boolean stream) {
        //构造请求
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("GLM-4-Flash")
                .stream(stream)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .build();
        
        try {
            ModelApiResponse invokeModelApiResp = client.invokeModelApi(chatCompletionRequest);
            return invokeModelApiResp.getData().getChoices().get(0).getMessage().getContent().toString();
        } catch (Exception e) {
            log.error("AI请求异常", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());
        }
        
        
    }

}
