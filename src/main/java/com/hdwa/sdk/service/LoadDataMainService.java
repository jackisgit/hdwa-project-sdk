package com.hdwa.sdk.service;

import com.hdwa.sdk.constant.BaseDecConstant;
import com.hdwa.sdk.entity.repository.DataContainer;
import com.hdwa.sdk.entity.repository.RepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author abao
 * @since 2023/8/30
 * 加载数据入口
 */
@Slf4j
@Service
public class LoadDataMainService {

    @Autowired
    private PhysicalWorldService physicalWorldService;

    @Autowired
    private IbmsPhysicalWorldService ibmsPhysicalWorldService;

    @Autowired
    private IbmsLogicalGroupService ibmsLogicalGroupService;

    @Autowired
    private PointService pointService;

    @Autowired
    private AlarmService alarmService;

    @Autowired
    private ConfigApiService configApiService;

    /**
     * 下载数据入口
     */
    public void downLoadDataMain() {
        physicalWorldService.downLoadPhysicalWorldData();
        ibmsPhysicalWorldService.downLoadIbmsPhysicalWorldData();
        ibmsLogicalGroupService.downLoadLogicalGroupData();
        pointService.downLoadPoint();
        configApiService.downLoadConfig();
    }

    /**
     * 先下载后加载数据全流程
     */
    public void main() {
        try {
            downLoadDataMain();
            loadDataMain();
        } catch (Exception e) {
            log.error("******** 执行全流程出现异常", e);
        }
    }


    /**
     * 下载逻辑编组数据后加载数据流程
     */
    public void logicGroupMain() {
        try {
            ibmsLogicalGroupService.downLoadLogicalGroupData();
            loadDataMain();
        } catch (Exception e) {
            log.error("******** 执行加载逻辑编组流程出现异常", e);
        }
    }

    /**
     * 加载数据入口
     */
    public void loadDataMain() {
        RepositoryImpl repository = new RepositoryImpl();
        RepositoryImpl repositoryOld = DataContainer.projectMap.get(BaseDecConstant.CURRENT_PROJECT_ID);
        try {
            //加载物理世界数据
            physicalWorldService.loadPhysicalWorldData(repository);
            //加载IBMS物理世界数据
            ibmsPhysicalWorldService.loadIbmsPhysicalWorldData(repository);
            //加载IBMS逻辑编组数据
            ibmsLogicalGroupService.loadLogicalGroupData(repository);
            //加载点位数据
            pointService.loadPointData(repository);
            //加载报警数据
            alarmService.loadAlarmData(repository);
            //构建依赖
            repository.refreshDependency();
            //加载接口数据
            configApiService.loadConfigData(repository);
            //加载到数据容器
            DataContainer.projectMap.put(BaseDecConstant.CURRENT_PROJECT_ID, repository);
            //关闭老的计算线程
            if (repositoryOld != null) {
                repositoryOld.threadStop();
            }
            //启动新的计算线程
            repository.threadStart();
        } catch (Exception e) {
            log.error("******** 加载数据入口异常", e);
        }
    }


    /**
     * 更新点位过滤数据
     */
    public void updatePoint(RepositoryImpl repository) {
        try {
            //加载点位数据
            pointService.loadPointData(repository);
            //加载接口数据
            configApiService.loadConfigData(repository);
            //加载到数据容器
            DataContainer.projectMap.put(System.getProperty(BaseDecConstant.PROJECT_ID), repository);
        } catch (Exception e) {
            log.error("******** 更新点位数据异常", e);
        }
    }
}
