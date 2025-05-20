package com.daacoo.cms.service.impl;


import com.daacoo.cms.entity.Advertising;
import com.daacoo.cms.mapper.AdvertisingMapper;
import com.daacoo.cms.service.IAdvertisingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 广告管理 业务层处理
 * @author daacoo
 * @date 2019/12/15
 */
@Service
public class AdvertisingServiceImpl implements IAdvertisingService {
    @Autowired
    private AdvertisingMapper advertisingMapper;

    @Override
    public List<Advertising> selectAdvertisingList(Map<String, Object> paramMap) {
        return advertisingMapper.selectAdvertisingList(paramMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertAdvertising(Map<String, Object> paramMap) {
        return advertisingMapper.insertAdvertising(paramMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateAdvertising(Map<String, Object> paramMap) {
        return advertisingMapper.updateAdvertising(paramMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteAdvertising(String[] ids) {
        return advertisingMapper.deleteAdvertisingById(ids);
    }
}
