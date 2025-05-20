package com.daacoo.demo.service;

import com.daacoo.demo.entity.ImportSysUser;

import java.util.List;

public interface IImportDemoService {
    public List<ImportSysUser> selectUserList(ImportSysUser user);
}
