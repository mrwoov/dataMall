package com.dataMall.goodsCenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dataMall.common.common.BaseResponse;
import com.dataMall.common.common.ErrorCode;
import com.dataMall.common.common.ResultUtils;
import com.dataMall.common.entity.*;
import com.dataMall.common.exception.BusinessException;
import com.dataMall.goodsCenter.feign.AccountService;
import com.dataMall.goodsCenter.service.*;
import com.dataMall.goodsCenter.utils.OssUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
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
    @Resource
    private AccountService accountService;
    @Resource
    private GoodsService goodsService;
    @Resource
    private GoodsCategoriesService goodsCategoriesService;
    @Resource
    private GoodsPicService goodsPicService;
    @Resource
    private GoodsFileService goodsFileService;
    @Resource
    private OssUtils ossUtils;
    @Resource
    private GoodsPortalShowService goodsPortalShowService;

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

    @GetMapping("/getPortalIndex")
    public BaseResponse<List<Goods>> getPortalIndex() {
        List<GoodsPortalShow> goodsPortalShowList = goodsPortalShowService.list();
        List<Goods> goodsList = new ArrayList<>();
        for (GoodsPortalShow goodsPortalShow : goodsPortalShowList) {
            Goods goods = goodsService.getById(goodsPortalShow.getGoodsId());
            goods.setFileMd5("");
            goodsService.getGoodsOtherParam(goods);
            goodsList.add(goods);
        }
        return ResultUtils.success(goodsList);
    }

    // 用户上架商品
    @PostMapping("release_on")
    public BaseResponse<Object> releaseOn(@RequestHeader("token") String token, @RequestParam("goodsId") Integer goodsId) {
        Integer uid = accountService.tokenToUid(token);
        if (uid == -1) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        boolean state = goodsService.userUpdateGoodsState(uid, goodsId, 0);
        if (!state) {
            throw new BusinessException(ErrorCode.FAIL);
        }
        return ResultUtils.success();
    }

    // 用户下架商品
    @PostMapping("/release_off")
    public BaseResponse<Object> releaseOff(@RequestHeader("token") String token, @RequestParam("goodsId") Integer goodsId) {
        Integer uid = accountService.tokenToUid(token);
        if (uid == -1) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        boolean state = goodsService.userUpdateGoodsState(uid, goodsId, 1);
        if (!state) {
            throw new BusinessException(ErrorCode.FAIL);
        }
        return ResultUtils.success();
    }

    // 用户发布商品
    @PostMapping("/")
    public BaseResponse<Object> release(@RequestHeader("token") String token, @RequestBody Goods goods) {
        Integer accountId = accountService.tokenToUid(token);
        if (accountId == -1) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        String fileMd5 = goods.getFileMd5();
        GoodsFile goodsFile = goodsFileService.getOneByOption("md5", fileMd5);
        if (goodsFile == null) {
            throw new BusinessException(ErrorCode.FAIL);
        }
        List<String> imagesFilesMd5 = goods.getImagesMd5();
        List<GoodsFile> goodsImages = new ArrayList<>();
        for (String s : imagesFilesMd5) {
            GoodsFile goodsImage = goodsFileService.getOneByOption("md5", s);
            goodsImages.add(goodsImage);
        }
        //保存商品信息
        goods.setUid(accountId);
        goods.moneyToPrice();
        //将商品图片第一个图作为主图
        goods.setPicIndex(goodsImages.get(0).getFilePath());
        //设置商品状态为待审核
        goods.setState(-3);
        //保存至商品表
        boolean goodsStatus = goodsService.save(goods);
        if (!goodsStatus) {
            throw new BusinessException(ErrorCode.FAIL);
        }
        Integer goodsId = goods.getId();
        boolean flag = true;
        //保存至商品图片表
        for (GoodsFile image : goodsImages) {
            GoodsPic goodsPic = new GoodsPic();
            goodsPic.setGoodsId(goodsId);
            goodsPic.setUrl(image.getFilePath());
            boolean goodsPicState = goodsPicService.save(goodsPic);
            flag = flag & goodsPicState;
        }
        if (!flag) {
            throw new BusinessException(ErrorCode.FAIL);
        }
        return ResultUtils.success();
    }

    // 用户删除商品
    @DeleteMapping("/")
    public BaseResponse<Object> del(@RequestHeader("token") String token, @RequestParam("goodsId") Integer goodsId) {
        Integer uid = accountService.tokenToUid(token);
        if (uid == -1) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        boolean owner = goodsService.isOwner(uid, goodsId);
        if (!owner) {
            throw new BusinessException(ErrorCode.FAIL);
        }
        Goods goods = goodsService.getById(goodsId);
        goods.setState(-2);
        boolean state = goodsService.updateById(goods);
        if (!state) {
            throw new BusinessException(ErrorCode.FAIL);
        }
        return ResultUtils.success();
    }

    // 获取单个商品信息
    @GetMapping("/info/{goodsId}")
    public BaseResponse<Goods> getInfo(@PathVariable("goodsId") Integer goodsId) {
        Goods goods = goodsService.getGoodsInfoById(goodsId);
        if (goods.getState() == 0) {
            return ResultUtils.success(goods);
        }
        return ResultUtils.success();
    }

    //批量获取多个商品信息
    @PostMapping("/infos")
    public BaseResponse<List<Goods>> getInfos(@RequestBody List<Integer> goodsIds) {
        List<Goods> goodsList = goodsService.getGoodsListByIds(goodsIds);
        goodsService.getGoodsListOtherParam(goodsList);
        return ResultUtils.success(goodsList);
    }

    // 用户修改商品信息
    @PostMapping("/update")
    public BaseResponse<Object> updateGoods(@RequestHeader("token") String token, @RequestBody Goods goods) {
        Integer uid = accountService.tokenToUid(token);
        if (uid == -1) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        boolean owner = goodsService.isOwner(uid, goods.getId());
        if (!owner) {
            throw new BusinessException(ErrorCode.FAIL);
        }
        boolean state = goodsService.updateById(goods.dealUserUpdateGoods());
        if (!state) {
            throw new BusinessException(ErrorCode.FAIL);
        }
        return ResultUtils.success();
    }

    //搜索商品
    @GetMapping("/search")
    public BaseResponse<List<Goods>> search(@RequestParam("keyword") String keyword) {
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", keyword);
        List<Goods> goodsList = goodsService.getGoodsList(queryWrapper);
        goodsList.removeIf(goods -> goods.getState() != 0);
        return ResultUtils.success(goodsList);
    }

    //获取类别下的商品
    @GetMapping("/categories")
    public BaseResponse<List<Goods>> categoriesGoods(@RequestParam("categories") String categories) {
        GoodsCategories goodsCategories = goodsCategoriesService.getOneByOption("url", categories);
        if (goodsCategories == null) {
            throw new BusinessException(ErrorCode.FAIL);
        }
        Integer categoriesId = goodsCategories.getId();
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("categories_id", categoriesId);
        queryWrapper.eq("state", 0);
        List<Goods> goodsList = goodsService.getGoodsList(queryWrapper);
        return ResultUtils.success(goodsList);
    }

    // 查询单个用户发布的商品列表
    @GetMapping("/list/{uid}")
    public BaseResponse<List<Goods>> getUserGoodsList(@PathVariable("uid") String uid) {
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        List<Goods> list = goodsService.list(queryWrapper);
        goodsService.getGoodsListOtherParam(list);
        return ResultUtils.success(list);
    }

    @GetMapping("/getById/{id}")
    public Goods getById(@PathVariable Integer id) {
        Goods goods = goodsService.getGoodsInfoById(id);
        goods.moneyToPrice();
        return goods;
    }

    @GetMapping("/getGoodsPrice/{id}")
    public Integer getGoodsPrice(@PathVariable Integer id) {
        return goodsService.getById(id).getPrice();
    }

    @GetMapping("/getGoodsListAll")
    public List<Goods> getGoodsListAll() {
        return goodsService.list();
    }

    @PostMapping("/getGoodsListByIds")
    public List<Goods> getGoodsListByIds(@RequestBody List<Integer> ids) {
        List<Goods> goodsList = goodsService.listByIds(ids);
        return goodsList.stream().peek(Goods::priceToMoney)
                .peek(goods -> goodsService.getGoodsOtherParam(goods)).toList();
    }
    
    @GetMapping("/getGoodsWithFiveMinutesAgoUpdate")
    public List<Goods> getGoodsWithFiveMinutesAgoUpdate() {
        return goodsService.getGoodsWithFiveMinutesAgoUpdate();
    }
}