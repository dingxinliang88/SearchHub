package com.juzi.searchhub.model.dto.article;

import com.google.gson.Gson;
import com.juzi.searchhub.model.entity.Article;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * 文章-ES
 *
 * @author codejuzi
 */
@Data
@Document(indexName = "article")
public class ArticleEsDTO implements Serializable {

    private static final long serialVersionUID = -8702692042641812637L;

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 创建时间
     */
    @Field(index = false, store = true, type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date createTime;

    /**
     * 是否删除 0 - 未删除， 1 - 已删除
     */
    private Integer isDelete;


    private static final Gson GSON = new Gson();


    /**
     * 对象转包装类
     *
     * @param article 文章对象
     * @return 文章包装类
     */
    public static ArticleEsDTO obj2Dto(Article article) {
        if (Objects.isNull(article)) {
            return null;
        }

        ArticleEsDTO articleEsDTO = new ArticleEsDTO();
        BeanUtils.copyProperties(article, articleEsDTO);
        return articleEsDTO;
    }

    /**
     * 包装类转对象
     *
     * @param articleEsDTO 文章包装类
     * @return 文章对象
     */
    public static Article dto2Obj(ArticleEsDTO articleEsDTO) {
        if (Objects.isNull(articleEsDTO)) {
            return null;
        }
        Article article = new Article();
        BeanUtils.copyProperties(articleEsDTO, article);
        return article;
    }

}
