package com.dataMall.adminCenter.controller;


import com.dataMall.adminCenter.entity.GoodsPic;
import com.dataMall.adminCenter.service.AccountService;
import com.dataMall.adminCenter.service.GoodsPicService;
import com.dataMall.adminCenter.service.GoodsService;
import com.dataMall.adminCenter.vo.ResultData;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 商品图片表 前端控制器
 * </p>
 *
 * @author woov
 * @since 2023-08-29
 */
@SuppressWarnings("ALL")
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

   

    //管理员冻结商品图片
    @GetMapping("/admin")
    public ResultData freeze(@RequestHeader("token") String token, @RequestParam("picId") String picId) {
        boolean isAdmin = accountService.checkAdminHavaAuth(authPath, token);
        if (!isAdmin) {
            return ResultData.fail("无权限");
        }
        GoodsPic goodsPic = goodsPicService.getById(picId);
        if (goodsPic == null) {
            return ResultData.fail();
        }
        goodsPic.setStates(-1);
        boolean state = goodsPicService.updateById(goodsPic);
        return ResultData.state(state);
    }
}

