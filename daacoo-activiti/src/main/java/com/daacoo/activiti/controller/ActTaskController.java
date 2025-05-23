package com.daacoo.activiti.controller;

import com.daacoo.activiti.constant.ActConstant;
import com.daacoo.activiti.entity.Act;
import com.daacoo.activiti.service.impl.ActTaskService;
import com.daacoo.activiti.util.ActUtils;
import com.daacoo.common.core.controller.BaseController;
import com.daacoo.common.core.entity.AjaxResult;
import com.daacoo.common.core.page.TableDataInfo;
import com.daacoo.common.utils.StringUtils;
import com.daacoo.common.utils.WebUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 流程个人任务相关Controller
 * @author daacoo
 * @version 2019/9/25
 */
@Controller
@RequestMapping("/activiti/task")
public class ActTaskController extends BaseController {

    private String prefix = "activiti/task";

    @Autowired
    private ActTaskService actTaskService;

    /**
     * 我的任务
     *
     * @return
     */
    @RequestMapping("/todo")
    public String todo() {
        return prefix + "/actTaskTodoList";
    }

    /**
     * 我的任务 列表
     *
     * @param request
     * @return
     */
    @RequestMapping("/todoList")
    @ResponseBody
    public TableDataInfo todoList(HttpServletRequest request) {
        Map<String, Object> paramMap = WebUtil.paramsToMap(request.getParameterMap());
        List<Map<String, Object>> list = actTaskService.todoList(paramMap);
        return getDataTable(list);
    }

    /**
     * 获取流转历史列表
     * procInsId 流程实例
     * startAct 开始活动节点名称
     * endAct 结束活动节点名称
     */
    @RequestMapping("/histoicFlow")
    @ResponseBody
    public TableDataInfo histoicFlow(HttpServletRequest request) {
        Map<String, Object> paramMap = WebUtil.paramsToMap(request.getParameterMap());
        List<Map<String, Object>> list = null;
        /*请假详情-历史流转信息*/
        if (paramMap.containsKey(ActConstant.PROC_INS_ID)) {
            list = actTaskService.histoicFlowList(paramMap);
        }
        /*任务办理-历史流转信息*/
        if(paramMap.containsKey(ActConstant.ACT_PROC_INS_ID)){
            paramMap.put("procInsId",paramMap.get("act.procInsId"));
            list = actTaskService.histoicFlowList(paramMap);
        }
        return getDataTable(list);
    }

    @RequestMapping("/form")
    public String form(Act act) {
        // 获取流程XML上的表单KEY
        String formKey = actTaskService.getFormKey(act.getProcDefId(), act.getTaskDefKey());

        // 获取流程实例对象
        if (act.getProcInsId() != null) {
            act.setProcIns(actTaskService.getProcIns(act.getProcInsId()));
        }
        return "redirect:" + ActUtils.getFormUrl(formKey, act);
    }

    /**
     * 签收任务
     * taskId 任务ID
     */
    @RequestMapping("/claim")
    @ResponseBody
    public AjaxResult claim(HttpServletRequest request) {
        Map<String, Object> paramMap = WebUtil.paramsToMap(request.getParameterMap());
        try {
            actTaskService.claim(paramMap);
            return success(StringUtils.format("操作成功"));
        } catch (Exception e) {
            return error("操作失败" + e.getMessage());
        }
    }

    /**
     * 删除任务
     * taskId 流程实例ID
     * reason 删除原因
     */
    @RequiresPermissions("activiti:process:edit")
    @RequestMapping("/remove")
    public AjaxResult deleteTask(HttpServletRequest request) {
        Map<String, Object> paramMap = WebUtil.paramsToMap(request.getParameterMap());
        try {
            actTaskService.deleteTask(paramMap);
            return success(StringUtils.format("操作成功"));
        } catch (Exception e) {
            return error("操作失败" + e.getMessage());
        }
    }

}
