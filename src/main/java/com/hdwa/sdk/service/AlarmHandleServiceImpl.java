package com.hdwa.sdk.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.hdwa.sdk.entity.ZktAlarmRecord;
import com.hdwa.sdk.kafka.KafkaProducer;
import com.redxun.core.cache.alarm.AlarmInfoCache;
import com.redxun.core.cache.alarm.CurrentDataCache;
import com.redxun.core.cache.alarm.ExpireAlarmQueue;
import com.redxun.core.constant.alarm.CommonConst;
import com.redxun.core.entity.alarm.*;
import com.redxun.core.entity.alarm.netty.NettyMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ONE;

/**
 * 报警处理实现类：包含报警产生和报警恢复
 **/
@Service
@Slf4j
public class AlarmHandleServiceImpl {

    @Autowired
    CurrentDataCache currentDataCache;

    @Autowired
    ZktAlarmRecordServiceImpl zktAlarmRecordService;

    @Autowired
    KafkaProducer kafkaProducer;

    /**
     * 处理iot采集数据
     */
    public void handleIOTData(String msg) throws InterruptedException {
        JSONObject parseObject = JSONObject.parseObject(msg);
        msg = parseObject.getString("data");
        if (StrUtil.isBlank(msg)) {
            return;
        }
        String[] split = msg.split(";", -1);
        if (split.length % 4 != 0) {
            return;
        }
        //返回的信息点个数
        int nums = (split.length) / 4;
        for (int i = 0; i < nums; i++) {
            String dateTime = split[i * 4];
            String meterId = split[i * 4 + 1];
            String funcId = split[i * 4 + 2];
            String valueStr = split[i * 4 + 3];
            if (StrUtil.isNotBlank(valueStr) || "null".equalsIgnoreCase(valueStr)) {
                double value = Double.parseDouble(valueStr);
                if (AlarmInfoCache.hasKey(meterId, funcId)) {
                    validIotData(dateTime, meterId, funcId, value);
                }
            }
        }
    }

