package com.daacoo.demo.service.impl;

import com.daacoo.demo.entity.ImportSysUser;
import com.daacoo.demo.mapper.ImportDemoMapper;
import com.daacoo.demo.service.IImportDemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ImportDemoServiceImpl implements IImportDemoService {

    @Autowired
    private ImportDemoMapper importDemoMapper;

    @Override
    public List<ImportSysUser> selectUserList(ImportSysUser user) {
        return importDemoMapper.selectUserList(user);
    }
}
