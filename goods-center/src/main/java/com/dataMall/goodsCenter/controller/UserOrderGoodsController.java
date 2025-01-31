package com.dataMall.goodsCenter.controller;


import com.dataMall.common.common.BaseResponse;
import com.dataMall.common.common.ErrorCode;
import com.dataMall.common.common.ResultUtils;
import com.dataMall.common.entity.Goods;
import com.dataMall.common.entity.GoodsSnapshot;
import com.dataMall.common.entity.User;
import com.dataMall.common.entity.UserOrder;
import com.dataMall.common.exception.BusinessException;
import com.dataMall.goodsCenter.feign.UserOrderService;
import com.dataMall.goodsCenter.feign.UserService;
import com.dataMall.goodsCenter.service.GoodsSnapshotService;
import com.dataMall.goodsCenter.service.UserOrderGoodsService;
import com.dataMall.goodsCenter.utils.MailService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 订单商品表 前端控制器
 * </p>
 *
 * @author woov
 * @since 2023-11-16
 */
@RestController
@RequestMapping("/userOrderGoods")
public class UserOrderGoodsController {
    @Resource
    private UserOrderGoodsService userOrderGoodsService;
    @Resource
    private UserService userService;
    @Resource
    private UserOrderService userOrderService;
    @Autowired
    private MailService mailService;
    @Resource
    private GoodsSnapshotService goodsSnapshotService;

    //用户下载订单商品的资源
    @GetMapping("/download/{tradeNo}")
    public BaseResponse<List<String>> downloadGoodsSource(@PathVariable String tradeNo, @RequestHeader("token") String token) {
        Integer accountId = userService.tokenToUid(token);
        if (accountId == -1) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        UserOrder userOrder = userOrderService.getOrderFromFeign(tradeNo);
        if (userOrder == null) {
            throw new BusinessException(ErrorCode.FAIL);
        }
        if (!userOrder.getAccountId().equals(accountId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        List<String> md5List = userOrderGoodsService.downloadByMd5List(userOrder.getId());
        return ResultUtils.success(md5List);
    }

    //发送下载链接
    @GetMapping("/sendDownload/{tradeNo}")
    public BaseResponse<Object> sendDownload(@PathVariable String tradeNo, @RequestHeader("token") String token) {
        Integer accountId = userService.tokenToUid(token);
        if (accountId == -1) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        UserOrder userOrder = userOrderService.getOrderFromFeign(tradeNo);
        if (userOrder == null) {
            throw new BusinessException(ErrorCode.FAIL);
        }
        if (!userOrder.getAccountId().equals(accountId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        List<String> md5List = userOrderGoodsService.downloadByMd5List(userOrder.getId());
        if (md5List.isEmpty()) {
            throw new BusinessException(ErrorCode.FAIL, "没有资源");
        }
        User user = userService.getById(accountId);
        if (user == null) {
            throw new BusinessException(ErrorCode.FAIL, "用户不存在");
        }
        StringBuilder html = new StringBuilder();
        int i = 1;
        for (String md5 : md5List) {
            html.append("<p>资源").append(i).append("：").append(md5).append("</p> <a href='http://localhost:8080/order/download/").append(tradeNo).append("'>点击下载</a>");
            i++;
        }
        mailService.sendTextMailMessage(user.getEmail(), "资源下载链接", "资源下载链接：" + html);
        return ResultUtils.success();
    }

    //Feign，快照是否有商品
    @GetMapping("/snapshotHaveGoods")
    public Boolean snapshotHaveGoods(@RequestParam Integer snapshotId, @RequestParam Integer goodsId) {
        return goodsSnapshotService.snapshotHaveGoods(snapshotId, goodsId);
    }

    //Feign,保存商品快照
    @GetMapping("/saveGoodsSnapshot")
    public Integer saveGoodsSnapshot(@RequestParam Integer goodsId) {
        return goodsSnapshotService.saveGoodsSnapshot(goodsId);
    }

    //Feign，根据订单id获取商品快照
    @GetMapping("/isExistSnapshot")
    public Integer isExistSnapshot(Goods goods) {
        return goodsSnapshotService.isExistSnapshot(goods);
    }

    @GetMapping("/getOrderGoodsSnapshot")
    public List<GoodsSnapshot> getOrderGoodsSnapshot(@RequestParam Integer orderId) {
        return userOrderGoodsService.getOrderGoodsSnapshot(orderId);
    }

    @GetMapping("/deleteGoodsSnapshot")
    public Boolean deleteGoodsSnapshot(@RequestParam Integer goodsId) {
        return userOrderGoodsService.deleteGoodsSnapshot(goodsId);
    }

    //Feign，保存订单商品
    @GetMapping("/saveOrderGoods")
    public Boolean saveOrderGoods(@RequestParam Integer goodsId, @RequestParam Integer orderId) {
        return userOrderGoodsService.saveOrderGoods(goodsId, orderId);
    }
}

