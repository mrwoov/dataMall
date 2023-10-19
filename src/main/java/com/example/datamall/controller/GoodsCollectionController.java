package com.example.datamall.controller;


import com.example.datamall.service.AccountService;
import com.example.datamall.service.GoodsCollectionService;
import com.example.datamall.vo.ResultData;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

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

