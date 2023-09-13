package com.example.datamall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 商品表
 * </p>
 *
 * @author woov
 * @since 2023-08-29
 */
@Getter
@Setter
@Accessors(chain = true)
public class Goods implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品分类
     */
    private Integer categoriesId;

    /**
     * 商品详情
     */
    private String detail;

    /**
     * 商品主图
     */
    private String picIndex;

    /**
     * 价格，单位分
     */
    private Integer price;

    /**
     * 上传用户
     */
    private Integer uid;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 商品状态0正常1下架-1冻结-2假删
     */
    private Integer state;

    @TableField(exist = false)
    private String username;

    @TableField(exist = false)
    private String categoriesName;

    @TableField(exist = false)
    private double money;

    private Goods(Integer goodsId, String name, Integer categoriesId, String detail, String picIndex, Integer price) {
        this.setId(goodsId);
        this.setName(name);
        this.setCategoriesId(categoriesId);
        this.setDetail(detail);
        this.setPicIndex(picIndex);
        this.setPrice(price);
    }

    public Goods dealUserUpdateGoods() {
        Integer goodsId = this.getId();
        String name = this.getName();
        Integer categoriesId = this.getCategoriesId();
        String detail = this.getDetail();
        String picIndex = this.getPicIndex();
        Integer price = this.getPrice();
        return new Goods(goodsId, name, categoriesId, detail, picIndex, price);
    }

    public void moneyToPrice() {
        this.price = (int) (this.money * 100);
    }

    public void priceToMoney() {
        this.money = this.price / 100.00;
    }
}
