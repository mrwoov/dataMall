package com.example.datamall.controller;


import com.example.datamall.entity.GoodsCategories;
import com.example.datamall.service.AccountService;
import com.example.datamall.service.GoodsCategoriesService;
import com.example.datamall.vo.ResultData;
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
    private AccountService accountService;
    @Resource
    private GoodsCategoriesService goodsCategoriesService;


    //管理员新建或修改商品分类
    @PatchMapping("/admin")
    public ResultData save(@RequestBody GoodsCategories goodsCategories, @RequestHeader("token") String token) {
        boolean isAdmin = accountService.checkAdminHavaAuth(authPath, token);
        if (!isAdmin) {
            return ResultData.fail("无权限");
        }
        boolean state = goodsCategoriesService.saveOrUpdate(goodsCategories);
        return ResultData.state(state);
    }

    //管理员删除商品分类
    @DeleteMapping("/admin/{id}")
    public ResultData delete(@PathVariable Integer id, @RequestHeader("token") String token) {
        boolean isAdmin = accountService.checkAdminHavaAuth(authPath, token);
        if (!isAdmin) {
            return ResultData.fail("无权限");
        }
        boolean state = goodsCategoriesService.removeById(id);
        return ResultData.state(state);
    }

    //管理员批量删除商品分类
    @PostMapping("/admin/del_batch")
    public ResultData deleteBatch(@RequestBody List<Integer> ids, @RequestHeader("token") String token) {
        boolean isAdmin = accountService.checkAdminHavaAuth(authPath, token);
        if (!isAdmin) {
            return ResultData.fail("无权限");
        }
        boolean state = goodsCategoriesService.removeByIds(ids);
        return ResultData.state(state);
    }

    //管理员分页查询商品分类：名称，url（模糊查询）
    @PostMapping("/admin/query")
    public ResultData queryGoodsCategoriesInfoPageByOption(@RequestHeader("token") String token, @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize, @RequestBody GoodsCategories goodsCategories) {
        boolean isAdmin = accountService.checkAdminHavaAuth(authPath, token);
        if (!isAdmin) {
            return ResultData.fail("无权限");
        }
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

