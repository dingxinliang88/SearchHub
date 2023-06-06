package com.juzi.searchhub.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 央视新闻VO
 *
 * @author codejuzi
 */
@Data
public class NewsVO implements Serializable {

    private static final long serialVersionUID = 1013154092380447548L;

    private String title;
    private String url;
    private String image;
    private String brief;
    private String focusDate;
}