    private void validIotData(String dateTime, String meterId, String funcId, double value) throws InterruptedException {
        //获取报警定义
        List<AlarmDefineVO> alarmDefines = AlarmInfoCache.getAlarmDefinitionIdByMeterFuncId(meterId, funcId);
        for (AlarmDefineVO alarmDefine : alarmDefines) {
            //重新拼接报警记录唯一ID
            String defineId = AlarmInfoCache.getAlarmDefineId(alarmDefine);
            synchronized (defineId.intern()) {
                //实时数据缓存
                currentDataCache.putCurrentData(meterId, funcId, value);
                //报警触发条件
                Condition condition = alarmDefine.getCondition();
                List<JSONObject> codeDetail = condition.getInfoCodes();
                boolean match = codeDetail.stream().allMatch(p -> currentDataCache.hasKey(p.getString("meterId"), p.getString("funcId")));
                //报警定义的所有信息点都有采集数值，具备判断条件
                if (match) {
                    String trigger = condition.getTrigger();
                    String end = condition.getEnd();
                    HashMap<String, Object> paramMap = new HashMap<>();
                    for (JSONObject code : codeDetail) {
                        //缓存：key是infoCode，取出当前iot数据值
                        paramMap.put(code.getString("infoCode"), currentDataCache.getCurrentData(code.getString("meterId"), code.getString("funcId")));
                    }
                    //匹配计算
                    Expression triggerExp = AviatorEvaluator.compile(trigger, true);
                    Expression endExp = AviatorEvaluator.compile(end, true);
                    Boolean triggerResult = (Boolean) triggerExp.execute(paramMap);
                    Boolean endResult = (Boolean) endExp.execute(paramMap);
                    log.info("triggerResult:[{}],endResult:[{}]", triggerResult, endResult);
                    if (triggerResult && endResult) {
                        log.warn("报警触发条件和报警恢复条件同时满足，请检查，报警定义详情【{}】", alarmDefine);
                    }
                    //获取当前报警状态
                    AlarmStateVO alarmState = AlarmInfoCache.getAlarmState(defineId);
                    //如果为空证明第一次报警
                    if (Objects.isNull(alarmState)) {
                        //默认正常报警状态
                        alarmState = new AlarmStateVO(defineId);
                        //查询当前报警记录
                        ZktAlarmRecord zktAlarmRecord = zktAlarmRecordService.getById(defineId);
                        //判断对象是否存在
                        if (zktAlarmRecord != null) {
                            //数据库报警状态：1-未处理
                            if ("1".equals(zktAlarmRecord.getState())) {
                                alarmState.setState(AlarmStateVO.State.NOT_DEAL.getType());
                                alarmState.setAlarmStartTime(zktAlarmRecord.getAlarmTime());
                            }
                        }
                    }
                    //报警产生值满足（这里的满足不考虑报警持续时间）
                    if (triggerResult) {
                        log.info("有一条满足报警条件{}，-----{}----{}", defineId, paramMap, alarmDefine.getCondition());
                        //屏蔽报警
                        if (alarmDefine.getOpen() == 0) {
                            log.info("报警定义ID为[{}]已经屏蔽", defineId);
                            continue;
                        }
                        if (AlarmInfoCache.isolationSystemList.contains(alarmDefine.getSystemCode())) {
                            log.info("报警定义ID为[{}]的系统[{}]已经隔离，不产生报警", defineId, alarmDefine.getSystemCode());
                            continue;
                        }
                        //报警的时候不考虑报警恢复，因为同时报警和报警恢复是不应该出现的
                        handlerNowDataAlarm(alarmDefine, alarmState, dateTime, condition, defineId, paramMap, meterId, funcId, value);
                    } else {
                        log.info("不满足报警条件{}，{}----{}", defineId, paramMap, alarmDefine.getCondition());
                        //当前数据正常
                        handlerNowDataNormal(alarmDefine, dateTime, condition, defineId, endResult, alarmState, paramMap, meterId, funcId, value);
                    }
                } else {
                    log.warn("部分信息点没有数值:[{}]", codeDetail);
                }
            }
        }
    }

    /**
     * 当前数据正常判断逻辑
     */
    private void handlerNowDataNormal(AlarmDefineVO alarmDefine, String dateTime, Condition condition, String defineId, Boolean endResult, AlarmStateVO alarmState, HashMap<String, Object> paramMap, String meterId, String funcId, double value) throws InterruptedException {
        //当前数据正常，报警状态为正常：清空之前的报警计时，重置回默认状态
        if (AlarmStateVO.State.NORMAL.getType().equals(alarmState.getState())) {
            alarmState = new AlarmStateVO(defineId);
            AlarmInfoCache.setAlarmState(defineId, alarmState);
        } else if (AlarmStateVO.State.NOT_DEAL.getType().equals(alarmState.getState())) {
            handlerNormalWithLock(alarmDefine, dateTime, condition, defineId, endResult, alarmState, paramMap);
        }
    }

