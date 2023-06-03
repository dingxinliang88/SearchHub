package com.juzi.searchhub.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.juzi.searchhub.model.dto.QueryRequest;
import com.juzi.searchhub.model.entity.Picture;

/**
 * @author codejuzi
 */
public interface PictureService {

    /**
     * 图片查询文章
     *
     * @param queryRequest 图片查询请求
     * @return picture page
     */
    Page<Picture> queryPictureByPage(QueryRequest queryRequest);
}
