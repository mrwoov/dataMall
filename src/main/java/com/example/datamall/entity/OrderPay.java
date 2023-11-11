package com.example.datamall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 订单表
 * </p>
 *
 * @author woov
 * @since 2023-11-11
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("order_pay")
public class OrderPay implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String platTradeNo;

    private String tradeNo;

    private Integer orderId;

    /**
     * bigdecimal(16,2)
     */
    private Integer totalAmount;

    private String payType;

    /**
     * -1未支付0已支付
     */
    private Integer state;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
