package com.juzi.searchhub.model.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.juzi.searchhub.model.entity.Picture;
import lombok.Data;

import java.io.Serializable;

/**
 * @author codejuzi
 */
@Data
public class SearchVO implements Serializable {

    private static final long serialVersionUID = -4049166578637866842L;

    private Page<Picture> picturePage;
    private Page<ArticleVO> articleVOPage;

    private Page<?> dataPage;
}
