package com.daacoo.activiti.controller;

import com.daacoo.activiti.entity.ProcessDefinitionDto;
import com.daacoo.activiti.service.ActProcessService;
import com.daacoo.common.annotation.Log;
import com.daacoo.common.core.controller.BaseController;
import com.daacoo.common.core.entity.AjaxResult;
import com.daacoo.common.core.page.TableDataInfo;
import com.daacoo.common.enums.BusinessType;
import com.daacoo.common.utils.Encodes;
import com.daacoo.common.utils.StringUtils;
import com.daacoo.common.utils.WebUtil;
import org.activiti.engine.repository.Model;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 流程管理 操作处理
 * @author daacoo
 */
@Controller
@RequestMapping("/activiti/process")
public class ActProcessController extends BaseController {
    private String prefix = "activiti/process";

    @Autowired
    private ActProcessService actProcessService;

    @RequiresPermissions("activiti:process:view")
    @GetMapping
    public String process() {
        return prefix + "/process";
    }

    @RequiresPermissions("activiti:process:view")
    @RequestMapping("/myProcess")
    public String myProcess() {
        return prefix + "/processList";
    }

    @RequiresPermissions("activiti:process:list")
    @PostMapping("list")
    @ResponseBody
    public TableDataInfo list(ProcessDefinitionDto processDefinitionDto) {
        return actProcessService.selectProcessDefinitionList(processDefinitionDto);
    }

    @GetMapping("/add")
    public String add() {
        return prefix + "/add";
    }

    @RequiresPermissions("activiti:process:add")
    @Log(title = "流程管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(@RequestParam String category, @RequestParam("file") MultipartFile file)
            throws IOException {
        InputStream fileInputStream = file.getInputStream();
        String fileName = file.getOriginalFilename();
        return actProcessService.saveNameDeplove(fileInputStream, fileName, category);
    }

    @RequiresPermissions("activiti:process:model")
    @GetMapping(value = "/convertToModel/{processId}")
    @ResponseBody
    public AjaxResult convertToModel(@PathVariable("processId") String processId) {
        try {
            Model model = actProcessService.convertToModel(processId);
            return success(StringUtils.format("转换模型成功，模型编号[{}]", model.getId()));
        } catch (Exception e) {
            return error("转换模型失败" + e.getMessage());
        }
    }

    @GetMapping(value = "/resource/{imageName}/{deploymentId}")
    public void viewImage(@PathVariable("imageName") String imageName,
                          @PathVariable("deploymentId") String deploymentId, HttpServletResponse response) {
        try {
            InputStream in = actProcessService.findImageStream(deploymentId, Encodes.urlDecode(imageName));
            for (int bit = -1; (bit = in.read()) != -1; ) {
                response.getOutputStream().write(bit);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresPermissions("activiti:process:remove")
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(actProcessService.deleteProcessDefinitionByDeploymentIds(ids));
    }

    @GetMapping("/toRunningList")
    public String toRunningList() {
        return prefix + "/actProcessRunningList";
    }
    //=================================================================================================

    /**
     * 流程管理列表
     */
    @RequiresPermissions("activiti:process:list")
    @RequestMapping("/processList")
    @ResponseBody
    public TableDataInfo processList(HttpServletRequest request) {
        Map<String, Object> paramMap = WebUtil.paramsToMap(request.getParameterMap());
        paramMap.put("category", "");
        startPage();
        List<Map<String, Object>> mapList = actProcessService.processList(paramMap);
        return getDataTable(mapList);
    }

    /**
     * 运行中的实例列表
     */
    @RequiresPermissions("activiti:process:list")
    @RequestMapping("/runningList")
    @ResponseBody
    public TableDataInfo runningList(HttpServletRequest request) {
        Map<String, Object> paramMap = WebUtil.paramsToMap(request.getParameterMap());
        startPage();
        List<Map<String, Object>> mapList = actProcessService.runningList(paramMap);
        return getDataTable(mapList);
    }

    /**
     * 根据 流程实例ID 删除流程实例
     * procInsId 流程实例ID
     * reason 删除原因
     */
    @RequiresPermissions("activiti:process:edit")
    @RequestMapping("/deleteProcIns")
    @ResponseBody
    public AjaxResult deleteProcIns(HttpServletRequest request) {
        Map<String, Object> paramMap = WebUtil.paramsToMap(request.getParameterMap());
        return toAjax(actProcessService.deleteProcIns(paramMap));
    }

    /**
     * 挂起、激活流程实例
     * processId
     *
     * @return
     */
    @RequiresPermissions("activiti:process:edit")
    @RequestMapping("/updateState/{state}")
    @ResponseBody
    public AjaxResult updateState(@PathVariable("state") String state, HttpServletRequest request) {
        Map<String, Object> paramMap = WebUtil.paramsToMap(request.getParameterMap());
        try {
            actProcessService.updateState(state, paramMap);
            return success(StringUtils.format("操作成功"));
        } catch (Exception e) {
            return error("操作失败" + e.getMessage());
        }
    }
}
