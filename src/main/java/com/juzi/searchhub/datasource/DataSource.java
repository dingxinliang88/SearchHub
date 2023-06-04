package com.juzi.searchhub.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 数据源适配器
 *
 * @author codejuzi
 */
public interface DataSource<T> {


    /**
     * 执行查询
     *
     * @param searchText 搜索关键词
     * @param current    当前页码 || 游标
     * @param pageSize   当前页面大小
     * @return page
     */
    Page<T> doSearch(String searchText, long current, long pageSize);
}
