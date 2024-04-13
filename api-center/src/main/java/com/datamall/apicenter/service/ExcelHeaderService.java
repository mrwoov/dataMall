package com.datamall.apicenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.datamall.apicenter.entity.ExcelHeader;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author woov
 * @since 2024-04-02
 */
public interface ExcelHeaderService extends IService<ExcelHeader> {

    List<ExcelHeader> listByAppId(String appId);

    List<String> getExcelColDropdown(String appId, String colName);

    boolean removeByAppId(String appId);
}