    private void handlerNormalWithLock(AlarmDefineVO alarmDefine, String dateTime, Condition condition, String defineId, Boolean endResult, AlarmStateVO alarmState, HashMap<String, Object> paramMap) throws InterruptedException {
        //报警状态异常时候
        alarmState.setLatestDataNormalstate(true);
        //报警恢复条件满足，判断是否满足报警恢复持续时间
        if (endResult) {
            String endTime = alarmState.getAlarmEndTime();
            if (StringUtil.isEmpty(endTime)) {
                endTime = dateTime;
            }
            alarmState.setAlarmEndTime(endTime);
            //设置开始恢复时间
            int uphold = condition.getEndUphold();
            //超过报警恢复设置的持续时间
            if (com.redxun.core.util.alarm.DateUtil.betweenTwoTimeSecond(endTime, dateTime) >= uphold) {
                log.info("产生一条报警恢复消息[{}]>[{}]", com.redxun.core.util.alarm.DateUtil.betweenTwoTimeSecond(endTime, dateTime), uphold);
                NettyMessage<AlarmRecordVO> nettyMessage = new NettyMessage<>("", 6, CommonConst.projectId, CommonConst.groupCode);
                ZktAlarmRecord alarmRecordDO = zktAlarmRecordService.getById(AlarmInfoCache.getAlarmDefineId(alarmDefine));
                if (alarmRecordDO == null) {
                    alarmRecordDO = new ZktAlarmRecord();
                }
                alarmRecordDO.setId(defineId);
                alarmRecordDO.setObjId(alarmDefine.getObjId());
                alarmRecordDO.setItemCode(alarmDefine.getItemCode());
                alarmRecordDO.setItemId(alarmDefine.getItemId());
                alarmRecordDO.setState("2");
                alarmRecordDO.setEndTime(com.redxun.core.util.alarm.DateUtil.parseDate(dateTime));
                alarmRecordDO.setEndInfo(JSONObject.toJSONString(paramMap));
                //更新报警状态
                zktAlarmRecordService.save(alarmRecordDO);
                String alarmId = alarmRecordDO.getAlarmId();
                //报警恢复参数
                AlarmRecordVO alarmResumeRecord = AlarmRecordVO.builder()
                        .state(2)
                        .groupCode(CommonConst.groupCode)
                        .projectId(CommonConst.projectId)
                        .endTime(com.redxun.core.util.alarm.DateUtil.parse(dateTime))
                        .endInfo(JSONObject.toJSONString(paramMap))
                        .build();
                //如果有报警ID,直接报警恢复
                if (StringUtils.isNotEmpty(alarmId)) {
                    alarmResumeRecord.setId(alarmId);
                    nettyMessage.setContent(Collections.singletonList(alarmResumeRecord));
                    //{"id","123", "state":1, "groupCode":"wd", "projectId":"Pj123","endTime":"","endInfo":""}
                    // todo 改为kafka推送
                    //nettyClient.sendMessage(nettyMessage);
                    kafkaProducer.send(nettyMessage);
                } else {
                    //如果没有报警ID,定时任务再次测试
                    JobDataMap jobDataMap = new JobDataMap();
                    jobDataMap.put("alarmRecord", alarmRecordDO.toString());
                    jobDataMap.put("refire", "0");
                    jobDataMap.put("endTime", dateTime);
                    jobDataMap.put("endInfo", JSONObject.toJSONString(paramMap));
                    jobDataMap.put("defineId", defineId);
                    //恢复
                    jobDataMap.put("state", "2");
                    log.info(JSONObject.toJSONString(jobDataMap));
                    ExpireAlarmMessageVO em = new ExpireAlarmMessageVO();
                    //过期消息
                    em.setType("1");
                    em.setStartTime(DateUtil.offsetMinute(new Date(), 3).toJdkDate());
                    em.setJobDataMap(jobDataMap);
                    em.setJobName(defineId);
                    em.setJobGroupName("resume");
                    ExpireAlarmQueue.getExpireAlarmMessageQueue().produce(em);
                }

                String jobName = AlarmInfoCache.getAlarmDefineId(alarmDefine);
                ExpireAlarmMessageVO em = new ExpireAlarmMessageVO();
                //取消过期消息
                em.setType("2");
                em.setJobName(jobName);
                em.setJobGroupName("expire");
                ExpireAlarmQueue.getExpireAlarmMessageQueue().produce(em);
                //报警恢复，报警状态重置回默认
                alarmState.reset();
            }
        }
        AlarmInfoCache.setAlarmState(defineId, alarmState);
    }

