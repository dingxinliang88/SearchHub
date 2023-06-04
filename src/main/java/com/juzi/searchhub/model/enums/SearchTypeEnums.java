package com.juzi.searchhub.model.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 搜索类型枚举
 *
 * @author codejuzi
 */
public enum SearchTypeEnums {
    ARTICLE("article", "文章"),
    PICTURE("picture", "图片");

    private final String type;

    private final String description;

    SearchTypeEnums(String type, String description) {
        this.type = type;
        this.description = description;
    }

    /**
     * 根据type获取枚举
     *
     * @param type 搜索类型
     * @return search enum
     */
    public static SearchTypeEnums getEnumByType(String type) {
        if (StringUtils.isBlank(type)) {
            return null;
        }
        for (SearchTypeEnums searchTypeEnums : SearchTypeEnums.values()) {
            if (searchTypeEnums.getType().equals(type)) {
                return searchTypeEnums;
            }
        }
        return null;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }
}
