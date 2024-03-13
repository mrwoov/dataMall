package com.dataMall.userCenter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author woov
 * @since 2024-03-02
 */
@Getter
@Setter
@Accessors(chain = true)
public class Sso implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer type;

    private Integer uid;

    private String ssoUser;

    private String ssoToken;

    private Integer state;


}
