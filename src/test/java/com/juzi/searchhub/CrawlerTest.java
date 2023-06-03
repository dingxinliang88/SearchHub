package com.juzi.searchhub;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.juzi.searchhub.model.entity.Article;
import com.juzi.searchhub.service.ArticleService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 爬虫测试
 *
 * @author codejuzi
 */
@SpringBootTest
public class CrawlerTest {

    @Resource
    private ArticleService articleService;

    @Test
    void testFetchArticle() {
        // 1、获取数据
        String requestBody = "{\n" +
                "  \"current\": 2,\n" +
                "  \"pageSize\": 15,\n" +
                "  \"sortField\": \"thumbNum\",\n" +
                "  \"sortOrder\": \"descend\",\n" +
                "  \"category\": \"文章\",\n" +
                "  \"reviewStatus\": 1\n" +
                "}";
        String url = "https://www.code-nav.cn/api/post/search/page/vo";
        String originJson = HttpRequest
                .post(url)
                .body(requestBody)
                .execute()
                .body();
        // 2、解析数据
        @SuppressWarnings("unchecked")
        Map<String, Object> resMap = JSONUtil.toBean(originJson, Map.class);
        JSONObject data = (JSONObject) resMap.get("data");
        JSONArray records = (JSONArray) data.get("records");
        List<Article> articleList = new ArrayList<>(records.size());
        int count = 1;
        for (Object record : records) {
            JSONObject tmpRecord = (JSONObject) record;
            // 取值
            String content = tmpRecord.getStr("content");
            if (StrUtil.isBlank(content)
                    || "<a href=\"https://www.code-nav.cn/vip\" target=\"_blank\">成为会员解锁所有内容</a>".equals(content)) {
                continue;
            }
            String title = tmpRecord.getStr("title");
            if (StrUtil.isBlank(title)) {
                title = "Article_" + count++;
            }
            Article article = new Article();
            article.setTitle(title);
            article.setContent(content);
            articleList.add(article);
        }
        // 3、数据入库
        boolean saveRes = articleService.saveBatch(articleList);
        Assertions.assertTrue(saveRes);
    }
}
