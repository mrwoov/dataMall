package com.datamall.apicenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.model.Aggregates;
import jakarta.annotation.Resource;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import com.datamall.apicenter.entity.ExcelHeader;
import com.datamall.apicenter.mapper.ExcelHeaderMapper;
import com.datamall.apicenter.service.ExcelAppService;
import com.datamall.apicenter.service.ExcelColDropdownService;
import com.datamall.apicenter.service.ExcelHeaderService;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author woov
 * @since 2024-04-02
 */
@Service
public class ExcelHeaderServiceImpl extends ServiceImpl<ExcelHeaderMapper, ExcelHeader> implements ExcelHeaderService {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Resource
    private ExcelAppService excelAppService;
    @Resource
    private ExcelColDropdownService excelColDropdownService;

    @Override
    public List<ExcelHeader> listByAppId(String appId) {
        int app = excelAppService.getOneByOption("app_id", appId).getId();
        QueryWrapper<ExcelHeader> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("excel_app", app);
        queryWrapper.orderByAsc("sort");
        return list(queryWrapper);
    }

    @Override
    public List<String> getExcelColDropdown(String appId, String colName) {
        //查app_id对应的集合中colName字段的所有不重复值，appId对应的集合名为appId
        AggregateIterable<Document> output = mongoTemplate.getCollection(appId).aggregate(
                List.of(
                        Aggregates.group("$" + colName),
                        project(fields(include("_id")))
                )
        );
        List<String> result = new ArrayList<>();
        for (Document document : output) {
            result.add(document.getString("_id"));
        }
        return result;
    }

    @Override
    public boolean removeByAppId(String appId) {
        int app = excelAppService.getOneByOption("app_id", appId).getId();
        QueryWrapper<ExcelHeader> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("excel_app", app);
        //查出所有header的id
        List<ExcelHeader> headers = list(queryWrapper);
        List<Integer> headerIds = new ArrayList<>();
        for (ExcelHeader header : headers) {
            headerIds.add(header.getId());
        }
        //删除header对应的下拉框
        for (Integer headerId : headerIds) {
            excelColDropdownService.removeByHeaderId(headerId);
        }
        //删除header
        return remove(queryWrapper);
    }
}
