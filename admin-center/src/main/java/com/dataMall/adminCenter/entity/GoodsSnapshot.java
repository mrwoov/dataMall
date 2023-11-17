package com.dataMall.adminCenter.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 商品快照表
 * </p>
 *
 * @author woov
 * @since 2023-11-16
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("goods_snapshot")
public class GoodsSnapshot implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 商家id
     */
    private Integer ownerId;

    /**
     * 商品id
     */
    private Integer goodsId;

    /**
     * 商品名
     */
    private String name;

    /**
     * 商品首页图
     */
    private String picIndex;

    /**
     * 商品当前价格
     */
    private Integer price;

    /**
     * 文件md5
     */
    private String fileMd5;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    /**
     * 状态
     */
    private Integer state;

    @TableField(exist = false)
    private double money;

}
