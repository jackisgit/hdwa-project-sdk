package com.hdwa.control.job;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.control.client.EmsControlClient;
import com.hdwa.control.constant.CommonConst;
import com.hdwa.control.entity.*;
import com.hdwa.control.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
@Slf4j
public class CommandJob extends QuartzJobBean {

    @Autowired
    EmsControlClient emsControlClient;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Value("${iot.project.url:127.0.0.1:8852}")
    public String url;

    @Value("${group.control.project.iotid}")
    public String projectId;

    @Value("${misfire.discard.minute:10}")
    public int misfireDiscardMinute;

    @Value("${spring.kafka.enable:false}")
    public boolean enable;

    @Value("${spring.kafka.producer.edgeTopic}")
    public String topics;

    public static AtomicLong num = new AtomicLong(0);
    public static int batchSize = 200;

    private String commandResults;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            TimeInterval timer = DateUtil.timer();
            JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
            commandResults = mergedJobDataMap.getString("commandResult");
            Date fireTime = context.getFireTime();
            Date scheduledFireTime = context.getScheduledFireTime();
            long delayMinute = DateUtils.betweenTwoTime(DateUtils.date2LocalDateTime(scheduledFireTime), DateUtils.date2LocalDateTime(fireTime), ChronoUnit.MINUTES);
            if (delayMinute > misfireDiscardMinute) {
                log.warn("丢弃历史数据{}：{}分钟", context.getJobDetail().getKey(), delayMinute);
                return;
            }
            log.warn("commandResults：" + commandResults);
            if (StringUtils.isNotBlank(commandResults)) {
                //转换成list 需要字符串有[]
                if (commandResults.charAt(0) == '{') {
                    StringBuilder string = new StringBuilder(commandResults);
                    string.insert(0, "[").insert(string.length(), "]");
                    commandResults = string.toString();
                }
                List<ControlCommand> controlCommandList = JSONArray.parseArray(commandResults, ControlCommand.class);
                ArrayBlockingQueue<PointSetParam> pointsetQueue = new ArrayBlockingQueue<>(controlCommandList.size());
                ArrayList<ControlCommand> commandbacks = new ArrayList<>();
                for (ControlCommand command : controlCommandList) {
                    String value = JSONObject.parseObject(command.getPointAction()).getString("value");
                    if (value.contains("true")) {
                        value = "1";
                    } else if (value.contains("false")) {
                        value = "0";
                    }
                    PointSetParam pointset = PointSetParam.builder().building(projectId).funcid(Integer.parseInt(command.getFuncId())).meter(command.getMeterId()).data(Double.parseDouble(value)).operation("pointset").build();
                    pointsetQueue.add(pointset);
                    ControlCommand commandback = new ControlCommand();
                    commandback.setCommandResult(2);
                    commandback.setId(command.getId());
                    commandback.setProjectId(CommonConst.projectId);
                    commandbacks.add(commandback);
                }
                ArrayList<PointSetParam> pointSetParams = new ArrayList<>(batchSize);
                while (pointsetQueue.drainTo(pointSetParams, batchSize) > 0) {
                    try {
                        BatchPointSetParam batchPointSetParam = new BatchPointSetParam();
                        batchPointSetParam.setBuilding(projectId);
                        batchPointSetParam.setPoints(pointSetParams);
                        BatchPointSetResult batchPointSetResult = emsControlClient.pointSetBatch(batchPointSetParam);
                        log.info("控制指令:[{}]:[{}] 的执行结果为：[{}]", url + "/pointsetbatch_post", JSONObject.toJSONString(batchPointSetParam), JSONObject.toJSONString(batchPointSetResult));
                        pointSetParams.clear();
                    } catch (Exception e) {
                        log.error("控制指令下发失败", e);
                    }
                }
                ControlCommandMessage message = new ControlCommandMessage(2);
                message.setContent(commandbacks);
                kafkaTemplate.send(topics, JSONObject.toJSONString(message));
                log.info("边端指令反馈云端2, message: {}", JSONObject.toJSONString(message));
            }
            log.info("定时任务[{}]:[{}min]执行毫秒数为：{} 毫秒", context.getJobDetail().getKey(), delayMinute, timer.interval());
        } catch (Exception e) {
            log.error("job {} hander error，total error num {}", context.getJobDetail().getKey(), num.incrementAndGet(), e);
        }
    }

    public String getCommandResults() {
        return commandResults;
    }

    public void setCommandResults(String commandResults) {
        this.commandResults = commandResults;
    }

}