package com.datamall.apicenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.model.Aggregates;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import com.datamall.apicenter.entity.ExcelColDropdown;
import com.datamall.apicenter.mapper.ExcelColDropdownMapper;
import com.datamall.apicenter.service.ExcelColDropdownService;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author woov
 * @since 2024-04-02
 */
@Service
public class ExcelColDropdownServiceImpl extends ServiceImpl<ExcelColDropdownMapper, ExcelColDropdown> implements ExcelColDropdownService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public boolean refreshDropdown(String appId, int headerId, String colName) {
        //删除原有的下拉选项
        removeByHeaderId(headerId);
        AggregateIterable<Document> output = mongoTemplate.getCollection(appId).aggregate(
                List.of(
                        Aggregates.group("$" + colName),
                        project(fields(include("_id")))
                )
        );
        boolean saveStatus = true;
        for (Document document : output) {
            ExcelColDropdown dropdown = new ExcelColDropdown();
            //判断colName在document中是否为字符串，不是则转为字符串存入数据库
            //如果是null则不存入数据库
            if (document.get("_id") == null) {
                continue;
            }
            dropdown.setHeaderId(headerId).setName(document.get("_id").toString());
            saveStatus = save(dropdown) && saveStatus;
        }
        return  saveStatus;
    }

    @Override
    public boolean removeByHeaderId(int headerId) {
        QueryWrapper<ExcelColDropdown> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("header_id", headerId);
        return remove(queryWrapper);
    }

    @Override
    public List<String> listByHeaderId(Integer id) {
        QueryWrapper<ExcelColDropdown> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("header_id", id);
        List<ExcelColDropdown> list = list(queryWrapper);
        List<String> result = new ArrayList<>();
        for (ExcelColDropdown dropdown : list) {
            result.add(dropdown.getName());
        }
        return result;
    }


}
