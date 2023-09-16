package com.example.datamall.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.datamall.entity.Goods;
import com.example.datamall.entity.GoodsCategories;
import com.example.datamall.service.AccountService;
import com.example.datamall.service.GoodsCategoriesService;
import com.example.datamall.service.GoodsCollectionService;
import com.example.datamall.service.GoodsService;
import com.example.datamall.vo.ResultData;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 商品表 前端控制器
 * </p>
 *
 * @author woov
 * @since 2023-08-29
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Resource
    private AccountService accountService;
    @Resource
    private GoodsService goodsService;

    @Resource
    private GoodsCategoriesService goodsCategoriesService;
    @Resource
    private GoodsCollectionService goodsCollectionService;

    // 用户发布商品
    @PatchMapping("/")
    public ResultData release(@RequestHeader("token") String token, @RequestBody Goods goods) {
        Integer uid = accountService.tokenToUid(token);
        if (uid == -1) {
            return ResultData.fail("登录过期");
        }
        goods.setUid(uid);
        goods.moneyToPrice();
        boolean state = goodsService.save(goods);
        return ResultData.state(state);
    }

    // 用户下架商品
    @PostMapping("/release_off")
    public ResultData releaseOff(@RequestHeader("token") String token, @RequestParam("goodsId") Integer goodsId) {
        Integer uid = accountService.tokenToUid(token);
        if (uid == -1) {
            return ResultData.fail("登录过期");
        }
        boolean state = goodsService.userUpdateGoodsState(uid, goodsId, 1);
        return ResultData.state(state);
    }

    // 用户上架商品
    @PostMapping("release_on")
    public ResultData releaseOn(@RequestHeader("token") String token, @RequestParam("goodsId") Integer goodsId) {
        Integer uid = accountService.tokenToUid(token);
        if (uid == -1) {
            return ResultData.fail("登录过期");
        }
        boolean state = goodsService.userUpdateGoodsState(uid, goodsId, 0);
        return ResultData.state(state);
    }

    // 用户删除商品
    @DeleteMapping("/")
    public ResultData del(@RequestHeader("token") String token, @RequestParam("goodsId") Integer goodsId) {
        Integer uid = accountService.tokenToUid(token);
        if (uid == -1) {
            return ResultData.fail("登录过期");
        }
        boolean owner = goodsService.isOwner(uid, goodsId);
        if (!owner) {
            return ResultData.fail();
        }
        boolean state = goodsService.removeById(goodsId);
        return ResultData.state(state);
    }

    // 管理员冻结商品
    @PostMapping("/admin/freeze")
    public ResultData freeze(@RequestHeader("token") String token, @RequestParam("goodsId") Integer goodsId) {
        boolean admin = accountService.checkAdminHavaAuth("/admin", token);
        if (!admin) {
            return ResultData.fail("无权限");
        }
        return ResultData.state(goodsService.adminUpdateGoodsState(goodsId, -1));
    }

    // 管理员解冻商品
    @PostMapping("/admin/unfreeze")
    public ResultData unfreeze(@RequestHeader("token") String token, @RequestParam("goodsId") Integer goodsId) {
        boolean admin = accountService.checkAdminHavaAuth("/admin", token);
        if (!admin) {
            return ResultData.fail("无权限");
        }
        return ResultData.state(goodsService.adminUpdateGoodsState(goodsId, 0));
    }

    // 获取单个商品信息
    @GetMapping("/info/{goodsId}")
    public ResultData getInfo(@PathVariable("goodsId") Integer goodsId) {
        Goods goods = goodsService.getById(goodsId);
        goods.setUsername(accountService.getById(goods.getUid()).getUsername());
        goods.setCategoriesName(goodsCategoriesService.getById(goods.getCategoriesId()).getName());
        goods.priceToMoney();
        goods.setCollection(goodsCollectionService.goodsCollectionNum(goodsId));
        return ResultData.success(goods);
    }

    // 管理员分页查询商品列表
    @PostMapping("/admin/page")
    public ResultData page(@RequestHeader("token") String token, @RequestParam("pageSize") Integer pageSize, @RequestParam("pageNum") Integer pageNum, @RequestBody Goods goods) {
        boolean admin = accountService.checkAdminHavaAuth("/admin", token);
        if (!admin) {
            return ResultData.fail("无权限");
        }
        String name = goods.getName();
        String categoriesName = goods.getCategoriesName();
        String username = goods.getUsername();
        if (pageNum == null || pageSize == null) {
            return ResultData.fail("缺少参数");
        }
        IPage<Goods> page = goodsService.getGoodsPage(name, categoriesName, username, pageNum, pageSize);
        return ResultData.success(page);
    }

    // 查询单个用户发布的商品列表
    @GetMapping("/list/{uid}")
    public ResultData getUserGoodsList(@PathVariable("uid") String uid) {
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        List<Goods> list = goodsService.list(queryWrapper);
        for (Goods goods : list) {
            goods.setUsername(accountService.getById(goods.getUid()).getUsername());
            goods.setCategoriesName(goodsCategoriesService.getById(goods.getCategoriesId()).getName());
            goods.priceToMoney();
        }
        return ResultData.success(list);
    }

    // 用户修改商品信息
    @PostMapping("/update")
    public ResultData updateGoods(@RequestHeader("token") String token, @RequestBody Goods goods) {
        Integer uid = accountService.tokenToUid(token);
        if (uid == -1) {
            return ResultData.fail("登录过期");
        }
        boolean owner = goodsService.isOwner(uid, goods.getId());
        if (!owner) {
            return ResultData.fail();
        }
        boolean state = goodsService.updateById(goods.dealUserUpdateGoods());
        return ResultData.state(state);
    }

    //搜索商品
    @GetMapping("/search")
    public ResultData search(@RequestParam("keyword") String keyword) {
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", keyword);
        List<Goods> goodsList = goodsService.getGoodsList(queryWrapper);
        return ResultData.success(goodsList);
    }

    //获取类别下的商品
    @GetMapping("/categories")
    public ResultData categoriesGoods(@RequestParam("categories") String categories) {
        GoodsCategories goodsCategories = goodsCategoriesService.getOneByOption("url", categories);
        if (goodsCategories == null) {
            return ResultData.fail();
        }
        Integer categoriesId = goodsCategories.getId();
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("categories_id", categoriesId);
        List<Goods> goodsList = goodsService.getGoodsList(queryWrapper);
        return ResultData.success(goodsList);
    }

}

