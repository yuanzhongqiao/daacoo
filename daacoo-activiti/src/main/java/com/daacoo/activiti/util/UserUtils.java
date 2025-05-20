package com.daacoo.activiti.util;

import com.daacoo.common.core.entity.sys.SysUser;
import com.daacoo.common.utils.SpringContextHolder;
import com.daacoo.system.mapper.SysRoleMapper;
import com.daacoo.system.mapper.SysUserMapper;

/**
 * 用户工具类
 *
 * @author daacoo
 * @version 2019/10/12
 */
public class UserUtils {

    private static SysUserMapper userDao = SpringContextHolder.getBean(SysUserMapper.class);
    private static SysRoleMapper roleDao = SpringContextHolder.getBean(SysRoleMapper.class);

    /**
     * 根据登录名获取用户
     *
     * @param loginName
     * @return SysUser
     */
    public static SysUser getByLoginName(String loginName) {
        SysUser user = userDao.selectUserByLoginName(loginName);
        if (user != null) {
            user.setRoles(roleDao.selectRolesByUserId(user.getUserId()));
        }
        return user;
    }

    public static SysUser getById(String startUserId) {
        SysUser user = userDao.selectUserById(startUserId);
        if (user != null) {
            user.setRoles(roleDao.selectRolesByUserId(user.getRoleId()));
        }
        return user;
    }
}
