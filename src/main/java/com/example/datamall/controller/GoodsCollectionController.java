package com.example.datamall.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.datamall.entity.GoodsCollection;
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
        Integer uid = accountService.tokenToUid(token);
        if (uid == -1) {
            return ResultData.fail("登陆过期");
        }
        GoodsCollection goodsCollection = new GoodsCollection();
        goodsCollection.setGoodsId(goodsId);
        goodsCollection.setUid(uid);
        boolean state = goodsCollectionService.save(goodsCollection);
        return ResultData.state(state);
    }

    //取消收藏商品
    @GetMapping("/unfollow")
    public ResultData unfollow(@RequestHeader("token") String token, @RequestParam("goodsId") Integer goodsId) {
        Integer uid = accountService.tokenToUid(token);
        if (uid == -1) {
            return ResultData.fail("登陆过期");
        }
        QueryWrapper<GoodsCollection> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        queryWrapper.eq("goods_id", goodsId);
        boolean state = goodsCollectionService.remove(queryWrapper);
        return ResultData.state(state);
    }

    //获取商品收藏数
    @GetMapping("/getNum")
    public ResultData getFollowNum(@RequestParam("goodsId") String goodsId) {
        QueryWrapper<GoodsCollection> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("goods_id", goodsId);
        long count = goodsCollectionService.count(queryWrapper);
        return ResultData.success(count);
    }
}

