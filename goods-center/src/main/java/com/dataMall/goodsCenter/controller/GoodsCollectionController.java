package com.dataMall.goodsCenter.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dataMall.goodsCenter.entity.Goods;
import com.dataMall.goodsCenter.entity.GoodsCollection;
import com.dataMall.goodsCenter.feign.AccountService;
import com.dataMall.goodsCenter.service.GoodsCollectionService;
import com.dataMall.goodsCenter.service.GoodsService;
import com.dataMall.goodsCenter.vo.ResultData;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 商品收藏表 前端控制器
 * </p>
 *
 * @author woov
 * @since 2023-08-29
 */
@RestController
@RequestMapping("/goodsCollections")
public class GoodsCollectionController {
    @Resource
    private GoodsCollectionService goodsCollectionService;
    @Resource
    private AccountService accountService;
    @Resource
    private GoodsService goodsService;

    //获取用户收藏的商品
    @GetMapping("/get_user_follow")
    public ResultData getUserFollowGoods(@RequestHeader("token") String token) {
        Integer accountId = accountService.tokenToUid(token);
        if (accountId == -1) {
            return ResultData.fail("登陆过期");
        }
        QueryWrapper<GoodsCollection> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", accountId);
        List<GoodsCollection> goodsCollectionList = goodsCollectionService.list(queryWrapper);
        List<Goods> goodsList = new ArrayList<>();
        for (GoodsCollection goodsCollection : goodsCollectionList) {
            Goods goods = goodsService.getById(goodsCollection.getGoodsId());
            if (goods.getState() != 0) continue;
            goodsList.add(goods);
        }
        return ResultData.success(goodsList);
    }

    //收藏商品
    @GetMapping("/follow/{goodsId}")
    public ResultData follow(@RequestHeader("token") String token, @PathVariable("goodsId") Integer goodsId) {
        Integer accountId = accountService.tokenToUid(token);
        if (accountId == -1) {
            return ResultData.fail("登陆过期");
        }
        if (goodsCollectionService.isCollection(accountId, goodsId)) {
            ResultData.fail();
        }
        return ResultData.state(goodsCollectionService.follow(accountId, goodsId));
    }

    //取消收藏商品
    @GetMapping("/unfollow/{goodsId}")
    public ResultData unfollow(@RequestHeader("token") String token, @PathVariable("goodsId") Integer goodsId) {
        Integer accountId = accountService.tokenToUid(token);
        if (accountId == -1) {
            return ResultData.fail("登陆过期");
        }
        if (!goodsCollectionService.isCollection(accountId, goodsId)) {
            ResultData.fail();
        }
        return ResultData.state(goodsCollectionService.unfollow(accountId, goodsId));
    }

    //获取商品收藏数
    @GetMapping("/get_num/{goodsId}")
    public ResultData getFollowNum(@PathVariable("goodsId") String goodsId) {
        return ResultData.success(goodsCollectionService.goodsCollectionNum(Integer.valueOf(goodsId)));
    }

    //判断用户是否收藏
    @GetMapping("/isCollection/{goodsId}")
    public ResultData userIsCollection(@RequestHeader("token") String token, @PathVariable Integer goodsId) {
        Integer uid = accountService.tokenToUid(token);
        if (uid == -1) {
            return ResultData.fail("登录过期");
        }
        return ResultData.state(goodsCollectionService.isCollection(uid, goodsId));
    }
}

