package com.dataMall.searchCenter.service;

import com.dataMall.common.entity.Goods;

import java.util.List;

public interface SearchService {
    List<Goods> searchGoods(String keyword, Integer pageNum, Integer pageSize, Integer categoryId);
}
