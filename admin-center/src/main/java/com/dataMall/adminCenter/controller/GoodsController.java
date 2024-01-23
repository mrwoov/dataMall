package com.dataMall.adminCenter.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dataMall.adminCenter.aop.AdminAuth;
import com.dataMall.adminCenter.entity.Goods;
import com.dataMall.adminCenter.service.AccountService;
import com.dataMall.adminCenter.service.GoodsFreezeService;
import com.dataMall.adminCenter.service.GoodsPortalShowService;
import com.dataMall.adminCenter.service.GoodsService;
import com.dataMall.adminCenter.utils.OssUtils;
import com.dataMall.adminCenter.vo.ResultData;
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
    private AccountService accountService;
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
    public ResultData changeGoodsPortalShow(@RequestBody Goods goods) {
        boolean state = goodsPortalShowService.changeGoodsPortalShow(goods.getId(), goods.isShowPortal());
        return ResultData.state(state);
    }

    // 管理员冻结与解冻商品
    @PostMapping("/admin/freeze")
    @AdminAuth(value = authPath)
    public ResultData freeze(@RequestHeader("token") String token, @RequestBody Goods goods) {
        Integer accountId = accountService.tokenToUid(token);
        if (accountId == -1) {
            return ResultData.fail("登录过期");
        }
        if ("freeze".equals(goods.getOption())) {
            //冻结
            boolean state = goodsFreezeService.option(goods.getId(), accountId, true, goods.getMessage());
            if (!state) {
                return ResultData.fail();
            }
            return ResultData.state(goodsService.adminUpdateGoodsState(goods.getId(), -1));
        }
        if ("unfreeze".equals(goods.getOption())) {
            //解冻
            goodsFreezeService.option(goods.getId(), accountId, false, goods.getMessage());
            return ResultData.state(goodsService.adminUpdateGoodsState(goods.getId(), 0));
        }
        return ResultData.fail();
    }

    // 管理员分页查询商品列表
    @PostMapping("/admin/page")
    @AdminAuth(value = authPath)
    public ResultData page(@RequestParam("pageSize") Integer pageSize, @RequestParam("pageNum") Integer pageNum, @RequestBody Goods goods) {
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
    @AdminAuth(value = authPath)
    public ResultData audit(@RequestParam("goodsId") Integer goodsId, @RequestParam("state") boolean state) {
        int status = state?0:-4;
        state = goodsService.adminUpdateGoodsState(goodsId, status);
        return ResultData.state(state);
    }

    //管理员获取一个待审核的商品信息
    @GetMapping("/get_not_audit")
    @AdminAuth(value = authPath)
    public ResultData getNotAuditGoodsInfo() {
        Goods goods = goodsService.getNotAuditGoods();
        return ResultData.success(goods);
    }
}