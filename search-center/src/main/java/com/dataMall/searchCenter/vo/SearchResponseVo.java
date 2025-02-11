package com.dataMall.searchCenter.vo;

import lombok.Data;

import java.util.List;

@Data
public class SearchResponseVo {
    private long total;
    private int pageNum;
    private int pageSize;
    private List<SearchVo> records;
}
