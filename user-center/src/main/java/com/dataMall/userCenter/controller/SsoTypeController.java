package com.dataMall.userCenter.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataMall.common.common.BaseResponse;
import com.dataMall.common.common.ErrorCode;
import com.dataMall.common.common.ResultUtils;
import com.dataMall.common.entity.SsoType;
import com.dataMall.common.exception.BusinessException;
import com.dataMall.userCenter.service.SsoTypeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author woov
 * @since 2024-03-02
 */
@RestController
@RequestMapping("/ssoType")
public class SsoTypeController {
    @Resource
    private SsoTypeService ssoTypeService;

    //新增或修改
    @PatchMapping("/")
    public BaseResponse<Object> saveOrUpdate(@RequestBody SsoType ssoType) {
        boolean state = ssoTypeService.saveOrUpdate(ssoType);
        if (!state) {
            throw new BusinessException(ErrorCode.FAIL);
         }
        return ResultUtils.success();
    }

    //删除by id
    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Integer id) {
        //id为1和2的不可删除
        if (id == 1 || id == 2) {
            ResultUtils.error(ErrorCode.FAIL);
        }
        return ssoTypeService.removeById(id);
    }

    //批量删除
    @PostMapping("/del_batch")
    public Boolean deleteBatch(@RequestBody List<Integer> ids) {
        //id为1和2的不可删除
        if (ids.contains(1) || ids.contains(2)) {
            ResultUtils.error(ErrorCode.FAIL);
        }
        return ssoTypeService.removeByIds(ids);
    }

    //查找全部
    @GetMapping
    public List<SsoType> findAll() {
        return ssoTypeService.list();
    }

    //查找单个
    @GetMapping("/{id}")
    public List<SsoType> findOne(@PathVariable Integer id) {
        return ssoTypeService.list();
    }

    //分页查询
    @GetMapping("/page")
    public Page<SsoType> findPage(@RequestParam Integer pageNum,
                                  @RequestParam Integer pageSize) {
        return ssoTypeService.page(new Page<>(pageNum, pageSize));
    }
}

