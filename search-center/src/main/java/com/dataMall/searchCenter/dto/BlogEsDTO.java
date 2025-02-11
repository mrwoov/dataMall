package com.dataMall.searchCenter.dto;

import com.dataMall.common.vo.BlogVO;
import com.google.gson.Gson;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.sql.Timestamp;
import java.util.Date;

@Document(indexName = "blog")
@Data
public class BlogEsDTO {
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final long serialVersionUID = 1L;
    private static final Gson GSON = new Gson();
    /**
     * id
     */
    @Id
    private int id;
    /**
     * 博客标题
     */
    private String title;
    /**
     * 详情
     */
    private String description;
    /**
     * 标签
     */
    private String tags;
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

    public static BlogEsDTO objToDto(BlogVO blogVO) {
        BlogEsDTO blogEsDTO = new BlogEsDTO();
        blogEsDTO.setId(blogVO.getId().intValue());
        blogEsDTO.setTitle(blogVO.getTitle());
        blogEsDTO.setDescription(blogVO.getDescription());
        blogEsDTO.setTags(GSON.toJson(blogVO.getTagsList()));
        blogEsDTO.setUid(blogVO.getAuthorId());
        blogEsDTO.setCreateTime(timeToDateTime(Timestamp.valueOf(blogVO.getCreateTime())));
        blogEsDTO.setUpdateTime(timeToDateTime(Timestamp.valueOf(blogVO.getUpdateTime())));
        blogEsDTO.setIsDelete(0);
        return blogEsDTO;
    }
    
    

    public static Date timeToDateTime(Timestamp ts) {
        return new Date(ts.getTime());
    }
    
}
