package com.dataMall.adminCenter.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 用户订单表
 * </p>
 *
 * @author woov
 * @since 2023-11-16
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("user_order")
public class UserOrder implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer accountId;

    /**
     * 订单号
     */
    private String tradeNo;

    /**
     * 支付方式
     */
    private String payType;

    /**
     * 总费用，单位分
     */
    private Integer totalAmount;

    /**
     * 平台订单号
     */
    private String platTradeNo;

    /**
     * 0进行中1已付款2交易完成-1交易关闭-2已退款
     */
    private Integer state;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String username;


    @TableField(exist = false)
    private double money;

    @TableField(exist = false)
    private List<GoodsSnapshot> goodsSnapshots;
}
