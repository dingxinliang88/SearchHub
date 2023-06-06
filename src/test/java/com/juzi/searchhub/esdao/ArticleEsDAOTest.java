package com.juzi.searchhub.esdao;

import com.juzi.searchhub.model.dto.article.ArticleEsDTO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;
import java.util.Optional;


/**
 * @author codejuzi
 */
@SpringBootTest
class ArticleEsDAOTest {

    @Resource
    private ArticleEsDAO articleEsDAO;

    @Test
    void testAdd2Es() {
        ArticleEsDTO articleEsDTO = new ArticleEsDTO();
        articleEsDTO.setId(100L);
        articleEsDTO.setTitle("测试文章");
        articleEsDTO.setContent("测试文章的内容《CodeJuzi》");
        articleEsDTO.setCreateTime(new Date());
        articleEsDTO.setIsDelete(0);

        articleEsDAO.save(articleEsDTO);

        System.out.println(articleEsDTO.getId());
    }

    @Test
    void testEsSelect() {
        long count = articleEsDAO.count();
        System.out.println("count = " + count);

        Page<ArticleEsDTO> articleEsDTOPage =
                articleEsDAO.findAll(
                        PageRequest.of(0, 5, Sort.by("createTime"))
                );
        List<ArticleEsDTO> content = articleEsDTOPage.getContent();
        System.out.println("content = " + content);

        Optional<ArticleEsDTO> dao = articleEsDAO.findById(100L);
        System.out.println("dao = " + dao);
    }

    @Test
    void testEsFindByTitle() {
        String title = "测试";
        List<ArticleEsDTO> articleEsDTOList = articleEsDAO.findByTitle(title);
        System.out.println("articleEsDTOList = " + articleEsDTOList);
    }
}