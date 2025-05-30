package com.daacoo.cms.service.impl;


import com.google.common.collect.Maps;
import com.daacoo.cms.entity.Article;
import com.daacoo.cms.mapper.ArticleMapper;
import com.daacoo.cms.service.IArticleService;
import com.daacoo.cms.service.ISiteService;
import com.daacoo.common.utils.ShiroUtils;
import com.daacoo.common.utils.uuid.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 文章 业务层处理
 * @author daacoo
 * @date 2019/12/18
 */
@Service
public class ArticleServiceImpl implements IArticleService {
    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ISiteService siteService;

    @Override
    public List<Article> selectArticleList(Map<String, Object> paramMap) {
        return articleMapper.selectArticleList(paramMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addArticle(Map<String, Object> paramMap) {
        String articleId = UUID.fastUUID().toString(true);
        paramMap.put("id", articleId);
        paramMap.put("siteId", siteService.selectOneSite().getId());
        paramMap.put("createBy", ShiroUtils.getLoginName());
        paramMap.put("updateBy", ShiroUtils.getLoginName());

        //保存文章内容关联表数据
        Map<String, Object> contentParamMap = Maps.newHashMap();
        contentParamMap.put("id", UUID.fastUUID().toString(true));
        contentParamMap.put("articleId", articleId);
        contentParamMap.put("content", paramMap.get("content"));
        articleMapper.addArticleContent(contentParamMap);

        return articleMapper.addArticle(paramMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteArticle(String[] ids) {
        //删除文章内容关联表数据
        articleMapper.deleteArticleContentByArticleIds(ids);
        return articleMapper.deleteArticle(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateArticle(Map<String, Object> paramMap) {
        //修改文章内容关联表数据
        Map<String, Object> contentParamMap = Maps.newHashMap();
        contentParamMap.put("articleId", paramMap.get("id"));
        contentParamMap.put("content", paramMap.get("content"));
        articleMapper.updateArticleContent(contentParamMap);

        paramMap.put("updateBy", ShiroUtils.getLoginName());
        return articleMapper.updateArticle(paramMap);
    }
}
