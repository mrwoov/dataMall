package com.dataMall.adminCenter.controller;


import com.dataMall.adminCenter.service.AccountService;
import com.dataMall.adminCenter.service.GoodsFileService;
import com.dataMall.adminCenter.utils.OssUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 商品数据文件 前端控制器
 * </p>
 *
 * @author woov
 * @since 2023-10-26
 */
@RestController
@RequestMapping("/goodsFiles")
public class GoodsFileController {
    @Resource
    private GoodsFileService goodsFileService;
    @Resource
    private AccountService accountService;
    @Resource
    private OssUtils ossUtils;

    public GoodsFileController(OssUtils ossUtils) {
        this.ossUtils = ossUtils;
    }

    //todo:del
}

