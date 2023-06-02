package com.juzi.searchhub.mapper;

import com.juzi.searchhub.model.entity.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author codejuzi
 * @description 针对表【article(文章)】的数据库操作Mapper
 * @createDate 2023-06-02 19:27:00
 * @Entity com.juzi.searchhub.model.entity.Article
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

}




