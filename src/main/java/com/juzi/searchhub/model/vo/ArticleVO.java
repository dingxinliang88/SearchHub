package com.juzi.searchhub.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author codejuzi
 */
@Data
public class ArticleVO implements Serializable {

    private static final long serialVersionUID = -7599484242628382110L;

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 创建时间
     */
    private Date createTime;
}
