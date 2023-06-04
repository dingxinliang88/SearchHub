package com.juzi.searchhub.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.juzi.searchhub.common.StatusCode;
import com.juzi.searchhub.exception.BusinessException;
import com.juzi.searchhub.model.dto.QueryRequest;
import com.juzi.searchhub.model.entity.Picture;
import com.juzi.searchhub.service.PictureService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author codejuzi
 */
@Service
public class PictureServiceImpl implements PictureService {

    private static final String DEFAULT_SEARCH_TEXT = "study";

    @Override
    public Page<Picture> queryPictureByPage(QueryRequest queryRequest) {

        String searchText = queryRequest.getSearchText();
        long current = queryRequest.getCurrent();
        long pageSize = queryRequest.getPageSize();
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
