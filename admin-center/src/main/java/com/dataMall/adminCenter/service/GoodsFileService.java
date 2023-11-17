package com.dataMall.adminCenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dataMall.adminCenter.entity.GoodsFile;

/**
 * <p>
 * 商品数据文件 服务类
 * </p>
 *
 * @author woov
 * @since 2023-10-26
 */
public interface GoodsFileService extends IService<GoodsFile> {
    //根据一个条件查找
    GoodsFile getOneByOption(String column, Object value);
}
