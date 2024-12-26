package com.dataMall.orderCenter.controller;


import com.dataMall.orderCenter.common.BaseResponse;
import com.dataMall.orderCenter.common.ErrorCode;
import com.dataMall.orderCenter.common.ResultUtils;
import com.dataMall.orderCenter.entity.GoodsSnapshot;
import com.dataMall.orderCenter.exception.BusinessException;
import com.dataMall.orderCenter.service.GoodsSnapshotService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 商品快照表 前端控制器
 * </p>
 *
 * @author woov
 * @since 2023-11-16
 */
@RestController
@RequestMapping("/goodsSnapshot")
public class GoodsSnapshotController {
    @Resource
    private GoodsSnapshotService goodsSnapshotService;

    //新增或修改
    @PatchMapping("/")
    public BaseResponse<Object> saveOrUpdate(@RequestBody GoodsSnapshot goodsSnapshot) {
        boolean state = goodsSnapshotService.saveOrUpdate(goodsSnapshot);
        if (!state) {
            throw new BusinessException(ErrorCode.FAIL);
         }
        return ResultUtils.success();
    }

}

