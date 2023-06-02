package com.juzi.searchhub.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.juzi.searchhub.model.entity.Article;
import com.juzi.searchhub.service.ArticleService;
import com.juzi.searchhub.mapper.ArticleMapper;
import org.springframework.stereotype.Service;

/**
* @author codejuzi
* @description 针对表【article(文章)】的数据库操作Service实现
* @createDate 2023-06-02 19:27:00
*/
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
    implements ArticleService{

}




