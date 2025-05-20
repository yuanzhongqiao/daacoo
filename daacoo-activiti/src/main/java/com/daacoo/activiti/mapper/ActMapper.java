package com.daacoo.activiti.mapper;

import com.daacoo.activiti.entity.Act;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * act  数据访问层
 * @author daacoo
 * @version 2019/9/29
 */
@Mapper
@Component
public interface ActMapper {

    public void updateProcInsIdByBusinessId(Act act);

}
