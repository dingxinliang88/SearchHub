package com.juzi.searchhub.controller;

import com.juzi.searchhub.common.BaseResponse;
import com.juzi.searchhub.common.StatusCode;
import com.juzi.searchhub.model.dto.QueryRequest;
import com.juzi.searchhub.model.vo.SearchVO;
import com.juzi.searchhub.datasource.SearchFacade;
import com.juzi.searchhub.utils.ResultUtils;
import com.juzi.searchhub.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
    private SearchFacade searchFacade;

    @GetMapping("/all")
    public BaseResponse<SearchVO> searchAll(QueryRequest queryRequest) {
        // 限制爬虫
        ThrowUtils.throwIf(queryRequest.getPageSize() > ONCE_MAX_PAGE_SIZE,
                StatusCode.PARAMS_ERROR, "一次获取资源过多");

        SearchVO searchVO = searchFacade.doSearch(queryRequest);
        return ResultUtils.success(searchVO);

    }
}
