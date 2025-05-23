package com.daacoo.web.controller.cms;


import com.daacoo.cms.entity.Article;
import com.daacoo.cms.entity.Column;
import com.daacoo.cms.service.IColumnService;
import com.daacoo.cms.service.ISiteService;
import com.daacoo.cms.service.impl.ArticleServiceImpl;
import com.daacoo.common.annotation.Log;
import com.daacoo.common.core.controller.BaseController;
import com.daacoo.common.core.entity.AjaxResult;
import com.daacoo.common.core.page.TableDataInfo;
import com.daacoo.common.core.text.Convert;
import com.daacoo.common.enums.BusinessType;
import com.daacoo.common.utils.WebUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 文章Controller
 * @author daacoo
 * @date 2019/12/3
 */
@Controller
@RequestMapping("/cms/article")
public class ArticleController extends BaseController {
    private String prefix = "cms/article";

    @Autowired
    private ArticleServiceImpl articleService;

    @Autowired
    private IColumnService columnService;

    @Autowired
    private ISiteService siteService;

    @GetMapping
    public String articleList() {
        return prefix + "/articleIndex";
    }

    @RequestMapping("/list")
    @ResponseBody
    public TableDataInfo selectArticleList(HttpServletRequest request) {
        Map<String, Object> paramMap = WebUtil.paramsToMap(request.getParameterMap());
        paramMap.put("siteId", siteService.selectOneSite().getId());
        startPage();
        List<Article> list = articleService.selectArticleList(paramMap);
        return getDataTable(list);
    }

    @GetMapping("/selectColumnTree")
    public String selectColumnTree(HttpServletRequest request, Model model) {
        Map<String, Object> paramMap = WebUtil.paramsToMap(request.getParameterMap());
        paramMap.put("siteId", siteService.selectOneSite().getId());
        List<Column> columns = columnService.selectColumnList(paramMap);
        model.addAttribute("column", columns.get(0));
        return prefix + "/columnTree";
    }

    @RequestMapping("/columnTreeData")
    @ResponseBody
    public List<Column> columnTreeData(HttpServletRequest request) {
        Map<String, Object> paramMap = WebUtil.paramsToMap(request.getParameterMap());
        paramMap.put("siteId", siteService.selectOneSite().getId());
        List<Column> columns = columnService.selectColumnList(paramMap);
        return columns;
    }

    @GetMapping("/add")
    public String add(HttpServletRequest request, Model model) {
        Map<String, Object> paramMap = WebUtil.paramsToMap(request.getParameterMap());
        paramMap.put("siteId", siteService.selectOneSite().getId());
        paramMap.put("columnId", paramMap.get("id"));
        List<Column> columns = columnService.selectColumnList(paramMap);
        model.addAttribute("column", columns.get(0));
        return prefix + "/addArticle";
    }

    /**
     * 新增文章
     */
    @Log(title = "CMS-文章管理-新增", businessType = BusinessType.INSERT)
    @RequestMapping("/addArticle")
    @ResponseBody
    public AjaxResult addArticle(HttpServletRequest request) {
        Map<String, Object> paramMap = WebUtil.paramsToMap(request.getParameterMap());
        return toAjax(articleService.addArticle(paramMap));
    }

    @RequestMapping("/edit")
    public String edit(HttpServletRequest request, Model model) {
        Map<String, Object> paramMap = WebUtil.paramsToMap(request.getParameterMap());
        List<Article> list = articleService.selectArticleList(paramMap);
        if (!CollectionUtils.isEmpty(list)) {
            model.addAttribute("article", list.get(0));
        }
        return prefix + "/editArticle";
    }

    /**
     * 修改文章
     */
    @Log(title = "CMS-文章管理-编辑", businessType = BusinessType.UPDATE)
    @PostMapping("/editArticle")
    @ResponseBody
    public AjaxResult editArticle(HttpServletRequest request) {
        Map<String, Object> paramMap = WebUtil.paramsToMap(request.getParameterMap());
        return toAjax(articleService.updateArticle(paramMap));
    }

    /**
     * 删除文章
     */
    @Log(title = "CMS-文章管理-删除", businessType = BusinessType.DELETE)
    @RequestMapping("/remove")
    @ResponseBody
    public AjaxResult deleteArticle(String ids) {
        return toAjax(articleService.deleteArticle(Convert.toStrArray(ids)));
    }
}
