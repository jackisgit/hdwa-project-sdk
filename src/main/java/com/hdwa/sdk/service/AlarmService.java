package com.hdwa.sdk.service;

import com.hdwa.sdk.constant.BaseDecConstant;
import com.hdwa.sdk.entity.repository.DataContainer;
import com.hdwa.sdk.entity.repository.RepositoryImpl;
import com.hdwa.sdk.entity.scene.DataObject;
import com.hdwa.sdk.entity.scene.DataPrimitive;
import com.hdwa.sdk.entity.scene.DataSet;
import com.hdwa.sdk.entity.scene.DataValue;
import com.hdwa.sdk.utils.AlarmUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author abao
 * @since 2023/9/5
 * 报警数据服务
 */
@Service
@Slf4j
public class AlarmService {

    /**
     * 加载报警数据
     *
     * @param repository
     */
    public void loadAlarmData(RepositoryImpl repository) {
        try {
            log.warn("************开始加载-报警数据");
            long startTime = System.currentTimeMillis();

            AlarmUtil.alarmColChange.forEach(s -> DataContainer.alarmArray.setColChange(s));
            repository.objectArrayDic.forEach((s, sdv) -> {
                if (!repository.code2objTypeMap.containsKey(s)) {
                    return;
                }
                String objType = repository.code2objTypeMap.get(s);
                if (!objType.equals(BaseDecConstant.EQUIPMENT) && !objType.equals(BaseDecConstant.SYSTEM) && !objType.equals(BaseDecConstant.SPACE)) {
                    return;
                }
                DataSet objectArray = repository.objectArrayDic.get(s).valueArray;
                objectArray.set.forEach(sdo -> {
                    String objId = (String) sdo.get(BaseDecConstant.ID).valuePrim.value;
                    //报警列表
                    DataValue advList = new DataValue(null, null, BaseDecConstant.ALARM_LIST, null);
                    advList.finish = true;
                    advList.valueArray = new DataSet(false);
                    advList.valueArray.set = new CopyOnWriteArrayList<DataObject>();
                    advList.valueArray.setRowChange(true);
                    DataContainer.id2alarmList.putIfAbsent(objId, advList);
                    AlarmUtil.alarmColChange.forEach(s1 -> {
                        advList.valueArray.setColChange(s1);
                    });

                    DataValue alarmList = DataContainer.id2alarmList.get(objId);
                    DataValue sv_alarmList = new DataValue(repository, sdo, BaseDecConstant.ALARM_LIST, null);
                    sv_alarmList.finish = true;
                    sv_alarmList.valueArray = alarmList.valueArray;
                    sdo.put(BaseDecConstant.ALARM_LIST, sv_alarmList);


                    //报警数量
                    DataValue advCount = new DataValue(null, null, BaseDecConstant.ALARM_COUNT, null);
                    advCount.finish = true;
                    advCount.valuePrim = new DataPrimitive();
                    advCount.valuePrim.value = 0;
                    advCount.valuePrim.change = true;
                    DataContainer.id2alarmCount.putIfAbsent(objId, advCount);

                    DataValue alarmCount = DataContainer.id2alarmCount.get(objId);
                    DataValue sv_alarmCount = new DataValue(repository, sdo, BaseDecConstant.ALARM_COUNT, null);
                    sv_alarmCount.finish = true;
                    sv_alarmCount.valuePrim = alarmCount.valuePrim;
                    sdo.put(BaseDecConstant.ALARM_COUNT, sv_alarmCount);

                    objectArray.setColChange(BaseDecConstant.ALARM_COUNT);
                });
            });
            log.warn("************结束加载-报警数据-用时：" + (System.currentTimeMillis() - startTime) / 1000 + " 秒");
        } catch (Exception e) {
            log.error("加载报警数据异常", e);
            throw e;
        }
    }

}
