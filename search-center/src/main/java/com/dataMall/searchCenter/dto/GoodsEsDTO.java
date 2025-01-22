package com.dataMall.searchCenter.dto;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.dataMall.common.entity.Goods;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Document(indexName = "goods")
@Data
public class GoodsEsDTO {
    
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * id
     */
    @Id
    private int id;
    
    /**
     * 商品名称
     */
    private String name;
    
    /**
     * 分类名称
     */
    private int categoryId;
    
    /**
     * 详情
     */
    private String detail;

    /**
     * 标签
     */
    private List<String> tags;

    /**
     * 创建用户 id
     */
    private int uid;

    /**
     * 创建时间
     */
    @Field(index = false, store = true, type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date createTime;

    /**
     * 更新时间
     */
    @Field(index = false, store = true, type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    private static final long serialVersionUID = 1L;

    private static final Gson GSON = new Gson();

    /**
     * 对象转包装类
     *
     * @param goods
     * @return
     */
    public static GoodsEsDTO objToDto(Goods goods) {
        if (goods == null) {
            return null;
        }
        GoodsEsDTO goodsEsDTO = new GoodsEsDTO();
        BeanUtils.copyProperties(goods, goodsEsDTO);
        // 上面只能拷贝基本类型：id、name、detail、uid，这里需要手动拷贝
        String tagsStr = goods.getTags();
        if (StringUtils.isNotBlank(tagsStr)) {
            goodsEsDTO.setTags(GSON.fromJson(tagsStr, new TypeToken<List<String>>() {
            }.getType()));
        }
        //createTime、updateTime数据库里是timestamp类型，es是Date，这里需要手动拷贝
        //timeToDateTime()方法是自定义的，用于将timestamp类型转为Date类型
        goodsEsDTO.setCreateTime(timeToDateTime(Timestamp.valueOf(goods.getCreateTime())));
        goodsEsDTO.setUpdateTime(timeToDateTime(Timestamp.valueOf(goods.getUpdateTime())));
        //isDelete数据库里是int类型，es是Integer，这里需要手动拷贝
        goodsEsDTO.setIsDelete(goods.getState());
        goodsEsDTO.setCategoryId(goods.getCategoriesId());
        return goodsEsDTO;
    }

    /**
     * 包装类转对象
     *
     * @param goodsESDTO
     * @return
     */
    public static Goods dtoToObj(GoodsEsDTO goodsESDTO) {
        if (goodsESDTO == null) {
            return null;
        }
        Goods goods = new Goods();
        BeanUtils.copyProperties(goodsESDTO, goods);
        List<String> tagList = goodsESDTO.getTags();
        if (CollectionUtils.isNotEmpty(tagList)) {
            goods.setTags(GSON.toJson(tagList));
        }
        return goods;
    }
    
    public static Date timeToDateTime(Timestamp ts) {
        return new Date(ts.getTime());
    }

}
