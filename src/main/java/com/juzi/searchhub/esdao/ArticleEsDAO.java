package com.juzi.searchhub.esdao;


import com.juzi.searchhub.model.dto.article.ArticleEsDTO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * 文章es操作
 *
 * @author codejuzi
 */
public interface ArticleEsDAO extends ElasticsearchRepository<ArticleEsDTO, Long> {

    /**
     * 根据标题查询文章
     *
     * @param title 文章标题（部分）
     * @return article dto list
     */
    List<ArticleEsDTO> findByTitle(String title);
}
