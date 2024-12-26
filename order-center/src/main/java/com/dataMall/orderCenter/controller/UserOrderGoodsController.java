package com.dataMall.orderCenter.controller;


import com.dataMall.orderCenter.common.BaseResponse;
import com.dataMall.orderCenter.common.ErrorCode;
import com.dataMall.orderCenter.common.ResultUtils;
import com.dataMall.orderCenter.entity.UserOrderGoods;
import com.dataMall.orderCenter.exception.BusinessException;
import com.dataMall.orderCenter.service.UserOrderGoodsService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    //新增或修改
    @PatchMapping("/")
    public BaseResponse<Object> saveOrUpdate(@RequestBody UserOrderGoods userOrderGoods) {
        boolean state = userOrderGoodsService.saveOrUpdate(userOrderGoods);
        if (!state) {
            throw new BusinessException(ErrorCode.FAIL);
         }
        return ResultUtils.success();
    }
}

