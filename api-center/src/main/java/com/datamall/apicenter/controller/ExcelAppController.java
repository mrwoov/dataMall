package com.datamall.apicenter.controller;


import cn.hutool.core.date.DateTime;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.datamall.apicenter.entity.ExcelApp;
import com.datamall.apicenter.entity.ExcelHeader;
import com.datamall.apicenter.service.ExcelAppService;
import com.datamall.apicenter.service.ExcelColDropdownService;
import com.datamall.apicenter.service.ExcelHeaderService;
import com.datamall.apicenter.utils.ExcelExportUtils;
import com.datamall.apicenter.utils.ExcelToJsonConverter;
import com.datamall.apicenter.vo.ExcelQueryConditionVo;
import com.datamall.apicenter.vo.ResultData;

import java.io.*;
import java.util.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author woov
 * @since 2024-04-02
 */
@RestController
@RequestMapping("/excelApp")
public class ExcelAppController {
    @Resource
    private ExcelAppService excelAppService;
    @Resource
    private ExcelColDropdownService excelColDropdownService;
    @Resource
    private ExcelHeaderService excelHeaderService;
    @Autowired
    private MongoTemplate mongoTemplate;

    @PostMapping("/upload")
    public ResultData upload(@RequestPart MultipartFile file) {
        //判断文件是否是xls或者xlsx
        if (!file.getOriginalFilename().endsWith(".xls") && !file.getOriginalFilename().endsWith(".xlsx")) {
            return ResultData.fail("文件格式错误");
        }
        //将execl数据json化
        List<String> jsonList = ExcelToJsonConverter.convertExcelToJson(file);
        //生成api_id,当前时间加随机数加文件名的md5
        int random = (int) (Math.random() * 100000);
        String temp = DateTime.now().toString() + random + Objects.requireNonNull(file.getOriginalFilename()).hashCode();
        //将temp转为md5
        String appId = "app" + SecureUtil.md5(temp);
        //将json存入mongodb
        mongoTemplate.createCollection(appId);
        for (String json : jsonList) {
            mongoTemplate.insert(json, appId);
        }
        //将excel存入数据库
        ExcelApp excelApp = new ExcelApp();
        //将state设为-2，标识未初始化
        excelApp.setAppId(appId).setName(file.getOriginalFilename()).setStatus(-1);
        boolean state = excelAppService.save(excelApp);
        if (!state) {
            return ResultData.fail("excel存入数据库失败");
        }
        int app = excelApp.getId();
        //拿到excel表头
        List<String> headerList = ExcelToJsonConverter.getHeaderList(file);
        //将表头存入数据库
        boolean flag = false;
        for (String header : headerList) {
            ExcelHeader excelHeader = new ExcelHeader();
            excelHeader.setExcelApp(app).setHeaderName(header);
            state = excelHeaderService.save(excelHeader);
            if (!state) {
                flag = true;
                break;
            }
        }
        if (flag) {
            return ResultData.fail("表头存入数据库失败");
        }
        return ResultData.success(appId);
    }

    //获取excel表头
    @GetMapping("/getHeader/{appId}")
    public ResultData getHeader(@PathVariable String appId) {
        List<ExcelHeader> list = excelHeaderService.listByAppId(appId);
        return ResultData.success(list);
    }

    //获取excel查询表头带dropdown
    @GetMapping("/getHeaderWithQuery/{appId}")
    public ResultData getHeaderWithDropdown(@PathVariable String appId) {

        List<ExcelHeader> list = excelHeaderService.listByAppId(appId);
        for (ExcelHeader excelHeader : list) {
            if ("dropdown".equals(excelHeader.getHeaderType())) {
                List<String> dropdownList = excelColDropdownService.listByHeaderId(excelHeader.getId());
                excelHeader.setDropdownList(dropdownList);
            }
        }
        return ResultData.success(list);
    }

    //修改excel表头
    @PostMapping("/updateHeader/{appId}")
    public ResultData updateHeader(@RequestBody List<ExcelHeader> excelHeaders, @PathVariable String appId) {
        //不允许修改headerName
        for (ExcelHeader excelHeader : excelHeaders) {
            if ("dropdown".equals(excelHeader.getHeaderType())) {
                boolean state = excelColDropdownService.refreshDropdown(appId, excelHeader.getId(), excelHeader.getHeaderName());
                if (!state) {
                    return ResultData.fail("修改失败");
                }
            }
            excelHeader.setHeaderName(null);
            boolean state = excelHeaderService.updateBatchById(excelHeaders);
            if (!state) {
                return ResultData.fail("修改失败");
            }
        }
        return ResultData.success();
    }

    //条件分页查excel数据
    @PostMapping("/listData/{appId}")
    public ResultData listData(@PathVariable String appId, @RequestParam int pageNum, @RequestParam int pageSize, @RequestBody List<ExcelQueryConditionVo> excelQueryConditionVoList) {
        //在集合名为appId的集合中查找，满足查询条件为excelQueryConditionVoList中的条件，条件为and，分页查询，pageNum为页码，pageSize为每页数据量
        Query query = new Query();
        for (ExcelQueryConditionVo excelQueryConditionVo : excelQueryConditionVoList) {
            //如果条件值为空，不加入查询条件
            if (excelQueryConditionVo.getValue() == null || excelQueryConditionVo.getValue().isEmpty()) {
                continue;
            }
            //如果条件值不为空，加入查询条件
            query.addCriteria(new Criteria(excelQueryConditionVo.getColName()).is(excelQueryConditionVo.getValue()));
        }
        query.skip((long) (pageNum - 1) * pageSize);
        query.limit(pageSize);
        List<String> list = mongoTemplate.find(query, String.class, appId);
        List<Map<String, String>> result = new ArrayList<>();
        //将json数据转为列表
        for (String json : list) {
            Map<String, String> map = (Map) JSON.parse(json);
            result.add(map);
        }
        //查询满足条件的总数，不要分页
        query = new Query();
        for (ExcelQueryConditionVo excelQueryConditionVo : excelQueryConditionVoList) {
            if (excelQueryConditionVo.getValue() == null || excelQueryConditionVo.getValue().isEmpty()) {
                continue;
            }
            query.addCriteria(new Criteria(excelQueryConditionVo.getColName()).is(excelQueryConditionVo.getValue()));
        }
        long total = mongoTemplate.count(query, appId);
        Map<String, Object> res = new HashMap<>();
        res.put("total", total);
        res.put("records", result);
        return ResultData.success(res);
    }

