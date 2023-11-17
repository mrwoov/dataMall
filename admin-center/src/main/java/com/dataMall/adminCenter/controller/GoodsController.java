package com.dataMall.adminCenter.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dataMall.adminCenter.entity.Goods;
import com.dataMall.adminCenter.service.AccountService;
import com.dataMall.adminCenter.service.GoodsFreezeService;
import com.dataMall.adminCenter.service.GoodsService;
import com.dataMall.adminCenter.utils.OssUtils;
import com.dataMall.adminCenter.vo.ResultData;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

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
    private final String authPath = "goods";
    @Resource
    private AccountService accountService;
    @Resource
    private GoodsService goodsService;
    @Resource
    private GoodsFreezeService goodsFreezeService;
    @Resource
    private OssUtils ossUtils;

    public GoodsController(OssUtils ossUtils) {
        this.ossUtils = ossUtils;
    }


    public static String generateFileName(String originalFileName) {
        long timestamp = System.currentTimeMillis();
        Random random = new Random();
        int randomNumber = random.nextInt(900000) + 10000;
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String processedFileName = timestamp + "_" + originalFileName + "_" + randomNumber;

        // 对处理后的文件名进行加密，使用MD5
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(processedFileName.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            processedFileName = sb.toString();
        } catch (NoSuchAlgorithmException ignored) {
        }
        return processedFileName + extension;
    }

    // 管理员冻结与解冻商品
    @PostMapping("/admin/freeze")
    public ResultData freeze(@RequestHeader("token") String token, @RequestBody Goods goods) {
        boolean isAdmin = accountService.checkAdminHavaAuth(authPath, token);
        if (!isAdmin) {
            return ResultData.fail("无权限");
        }
        Integer accountId = accountService.tokenToUid(token);
        if (accountId == -1) {
            return ResultData.fail("登录过期");
        }
        if (goods.getOption().equals("freeze")) {
            //冻结
            goodsFreezeService.option(goods.getId(), accountId, true, goods.getMessage());
            return ResultData.state(goodsService.adminUpdateGoodsState(goods.getId(), -1));
        } else if (goods.getOption().equals("unfreeze")) {
            //解冻
            goodsFreezeService.option(goods.getId(), accountId, false, goods.getMessage());
            return ResultData.state(goodsService.adminUpdateGoodsState(goods.getId(), 0));
        }
        return ResultData.fail();
    }

    // 管理员分页查询商品列表
    @PostMapping("/admin/page")
    public ResultData page(@RequestHeader("token") String token, @RequestParam("pageSize") Integer pageSize, @RequestParam("pageNum") Integer pageNum, @RequestBody Goods goods) {
        boolean isAdmin = accountService.checkAdminHavaAuth(authPath, token);
        if (!isAdmin) {
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

    // 获取单个商品信息
    @GetMapping("/info/{goodsId}")
    public ResultData getInfo(@PathVariable("goodsId") Integer goodsId) {
        Goods goods = goodsService.getGoodsInfoById(goodsId);
        return ResultData.success(goods);
    }


    //管理员审核商品
    @GetMapping("/audit")
    public ResultData audit(@RequestHeader("token") String token, @RequestParam("goodsId") Integer goodsId, @RequestParam("state") boolean state) {
        //审核通过转为0，失败转为-4
        boolean isAdmin = accountService.checkAdminHavaAuth(authPath, token);
        if (!isAdmin) {
            return ResultData.fail("无权限");
        }
        if (state) {
            goodsService.adminUpdateGoodsState(goodsId, 0);
        } else {
            goodsService.adminUpdateGoodsState(goodsId, -4);
        }
        return ResultData.success();
    }

    //管理员获取一个待审核的商品信息
    @GetMapping("/get_not_audit")
    public ResultData getNotAuditGoodsInfo(@RequestHeader("token") String token) {
        boolean isAdmin = accountService.checkAdminHavaAuth(authPath, token);
        if (!isAdmin) {
            return ResultData.fail("无权限");
        }
        Goods goods = goodsService.getNotAuditGoods();
        return ResultData.success(goods);
    }
}