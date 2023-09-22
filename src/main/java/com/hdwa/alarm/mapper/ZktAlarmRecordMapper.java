package com.hdwa.alarm.mapper;

import com.hdwa.alarm.entity.ZktAlarmRecord;
import com.redxun.common.base.db.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
* 报警记录数据库访问层
*/
@Mapper
public interface ZktAlarmRecordMapper extends BaseDao<ZktAlarmRecord> {

}
