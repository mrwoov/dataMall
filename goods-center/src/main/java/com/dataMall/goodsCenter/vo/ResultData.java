package com.dataMall.goodsCenter.vo;

import lombok.Data;

@Data
public class ResultData {
    private int status;
    private String message;
    private String errorMsg;
    private Object data;
    private long timestamp;

    public ResultData() {
        this.timestamp = System.currentTimeMillis();
    }

    public static ResultData success(Object data) {
        if (data == null) {
            return ResultData.fail(500, "No data found");
        }
        ResultData resultData = new ResultData();
        resultData.setStatus(200);
        resultData.setMessage("success");
        resultData.setData(data);
        return resultData;
    }

    public static ResultData success(String message) {
        ResultData resultData = new ResultData();
        resultData.setStatus(200);
        resultData.setMessage(message);
        return resultData;
    }

    public static ResultData success() {
        return success("操作成功");
    }

    public static ResultData fail(int code, String message) {
        ResultData resultData = new ResultData();
        resultData.setStatus(code);
        resultData.setErrorMsg(message);
        return resultData;
    }

    public static ResultData fail(String message) {
        ResultData resultData = new ResultData();
        resultData.setStatus(300); //业务错误
        resultData.setErrorMsg(message);
        return resultData;
    }

    public static ResultData fail() {
        return fail("操作失败");
    }

    public static ResultData state(boolean statement) {
        if (statement) {
            return success();
        } else return fail();
    }

}
