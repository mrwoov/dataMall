package com.datamall.apicenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.datamall.apicenter.entity.ExcelApp;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author woov
 * @since 2024-04-02
 */
public interface ExcelAppService extends IService<ExcelApp> {
    ExcelApp getOneByOption(String colum,String value);

    boolean removeByAppId(String appId);
}
