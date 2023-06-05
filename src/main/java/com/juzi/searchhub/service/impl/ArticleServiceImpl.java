package com.juzi.searchhub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.juzi.searchhub.mapper.ArticleMapper;
import com.juzi.searchhub.model.dto.QueryRequest;
import com.juzi.searchhub.model.entity.Article;
import com.juzi.searchhub.model.vo.ArticleVO;
import com.juzi.searchhub.service.ArticleService;
import com.juzi.searchhub.utils.SqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.juzi.searchhub.constant.CommonConstant.SORT_ORDER_ASC;
import static com.juzi.searchhub.constant.RedisConstant.CACHE_ARTICLE_KEY_PREFIX;
import static com.juzi.searchhub.constant.RedisConstant.CACHE_ARTICLE_TTL;

/**
 * @author codejuzi
 * @description 针对表【article(文章)】的数据库操作Service实现
 * @createDate 2023-06-02 19:27:00
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
        implements ArticleService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    private static final Gson GSON = new Gson();

    @Override
    public Page<ArticleVO> queryArticleByPage(QueryRequest queryRequest) {
        long current = queryRequest.getCurrent();
        long pageSize = queryRequest.getPageSize();
        String searchText = queryRequest.getSearchText();

        String cacheKey = CACHE_ARTICLE_KEY_PREFIX + ":" + searchText.toLowerCase();
        Long size = redisTemplate.opsForZSet().size(cacheKey);
        if (size != null && size > 0) {
            long startIndex = (current - 1) * pageSize;
            long endIndex = startIndex + pageSize - 1;

            Set<Object> articleVOJsonSet = redisTemplate.opsForZSet().reverseRange(cacheKey, startIndex, endIndex);

            assert articleVOJsonSet != null;
            List<ArticleVO> articleVOList = new ArrayList<>(articleVOJsonSet.size());
            Type type = new TypeToken<ArticleVO>() {
            }.getType();

            for (Object o : articleVOJsonSet) {
                String articleVOJson = (String) o;
                ArticleVO articleVO = GSON.fromJson(articleVOJson, type);
                articleVOList.add(articleVO);
            }

            Page<ArticleVO> articleVOPage = new Page<>(current, pageSize, size);
            articleVOPage.setRecords(articleVOList);
            return articleVOPage;
        }

        QueryWrapper<Article> queryWrapper = getQueryWrapper(queryRequest);
        Page<Article> articlePage = this.page(new Page<>(current, pageSize), queryWrapper);

        // Cache the retrieved articles with relevance scores
        doCacheArticle(articlePage.getRecords(), searchText);

        return getArticleVOPage(articlePage);
    }

    @Override
    public QueryWrapper<Article> getQueryWrapper(QueryRequest queryRequest) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        if (Objects.isNull(queryRequest)) {
            return queryWrapper;
        }
        String searchText = queryRequest.getSearchText();
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("title", searchText)
                    .or()
                    .like("content", searchText);
        }
        String sortField = queryRequest.getSortField();
        String sortOrder = queryRequest.getSortOrder();
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), SORT_ORDER_ASC.equals(sortOrder), sortField);
        return queryWrapper;
    }

    @Override
    public Page<ArticleVO> getArticleVOPage(Page<Article> articlePage) {
        List<Article> articleList = articlePage.getRecords();
        List<ArticleVO> articleVOList = articleList.stream().map(article -> {
            ArticleVO articleVO = new ArticleVO();
            BeanUtils.copyProperties(article, articleVO);
            return articleVO;
        }).collect(Collectors.toList());

        long current = articlePage.getCurrent();
        long size = articlePage.getSize();
        long total = articlePage.getTotal();
        Page<ArticleVO> articleVOPage = new Page<>(current, size, total);
        articleVOPage.setRecords(articleVOList);
        return articleVOPage;
    }

    @Override
    public synchronized void doCacheArticle(List<Article> articles, String searchText) {
        try {
            for (Article article : articles) {
                ArticleVO articleVO = new ArticleVO();
                BeanUtils.copyProperties(article, articleVO);
                String articleVOJson = GSON.toJson(articleVO);

                // Calculate the relevance score of the article based on the search text
                long score = StringUtils.countMatches(article.getTitle() + " " + article.getContent(), searchText);

                String cacheKey = CACHE_ARTICLE_KEY_PREFIX + ":" + searchText.toLowerCase();
                redisTemplate.opsForZSet().add(cacheKey, articleVOJson, score);
                redisTemplate.expire(cacheKey, CACHE_ARTICLE_TTL, TimeUnit.MINUTES);
            }
        } catch (Exception e) {
            log.error("Error caching articles: ", e);
        }
    }
}




