package com.dataMall.adminCenter.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dataMall.adminCenter.aop.AdminAuth;
import com.dataMall.adminCenter.service.GoodsFreezeService;
import com.dataMall.adminCenter.service.GoodsPortalShowService;
import com.dataMall.adminCenter.service.GoodsService;
import com.dataMall.adminCenter.service.UserService;
import com.dataMall.adminCenter.utils.OssUtils;
import com.dataMall.common.common.BaseResponse;
import com.dataMall.common.common.ErrorCode;
import com.dataMall.common.common.ResultUtils;
import com.dataMall.common.entity.Goods;
import com.dataMall.common.exception.BusinessException;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

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
    private UserService userService;
    @Resource
    private GoodsService goodsService;
    @Resource
    private GoodsFreezeService goodsFreezeService;
    @Resource
    private GoodsPortalShowService goodsPortalShowService;
    @Resource
    private OssUtils ossUtils;

    public GoodsController(OssUtils ossUtils) {
        this.ossUtils = ossUtils;
    }

    @PostMapping("/changeGoodsPortalShow")
    @AdminAuth(value = authPath)
    //切换商品是否首页轮播图展示
    public BaseResponse<Object> changeGoodsPortalShow(@RequestBody Goods goods) {
        boolean state = goodsPortalShowService.changeGoodsPortalShow(goods.getId(), goods.isShowPortal());
        if (!state) {
            throw new BusinessException(ErrorCode.FAIL);
         }
        return ResultUtils.success();
    }

    // 管理员冻结与解冻商品
    @PostMapping("/admin/freeze")
    @AdminAuth(value = authPath)
    public BaseResponse<Object> freeze(@RequestHeader("token") String token, @RequestBody Goods goods) {
        Integer accountId = userService.tokenToUid(token);
        if (accountId == -1) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        if ("freeze".equals(goods.getOption())) {
            //冻结
            boolean state = goodsFreezeService.option(goods.getId(), accountId, true, goods.getMessage());
            if (!state) {
                throw new BusinessException(ErrorCode.FAIL);
            }
            state = goodsService.adminUpdateGoodsState(goods.getId(), -1);
            if (!state) {
                throw new BusinessException(ErrorCode.FAIL);
            }
            return ResultUtils.success();
        }
        if ("unfreeze".equals(goods.getOption())) {
            //解冻
            goodsFreezeService.option(goods.getId(), accountId, false, goods.getMessage());
            boolean state = goodsService.adminUpdateGoodsState(goods.getId(), 0);
            if (!state) {
                throw new BusinessException(ErrorCode.FAIL);
            }
            return ResultUtils.success();
        }
       throw new BusinessException(ErrorCode.FAIL);
    }

    // 管理员分页查询商品列表
    @PostMapping("/admin/page")
    @AdminAuth(value = authPath)
    public BaseResponse<IPage<Goods>> page(@RequestParam("pageSize") Integer pageSize, @RequestParam("pageNum") Integer pageNum, @RequestBody Goods goods) {
        String name = goods.getName();
        String categoriesName = goods.getCategoriesName();
        String username = goods.getUsername();
        if (pageNum == null || pageSize == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        IPage<Goods> page = goodsService.getGoodsPage(name, categoriesName, username, pageNum, pageSize);
        return ResultUtils.success(page);
    }

    // 获取单个商品信息
    @GetMapping("/info/{goodsId}")
    public BaseResponse<Goods> getInfo(@PathVariable("goodsId") Integer goodsId) {
        Goods goods = goodsService.getGoodsInfoById(goodsId);
        return ResultUtils.success(goods);
    }

    //管理员审核商品
    @GetMapping("/audit")
    @AdminAuth(value = authPath)
    public BaseResponse<Object> audit(@RequestParam("goodsId") Integer goodsId, @RequestParam("state") boolean state) {
        int status = state?0:-4;
        state = goodsService.adminUpdateGoodsState(goodsId, status);
        if (!state) {
            throw new BusinessException(ErrorCode.FAIL);
         }
        return ResultUtils.success();
    }

    //管理员获取一个待审核的商品信息
    @GetMapping("/get_not_audit")
    @AdminAuth(value = authPath)
    public BaseResponse<Goods> getNotAuditGoodsInfo() {
        Goods goods = goodsService.getNotAuditGoods();
        return ResultUtils.success(goods);
    }
}