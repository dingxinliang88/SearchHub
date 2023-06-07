package com.juzi.searchhub.datasource;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.juzi.searchhub.common.StatusCode;
import com.juzi.searchhub.exception.BusinessException;
import com.juzi.searchhub.model.vo.BiliVideoVO;
import com.juzi.searchhub.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * B站视频数据源
 *
 * @author codejuzi
 */
@Slf4j
@Component
public class BiliVideoDataSource implements DataSource<BiliVideoVO> {

    private static final String COOKIE_KEY = "buvid3";

    private static final String BILI_INDEX_URL
            = "https://www.bilibili.com/";

    private static final String FETCH_BILI_VIDEO_URL
            = "https://api.bilibili.com/x/web-interface/search/type?search_type=video&keyword=%s";

    @Resource
    private Retryer<String> retryer;

    @Override
    public Page<BiliVideoVO> doSearch(String searchText, long current, long pageSize) {
        if (StrUtil.isBlank(searchText)) {
            searchText = "努力";
        }
        String url = String.format(FETCH_BILI_VIDEO_URL, searchText);
        HttpCookie biliCookie = getBiliCookie();
        String resBody;
        try {
            resBody = retryer.call(() -> HttpRequest.get(url)
                    .cookie(biliCookie)
                    .execute().body());
        } catch (ExecutionException | RetryException e) {
            log.error("获取视频重试失败, ", e);
            throw new BusinessException(StatusCode.INTERNAL_SERVER_ERROR, "获取失败");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> biliVideoMap = JSONUtil.toBean(resBody, Map.class);
        // 解析数据
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) biliVideoMap.get("data");
        JSONArray videoDataList = (JSONArray) dataMap.get("result");

        // 封装数据
        List<BiliVideoVO> biliVideoVOList = new ArrayList<>();

        for (Object o : videoDataList) {
            JSONObject tmpVideoData = (JSONObject) o;
            BiliVideoVO biliVideoVO = new BiliVideoVO();
            biliVideoVO.setAuthor(tmpVideoData.getStr("author"));
            biliVideoVO.setArcurl(tmpVideoData.getStr("arcurl"));
            biliVideoVO.setPic(tmpVideoData.getStr("pic"));
            biliVideoVO.setTypename(tmpVideoData.getStr("typename"));

            biliVideoVOList.add(biliVideoVO);
            if (biliVideoVOList.size() >= pageSize) {
                break;
            }
        }

        Page<BiliVideoVO> biliVideoVOPage = new Page<>(current, pageSize, biliVideoVOList.size());
        biliVideoVOPage.setRecords(biliVideoVOList);

        return biliVideoVOPage;
    }

    /**
     * 获取b站搜索视频请求Cookie
     *
     * @return http cookie
     */
    private HttpCookie getBiliCookie() {
        HttpResponse response = HttpRequest.get(BILI_INDEX_URL).execute();
        HttpCookie cookie = response.getCookie(COOKIE_KEY);
        ThrowUtils.throwIf(Objects.isNull(cookie) || StrUtil.isBlank(cookie.getValue())
                , StatusCode.INTERNAL_SERVER_ERROR, "获取Cookie失败");
        return cookie;
    }
}
