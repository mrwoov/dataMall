package com.dataMall.adminCenter.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dataMall.adminCenter.aop.AdminAuth;
import com.dataMall.common.common.BaseResponse;
import com.dataMall.common.common.ErrorCode;
import com.dataMall.common.common.ResultUtils;
import com.dataMall.common.entity.GoodsCategories;
import com.dataMall.common.exception.BusinessException;
import com.dataMall.adminCenter.service.GoodsCategoriesService;
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
    public BaseResponse<Object> save(@RequestBody GoodsCategories goodsCategories) {
        boolean state = goodsCategoriesService.saveOrUpdate(goodsCategories);
        if (!state) {
            throw new BusinessException(ErrorCode.FAIL);
         }
        return ResultUtils.success();
    }

    //管理员删除商品分类
    @DeleteMapping("/admin/{id}")
    @AdminAuth(value = authPath)
    public BaseResponse<Object> delete(@PathVariable Integer id) {
        boolean state = goodsCategoriesService.removeById(id);
        if (!state) {
            throw new BusinessException(ErrorCode.FAIL);
         }
        return ResultUtils.success();
    }

    //管理员批量删除商品分类
    @PostMapping("/admin/del_batch")
    @AdminAuth(value = authPath)
    public BaseResponse<Object> deleteBatch(@RequestBody List<Integer> ids) {
        boolean state = goodsCategoriesService.removeByIds(ids);
        if (!state) {
            throw new BusinessException(ErrorCode.FAIL);
         }
        return ResultUtils.success();
    }

    //管理员分页查询商品分类：名称，url（模糊查询）
    @PostMapping("/admin/query")
    @AdminAuth(value = authPath)
    public BaseResponse<IPage<GoodsCategories>> queryGoodsCategoriesInfoPageByOption(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize, @RequestBody GoodsCategories goodsCategories) {
        String name = goodsCategories.getName();
        String url = goodsCategories.getUrl();
        if (pageNum == null || pageSize == null) {
            throw new BusinessException(ErrorCode.FAIL);
        }
        return ResultUtils.success(goodsCategoriesService.queryGoodsCategoriesPageByOption(name, url, pageNum, pageSize));
    }

    //查询商品分类
    @GetMapping("/")
    public BaseResponse<List<GoodsCategories>> getList() {
        List<GoodsCategories> list = goodsCategoriesService.list();
        list.removeIf(goodsCategories -> goodsCategories.getState() != 0);
        return ResultUtils.success(list);
    }
}

