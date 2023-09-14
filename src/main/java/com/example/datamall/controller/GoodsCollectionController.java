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
@RequestMapping("/goodsCollection")
public class GoodsCollectionController {
    @Resource
    private GoodsCollectionService goodsCollectionService;
    @Resource
    private AccountService accountService;

    //收藏商品
    @GetMapping("/follow")
    public ResultData follow(@RequestHeader("token") String token, @RequestParam("goodsId") Integer goodsId) {
        Integer accountId = accountService.tokenToUid(token);
        if (accountId == -1) {
            return ResultData.fail("登陆过期");
        }
        return ResultData.state(goodsCollectionService.follow(accountId, goodsId));
    }

    //取消收藏商品
    @GetMapping("/unfollow")
    public ResultData unfollow(@RequestHeader("token") String token, @RequestParam("goodsId") Integer goodsId) {
        Integer accountId = accountService.tokenToUid(token);
        if (accountId == -1) {
            return ResultData.fail("登陆过期");
        }
        return ResultData.state(goodsCollectionService.unfollow(accountId, goodsId));
    }

    //获取商品收藏数
    @GetMapping("/getNum")
    public ResultData getFollowNum(@RequestParam("goodsId") String goodsId) {
        return ResultData.success(goodsCollectionService.goodsCollectionNum(Integer.valueOf(goodsId)));
    }
}

