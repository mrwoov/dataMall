package com.dataMall.goodsCenter.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dataMall.goodsCenter.entity.GoodsPic;
import com.dataMall.goodsCenter.feign.AccountService;
import com.dataMall.goodsCenter.service.GoodsPicService;
import com.dataMall.goodsCenter.service.GoodsService;
import com.dataMall.goodsCenter.vo.ResultData;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 商品图片表 前端控制器
 * </p>
 *
 * @author woov
 * @since 2023-08-29
 */
@RestController
@RequestMapping("/goodsPic")
public class GoodsPicController {
    private final String authPath = "goodsPic";
    @Resource
    private AccountService accountService;
    @Resource
    private GoodsPicService goodsPicService;
    @Resource
    private GoodsService goodsService;

    //用户新增商品图片
    @PatchMapping("/")
    public ResultData save(@RequestHeader("token") String token, @RequestBody GoodsPic goodsPic) {
        Integer uid = accountService.tokenToUid(token);
        if (uid == -1) {
            return ResultData.fail("登录过期");
        }
        boolean state = goodsPicService.save(goodsPic);
        return ResultData.state(state);
    }

    //用户删除商品图片
    @DeleteMapping("/")
    public ResultData del(@RequestHeader("token") String token, @RequestParam("picId") String picId) {
        Integer uid = accountService.tokenToUid(token);
        if (uid == -1) {
            return ResultData.fail("登陆过期");
        }
        GoodsPic goodsPic = goodsPicService.getById(picId);
        Integer goodsId = goodsPic.getGoodsId();
        boolean owner = goodsService.isOwner(uid, goodsId);
        if (!owner) {
            return ResultData.fail();
        }
        boolean state = goodsPicService.removeById(picId);
        return ResultData.state(state);
    }

    //根据商品id返回图片链接
    @GetMapping("/{goodsId}")
    public ResultData getGoodsUrl(@PathVariable("goodsId") Integer goodsId) {
        QueryWrapper<GoodsPic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("goods_id", goodsId);
        List<GoodsPic> list = goodsPicService.list(queryWrapper);
        return ResultData.success(list);
    }
}

