package com.juzi.searchhub.model.dto;

import com.juzi.searchhub.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author codejuzi
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QueryRequest extends PageRequest implements Serializable {

    /**
     * 搜索关键词（标题、内容）
     */
    private String searchText;
}
