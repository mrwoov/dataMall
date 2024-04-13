package com.dataMall.goodsCenter.controller;


import cn.hutool.core.date.DateTime;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataMall.goodsCenter.entity.XlsxApi;
import com.dataMall.goodsCenter.service.XlsxApiService;
import com.dataMall.goodsCenter.utils.ExcelToJsonConverter;
import com.dataMall.goodsCenter.vo.ResultData;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author woov
 * @since 2024-01-24
 */
@RestController
@RequestMapping("/xlsxApi")
public class XlsxApiController {
    @Resource
    private XlsxApiService xlsxApiService;

    @Autowired
    private MongoTemplate mongoTemplate;

    //上传execl
    @PostMapping("/upload")
    public ResultData upload(@RequestPart MultipartFile file) {
        //判断文件是否是xls或者xlsx
        if (!file.getOriginalFilename().endsWith(".xls") && !file.getOriginalFilename().endsWith(".xlsx")) {
            return ResultData.fail("文件格式错误");
        }
        //将execl数据json化
        List<String> jsonList = ExcelToJsonConverter.convertExcelToJson(file);
        //生成api_id,当前时间加随机数加文件名的md5
        Integer random = (int) (Math.random() * 100000);
        String temp = DateTime.now().toString() + random + Objects.requireNonNull(file.getOriginalFilename()).hashCode();
        //将temp转为md5
        String apiId = "app"+SecureUtil.md5(temp);
        //将json存入mongodb
        mongoTemplate.createCollection(apiId);
        for (String json : jsonList) {
            mongoTemplate.insert(json, apiId);
        }
        return ResultData.success(apiId);
    }


    //新增或修改
    @PatchMapping("/")
    public ResultData saveOrUpdate(@RequestBody XlsxApi xlsxApi) {
        return ResultData.state(xlsxApiService.saveOrUpdate(xlsxApi));
    }

    //删除by id
    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Integer id) {
        return xlsxApiService.removeById(id);
    }

    //批量删除
    @PostMapping("/del_batch")
    public Boolean deleteBatch(@RequestBody List<Integer> ids) {
        return xlsxApiService.removeByIds(ids);
    }

    //查找全部
    @GetMapping
    public List<XlsxApi> findAll() {
        return xlsxApiService.list();
    }

    //查找单个
    @GetMapping("/{id}")
    public List<XlsxApi> findOne(@PathVariable Integer id) {
        return xlsxApiService.list();
    }

    //分页查询
    @GetMapping("/page")
    public Page<XlsxApi> findPage(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        return xlsxApiService.page(new Page<>(pageNum, pageSize));
    }
}

