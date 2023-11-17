package com.dataMall.goodsCenter.controller;


import com.dataMall.goodsCenter.entity.GoodsCategories;
import com.dataMall.goodsCenter.service.GoodsCategoriesService;
import com.dataMall.goodsCenter.vo.ResultData;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 商品分类表 前端控制器
 * </p>
 *
 * @author woov
 * @since 2023-08-29
 */
@RestController
@RequestMapping("/goodsCategories")
public class GoodsCategoriesController {
    @Resource
    private GoodsCategoriesService goodsCategoriesService;


    //查询商品分类
    @GetMapping("/")
    public ResultData getList() {
        List<GoodsCategories> list = goodsCategoriesService.list();
        list.removeIf(goodsCategories -> goodsCategories.getState() != 0);
        return ResultData.success(list);
    }
}

