package com.daacoo.framework.shiro.service;

import com.daacoo.common.constant.Constants;
import com.daacoo.common.constant.ShiroConstants;
import com.daacoo.common.constant.UserConstants;
import com.daacoo.common.core.entity.sys.SysUser;
import com.daacoo.common.utils.*;
import com.daacoo.framework.manager.AsyncManager;
import com.daacoo.framework.manager.factory.AsyncFactory;
import com.daacoo.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 注册校验方法
 *
 * @author ruoyi
 */
@Component
public class SysRegisterService {
    @Autowired
    private ISysUserService userService;

    @Autowired
    private SysPasswordService passwordService;

    /**
     * 注册
     */
    public String register(SysUser user) {
        String msg = "", loginName = user.getLoginName(), password = user.getPassword(),idCard=user.getIdCard();

        if (ShiroConstants.CAPTCHA_ERROR.equals(ServletUtils.getRequest().getAttribute(ShiroConstants.CURRENT_CAPTCHA))) {
            msg = "验证码错误";
        } else if (StringUtils.isEmpty(loginName)) {
            msg = "用户名不能为空";
        } else if (StringUtils.isEmpty(password)) {
            msg = "用户密码不能为空";
        } else if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            msg = "密码长度必须在5到20个字符之间";
        } else if (loginName.length() < UserConstants.USERNAME_MIN_LENGTH
                || loginName.length() > UserConstants.USERNAME_MAX_LENGTH) {
            msg = "账户长度必须在2到20个字符之间";
        } else if (!userService.checkLoginNameUnique(user)) {
            msg = "保存用户'" + loginName + "'失败，注册账号已存在";
        }else if(!StringUtils.isEmpty(idCard)){
            if(!"".equals(IDCardUtils.IDCardValidate(idCard))){
                msg = IDCardUtils.IDCardValidate(idCard);
            }
        } else {
            user.setSalt(ShiroUtils.randomSalt());
            user.setPassword(passwordService.encryptPassword(loginName, password, user.getSalt()));
            boolean regFlag = userService.registerUser(user);
            if (!regFlag) {
                msg = "注册失败,请联系系统管理人员";
            } else {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(loginName, Constants.REGISTER, MessageUtils.message("user.register.success")));
            }
        }
        return msg;
    }
}
