package com.juzi.searchhub.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.juzi.searchhub.common.BaseResponse;
import com.juzi.searchhub.common.StatusCode;
import com.juzi.searchhub.exception.BusinessException;
import com.juzi.searchhub.model.dto.QueryRequest;
import com.juzi.searchhub.model.entity.Picture;
import com.juzi.searchhub.model.vo.ArticleVO;
import com.juzi.searchhub.model.vo.SearchVO;
import com.juzi.searchhub.service.ArticleService;
import com.juzi.searchhub.service.PictureService;
import com.juzi.searchhub.utils.ResultUtils;
import com.juzi.searchhub.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.juzi.searchhub.constant.CommonConstant.ONCE_MAX_PAGE_SIZE;

/**
 * 搜索接口
 *
 * @author codejuzi
 */
@Slf4j
@RestController
@RequestMapping("/search")
public class SearchController {

    @Resource
    private PictureService pictureService;

    @Resource
    private ArticleService articleService;

    @GetMapping("/all")
    public BaseResponse<SearchVO> searchAll(QueryRequest queryRequest) {
        // 限制爬虫
        ThrowUtils.throwIf(queryRequest.getPageSize() > ONCE_MAX_PAGE_SIZE,
                StatusCode.PARAMS_ERROR, "一次获取资源过多");

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
        CompletableFuture<Page<Picture>> pictureTask
                = CompletableFuture.supplyAsync(() -> pictureService.queryPictureByPage(queryRequest));
        CompletableFuture<Page<ArticleVO>> articleTask
                = CompletableFuture.supplyAsync(() -> articleService.queryArticleByPage(queryRequest));

        CompletableFuture.allOf(pictureTask, articleTask).join();

        try {
            Page<Picture> picturePage = pictureTask.get();
            Page<ArticleVO> articleVOPage = articleTask.get();
            SearchVO searchVO = new SearchVO();
            searchVO.setArticleVOPage(articleVOPage);
            searchVO.setPicturePage(picturePage);
            return ResultUtils.success(searchVO);
        } catch (InterruptedException | ExecutionException e) {
            log.error("查询异常，", e);
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "查询异常");
        }

    }
}
