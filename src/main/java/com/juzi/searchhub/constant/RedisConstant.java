package com.juzi.searchhub.constant;

/**
 * @author codejuzi
 */
public interface RedisConstant {

    /**
     * 缓存文章的key
     */
    String CACHE_ARTICLE_KEY_PREFIX = "searchhub:article";

    /**
     * 文章缓存时间，30min
     */
    long CACHE_ARTICLE_TTL = 30L;
}
