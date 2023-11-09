package com.example.datamall.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.datamall.entity.Goods;
import com.example.datamall.entity.GoodsCategories;
import com.example.datamall.entity.GoodsFile;
import com.example.datamall.entity.GoodsPic;
import com.example.datamall.service.*;
import com.example.datamall.utils.OssUtils;
import com.example.datamall.vo.ResultData;
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
    private final String authPath = "goods";
    @Resource
    private AccountService accountService;
    @Resource
    private GoodsService goodsService;
    @Resource
    private GoodsCategoriesService goodsCategoriesService;
    @Resource
    private GoodsCollectionService goodsCollectionService;
    @Resource
    private GoodsPicService goodsPicService;
    @Resource
    private GoodsFileService goodsFileService;
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

    //管理员审核商品
//    public ResultData audit(@RequestHeader("token") String token, @RequestParam("goodsId") Integer goodsId, @RequestParam("status") boolean status) {
//        //审核通过转为0，失败转为-4
//    }

    // 管理员冻结商品
    @PostMapping("/admin/freeze")
    public ResultData freeze(@RequestHeader("token") String token, @RequestParam("goodsId") Integer goodsId) {
        boolean isAdmin = accountService.checkAdminHavaAuth(authPath, token);
        if (!isAdmin) {
            return ResultData.fail("无权限");
        }
        return ResultData.state(goodsService.adminUpdateGoodsState(goodsId, -1));
    }

    // 管理员解冻商品
    @PostMapping("/admin/unfreeze")
    public ResultData unfreeze(@RequestHeader("token") String token, @RequestParam("goodsId") Integer goodsId) {
        boolean isAdmin = accountService.checkAdminHavaAuth(authPath, token);
        if (!isAdmin) {
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
        goodsList.removeIf(goods -> goods.getState() != 0);
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
        queryWrapper.eq("state", 0);
        List<Goods> goodsList = goodsService.getGoodsList(queryWrapper);
        return ResultData.success(goodsList);
    }

    // 用户发布商品
    @PostMapping("/")
    public ResultData release(@RequestHeader("token") String token, @RequestBody Goods goods) {
        Integer accountId = accountService.tokenToUid(token);
        if (accountId == -1) {
            return ResultData.fail("登录过期");
        }
        //todo: 将上传接口分离后，发布商品接口将上传文件和图片文件的md5值
        String fileMd5 = goods.getFileMd5();
        GoodsFile goodsFile = goodsFileService.getOneByOption("md5", fileMd5);
        if (goodsFile == null) {
            return ResultData.fail();
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
            return ResultData.fail();
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
        return ResultData.state(flag);
    }
}

