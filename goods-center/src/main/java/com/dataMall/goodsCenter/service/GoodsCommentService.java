package com.dataMall.goodsCenter.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dataMall.goodsCenter.entity.GoodsComment;

/**
 * <p>
 * 商品评论表 服务类
 * </p>
 *
 * @author woov
 * @since 2023-08-29
 */
public interface GoodsCommentService extends IService<GoodsComment> {

    GoodsComment getOneByOption(String column, Object value);

    Integer commentIdToGoodsId(Integer commentId);

    boolean isSender(Integer uid, Integer commentsId);

    IPage<GoodsComment> query(Integer goodsId, Integer pageNum, Integer pageSize);
}
