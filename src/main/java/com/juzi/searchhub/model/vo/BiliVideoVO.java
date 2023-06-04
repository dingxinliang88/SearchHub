package com.juzi.searchhub.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * B站视频VO对象
 *
 * @author codejuzi
 */
@Data
public class BiliVideoVO implements Serializable {

    private static final long serialVersionUID = 7471009205498627747L;

    private String author;

    /**
     * 视频播放地址
     */
    private String arcurl;

    /**
     * 视频封面
     */
    private String pic;

    private String typename;
}
