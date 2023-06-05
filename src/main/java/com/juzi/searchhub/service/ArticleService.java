package com.juzi.searchhub.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.juzi.searchhub.model.dto.QueryRequest;
import com.juzi.searchhub.model.entity.Article;
import com.baomidou.mybatisplus.extension.service.IService;
import com.juzi.searchhub.model.vo.ArticleVO;

import java.util.List;

/**
 * @author codejuzi
 * @description 针对表【article(文章)】的数据库操作Service
 * @createDate 2023-06-02 19:27:00
 */
public interface ArticleService extends IService<Article> {

    /**
     * 分页查询文章
     *
     * @param queryRequest 文章查询请求
     * @return article vo page
     */
    Page<ArticleVO> queryArticleByPage(QueryRequest queryRequest);

    /**
     * 组装查询条件
     *
     * @param queryRequest 查询条件
     * @return query wrapper
     */
    QueryWrapper<Article> getQueryWrapper(QueryRequest queryRequest);

    /**
     * 获取封装后的文章分页信息
     *
     * @param articlePage 原始的文章分页信息
     * @return article page
     */
    Page<ArticleVO> getArticleVOPage(Page<Article> articlePage);


    /**
     * 缓存文章
     *
     * @param articles   文章
     * @param searchText 搜索关键词
     */
    void doCacheArticle(List<Article> articles, String searchText);
}
