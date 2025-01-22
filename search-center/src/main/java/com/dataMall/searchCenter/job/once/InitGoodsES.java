package com.dataMall.searchCenter.job.once;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dataMall.common.entity.Goods;
import com.dataMall.searchCenter.dto.GoodsEsDTO;
import com.dataMall.searchCenter.esDao.GoodsEsDao;
import com.dataMall.searchCenter.feign.GoodsService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

//全量同步商品到 es
// 取消注释开启任务
//@Component
@Slf4j
public class InitGoodsES implements CommandLineRunner {

    @Resource
    private GoodsService goodsService;
    
    @Resource
    private GoodsEsDao goodsEsDao;
    
    @Override
    public void run(String... args) {
        List<Goods> goodsList = goodsService.getGoodsListAll();
        if (CollectionUtils.isEmpty(goodsList)) {
            return;
        }
        List<GoodsEsDTO> goodsEsDTOList = goodsList.stream().map(GoodsEsDTO::objToDto).toList();
        final int pageSize = 500;
        int total = goodsEsDTOList.size();
        log.info("InitGoodsES start, total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            goodsEsDao.saveAll(goodsEsDTOList.subList(i, end));
        }
        log.info("InitGoodsES end, total {}", total);
    }
}
