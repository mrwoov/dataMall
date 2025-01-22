package com.dataMall.searchCenter.job.cycle;

import com.dataMall.common.entity.Goods;
import com.dataMall.searchCenter.dto.GoodsEsDTO;
import com.dataMall.searchCenter.esDao.GoodsEsDao;
import com.dataMall.searchCenter.feign.GoodsService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 增量同步商品到 es
 */
// 取消注释开启任务
//@Component
@Slf4j
public class IncSyncGoodsToEs {

    @Resource
    private GoodsService goodsService;
    @Resource
    private GoodsEsDao goodsEsDao;
    //每分钟执行一次
    @Scheduled(fixedRate = 60 * 1000)
    public void run() {
        // 查询近 5 分钟内的数据
        List<Goods> goodsList = goodsService.getGoodsWithFiveMinutesAgoUpdate();
        if (CollectionUtils.isEmpty(goodsList)) {
            log.info("no inc goods");
            return;
        }
        List<GoodsEsDTO> goodsEsDTOList = goodsList.stream()
                .map(GoodsEsDTO::objToDto)
                .toList();
        final int pageSize = 500;
        int total = goodsEsDTOList.size();
        log.info("IncSyncGoodsToEs start, total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            goodsEsDao.saveAll(goodsEsDTOList.subList(i, end));
        }
        log.info("IncSyncGoodsToEs end, total {}", total);
    }
}
