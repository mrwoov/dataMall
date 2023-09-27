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
 * 商品收藏表
 * </p>
 *
 * @author woov
 * @since 2023-08-29
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("goods_collection")
public class GoodsCollection implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    private Integer uid;

    /**
     * 商品id
     */
    private Integer goodsId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
