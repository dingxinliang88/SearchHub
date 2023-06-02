package com.juzi.searchhub.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.juzi.searchhub.common.BaseResponse;
import com.juzi.searchhub.common.StatusCode;
import com.juzi.searchhub.model.dto.ArticleQueryRequest;
import com.juzi.searchhub.model.vo.ArticleVO;
import com.juzi.searchhub.service.ArticleService;
import com.juzi.searchhub.utils.ResultUtils;
import com.juzi.searchhub.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static com.juzi.searchhub.constant.CommonConstant.ONCE_MAX_PAGE_SIZE;

/**
 * 文章接口
 *
 * @author codejuzi
 */
@Slf4j
@RestController
@RequestMapping("/article")
public class ArticleController {

    @Resource
    private ArticleService articleService;

    @GetMapping("/query/page")
    public BaseResponse<Page<ArticleVO>> queryArticleByPage(ArticleQueryRequest articleQueryRequest) {
        // 防止爬虫
        ThrowUtils.throwIf(articleQueryRequest.getPageSize() > ONCE_MAX_PAGE_SIZE,
                StatusCode.PARAMS_ERROR, "一次获取资源过多");
        Page<ArticleVO> articleVOPage = articleService.queryArticleByPage(articleQueryRequest);
        return ResultUtils.success(articleVOPage);
    }
}
