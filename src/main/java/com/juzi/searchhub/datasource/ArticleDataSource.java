package com.juzi.searchhub.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.juzi.searchhub.model.dto.QueryRequest;
import com.juzi.searchhub.model.enums.SearchTypeEnums;
import com.juzi.searchhub.model.vo.ArticleVO;
import com.juzi.searchhub.service.ArticleService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.juzi.searchhub.constant.CommonConstant.SORT_ORDER_ASC;

/**
 * 适配器——文章
 *
 * @author codejuzi
 */
@Component
public class ArticleDataSource implements DataSource<ArticleVO> {

    private static final String ARTICLE_SORT_FIELD = "createTime";

    @Resource
    private ArticleService articleService;

    @Override
    public Page<ArticleVO> doSearch(String searchText, long current, long pageSize) {

        QueryRequest queryRequest = new QueryRequest();
        queryRequest.setSearchText(searchText);
        queryRequest.setType(SearchTypeEnums.ARTICLE.getType());
        queryRequest.setCurrent(current);
        queryRequest.setPageSize(pageSize);
        queryRequest.setSortField(ARTICLE_SORT_FIELD);
        queryRequest.setSortOrder(SORT_ORDER_ASC);

//        return articleService.queryArticleByPage(queryRequest);
        return articleService.queryFromES(queryRequest);

    }
}
