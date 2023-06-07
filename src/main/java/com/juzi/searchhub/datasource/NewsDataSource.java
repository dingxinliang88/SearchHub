package com.juzi.searchhub.datasource;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.juzi.searchhub.common.StatusCode;
import com.juzi.searchhub.exception.BusinessException;
import com.juzi.searchhub.model.vo.NewsVO;
import com.juzi.searchhub.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author codejuzi
 */
@Slf4j
@Component
public class NewsDataSource implements DataSource<NewsVO> {

    private static final String FETCH_NEWS_URL
            = "https://news.cctv.com/2019/07/gaiban/cmsdatainterface/page/news_1.jsonp?cb=news";

    @Resource
    private Retryer<String> retryer;

    @Override
    public Page<NewsVO> doSearch(String searchText, long current, long pageSize) {
        String originRes;
        try {
            originRes = retryer.call(() -> HttpRequest.get(FETCH_NEWS_URL).execute().body());
        } catch (ExecutionException | RetryException e) {
            log.error("获取新闻重试失败, ", e);
            throw new BusinessException(StatusCode.INTERNAL_SERVER_ERROR, "获取失败");
        }


        String patternStr = "^[^(]*?\\((.*?)\\)$";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(originRes);

        ThrowUtils.throwIf(!matcher.find(), StatusCode.INTERNAL_SERVER_ERROR, "获取失败");

        // 提取出json
        String originJson = matcher.group(1);
        // 解析数据
        @SuppressWarnings("unchecked")
        Map<String, Object> jsonMap = JSONUtil.toBean(originJson, Map.class);
        JSONObject data = (JSONObject) jsonMap.get("data");
        JSONArray newsArr = (JSONArray) data.get("list");
        List<NewsVO> newsVOList = new ArrayList<>((int) pageSize);
        for (Object news : newsArr) {
            JSONObject newsObj = (JSONObject) news;

            NewsVO newsVO = new NewsVO();
            newsVO.setTitle(newsObj.getStr("title"));
            newsVO.setUrl(newsObj.getStr("url"));
            newsVO.setImage(newsObj.getStr("image"));
            newsVO.setBrief(newsObj.getStr("brief"));
            newsVO.setFocusDate(newsObj.getStr("focus_date"));

            newsVOList.add(newsVO);

            if (newsVOList.size() >= pageSize) {
                break;
            }
        }
        Page<NewsVO> newsVOPage = new Page<>(current, pageSize, newsVOList.size());
        newsVOPage.setRecords(newsVOList);
        return newsVOPage;
    }
}
