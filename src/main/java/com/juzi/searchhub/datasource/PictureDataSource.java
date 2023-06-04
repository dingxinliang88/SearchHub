package com.juzi.searchhub.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.juzi.searchhub.model.dto.QueryRequest;
import com.juzi.searchhub.model.entity.Picture;
import com.juzi.searchhub.model.enums.SearchTypeEnums;
import com.juzi.searchhub.service.PictureService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 适配器——图片
 *
 * @author codejuzi
 */
@Component
public class PictureDataSource implements DataSource<Picture> {


    @Resource
    private PictureService pictureService;

    @Override
    public Page<Picture> doSearch(String searchText, long current, long pageSize) {

        QueryRequest queryRequest = new QueryRequest();
        queryRequest.setSearchText(searchText);
        queryRequest.setType(SearchTypeEnums.PICTURE.getType());
        queryRequest.setCurrent(current);
        queryRequest.setPageSize(pageSize);

        return pictureService.queryPictureByPage(queryRequest);

    }
}
