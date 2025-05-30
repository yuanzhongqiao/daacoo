package com.daacoo.web.controller.oa;


import com.daacoo.common.annotation.Log;
import com.daacoo.common.core.controller.BaseController;
import com.daacoo.common.core.entity.AjaxResult;
import com.daacoo.common.core.entity.sys.SysUser;
import com.daacoo.common.core.page.TableDataInfo;
import com.daacoo.common.enums.BusinessType;
import com.daacoo.common.exception.ServiceException;
import com.daacoo.common.utils.ShiroUtils;
import com.daacoo.common.utils.StringUtils;
import com.daacoo.common.utils.WebUtil;
import com.daacoo.oa.entity.Leave;
import com.daacoo.oa.service.impl.LeaveServiceImpl;
import com.daacoo.system.service.ISysPostService;
import com.daacoo.system.service.ISysRoleService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 请假申请Controller
 * @author daacoo
 * @version 2019/9/27
 */
@Controller
@RequestMapping("/oa/leave")
public class LeaveController extends BaseController {

    private String prefix = "oa/";

    @Autowired
    private LeaveServiceImpl leaveService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysPostService postService;

    @RequiresPermissions("activiti:leave:list")
    @RequestMapping("/list")
    public String leaveList() {
        return prefix + "leaveList";
    }

    @RequiresPermissions("activiti:leave:list")
    @RequestMapping("/getLeaveList")
    @ResponseBody
    public TableDataInfo getLeaveList(HttpServletRequest request) {
        startPage();
        Map<String, Object> paramMap = WebUtil.paramsToMap(request.getParameterMap());
        List<Leave> list = leaveService.getLeaveList(paramMap);
        return getDataTable(list);
    }

    @RequiresPermissions("activiti:leave:add")
    @RequestMapping("/add")
    public String add(ModelMap mmap) {
        SysUser user = ShiroUtils.getSysUser();
        mmap.put("posts", postService.selectPostAll());
        mmap.put("user", user);
        return prefix + "leaveAdd";
    }

    @Log(title = "请假申请管理-新增", businessType = BusinessType.INSERT)
    @RequestMapping("/addLeave")
    @ResponseBody
    public AjaxResult addLeave(HttpServletRequest request) {
        Map<String, Object> paramMap = WebUtil.paramsToMap(request.getParameterMap());
        return toAjax(leaveService.addLeave(paramMap));

    }

    @Log(title = "请假申请管理-重新申请、销假", businessType = BusinessType.INSERT)
    @RequestMapping("/editLeave")
    @ResponseBody
    public AjaxResult editLeave(HttpServletRequest request) {
        Map<String, Object> paramMap = WebUtil.paramsToMap(request.getParameterMap());
        return toAjax(leaveService.editLeave(paramMap));

    }

    /**
     * 根据 ID 删除请假信息
     *
     * @param request
     * @return
     */
    @Log(title = "删除请假信息", businessType = BusinessType.UPDATE)
    @RequiresPermissions("activiti:leave:remove")
    @RequestMapping("/remove")
    @ResponseBody
    public AjaxResult updateLeaveInFoByIds(HttpServletRequest request) {
        Map<String, Object> paramMap = WebUtil.paramsToMap(request.getParameterMap());
        return toAjax(leaveService.updateLeaveInFoToArray(paramMap));
    }

    /**
     * 表单跳转
     *
     * @return
     */
    @RequestMapping("/form")
    public String form(Leave leave, Model model) {
        String view = "leaveAuditForm";
        Leave lv = new Leave();
        if (StringUtils.isNotBlank(leave.getId())) {

            // 环节编号
            String taskDefKey = leave.getAct().getTaskDefKey();

            lv = leaveService.getLeaveById(leave);
            if(lv == null){
                throw new ServiceException(String.format("请假id：%1$s 不存在或已删除，请联系管理员", leave.getId()));
            }

            // 请假详情
            if (leave.getAct().isFinishTask()) {
                view = "leaveAuditView";
            }// 修改环节
            else if ("modifyApply".equals(taskDefKey)) {
                lv.setAct(leave.getAct());
                view = "leaveAuditForm";
            }// 部门经理审核环节
            else if ("deptAudit".equals(taskDefKey)) {
                lv.setAct(leave.getAct());
                view = "leaveAudit";
            }
            // HR审核环节
            else if ("HRAudit".equals(taskDefKey)) {
                lv.setAct(leave.getAct());
                view = "leaveAudit";
            }
            // 总经理审核环节
            else if ("ceoAudit".equals(taskDefKey)) {
                lv.setAct(leave.getAct());
                view = "leaveAudit";
            }
            // 销假审核环节
            else if ("xiaojia".equals(taskDefKey)) {
                lv.setAct(leave.getAct());
                view = "leaveAudit";
            }
        }
        model.addAttribute("leave", lv);
        return prefix + view;
    }

    /**
     * 工单执行（完成任务）
     *
     * @param leave
     * @return
     */
    @RequestMapping("/saveLeaveAudit")
    @ResponseBody
    public AjaxResult saveAudit(Leave leave) {
        if (!"xiaojia".equals(leave.getAct().getTaskDefKey())) {
            if (StringUtils.isBlank(leave.getAct().getFlag()) || StringUtils.isBlank(leave.getAct().getComment())) {
                return AjaxResult.error(StringUtils.format("请检查任务流转标识是否正确或者审批意见是否为空。"));
            }
        }
        return AjaxResult.success(leaveService.auditSave(leave));
    }

}
