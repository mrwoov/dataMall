package com.dataMall.common.common;

import com.dataMall.common.common.BaseResponse;
import com.dataMall.common.common.ErrorCode;

public class ResultUtils {
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(200, data, "ok");
    }

    public static <T> BaseResponse<T> success() {
        return new BaseResponse<>(200, null, "ok");
    }

    public static BaseResponse error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }
    
    public static BaseResponse error(String message) {
        return new BaseResponse(ErrorCode.SYSTEM_ERROR.getCode(), null, message);
    }

    public static BaseResponse error(int code, String message, String description) {
        return new BaseResponse(code, null, message, description);
    }

    public static BaseResponse error(ErrorCode errorCode, String message, String description) {
        return new BaseResponse(errorCode.getCode(), null, message, description);
    }

    public static BaseResponse error(int code, String message) {
        return new BaseResponse(code, null, message);
    }

    public static BaseResponse state(boolean statement) {
        if (statement) {
            return success(null);
        } else {
            return error(ErrorCode.SYSTEM_ERROR);
        }
    }
}
