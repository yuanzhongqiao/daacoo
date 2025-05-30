package com.daacoo.oa.service.impl;

import com.daacoo.common.core.entity.Ztree;
import com.daacoo.common.core.entity.sys.SysDept;
import com.daacoo.common.core.entity.sys.SysUser;
import com.daacoo.common.core.text.Convert;
import com.daacoo.common.utils.ShiroUtils;
import com.daacoo.common.utils.StringUtils;
import com.daacoo.common.utils.uuid.UUID;
import com.daacoo.oa.entity.Notify;
import com.daacoo.oa.entity.NotifyRecord;
import com.daacoo.oa.mapper.NotifyMapper;
import com.daacoo.oa.mapper.NotifyRecordMapper;
import com.daacoo.oa.service.INotifyService;
import com.daacoo.system.entity.SysDeptUser;
import com.daacoo.system.mapper.SysDeptMapper;
import com.daacoo.system.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description 通知通告业务实现层
 * @Author daacoo
 * @Date 2020/12/19
 */
@Service
public class NotifyServiceImpl implements INotifyService {

    @Autowired
    private NotifyMapper notifyMapper;

    @Autowired
    private NotifyRecordMapper ntifyRecordMapper;

    @Autowired
    private SysDeptMapper deptMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public List<Notify> selectNotifyList(Notify notify) {
        return notifyMapper.selectNotifyList(notify);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertNotify(Notify notify) {
        String uuid = UUID.fastUUID().toString(true);
        notify.setId(uuid);
        notify.setCreateBy(ShiroUtils.getLoginName());
        notify.setUpdateBy(ShiroUtils.getLoginName());
        // 保存
        int i = notifyMapper.insertNotify(notify);

        //发布状态才保存到通知记录表
        if ("1".equals(notify.getStatus())) {
            // 更新发送接受人记录
            ntifyRecordMapper.deleteByNotifyId(Convert.toStrArray(notify.getId()));
            //获取接收人列表
            List<NotifyRecord> list = new ArrayList<>();
            List<String> userIds = notify.getUserIds();

            List<SysUser> sysUsers = sysUserMapper.selectUserList(new SysUser());
            List<String> userIdList = sysUsers.stream().map(SysUser::getUserId).collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(userIds)) {
                for (String userId:userIds) {
                    //系统存在此用户id，才添加到通知列表
                    if(userIdList.contains(userId)){
                        NotifyRecord notifyRecord = new NotifyRecord();
                        notifyRecord.setId(UUID.fastUUID().toString(true));
                        notifyRecord.setNotifyId(uuid);
                        notifyRecord.setUserId(userId);
                        list.add(notifyRecord);
                    }
                }
            }

            if (!CollectionUtils.isEmpty(list)) {
                //批量保存接收人id到通知记录表
                ntifyRecordMapper.insertAll(list);
            }
        }
        return i;
    }