    /**
     * 处理当前值报警的情况
     */
    private void handlerNowDataAlarm(AlarmDefineVO alarmDefine, AlarmStateVO alarmState, String dateTime, Condition condition, String defineId, HashMap<String, Object> paramMap, String meterId, String funcId, double value) throws InterruptedException {
        alarmState.setLatestDataNormalstate(false);
        JSONObject effectTime = condition.getEffectTime();
        //无生效时间设置，则任何时间生效
        boolean inEffectTime = true;
        //没有设置生效时间，所以也没有设置过期时间
        boolean hasExpire = false;
        JSONObject period = effectTime.getJSONObject("period");
        if (ObjectUtil.isAllNotEmpty(effectTime, effectTime.getString("type"))) {
            if ("period".equals(effectTime.getString("type"))) {
                //开始结束时间都不为空
                String startTime = period.getString("startTime");
                String endTime = period.getString("endTime");
                String dateTimeFormat = dateTime.substring(8, 14);
                if (ObjectUtil.isAllNotEmpty(startTime, endTime)) {
                    if (!"000000".equals(startTime) || !"235959".equals(endTime)) {
                        hasExpire = true;
                    }
                    if (startTime.compareTo(dateTimeFormat) > 0 || endTime.compareTo(dateTime.substring(8, 14)) < 0) {
                        inEffectTime = false;
                    }
                }
            }
        }
        if (inEffectTime) {
            //之前是报警状态，现在还是报警,报警值没变化
            //之前是是正常时候
            if (AlarmStateVO.State.NORMAL.getType().equals(alarmState.getState())) {
                handlerAlarmWithLock(alarmDefine, alarmState, dateTime, condition, defineId, paramMap, effectTime, hasExpire, period);
            }
        } else {
            //不在生效时间的段的产生报警,要保存最近一次的数值是否是是报警
            //其他条件全部改成默认值（不报警，不过期，报警开始时间和结束时间为空）
            alarmState.reset();
        }
        AlarmInfoCache.setAlarmState(defineId, alarmState);
    }

