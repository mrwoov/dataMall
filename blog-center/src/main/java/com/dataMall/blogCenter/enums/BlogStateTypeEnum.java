package com.dataMall.blogCenter.enums;

import lombok.Getter;

@Getter
public enum BlogStateTypeEnum {
    //状态：0已发布 -1假删 -2草稿 -3待审核 ，审核失败-4
    
    PUBLISHED("已发布", 0),
    DELETED("已删除", -1),
    DRAFT("草稿", -2),
    TO_BE_AUDITED("待审核", -3),
    AUDIT_FAILED("审核失败", -4);
    
    private final String name;
    private final Integer value;

    BlogStateTypeEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public static String getNameByValue(Integer value) {
        for (BlogStateTypeEnum blogStateTypeEnum : BlogStateTypeEnum.values()) {
            if (blogStateTypeEnum.getValue().equals(value)) {
                return blogStateTypeEnum.getName();
            }
        }
        return null;
    }
    
    public static Integer getValueByName(String name) {
        for (BlogStateTypeEnum blogStateTypeEnum : BlogStateTypeEnum.values()) {
            if (blogStateTypeEnum.getName().equals(name)) {
                return blogStateTypeEnum.getValue();
            }
        }
        return null;
    }
    
}
