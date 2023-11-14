package com.example.datamall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author woov
 * @since 2023-11-11
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("order_index")
public class OrderIndex implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer accountId;

    /**
     * 0进行中1交易完成-1交易关闭
     */
    private Integer state;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
    @TableField(exist = false)
    private String username;
    @TableField(exist = false)
    private String tradeNo;
    @TableField(exist = false)
    private double money;
    @TableField(exist = false)
    private Integer price;

}
