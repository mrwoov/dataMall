package com.dataMall.adminCenter.controller;


import com.dataMall.adminCenter.aop.AdminAuth;
import com.dataMall.adminCenter.entity.GoodsPic;
import com.dataMall.adminCenter.service.AccountService;
import com.dataMall.adminCenter.service.GoodsPicService;
import com.dataMall.adminCenter.service.GoodsService;
import com.dataMall.adminCenter.vo.ResultData;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @AdminAuth(value = authPath)
    public ResultData freeze(@RequestParam("picId") String picId) {
        GoodsPic goodsPic = goodsPicService.getById(picId);
        if (goodsPic == null) {
            return ResultData.fail();
        }
        goodsPic.setStates(-1);
        boolean state = goodsPicService.updateById(goodsPic);
        return ResultData.state(state);
    }
}

