package com.juzi.searchhub.common;

import com.juzi.searchhub.constant.CommonConstant;
import lombok.Data;

import java.io.Serializable;

/**
 * 分页查询请求
 *
 * @author codejuzi
 */
@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = 1276284131688786899L;

    /**
     * 当前页号
     */
    private long current = 1L;

    /**
     * 每页信息展示数
     */
    private long pageSize = 10L;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排列顺序，默认升序
     */
    private String sortOrder = CommonConstant.SORT_ORDER_ASC;
}
