package com.daacoo.cms.service.impl;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.daacoo.cms.entity.Article;
import com.daacoo.cms.entity.Site;
import com.daacoo.cms.mapper.AdvertisingMapper;
import com.daacoo.cms.mapper.ArticleMapper;
import com.daacoo.cms.mapper.ColumnMapper;
import com.daacoo.cms.mapper.SiteMapper;
import com.daacoo.cms.service.ISiteService;
import com.daacoo.common.utils.ShiroUtils;
import com.daacoo.common.utils.uuid.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 站点管理 业务层处理
 * @Author daacoo
 * @Date 2021/1/5
 */
@Service
public class SiteServiceImpl implements ISiteService {
    @Autowired
    private SiteMapper siteMapper;

    @Autowired
    private ColumnMapper columnMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private AdvertisingMapper advertisingMapper;

    @Override
    public List<Site> selectSiteList(Map<String, Object> paramMap) {
        return siteMapper.selectSiteList(paramMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSite(Map<String, Object> paramMap) {
        String siteId = UUID.fastUUID().toString(true);
        paramMap.put("id", siteId);
        paramMap.put("createBy", ShiroUtils.getLoginName());
        paramMap.put("updateBy", ShiroUtils.getLoginName());

        //每新增一个站点，栏目表都插入一条作为树形结构的顶级栏目
        Map<String, Object> columnMenuRoot = Maps.newHashMap();
        columnMenuRoot.put("id", UUID.fastUUID().toString(true));
        columnMenuRoot.put("parentId", 0);//默认栏目Root的父id为0
        columnMenuRoot.put("siteId", siteId);//站点id
        columnMenuRoot.put("name", "栏目");//顶级栏目名称
        columnMenuRoot.put("columnFlag", "");//栏目标识
        columnMenuRoot.put("outLink", 1);//是否外部链接页面（0：是    1：否），默认：1
        columnMenuRoot.put("href", "");//外部链接的访问路径
        columnMenuRoot.put("display", 0);//0:显示，1：隐藏
        columnMenuRoot.put("status", 0);//0：正常  1：删除
        columnMenuRoot.put("sort", 0);//排序：默认0
        columnMenuRoot.put("createBy", ShiroUtils.getLoginName());
        columnMenuRoot.put("remark", "顶级栏目，非专业管理员不要修改");
        columnMapper.insertColumnMenu(columnMenuRoot);

        return siteMapper.insertSite(paramMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSite(Map<String, Object> paramMap) {
        paramMap.put("updateBy", ShiroUtils.getLoginName());
        return siteMapper.updateSite(paramMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSite(String[] ids) {
        //1、删除站点之前，先删除该站点的所有栏目数据
        columnMapper.deleteColumnsBySiteIds(ids);

        //2、查询该站点的文章数据列表
        List<Article> articleList = articleMapper.selectArticleListBySiteIds(ids);
        List<String> articleIds = Lists.newArrayList();
        if(!CollectionUtils.isEmpty(articleList)){
            articleIds.addAll(articleList.stream().map(Article::getId).collect(Collectors.toList()));
        }
        //2.1、根据文章id删除该文章对应的数据
        if(!CollectionUtils.isEmpty(articleIds)){
            articleMapper.deleteArticleContentByArticleIds(articleIds.stream().toArray(String[]::new));
        }

        //3、删除文章
        articleMapper.deleteArticleBySiteIds(ids);

        //4、删除广告数据
        advertisingMapper.deleteAdvertisingBySiteIds(ids);
        return siteMapper.deleteSite(ids);
    }

    @Override
    public Site selectOneSite() {
        Map<String, Object> siteParamMap = Maps.newHashMap();
        siteParamMap.put("status", "0");//0：显示的
        List<Site> sites = siteMapper.selectSiteList(siteParamMap);
        Site site = CollectionUtils.isEmpty(sites) ? new Site() : sites.get(0);
        return site;
    }
}
