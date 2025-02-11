package com.dataMall.searchCenter.service;

import com.dataMall.searchCenter.vo.SearchResponseVo;

public interface SearchService {

    SearchResponseVo searchGoods(String keyword, Integer uid, Integer pageNum, Integer pageSize);

    SearchResponseVo searchAll(String keyword, Integer uid, int page, int size);

    SearchResponseVo searchBlog(String keyword, Integer uid, Integer page, Integer size);

    SearchResponseVo searchUser(String keyword, Integer page, Integer size);
}
