package com.hdwa.sdk.service;

import com.hdwa.sdk.constant.BaseDecConstant;
import com.hdwa.sdk.entity.repository.RepositoryImpl;
import com.hdwa.sdk.entity.scene.SceneDataObject;
import com.hdwa.sdk.entity.scene.SceneDataPrimitive;
import com.hdwa.sdk.entity.scene.SceneDataSet;
import com.hdwa.sdk.entity.scene.SceneDataValue;
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

            AlarmUtil.alarmColChange.forEach(s -> repository.alarmArray.setColChange(s));
            repository.objectArrayDic.forEach((s, sdv) -> {
                if (!repository.code2objTypeMap.containsKey(s)) {
                    return;
                }
                String objType = repository.code2objTypeMap.get(s);
                if (!objType.equals(BaseDecConstant.EQUIPMENT) && !objType.equals(BaseDecConstant.SYSTEM) && !objType.equals(BaseDecConstant.SPACE)) {
                    return;
                }
                SceneDataSet objectArray = repository.objectArrayDic.get(s).value_array;
                objectArray.set.forEach(sdo -> {
                    String objId = (String) sdo.get(BaseDecConstant.ID).value_prim.value;
                    //报警列表
                    SceneDataValue advList = new SceneDataValue(null, null, BaseDecConstant.ALARM_LIST, null);
                    advList.finish = true;
                    advList.value_array = new SceneDataSet(false);
                    advList.value_array.set = new CopyOnWriteArrayList<SceneDataObject>();
                    advList.value_array.setRowChange(true);
                    repository.id2alarmList.putIfAbsent(objId, advList);
                    AlarmUtil.alarmColChange.forEach(s1 -> {
                        advList.value_array.setColChange(s1);
                    });

                    SceneDataValue alarmList = repository.id2alarmList.get(objId);
                    SceneDataValue sv_alarmList = new SceneDataValue(repository, sdo, BaseDecConstant.ALARM_LIST, null);
                    sv_alarmList.finish = true;
                    sv_alarmList.value_array = alarmList.value_array;
                    sdo.put(BaseDecConstant.ALARM_LIST, sv_alarmList);


                    //报警数量
                    SceneDataValue advCount = new SceneDataValue(null, null, BaseDecConstant.ALARM_COUNT, null);
                    advCount.finish = true;
                    advCount.value_prim = new SceneDataPrimitive();
                    advCount.value_prim.value = 0;
                    advCount.value_prim.change = true;
                    repository.id2alarmCount.putIfAbsent(objId, advCount);

                    SceneDataValue alarmCount = repository.id2alarmCount.get(objId);
                    SceneDataValue sv_alarmCount = new SceneDataValue(repository, sdo, BaseDecConstant.ALARM_COUNT, null);
                    sv_alarmCount.finish = true;
                    sv_alarmCount.value_prim = alarmCount.value_prim;
                    sdo.put(BaseDecConstant.ALARM_COUNT, sv_alarmCount);

                    objectArray.setColChange(BaseDecConstant.ALARM_COUNT);
                });
            });
            log.warn("************结束加载-报警数据-用时：" + (System.currentTimeMillis() - startTime) / 1000 + " 秒");
        } catch (Exception e) {
            log.error("加载报警数据异常", e);
        }

    }
}