    //添加excel数据
    @PostMapping("/addData/{appId}")
    public ResultData addData(@PathVariable String appId, @RequestBody Map<String, String> map) {
        //将map转为json
        String json = JSON.toJSONString(map);
        //将json存入mongodb
        mongoTemplate.insert(json, appId);
        return ResultData.success();
    }

    //删除excel数据
    @DeleteMapping("/deleteData/{appId}")
    public ResultData deleteData(@PathVariable String appId, @RequestBody List<String> idList) {
        for (String id : idList) {
            Query query = new Query();
            query.addCriteria(new Criteria("_id").is(id));
            mongoTemplate.remove(query, appId);
        }
        return ResultData.success();
    }

    //修改excel数据
    @PutMapping("/updateData/{appId}")
    public ResultData updateData(@PathVariable String appId, @RequestBody Map<String, Object> map) {
        //修改
        //取出_id，$oid格式，需要转为ObjectId格式,原格式为字符串：$oid={660d7f0caaebf419f03d0fc9}:
        String temp = map.get("_id").toString().split("=")[1].split("}")[0];
        ObjectId id = new ObjectId(temp);
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if ("_id".equals(entry.getKey())) {
                continue;
            }
            if (entry.getValue() == null) {
                continue;
            }
            update.set(entry.getKey(), entry.getValue());
        }
        mongoTemplate.updateFirst(query, update, appId);
        return ResultData.success();
    }

    //查excelApp数据
    @GetMapping("/listExcelApp")
    public ResultData listExcelApp() {
        QueryWrapper<ExcelApp> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort");
        List<ExcelApp> list = excelAppService.list(queryWrapper);
        return ResultData.success(list);
    }
    //download file
    @GetMapping("/download/{fileName}")
    public void download(HttpServletResponse response, @PathVariable String fileName) {
        File file = new File( "tmp/"+ fileName);
        if(!file.exists()){
            return ;
        }
        response.reset();
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName );

        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));) {
            byte[] buff = new byte[1024];
            OutputStream os  = response.getOutputStream();
            int i = 0;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
                os.flush();
            }
        } catch (IOException e) {
            return;
        }
    }

    //导出excel数据为excel文件
    @PostMapping("/exportData/{appId}")
    public ResultData exportData(HttpServletResponse response, HttpServletRequest request, @PathVariable String appId, @RequestBody List<ExcelQueryConditionVo> excelQueryConditionVoList) {
        //在集合名为appId的集合中查找，满足查询条件为excelQueryConditionVoList中的条件，条件为and
        Query query = new Query();
        for (ExcelQueryConditionVo excelQueryConditionVo : excelQueryConditionVoList) {
            if (excelQueryConditionVo.getValue() == null || excelQueryConditionVo.getValue().isEmpty()) {
                continue;
            }
            query.addCriteria(new Criteria(excelQueryConditionVo.getColName()).is(excelQueryConditionVo.getValue()));
        }
        List<String> list = mongoTemplate.find(query, String.class, appId);
        List<List<Object>> result = new ArrayList<>();

        for (String json : list) {
            Map<String, String> map = (Map) JSON.parse(json);
            List<Object> row = new ArrayList<>();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                row.add(entry.getValue());
            }
            result.add(row);
        }
        List<ExcelHeader> headerList = excelHeaderService.listByAppId(appId);
        List<String> header = new ArrayList<>();
        for (ExcelHeader excelHeader : headerList) {
            header.add(excelHeader.getHeaderName());
        }
        //拿到文件名
        String fileName = excelAppService.getOneByOption("app_id", appId).getName();
        //调用导出excel工具类
        //ExcelExportUtils.exportData(response, result, header, fileName);
        //对文件名与时间戳md5加密
        String newFileName = SecureUtil.md5(fileName+System.currentTimeMillis());
        String path = ExcelExportUtils.exportData(result, header, newFileName);
        Map<String,String> res = new HashMap<>();
        res.put("fileName",fileName);
        res.put("path",path);
        return ResultData.success(res);
    }
    //删除excelApp数据
    @DeleteMapping("/deleteExcelApp/{appId}")
    public ResultData deleteExcelApp(@PathVariable String appId) {
        //要先删除excelDropdown表中的数据,再删除excelHeader表中的数据，最后删除excelApp表中的数据,最后删除mongodb中的数据
        boolean state = excelHeaderService.removeByAppId(appId);
        if (!state) {
            return ResultData.fail("删除失败");
        }
        state = excelAppService.removeByAppId(appId);
        if (!state) {
            return ResultData.fail("删除失败");
        }
        mongoTemplate.dropCollection(appId);
        return ResultData.success();
    }
    //修改excelApp数据
    @PutMapping("/updateExcelApp")
    public ResultData updateExcelApp(@RequestBody ExcelApp excelApp) {
        boolean state = excelAppService.updateById(excelApp);
        if (!state) {
            return ResultData.fail("修改失败");
        }
        return ResultData.success();
    }
}


