package com.dataMall.searchCenter.dto;

import com.dataMall.common.entity.User;
import com.google.gson.Gson;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.sql.Timestamp;
import java.util.Date;

@Document(indexName = "user")
@Data
public class UserEsDTO {
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final long serialVersionUID = 1L;
    private static final Gson GSON = new Gson();

    /**
     * id
     */
    @Id
    private int id;
    
    /**
     * 用户名
     */
    private String username;

    /**
     * 头像
     */
    private String avatar;
    
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
    
    public static UserEsDTO objToDto(User user) {
        UserEsDTO userEsDTO = new UserEsDTO();
        userEsDTO.setId(user.getId());
        userEsDTO.setUsername(user.getUsername());
        userEsDTO.setAvatar(user.getAvatar());
        userEsDTO.setCreateTime(timeToDateTime(Timestamp.valueOf(user.getCreateTime())));
        userEsDTO.setUpdateTime(timeToDateTime(Timestamp.valueOf(user.getUpdateTime())));
        userEsDTO.setIsDelete(user.getState());
        return userEsDTO;
    }

    public static Date timeToDateTime(Timestamp ts) {
        return new Date(ts.getTime());
    }
}
