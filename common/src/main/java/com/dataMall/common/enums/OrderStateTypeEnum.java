package com.dataMall.common.enums;

import lombok.Getter;

@Getter
public enum OrderStateTypeEnum {
    /**
     * 已删除
     */
    DELETED("已删除", -1),
    /**
     * 未支付
     */
    UNPAID("未支付", 0),
    /**
     * 已支付，未处理
     */
    PAYED_UNTREATED("已支付", 1),
    /**
     * 已处理
     */
    TREATED("已处理", 2),
    /**
     * 已取消
     */
    CANCEL("已取消", 3),
    /**
     * 已退款
     */
    REFUND("已退款", 4);

    private final String name;
    private final Integer value;

    OrderStateTypeEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public static OrderStateTypeEnum getEnumByValue(Integer value) {
        for (OrderStateTypeEnum e : OrderStateTypeEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }

    public static OrderStateTypeEnum getEnumByName(String name) {
        for (OrderStateTypeEnum e : OrderStateTypeEnum.values()) {
            if (e.getName().equals(name)) {
                return e;
            }
        }
        return null;
    }

}
