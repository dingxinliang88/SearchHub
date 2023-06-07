package com.juzi.searchhub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.juzi.searchhub.mapper.ArticleMapper;
import com.juzi.searchhub.model.dto.QueryRequest;
import com.juzi.searchhub.model.dto.article.ArticleEsDTO;
import com.juzi.searchhub.model.entity.Article;
import com.juzi.searchhub.model.vo.ArticleVO;
import com.juzi.searchhub.service.ArticleService;
import com.juzi.searchhub.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.juzi.searchhub.constant.CommonConstant.SORT_ORDER_ASC;

/**
 * @author codejuzi
 * @description 针对表【article(文章)】的数据库操作Service实现
 * @createDate 2023-06-02 19:27:00
 */
@Slf4j
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
        implements ArticleService {


    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    private static final String DEFAULT_SORT_FIELD = "createTime";

    @Override
    public Page<ArticleVO> queryArticleByPage(QueryRequest queryRequest) {
        long current = queryRequest.getCurrent();
        long pageSize = queryRequest.getPageSize();

        QueryWrapper<Article> queryWrapper = getQueryWrapper(queryRequest);
        Page<Article> articlePage = this.page(new Page<>(current, pageSize), queryWrapper);

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
    public Page<ArticleVO> queryFromES(QueryRequest queryRequest) {
        // 构造查询
        NativeSearchQuery searchQuery = this.getSearchQuery(queryRequest);
        SearchHits<ArticleEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, ArticleEsDTO.class);

        // 查询出结果，解析结果
        List<Article> resourceList = new ArrayList<>();
        if (searchHits.hasSearchHits()) {
            List<SearchHit<ArticleEsDTO>> searchHitList = searchHits.getSearchHits();

            List<Long> articleIdList = searchHitList.stream()
                    .map(searchHit -> searchHit.getContent().getId()).collect(Collectors.toList());
            @SuppressWarnings("DataFlowIssue") Map<String, List<SearchHit<ArticleEsDTO>>> highLightMap
                    = searchHitList.stream().collect(Collectors.groupingBy(SearchHit::getId));
            // 从db中获取最新数据
            List<Article> articleList = this.listByIds(articleIdList);
            if (!Objects.isNull(articleList)) {
                Map<Long, List<Article>> idArticleMap
                        = articleList.stream().collect(Collectors.groupingBy(Article::getId));
                articleIdList.forEach(articleId -> {
                    if (idArticleMap.containsKey(articleId)) {
                        Article article = idArticleMap.get(articleId).get(0);
                        SearchHit<ArticleEsDTO> articleEsDTOSearchHit = highLightMap.get(String.valueOf(articleId)).get(0);
                        List<String> titleHighlightField = articleEsDTOSearchHit.getHighlightField("title");
                        article.setTitle(
                                titleHighlightField.isEmpty() ?
                                        article.getTitle() :
                                        articleEsDTOSearchHit.getHighlightField("title").get(0)
                        );
                        List<String> contentHighlightField = articleEsDTOSearchHit.getHighlightField("content");
                        article.setContent(
                                contentHighlightField.isEmpty() ?
                                        article.getContent() :
                                        articleEsDTOSearchHit.getHighlightField("content").get(0)
                        );
                        resourceList.add(article);
                    } else {
                        // 从es中清空db已物理删除的数据
                        String delete = elasticsearchRestTemplate.delete(String.valueOf(articleId), ArticleEsDTO.class);
                        log.info("delete article, id{} => {}", articleId, delete);
                    }
                });
            }
        }
        Page<ArticleVO> articleVOPage = new Page<>(queryRequest.getCurrent(), queryRequest.getPageSize());
        articleVOPage.setTotal(searchHits.getTotalHits());
        List<ArticleVO> articleVOList = resourceList.stream().map(article -> {
            ArticleVO articleVO = new ArticleVO();
            BeanUtils.copyProperties(article, articleVO);
            return articleVO;
        }).collect(Collectors.toList());
        articleVOPage.setRecords(articleVOList);
        return articleVOPage;
    }

    @Override
    public NativeSearchQuery getSearchQuery(QueryRequest queryRequest) {
        String searchText = queryRequest.getSearchText();
        String sortField = queryRequest.getSortField();
        String sortOrder = queryRequest.getSortOrder();
        long current = queryRequest.getCurrent();
        long pageSize = queryRequest.getPageSize();


        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        // 按关键词搜索
        if (StringUtils.isNotBlank(searchText)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("title", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("content", searchText));
            boolQueryBuilder.minimumShouldMatch(1);
        }

        // 排序
        if (StringUtils.isBlank(sortField) || !SqlUtils.validSortField(sortField)) {
            sortField = DEFAULT_SORT_FIELD;
        }
        SortBuilder<?> sortBuilder = SortBuilders.fieldSort(sortField);
        sortBuilder.order(SORT_ORDER_ASC.equals(sortOrder) ? SortOrder.ASC : SortOrder.DESC);
        // 分页
        PageRequest pageRequest = PageRequest.of((int) current, (int) pageSize);

        // 高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("content").field("title")
                .preTags("<font color='red'>").postTags("</font>");

        // 搜索建议（有待实现）

        // 构造查询
        return new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withHighlightBuilder(highlightBuilder)
                .withPageable(pageRequest)
                .withSorts(sortBuilder)
                .build();
    }
}




