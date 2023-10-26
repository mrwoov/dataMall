package com.example.datamall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 冻结详解表
 * </p>
 *
 * @author woov
 * @since 2023-10-19
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("goods_freeze")
public class GoodsFreeze implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer goodsId;

    /**
     * 冻结理由
     */
    private String content;

    private Integer accountId;

    /**
     * 0正常1已解冻
     */
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;


}
