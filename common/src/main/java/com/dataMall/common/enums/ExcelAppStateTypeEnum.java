package com.dataMall.common.enums;

public enum ExcelAppStateTypeEnum {
    /**
     * 仅上传
     */
    ONLY_UPLOAD("仅上传", -2),
    /**
     * 待审核
     */
    WAITING_CHECK("待审核", -3),
    /**
     * 正常
     */
    NORMAL("正常", 1),
    /**
     * 禁用
     */
    DISABLE("禁用", 0),
    /**
     * 删除
     */
    DELETE("删除", -1);
    
    private final String description;
    
    private final Integer value;
    
    ExcelAppStateTypeEnum(String description, Integer value) {
        this.description = description;
        this.value = value;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Integer getValue() {
        return value;
    }
}
