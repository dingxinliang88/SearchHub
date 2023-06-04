package com.juzi.searchhub.datasource;

import com.juzi.searchhub.model.enums.SearchTypeEnums;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据源注册器
 *
 * @author codejuzi
 */
@Component
public class DataSourceRegister {

    @Resource
    private ArticleDataSource articleDataSource;

    @Resource
    private PictureDataSource pictureDataSource;

    @Resource
    private BiliVideoDataSource biliVideoDataSource;


    private final Map<String, DataSource<?>> DATA_SOURCE_REGISTER
            = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        DATA_SOURCE_REGISTER.put(SearchTypeEnums.ARTICLE.getType(), articleDataSource);
        DATA_SOURCE_REGISTER.put(SearchTypeEnums.PICTURE.getType(), pictureDataSource);
        DATA_SOURCE_REGISTER.put(SearchTypeEnums.VIDEO.getType(), biliVideoDataSource);
    }

    /**
     * 获取对应的数据源
     *
     * @param type 数据源类型
     * @return DataSource
     */
    public DataSource<?> getDataSourceByType(String type) {
        return DATA_SOURCE_REGISTER.get(type);
    }


}
