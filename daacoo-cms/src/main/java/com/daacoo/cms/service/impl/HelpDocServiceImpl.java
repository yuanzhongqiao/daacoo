package com.daacoo.cms.service.impl;

import com.daacoo.cms.entity.HelpDoc;
import com.daacoo.cms.mapper.HelpDocMapper;
import com.daacoo.cms.service.IHelpDocService;
import com.daacoo.common.utils.ShiroUtils;
import com.daacoo.common.utils.uuid.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Description 帮助文档 业务接口-实现层
 * @Author daacoo
 * @Date 2021/6/24
 * @Version 1.0
 **/
@Service
public class HelpDocServiceImpl implements IHelpDocService {

    @Autowired
    private HelpDocMapper helpDocMapper;

    @Override
    public List<HelpDoc> selectHelpDocList(Map<String, Object> paramMap) {
        return helpDocMapper.selectHelpDocList(paramMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addHelpDoc(Map<String, Object> paramMap) {
        paramMap.put("id", UUID.fastUUID().toString(true));
        paramMap.put("createBy", ShiroUtils.getLoginName());
        paramMap.put("updateBy",ShiroUtils.getLoginName());
        return helpDocMapper.addHelpDoc(paramMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateHelpDoc(Map<String, Object> paramMap) {
        paramMap.put("updateBy",ShiroUtils.getLoginName());
        return helpDocMapper.updateHelpDoc(paramMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteHelpDoc(String[] toStrArray) {
        return helpDocMapper.deleteHelpDoc(toStrArray);
    }
}
