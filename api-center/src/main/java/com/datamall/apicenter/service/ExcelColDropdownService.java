package com.datamall.apicenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.datamall.apicenter.entity.ExcelColDropdown;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author woov
 * @since 2024-04-02
 */
public interface ExcelColDropdownService extends IService<ExcelColDropdown> {

    boolean refreshDropdown(String appId, int headerId, String colName);

    boolean removeByHeaderId(int headerId);

    List<String> listByHeaderId(Integer id);

}
