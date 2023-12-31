package com.example.datamall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.datamall.entity.Account;
import com.example.datamall.entity.GoodsComment;
import com.example.datamall.mapper.GoodsCommentMapper;
import com.example.datamall.service.AccountService;
import com.example.datamall.service.GoodsCommentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品评论表 服务实现类
 * </p>
 *
 * @author woov
 * @since 2023-08-29
 */
@Service
public class GoodsCommentServiceImpl extends ServiceImpl<GoodsCommentMapper, GoodsComment> implements GoodsCommentService {
    @Resource
    private AccountService accountService;
    @Override
    public GoodsComment getOneByOption(String column, Object value) {
        QueryWrapper<GoodsComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(column, value);
        return getOne(queryWrapper);
    }

    @Override
    public Integer commentIdToGoodsId(Integer commentId) {
        Integer goodsId = getById(commentId).getGoodsId();
        if (goodsId == null) {
            return -1;
        }
        return goodsId;
    }

    @Override
    public boolean isSender(Integer uid, Integer commentsId) {
        Integer uid_temp = getById(commentsId).getUid();
        if (uid_temp == null) {
            return false;
        }
        return uid.equals(uid_temp);
    }

    @Override
    public IPage<GoodsComment> query(Integer goodsId, Integer pageNum, Integer pageSize) {
        QueryWrapper<GoodsComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("goods_id", goodsId);
        IPage<GoodsComment> page = page(new Page<>(pageNum, pageSize), queryWrapper);
        for (GoodsComment goodsComment : page.getRecords()) {
            Account account = accountService.getById(goodsComment.getUid());
            goodsComment.setUsername(account.getUsername());
            goodsComment.setAvatar(account.getAvatar());
        }
        return page;
    }
}
