package com.dataMall.adminCenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dataMall.adminCenter.entity.SystemDict;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author woov
 * @since 2023-10-07
 */
public interface SystemDictService extends IService<SystemDict> {
    List<SystemDict> getOneType(String type);
}
