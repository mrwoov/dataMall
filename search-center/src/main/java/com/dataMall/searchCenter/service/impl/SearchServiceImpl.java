package com.dataMall.searchCenter.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.dataMall.common.entity.Goods;
import com.dataMall.searchCenter.dto.GoodsEsDTO;
import com.dataMall.searchCenter.esDao.GoodsEsDao;
import com.dataMall.searchCenter.feign.GoodsService;
import com.dataMall.searchCenter.service.SearchService;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class SearchServiceImpl implements SearchService {
    @Resource
    private GoodsEsDao goodsEsDao;
    @Resource
    private GoodsService goodsService;
    
    @Override
    public List<Goods> searchGoods(String keyword, Integer pageNum, Integer pageSize, Integer categoryId) {
        if (StringUtils.isEmpty(keyword)) {
            return null;
        }
        //es初始页码为0
        if (pageNum == null || pageNum < 0) {
            pageNum = 0;
        }else {
            pageNum -= 1;
        }
        Page<GoodsEsDTO> goodsEsDTOList;
        if (categoryId == -1){
             goodsEsDTOList = goodsEsDao.findByName(keyword, PageRequest.of(pageNum, pageSize));
        }else {
            goodsEsDTOList = goodsEsDao.findByNameAndCategoryId(keyword, categoryId, PageRequest.of(pageNum, pageSize));
        }

        if (goodsEsDTOList == null || goodsEsDTOList.isEmpty()) {
            return null;
        }
        //取出商品id
        List<Integer> goodsIdList = goodsEsDTOList.stream().map(GoodsEsDTO::getId).toList();
        //根据id查询商品
        List<Goods> goodsList =  goodsService.getGoodsListByIds(goodsIdList);
        //脱敏处理
        goodsList = goodsList.stream().filter(goods -> goods.getState()==0)
                        .peek(goods -> goods.setFileMd5(null)).toList();
        return goodsList;
    }
}
