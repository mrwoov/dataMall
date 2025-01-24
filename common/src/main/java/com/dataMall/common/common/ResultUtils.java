package com.dataMall.common.common;

import com.dataMall.common.exception.BusinessException;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

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

    public static BaseResponse error(ErrorCode errorCode, String message) {
        return new BaseResponse(errorCode.getCode(), null, message, null);
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
    
    public static void throwIf(boolean statement, ErrorCode errorCode, String message) {
        if (statement) {
            throw new BusinessException(errorCode, message);
        }
    }
    public static void throwIfAndRollback(boolean statement, ErrorCode errorCode, String message) {
        if (statement) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new BusinessException(errorCode, message);
        }
    }
}
