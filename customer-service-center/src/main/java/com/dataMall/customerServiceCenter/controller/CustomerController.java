package com.dataMall.customerServiceCenter.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.dataMall.common.common.BaseResponse;
import com.dataMall.common.common.ErrorCode;
import com.dataMall.common.common.ResultUtils;
import com.dataMall.common.exception.BusinessException;
import com.dataMall.customerServiceCenter.config.Knowledge;
import com.dataMall.customerServiceCenter.dto.AIResponse;
import com.dataMall.customerServiceCenter.feign.UserService;
import com.dataMall.customerServiceCenter.manager.AIManager;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer")
public class CustomerController {
    @Resource
    private UserService userService;
    @Resource
    private AIManager aiManager;

    //AI客服回答问题
    @PostMapping("/ai")
    public BaseResponse<AIResponse> aiResponse(@RequestHeader("token") String token, @RequestBody String question) {
        //Integer userId = userService.tokenToUid(token);
        //ResultUtils.throwIf(userId == -1, ErrorCode.NOT_LOGIN, "token无效");
        if (StringUtils.isBlank(question)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "问题不能为空");
        }
        // AI回答问题
        String aiRes = aiManager.doSyncRequest(Knowledge.KNOWLEDGE, question);
        aiRes = aiRes.replace("\n", "").replace(" ", "");
        //解析json
        int start = aiRes.indexOf("{");
        int end = aiRes.lastIndexOf("}");
        String json = aiRes.substring(start, end + 1);
        AIResponse res = JSONUtil.toBean(json, AIResponse.class);
        return ResultUtils.success(res);
    }
    
    // 人工客服回答问题
    
}
