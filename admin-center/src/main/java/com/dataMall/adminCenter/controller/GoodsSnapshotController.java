package com.dataMall.adminCenter.controller;


import com.dataMall.adminCenter.service.GoodsSnapshotService;
import jakarta.annotation.Resource;
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

}

