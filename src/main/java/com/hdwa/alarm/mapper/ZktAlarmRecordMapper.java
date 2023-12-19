package com.hdwa.alarm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hdwa.alarm.entity.ZktAlarmRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 报警记录数据库访问层
 */
@Mapper
public interface ZktAlarmRecordMapper extends BaseMapper<ZktAlarmRecord> {

}
