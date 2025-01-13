package com.dataMall.goodsCenter.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dataMall.common.common.BaseResponse;
import com.dataMall.common.common.ErrorCode;
import com.dataMall.common.common.ResultUtils;
import com.dataMall.common.entity.Goods;
import com.dataMall.common.entity.GoodsCollection;
import com.dataMall.common.exception.BusinessException;
import com.dataMall.goodsCenter.feign.AccountService;
import com.dataMall.goodsCenter.service.GoodsCollectionService;
import com.dataMall.goodsCenter.service.GoodsService;
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
    public BaseResponse<List<Goods>> getUserFollowGoods(@RequestHeader("token") String token) {
        Integer accountId = accountService.tokenToUid(token);
        if (accountId == -1) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        QueryWrapper<GoodsCollection> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", accountId);
        List<GoodsCollection> goodsCollectionList = goodsCollectionService.list(queryWrapper);
        List<Goods> goodsList = new ArrayList<>();
        for (GoodsCollection goodsCollection : goodsCollectionList) {
            Goods goods = goodsService.getById(goodsCollection.getGoodsId());
            if (goods.getState() != 0) {
                continue;
            }
            goods.priceToMoney();
            goodsList.add(goods);
        }
        return ResultUtils.success(goodsList);
    }

    //收藏商品
    @GetMapping("/follow/{goodsId}")
    public BaseResponse<Object> follow(@RequestHeader("token") String token, @PathVariable("goodsId") Integer goodsId) {
        Integer accountId = accountService.tokenToUid(token);
        if (accountId == -1) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        if (goodsCollectionService.isCollection(accountId, goodsId)) {
            throw new BusinessException(ErrorCode.FAIL);
        }
        boolean state = goodsCollectionService.follow(accountId, goodsId);
        if (!state) {
            throw new BusinessException(ErrorCode.FAIL);
         }
        return ResultUtils.success();
    }

    //取消收藏商品
    @GetMapping("/unfollow/{goodsId}")
    public BaseResponse<Object> unfollow(@RequestHeader("token") String token, @PathVariable("goodsId") Integer goodsId) {
        Integer accountId = accountService.tokenToUid(token);
        if (accountId == -1) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        if (!goodsCollectionService.isCollection(accountId, goodsId)) {
           throw new BusinessException(ErrorCode.FAIL);
        }
        boolean state = goodsCollectionService.unfollow(accountId, goodsId);
        if (!state) {
            throw new BusinessException(ErrorCode.FAIL);
         }
        return ResultUtils.success();
    }

    //获取商品收藏数
    @GetMapping("/get_num/{goodsId}")
    public BaseResponse<Long> getFollowNum(@PathVariable("goodsId") String goodsId) {
        return ResultUtils.success(goodsCollectionService.goodsCollectionNum(Integer.valueOf(goodsId)));
    }

    //判断用户是否收藏
    @GetMapping("/isCollection/{goodsId}")
    public BaseResponse<Object> userIsCollection(@RequestHeader("token") String token, @PathVariable Integer goodsId) {
        Integer uid = accountService.tokenToUid(token);
        if (uid == -1) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        boolean state = goodsCollectionService.isCollection(uid, goodsId);
        if (!state) {
            throw new BusinessException(ErrorCode.FAIL);
         }
        return ResultUtils.success();
    }
}

