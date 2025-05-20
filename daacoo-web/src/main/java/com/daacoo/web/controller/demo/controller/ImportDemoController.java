package com.daacoo.web.controller.demo.controller;

import com.alibaba.excel.EasyExcel;
import com.daacoo.common.annotation.Log;
import com.daacoo.common.core.controller.BaseController;
import com.daacoo.common.core.entity.AjaxResult;
import com.daacoo.common.core.entity.sys.SysUser;
import com.daacoo.common.core.page.TableDataInfo;
import com.daacoo.common.enums.BusinessType;
import com.daacoo.common.utils.poi.ExcelUtil;
import com.daacoo.demo.entity.ImportSysUser;
import com.daacoo.demo.excel.template.ImportSysUserTemplate;
import com.daacoo.demo.mapper.ImportDemoMapper;
import com.daacoo.demo.service.impl.ImportDemoServiceImpl;
import com.daacoo.web.controller.demo.excel.EasyExcelListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Description 大数据（百万级）导入Demo
 * @Author daacoo
 * @Date 2021/6/17
 */
@Controller
@RequestMapping("/demo/import")
public class ImportDemoController extends BaseController {
    private String prefix = "demo/import";

    @Autowired
    private ImportDemoServiceImpl importDemoService;
    @Autowired
    private ImportDemoMapper importDemoMapper;

    @RequestMapping("/index")
    public String importIndex() {
        return prefix + "/importIndex";
    }

    /**
     * 查询数据
     */
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(ImportSysUser user) {
        startPage();
        List<ImportSysUser> list = importDemoService.selectUserList(user);
        return getDataTable(list);
    }

    @Log(title = "导入Demo模板下载")
    @GetMapping("/importTemplate")
    @ResponseBody
    public AjaxResult importTemplate() {
        ExcelUtil<ImportSysUser> util = new ExcelUtil<ImportSysUser>(ImportSysUser.class);
        return util.importTemplateExcel("导入Demo模板");
    }

    @Log(title = "导入Demo数据", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    @ResponseBody
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        EasyExcel.read(file.getInputStream(), ImportSysUserTemplate.class, new EasyExcelListener(importDemoMapper)).sheet().doRead();
        return AjaxResult.success(true);
    }

    @Log(title = "导出Demo数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(SysUser user) {
        List importSysUsers = importDemoMapper.selectUserList(new ImportSysUser());
        ExcelUtil<ImportSysUser> util = new ExcelUtil<ImportSysUser>(ImportSysUser.class);
        return util.exportExcel(importSysUsers, "测试导出用户数据");
    }

    @Log(title = "清空Demo数据", businessType = BusinessType.CLEAN)
    @PostMapping("/clean")
    @ResponseBody
    public AjaxResult clean() {
        importDemoMapper.clean();
        return success();
    }
}
