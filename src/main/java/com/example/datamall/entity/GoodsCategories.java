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
 * 商品分类表
 * </p>
 *
 * @author woov
 * @since 2023-08-29
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("goods_categories")
public class GoodsCategories implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 商品分类名称
     */
    private String name;

    /**
     * 分类url
     */
    private String url;

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
     * 商品分类名称0正常1隐藏-2假删
     */
    private Integer state;

    @TableField(exist = false)
    private String stateText;

}
