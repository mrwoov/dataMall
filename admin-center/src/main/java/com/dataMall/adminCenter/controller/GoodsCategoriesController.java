package com.dataMall.adminCenter.controller;


import com.dataMall.adminCenter.aop.AdminAuth;
import com.dataMall.adminCenter.entity.GoodsCategories;
import com.dataMall.adminCenter.service.GoodsCategoriesService;
import com.dataMall.adminCenter.vo.ResultData;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

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
    private final String authPath = "categories";
    @Resource
    private GoodsCategoriesService goodsCategoriesService;


    //管理员新建或修改商品分类
    @PatchMapping("/admin")
    @AdminAuth(value = authPath)
    public ResultData save(@RequestBody GoodsCategories goodsCategories) {
        boolean state = goodsCategoriesService.saveOrUpdate(goodsCategories);
        return ResultData.state(state);
    }

    //管理员删除商品分类
    @DeleteMapping("/admin/{id}")
    @AdminAuth(value = authPath)
    public ResultData delete(@PathVariable Integer id) {
        boolean state = goodsCategoriesService.removeById(id);
        return ResultData.state(state);
    }

    //管理员批量删除商品分类
    @PostMapping("/admin/del_batch")
    @AdminAuth(value = authPath)
    public ResultData deleteBatch(@RequestBody List<Integer> ids) {
        boolean state = goodsCategoriesService.removeByIds(ids);
        return ResultData.state(state);
    }

    //管理员分页查询商品分类：名称，url（模糊查询）
    @PostMapping("/admin/query")
    @AdminAuth(value = authPath)
    public ResultData queryGoodsCategoriesInfoPageByOption( @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize, @RequestBody GoodsCategories goodsCategories) {
        String name = goodsCategories.getName();
        String url = goodsCategories.getUrl();
        if (pageNum == null || pageSize == null) {
            return ResultData.fail("缺少参数");
        }
        return ResultData.success(goodsCategoriesService.queryGoodsCategoriesPageByOption(name, url, pageNum, pageSize));
    }

    //查询商品分类
    @GetMapping("/")
    public ResultData getList() {
        List<GoodsCategories> list = goodsCategoriesService.list();
        list.removeIf(goodsCategories -> goodsCategories.getState() != 0);
        return ResultData.success(list);
    }
}

