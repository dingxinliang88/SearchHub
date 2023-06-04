package com.juzi.searchhub.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.juzi.searchhub.common.StatusCode;
import com.juzi.searchhub.exception.BusinessException;
import com.juzi.searchhub.model.dto.QueryRequest;
import com.juzi.searchhub.model.entity.Picture;
import com.juzi.searchhub.model.enums.SearchTypeEnums;
import com.juzi.searchhub.model.vo.ArticleVO;
import com.juzi.searchhub.model.vo.SearchVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 搜索门面
 *
 * @author codejuzi
 */
@Slf4j
@Service
public class SearchFacade {

    @Resource
    private DataSourceRegister dataSourceRegister;

    @Resource
    private PictureDataSource pictureDataSource;

    @Resource
    private ArticleDataSource articleDataSource;

    public SearchVO doSearch(QueryRequest queryRequest) {
        String searchText = queryRequest.getSearchText();
        String type = queryRequest.getType();
        long current = queryRequest.getCurrent();
        long pageSize = queryRequest.getPageSize();

        SearchTypeEnums searchTypeEnums = SearchTypeEnums.getEnumByType(type);
        if (Objects.isNull(searchTypeEnums)) {
            // 搜索出所有的数据
            return doSearchAll(searchText, current, pageSize);
        } else {
            // 搜索出特定的数据
            SearchVO searchVO = new SearchVO();
            DataSource<?> dataSource = dataSourceRegister.getDataSourceByType(type);
            Page<?> dataPage = dataSource.doSearch(searchText, current, pageSize);
            searchVO.setDataPage(dataPage);
            return searchVO;
        }
    }

    /**
     * 查询所有
     */
    private SearchVO doSearchAll(String searchText, long current, long pageSize) {
        // region 串行

//        Page<Picture> picturePage = pictureService.queryPictureByPage(queryRequest);
//        Page<ArticleVO> articleVOPage = articleService.queryArticleByPage(queryRequest);
//
//        SearchVO searchVO = new SearchVO();
//        searchVO.setArticleVOPage(articleVOPage);
//        searchVO.setPicturePage(picturePage);
//        return ResultUtils.success(searchVO);

        // endregion
        // 并行
        CompletableFuture<Page<ArticleVO>> articleTask
                = CompletableFuture.supplyAsync(() -> articleDataSource.doSearch(searchText, current, pageSize));
        CompletableFuture<Page<Picture>> pictureTask
                = CompletableFuture.supplyAsync(() -> pictureDataSource.doSearch(searchText, current, pageSize));

        CompletableFuture.allOf(pictureTask, articleTask).join();

        try {
            Page<Picture> picturePage = pictureTask.get();
            Page<ArticleVO> articleVOPage = articleTask.get();
            SearchVO searchVO = new SearchVO();
            searchVO.setArticleVOPage(articleVOPage);
            searchVO.setPicturePage(picturePage);
            return searchVO;
        } catch (InterruptedException | ExecutionException e) {
            log.error("查询异常，", e);
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "查询异常");
        }
    }
}
