package com.daacoo.demo.mapper;

import com.daacoo.demo.entity.ImportSysUser;
import com.daacoo.demo.excel.template.ImportSysUserTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface ImportDemoMapper {
    public List<ImportSysUser> selectUserList(ImportSysUser user);

    int insertBatch(List<ImportSysUserTemplate> list);

    void clean();

}
