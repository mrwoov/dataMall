package com.dataMall.adminCenter.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataMall.adminCenter.aop.AdminAuth;
import com.dataMall.adminCenter.entity.SystemDict;
import com.dataMall.adminCenter.service.SystemDictService;
import com.dataMall.adminCenter.vo.ResultData;
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
    public ResultData iconPage(@RequestParam Integer pageNum,
                               @RequestParam Integer pageSize, @RequestBody SystemDict systemDict) {
        if (pageNum == null || pageSize == null) {
            return ResultData.fail("参数缺少");
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
        return ResultData.success(page);
    }

    //新增或修改
    @PatchMapping("/admin")
    @AdminAuth(value = authPath)
    public ResultData saveOrUpdate(@RequestBody SystemDict systemDict) {
        return ResultData.state(systemDictService.saveOrUpdate(systemDict));
    }

    //删除by id
    @DeleteMapping("/admin/{id}")
    @AdminAuth(value = authPath)
    public ResultData delete(@PathVariable Integer id) {
        return ResultData.state(systemDictService.removeById(id));
    }

    //批量删除
    @PostMapping("/admin/del_batch")
    @AdminAuth(value = authPath)
    public ResultData deleteBatch(@RequestBody List<Integer> ids) {
        return ResultData.state(systemDictService.removeByIds(ids));
    }

    //管理员查找全部
    @GetMapping("/")
    @AdminAuth(value = authPath)
    public ResultData findAll() {
        return ResultData.success(systemDictService.list());
    }

    //分页查询
    @GetMapping("/admin/page")
    @AdminAuth(value = authPath)
    public Page<SystemDict> findPage(@RequestParam Integer pageNum,
                                     @RequestParam Integer pageSize) {
        return systemDictService.page(new Page<>(pageNum, pageSize));
    }
}

