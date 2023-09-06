package com.hdwa.sdk.mapper;

import com.hdwa.sdk.entity.ZktAlarmRecord;
import com.redxun.common.base.db.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
* 报警记录ID数据库访问层
*/
@Mapper
public interface ZktAlarmRecordMapper extends BaseDao<ZktAlarmRecord> {

}
