package com.juzi.searchhub;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.juzi.searchhub.model.entity.Article;
import com.juzi.searchhub.model.vo.Picture;
import com.juzi.searchhub.model.vo.NewsVO;
import com.juzi.searchhub.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.juzi.searchhub.constant.CommonConstant.ONCE_MAX_PAGE_SIZE;

/**
 * 爬虫测试
 *
 * @author codejuzi
 */
@Slf4j
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

    @Test
    void testFetchPicture() throws IOException {
        String searchText = "小黑子";
        int current = 1;
        String url = String.format("https://www.bing.com/images/search?q=%s&first=%s", searchText, current);
        Document doc = Jsoup.connect(url).get();
        Elements imgElements = doc.select(".iuscp.isv");
        List<Picture> pictureList = new ArrayList<>();
        for (Element element : imgElements) {
            // 取出图片的url
            String imgJson = element.select(".iusc").get(0).attr("m");
            @SuppressWarnings("unchecked")
            Map<String, Object> imgJsonMap = JSONUtil.toBean(imgJson, Map.class);
            String imgUrl = (String) imgJsonMap.get("murl");
            // 取出图片的title
            String imgTitle = element.select(".inflnk").get(0).attr("aria-label");

            Picture picture = new Picture();
            picture.setUrl(imgUrl);
            picture.setTitle(imgTitle);
            pictureList.add(picture);
        }
        System.out.println(pictureList);
    }

    @Test
    void testFetchNews() {
        String fetchNewsUrl = "https://news.cctv.com/2019/07/gaiban/cmsdatainterface/page/news_1.jsonp?cb=news";
        String originRes = HttpRequest.get(fetchNewsUrl).execute().body();
        String patternStr = "^[^(]*?\\((.*?)\\)$";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(originRes);
        if (matcher.find()) {
            String originJson = matcher.group(1);
            // 解析数据
            @SuppressWarnings("unchecked")
            Map<String, Object> jsonMap = JSONUtil.toBean(originJson, Map.class);
            JSONObject data = (JSONObject) jsonMap.get("data");
            JSONArray newsArr = (JSONArray) data.get("list");
            List<NewsVO> newsVOList = new ArrayList<>((int) ONCE_MAX_PAGE_SIZE);
            for (Object news : newsArr) {
                JSONObject newsObj = (JSONObject) news;

                NewsVO newsVO = new NewsVO();
                newsVO.setTitle(newsObj.getStr("title"));
                newsVO.setUrl(newsObj.getStr("url"));
                newsVO.setImage(newsObj.getStr("image"));
                newsVO.setBrief(newsObj.getStr("brief"));
                newsVO.setFocusDate(newsObj.getStr("focus_date"));

                newsVOList.add(newsVO);

                if (newsVOList.size() >= ONCE_MAX_PAGE_SIZE) {
                    break;
                }
            }
            System.out.println(newsVOList);
        } else {
            log.error("获取新闻异常，响应信息：{}", originRes);
        }
    }
}
