package com.daacoo.oa.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.daacoo.activiti.persistence.ActEntity;
import com.daacoo.common.core.entity.sys.SysDept;
import com.daacoo.common.core.entity.sys.SysUser;

import java.util.Date;

/**
 * @Description 请假实体
 * @Author daacoo
 * @Date 2019/9/27
 **/
public class Leave extends ActEntity<Leave> {
    /**
     * 归属用户
     **/
    private SysUser user;
    /**
     * 归属部门
     **/
    private SysDept dept;
    /**
     * /岗位
     **/
    private String post;
    /**
     * 请假原因
     **/
    private String content;
    /**
     * 请假开始日期
     **/
    private Date startTime;
    /**
     * 请假结束日期
     **/
    private Date endTime;
    /**
     * 请假类型
     **/
    private String leaveType;
    /**
     * 执行时间
     **/
    private String exeDate;
    /**
     * 部门经理意见
     **/
    private String deptText;
    /**
     * HR意见
     **/
    private String hrText;
    /**
     * 总经理意见
     **/
    private String zjlText;

    public SysUser getUser() {
        return user;
    }

    public void setUser(SysUser user) {
        this.user = user;
    }

    public SysDept getDept() {
        return dept;
    }

    public void setDept(SysDept dept) {
        this.dept = dept;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public String getExeDate() {
        return exeDate;
    }

    public void setExeDate(String exeDate) {
        this.exeDate = exeDate;
    }

    public String getDeptText() {
        return deptText;
    }

    public void setDeptText(String deptText) {
        this.deptText = deptText;
    }

    public String getHrText() {
        return hrText;
    }

    public void setHrText(String hrText) {
        this.hrText = hrText;
    }

    public String getZjlText() {
        return zjlText;
    }

    public void setZjlText(String zjlText) {
        this.zjlText = zjlText;
    }

}
