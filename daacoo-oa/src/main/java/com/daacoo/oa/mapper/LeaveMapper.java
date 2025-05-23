package com.daacoo.oa.mapper;

import com.daacoo.oa.entity.Leave;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


/**
 * OA系统  数据层
 *
 * @author daacoo
 * @version 2019/9/27
 */
@Mapper
@Component
public interface LeaveMapper {
    /**
     * 获取请假列表
     *
     * @param paramMap
     * @return
     */
    public List<Leave> getLeaveList(Map<String, Object> paramMap);

    /**
     * 新增请假
     *
     * @param paramMap
     */
    public int insert(Map<String, Object> paramMap);

    /**
     * 更新编辑请假信息
     *
     * @param paramMap
     */
    public int update(Map<String, Object> paramMap);

    /**
     * 根据id删除请假信息
     *
     * @param paramsToMap
     */
    public int updateLeaveInFoByIds(Map paramsToMap);

    /**
     * 根据id删除请假信息
     *
     * @param idList
     */
    public int updateLeaveInFoToList(List<String> idList);

    /**
     * 根据id删除请假信息
     *
     * @param idArray
     */
    public int updateLeaveInFoToArray(String[] idArray);

    /**
     * 根据id查询请假信息
     *
     * @return
     */
    public Leave getLeaveById(Leave leave);

    /**
     * 更新保存部门经理意见
     *
     * @param leave
     */
    public void updateDeptText(Leave leave);

    /**
     * 更新保存HR意见
     *
     * @param leave
     */
    public void updateHrText(Leave leave);

    /**
     * 更新保存总经理意见
     *
     * @param leave
     */
    public void updateZjlText(Leave leave);

}
