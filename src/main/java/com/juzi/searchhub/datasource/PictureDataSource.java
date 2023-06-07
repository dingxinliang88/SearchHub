package com.juzi.searchhub.datasource;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.juzi.searchhub.common.StatusCode;
import com.juzi.searchhub.exception.BusinessException;
import com.juzi.searchhub.model.vo.Picture;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 适配器——图片
 *
 * @author codejuzi
 */
@Slf4j
@Component
public class PictureDataSource implements DataSource<Picture> {

    private static final String DEFAULT_SEARCH_TEXT = "努力";

    @Resource
    private Retryer<Page<Picture>> pictureRetryer;

    @Override
    public Page<Picture> doSearch(String searchText, long current, long pageSize) {

        try {
            return pictureRetryer.call(() -> this.searchPicture(searchText, current, pageSize));
        } catch (ExecutionException | RetryException e) {
            log.error("获取图片重试失败， ", e);
            throw new BusinessException(StatusCode.INTERNAL_SERVER_ERROR, "获取失败");
        }

    }

    private Page<Picture> searchPicture(String searchText, long current, long pageSize) {
        if (StrUtil.isBlank(searchText)) {
            searchText = DEFAULT_SEARCH_TEXT;
        }

        String url = String.format("https://www.bing.com/images/search?q=%s&first=%s", searchText, current);
        Document doc;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "获取图片异常");
        }
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
            if (pictureList.size() >= pageSize) {
                break;
            }
        }
        Page<Picture> picturePage = new Page<>(current, pageSize, pictureList.size());
        picturePage.setRecords(pictureList);
        return picturePage;
    }
}
