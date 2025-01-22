package com.dataMall.adminCenter.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataMall.adminCenter.aop.AdminAuth;
import com.dataMall.adminCenter.service.SystemDictService;
import com.dataMall.common.common.BaseResponse;
import com.dataMall.common.common.ErrorCode;
import com.dataMall.common.common.ResultUtils;
import com.dataMall.common.entity.SystemDict;
import com.dataMall.common.exception.BusinessException;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author woov
 * @since 2023-10-07
 */
@RestController
@RequestMapping("/systemDict")
public class SystemDictController {
    private final String authPath = "/system";
    @Resource
    private SystemDictService systemDictService;

    //分页查图标
    @PostMapping("/admin/icon_page")
    @AdminAuth(value = authPath)
    public BaseResponse<IPage<SystemDict>> iconPage(@RequestParam Integer pageNum,
                                                    @RequestParam Integer pageSize, @RequestBody SystemDict systemDict) {
        if (pageNum == null || pageSize == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<SystemDict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", "icon");
        if (systemDict.getName() != null) {
            queryWrapper.like("name", systemDict.getName());
        }
        if (systemDict.getValue() != null) {
            queryWrapper.like("value", systemDict.getValue());
        }
        IPage<SystemDict> page = systemDictService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return ResultUtils.success(page);
    }

    //新增或修改
    @PatchMapping("/admin")
    @AdminAuth(value = authPath)
    public BaseResponse<Object> saveOrUpdate(@RequestBody SystemDict systemDict) {
        boolean state = systemDictService.saveOrUpdate(systemDict);
        if (!state) {
            throw new BusinessException(ErrorCode.FAIL);
        }
        return ResultUtils.success();
    }

    //删除by id
    @DeleteMapping("/admin/{id}")
    @AdminAuth(value = authPath)
    public BaseResponse<Object> delete(@PathVariable Integer id) {
        boolean state = systemDictService.removeById(id);
        if (!state) {
            throw new BusinessException(ErrorCode.FAIL);
         }
        return ResultUtils.success();
    }

    //批量删除
    @PostMapping("/admin/del_batch")
    @AdminAuth(value = authPath)
    public BaseResponse<Object> deleteBatch(@RequestBody List<Integer> ids) {
        boolean state = systemDictService.removeByIds(ids);
        if (!state) {
            throw new BusinessException(ErrorCode.FAIL);
        }
        return ResultUtils.success();
    }

    //管理员查找全部
    @GetMapping("/")
    @AdminAuth(value = authPath)
    public BaseResponse<List<SystemDict>> findAll() {
        return ResultUtils.success(systemDictService.list());
    }

    //分页查询
    @GetMapping("/admin/page")
    @AdminAuth(value = authPath)
    public Page<SystemDict> findPage(@RequestParam Integer pageNum,
                                     @RequestParam Integer pageSize) {
        return systemDictService.page(new Page<>(pageNum, pageSize));
    }
}