    /**
     * 生成报警记录和报警恢复消息（对报警定义加锁）
     */
    private void handlerAlarmWithLock(AlarmDefineVO alarmDefine, AlarmStateVO alarmState, String dateTime, Condition condition, String defineId, HashMap<String, Object> paramMap, JSONObject effectTime, boolean hasExpire, JSONObject period) throws InterruptedException {
        long timeSecond = 0;
        if (StringUtil.isNotEmpty(alarmState.getAlarmStartTime())) {
            timeSecond = com.redxun.core.util.alarm.DateUtil.betweenTwoTimeSecond(alarmState.getAlarmStartTime(), dateTime);
        } else {
            //设置开始报警时间
            alarmState.setAlarmStartTime(dateTime);
        }
        LocalDateTime expireDateTime = null;
        Date expireDate = null;
        if (hasExpire && "period".equals(effectTime.getString("type"))) {
            //过期时间
            String expireTime = period.getString("endTime");
            LocalTime localTime = LocalTime.parse(expireTime, DateTimeFormatter.ofPattern(com.redxun.core.util.alarm.DateUtil.sdfTimeNotDate));
            expireDateTime = LocalDateTime.of(LocalDate.now(), localTime.withNano(0));
            expireDate = com.redxun.core.util.alarm.DateUtil.localDateTime2Date(expireDateTime);
        }

        if (timeSecond >= condition.getTriggerUphold()) {
            log.warn("大于持续时间了，产生一条报警[{}]>[{}]", timeSecond, condition.getTriggerUphold());
            AlarmRecordVO alarmRecord = AlarmRecordVO.builder()
                    .objType(alarmDefine.getObjType())
                    .concern(alarmDefine.getConcern())
                    .level(alarmDefine.getLevel())
                    .projectId(CommonConst.projectId)
                    .state(1)
                    //报警时间为第一次满足报警条件时候的时间
                    .triggerTime(com.redxun.core.util.alarm.DateUtil.parse(alarmState.getAlarmStartTime()))
                    .treatState(INTEGER_ONE)
                    .remark(alarmDefine.getRemark())
                    .triggerInfo(JSONObject.toJSONString(paramMap))
                    .condition(condition.toString())
                    .effectEndTime(expireDate)
                    .groupCode(CommonConst.groupCode)
                    .itemCode(alarmDefine.getItemCode())
                    .objId(alarmDefine.getObjId())
                    .itemId(alarmDefine.getItemId())
                    .classCode(alarmDefine.getClassCode())
                    .createUser(CommonConst.systemId)
                    .ibmsClassCode(alarmDefine.getIbmsClassCode())
                    .ibmsSceneCode(alarmDefine.getIbmsSceneCode())
                    .build();
            NettyMessage<AlarmRecordVO> nettyMessage = new NettyMessage<>("", 5, CommonConst.projectId, CommonConst.groupCode);
            nettyMessage.setContent(Collections.singletonList(alarmRecord));
            // todo 推送一条报警记录给远端，改为kafka推送
            //nettyClient.sendMessage(nettyMessage);
            kafkaProducer.send(nettyMessage);

            ZktAlarmRecord zktAlarmRecordDO = zktAlarmRecordService.getById(defineId);
            if (zktAlarmRecordDO == null) {
                zktAlarmRecordDO = new ZktAlarmRecord();
            }
            zktAlarmRecordDO.setId(defineId);
            zktAlarmRecordDO.setObjId(alarmDefine.getObjId());
            zktAlarmRecordDO.setItemCode(alarmDefine.getItemCode());
            zktAlarmRecordDO.setItemId(alarmDefine.getItemId());
            zktAlarmRecordDO.setState(AlarmStateVO.State.NOT_DEAL.getType());
            zktAlarmRecordDO.setAlarmTime(alarmState.getAlarmStartTime());
            zktAlarmRecordDO.setProjectId(alarmDefine.getProjectId());
            zktAlarmRecordService.save(zktAlarmRecordDO);
            alarmState.setState(AlarmStateVO.State.NOT_DEAL.getType());
            //有过期时间，生成报警过期消息
            if (hasExpire && "period".equals(effectTime.getString("type"))) {
                //过期时间
                log.error("产生一条定时过期报警消息");
                ZktAlarmRecord zktAlarmRecord = ZktAlarmRecord.builder()
                        .alarmTime(alarmState.getAlarmStartTime())
                        .effectEndTime(com.redxun.core.util.alarm.DateUtil.format(Objects.requireNonNull(expireDateTime)))
                        .id(defineId)
                        .itemCode(alarmDefine.getItemCode())
                        .name(alarmDefine.getName())
                        .objId(alarmDefine.getObjId())
                        .itemId(alarmDefine.getItemId())
                        .projectId(alarmDefine.getProjectId())
                        .state("3")  //要变成已过期
                        .build();

                ZktAlarmRecord res = zktAlarmRecordService.getById(defineId);
                if (res == null) {
                    res = new ZktAlarmRecord();
                }
                zktAlarmRecord.setAlarmId(res.getAlarmId());
                JobDataMap jobDataMap = new JobDataMap();
                jobDataMap.put("alarmRecord", zktAlarmRecord.toString());
                jobDataMap.put("refire", "0");
                jobDataMap.put("expireTime", com.redxun.core.util.alarm.DateUtil.format(expireDateTime));
                jobDataMap.put("defineId", defineId);
                //过期
                jobDataMap.put("state", "3");
                ExpireAlarmMessageVO em = new ExpireAlarmMessageVO();
                //过期消息
                em.setType("1");
                em.setStartTime(com.redxun.core.util.alarm.DateUtil.localDateTime2Date(expireDateTime));
                em.setJobDataMap(jobDataMap);
                em.setJobName(defineId);
                em.setJobGroupName("expire");
                ExpireAlarmQueue.getExpireAlarmMessageQueue().produce(em);
            }
        } else {
            alarmState.setState(AlarmStateVO.State.NORMAL.getType());
        }
    }
}