    @Override
    public Notify selectNotifyById(Notify notify) {
        List<Notify> notifies = notifyMapper.selectNotifyList(notify);
        if (!CollectionUtils.isEmpty(notifies)) {
            Notify notifyEnitty = notifies.get(0);
            NotifyRecord notifyRecord = new NotifyRecord();
            notifyRecord.setNotifyId(notifyEnitty.getId());
            List<NotifyRecord> ntifyRecordList = ntifyRecordMapper.selectByNotify(notifyRecord);
            if (!CollectionUtils.isEmpty(ntifyRecordList)) {
                List<String> userIds = new ArrayList<>();
                ntifyRecordList.stream().forEach(i -> {
                    userIds.add(i.getUserId());
                });
                notifies.get(0).setUserIds(userIds);
            }
            return notifies.get(0);
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateNotify(Notify notify) {
        //更新人
        notify.setUpdateBy(ShiroUtils.getLoginName());

        //发布状态才保存到通知记录表
        if ("1".equals(notify.getStatus())) {
            //先删除原有的通告记录数据
            ntifyRecordMapper.deleteByNotifyId(Convert.toStrArray(notify.getId()));

            //再重新插入接收人
            List<NotifyRecord> list = new ArrayList<>();
            List<String> userIds = notify.getUserIds();

            List<SysUser> sysUsers = sysUserMapper.selectUserList(new SysUser());
            List<String> userIdList = sysUsers.stream().map(SysUser::getUserId).collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(userIds)) {
                for (String userId:userIds) {
                    //系统存在此用户id，才添加到通知列表
                    if(userIdList.contains(userId)){
                        NotifyRecord notifyRecord = new NotifyRecord();
                        notifyRecord.setId(UUID.fastUUID().toString(true));
                        notifyRecord.setNotifyId(notify.getId());
                        notifyRecord.setUserId(userId);
                        list.add(notifyRecord);
                    }
                }
            }

            if (!CollectionUtils.isEmpty(list)) {
                //批量保存接收人id到通知记录表
                ntifyRecordMapper.insertAll(list);
            }
        }
        return notifyMapper.updateNotify(notify);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteNotifyByIds(String ids) {
        //删除通告记录表数据
        ntifyRecordMapper.deleteByNotifyId(Convert.toStrArray(ids));
        return notifyMapper.deleteNotifyByIds(Convert.toStrArray(ids));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNotifyRecordByNotifyIdAndUserId(String notifyId, String userId) {
        NotifyRecord notifyRecord = new NotifyRecord();
        notifyRecord.setNotifyId(notifyId);
        notifyRecord.setUserId(userId);
        List<NotifyRecord> notifyRecords = ntifyRecordMapper.selectByNotify(notifyRecord);
        if (!CollectionUtils.isEmpty(notifyRecords)) {
            if ("0".equals(notifyRecords.get(0).getReadFlag())) {
                ntifyRecordMapper.updateNotifyRecordByNotifyIdAndUserId(notifyId, userId);
            }
        }
    }

    @Override
    public List<Ztree> deptUserData(Notify notify) {
        //用户id列表
        List<String> userIds = notify.getUserIds();

        //新增页面 默认无选中用户
        if(userIds.size()==1){
            if("undefined".equals(userIds.get(0))){
                userIds.clear();
            }
        }

        //1、获取部门用户树
        List<SysDept> deptTree = packagingDeptUserTree();

        //2、编辑、查看时的回显部门用户树逻辑
        if(StringUtils.isNotEmpty(userIds)){
            userIds = editDeptUserTreeView(userIds);
        }

        //拼接部门人员树结构
        List<Ztree> ztrees;
        if (!CollectionUtils.isEmpty(userIds)) {
            ztrees = initZtree(deptTree,userIds);
        } else {
            ztrees = initZtree(deptTree,null);
        }
        return ztrees;
    }

    /**
     * 编辑、查看时的回显部门用户树逻辑
     * @param userIds 需勾选的用户id
     * @return
     */
    private List<String> editDeptUserTreeView(List<String> userIds) {
        //筛选出需要勾选的用户部门数据
        List<SysUser> sysUsers = sysUserMapper.selectUserListByIdList(userIds);
        List<String> deptIds= new ArrayList<>();
        if(!CollectionUtils.isEmpty(sysUsers)){
            //获取用户对应的部门id
            deptIds = sysUsers.stream().map(SysUser::getDeptId).collect(Collectors.toList()).stream().distinct().collect(Collectors.toList());
        }

        //所有需要选中的部门
        List<String> three = new ArrayList<>();

        if(!CollectionUtils.isEmpty(deptIds)){
            three.addAll(deptIds);
            //第一次查询
            List<String> one = deptMapper.selectCheckedDeptByDeptId(deptIds)
                    //根据部门父id进行分组
                    .stream().collect(Collectors.groupingBy(SysDept::getParentId))
                    //获取分组后的key集合
                    .keySet()
                    //转换成list
                    .stream().collect(Collectors.toList());

            List<SysDept> sysDepts = deptMapper.selectCheckedDeptByDeptId(one);
            List<String> two = sysDepts.stream().map(SysDept::getParentId).collect(Collectors.toList());

            while (!CollectionUtils.isEmpty(two)){
                three.addAll(one);
                //清空旧的部门列表
                one.clear();
                //保存新的部门列表
                one.addAll(two);
                two = deptMapper.selectCheckedDeptByDeptId(one).stream().map(SysDept::getParentId).collect(Collectors.toList());
            }
        }

        if(!CollectionUtils.isEmpty(three)){
            userIds.addAll(three.stream().distinct().collect(Collectors.toList()));
        }

        return userIds;
    }

    /**
     * 封装部门用户树
     * @return
     */
    private List<SysDept> packagingDeptUserTree() {
        //1、查询所有部门下的用户
        List<SysDeptUser> sysDeptUserList = deptMapper.selectDeptUser();
        //用户分组
        Map<String, List<SysDeptUser>> sysDeptUserCollect = new HashMap<>();
        if(!CollectionUtils.isEmpty(sysDeptUserList)){
            //根据部门id进行用户分组
            sysDeptUserCollect = sysDeptUserList.stream().collect(Collectors.groupingBy(SysDeptUser::getDeptId));
        }

        //2、查询所有部门
        List<SysDept> deptList = deptMapper.selectDeptAll();

        // 封装部门下的用户列表
        List<SysDept> childrenDept = new ArrayList<>();
        for (SysDept dept:deptList) {
            //如果该部门中存在用户
            if(sysDeptUserCollect.containsKey(dept.getDeptId())){
                //获取该部门中的用户列表
                List<SysDeptUser> deptUsers = sysDeptUserCollect.get(dept.getDeptId());
                for (SysDeptUser deptUser:deptUsers) {
                    // 把该部门下的用户封装成部门对象，以便形成需要的树型结构
                    SysDept sysDept = new SysDept();
                    sysDept.setDeptId(deptUser.getUserId());
                    sysDept.setParentId(dept.getDeptId());
                    sysDept.setDeptName(deptUser.getUserName());
                    childrenDept.add(sysDept);
                }
            }
        }

        // 把用户列表的数据添加到部门列表中，形成一棵完整的部门用户树
        if(!CollectionUtils.isEmpty(childrenDept)){
            deptList.addAll(childrenDept);
        }

        return deptList;
    }

    /**
     * 对象转部门用户树
     * @param depts     部门列表
     * @param userIds   需要勾选的部门及人员列表
     * @return 树结构列表
     */
    public List<Ztree> initZtree(List<SysDept> depts,List<String> userIds) {
        List<Ztree> ztrees = new ArrayList<>();
        boolean isCheck = StringUtils.isNotNull(userIds);
        for (SysDept dept : depts) {
            Ztree ztree = new Ztree();
            ztree.setId(dept.getDeptId());
            ztree.setpId(dept.getParentId());
            ztree.setName(dept.getDeptName());
            ztree.setTitle(dept.getDeptName());
            if (isCheck) {
                //包含该部门，则设置勾选状态
                if(userIds.contains(dept.getDeptId())){
                    ztree.setChecked(true);
                }
            }
            ztrees.add(ztree);
        }
        return ztrees;
    }

}
