package com.dataMall.adminCenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataMall.adminCenter.entity.Account;
import com.dataMall.adminCenter.entity.Goods;
import com.dataMall.adminCenter.mapper.GoodsMapper;
import com.dataMall.adminCenter.service.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author woov
 * @since 2023-08-29
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {
    @Resource
    private GoodsCategoriesService goodsCategoriesService;
    @Resource
    private AccountService accountService;
    @Resource
    private GoodsCollectionService goodsCollectionService;
    @Resource
    private GoodsFileService goodsFileService;
    @Resource
    private GoodsPicService goodsPicService;

    //获取商品总数
    @Override
    public Integer getGoodsCount() {
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        return (int) count(queryWrapper);
    }

    //获取未审核的商品的数量
    @Override
    public Integer getNotAuditGoodsCount() {
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("state", -3);
        queryWrapper.or(i -> i.eq("state", -5).lt("update_time", LocalDateTime.now().minusMinutes(3)));
        return (int) count(queryWrapper);
    }

    //获取上架商品数量
    @Override
    public Integer getNormalGoodsCount() {
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("state", 0);
        return (int) count(queryWrapper);
    }

    //判断是否是商品发布者
    @Override
    public boolean isOwner(Integer uid, Integer goodsId) {
        Integer uidTemp = getById(goodsId).getUid();
        if (uidTemp == null) {
            return false;
        }
        return uidTemp.equals(uid);
    }

    //获取Goods其他外键参数
    @Override
    public void getGoodsOtherParam(Goods goods) {
        Account account = accountService.getById(goods.getUid());
        goods.setUsername(account.getUsername());
        goods.setAvatar(account.getAvatar());
        goods.setCategoriesName(goodsCategoriesService.getById(goods.getCategoriesId()).getName());
        goods.priceToMoney();
        goods.setCollection(goodsCollectionService.goodsCollectionNum(goods.getId()));
        goods.priceToMoney();
    }

    //获取Goods List的其他外键参数
    @Override
    public void getGoodsListOtherParam(List<Goods> goodsList) {
        for (Goods goods : goodsList) {
            getGoodsOtherParam(goods);
        }
    }

    //通过商品id获取商品信息
    @Override
    public Goods getGoodsInfoById(Integer id) {
        Goods goods = getById(id);
        getGoodsOtherParam(goods);
        return goods;
    }

    //获取未审核的商品
    @Override
    public Goods getNotAuditGoods() {
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        //查找逻辑：状态等于未审核-3，或者（状态等于-5且处于审核状态大于3分钟的记录）
        queryWrapper.eq("state", -3);
        queryWrapper.or(i -> i.eq("state", -5).lt("update_time", LocalDateTime.now().minusMinutes(3)));
        queryWrapper.last("limit 1");
        Goods goods = getOne(queryWrapper);
        if (goods == null) {
            return null;
        }
        //将状态码改为-5表示审核中
        goods.setState(-5);
        //这里需要手动更新更新时间
        goods.setUpdateTime(LocalDateTime.now());
        updateById(goods);
        //获取goods其他外键信息
        getGoodsOtherParam(goods);
        String fileUrl = goodsFileService.getOneByOption("md5", goods.getFileMd5()).getFilePath();
        goods.setFileUrl(fileUrl);
        List<String> imageUrls = goodsPicService.getGoodsImageUrls(goods.getId());
        goods.setImagesUrls(imageUrls);
        return goods;
    }

    //用户更新商品状态
    @Override
    public boolean userUpdateGoodsState(Integer uid, Integer goodsId, Integer state) {
        Goods goods = getById(goodsId);
        if (goods == null) {
            return false;
        }
        if (!goods.getUid().equals(uid)) {
            return false;
        }
        if (goods.getState() != 1) {
            return false;
        }
        goods.setState(1);
        return updateById(goods);
    }

    //管理员更新商品状态
    @Override
    public boolean adminUpdateGoodsState(Integer goodsId, Integer state) {
        Goods goods = getById(goodsId);
        if (goods == null) {
            return false;
        }
        goods.setState(state);
        return updateById(goods);
    }

    //获取商品分页数据
    @Override
    public IPage<Goods> getGoodsPage(String name, String categoriesName, String username, Integer pageNum, Integer pageSize) {
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            queryWrapper.like("name", name);
        }
        if (categoriesName != null && !categoriesName.isEmpty()) {
            Integer categoriesId = goodsCategoriesService.getOneByOption("name", categoriesName).getId();
            queryWrapper.eq("categories_id", categoriesId);
        }
        if (username != null && !username.isEmpty()) {
            Account account = accountService.getOneByOption("username", username);
            if (account == null) {
                return new Page<>();
            }
            queryWrapper.eq("uid", account.getId());
        }
        queryWrapper.eq("state", 0).or().eq("state", 1).or().eq("state", -1);
        IPage<Goods> page = page(new Page<>(pageNum, pageSize), queryWrapper);
        for (Goods goods : page.getRecords()) {
            getGoodsOtherParam(goods);
        }
        return page;
    }

    //获取商品list
    @Override
    public List<Goods> getGoodsList(QueryWrapper<Goods> queryWrapper) {
        List<Goods> goodsList = list(queryWrapper);
        getGoodsListOtherParam(goodsList);
        return goodsList;
    }
}
