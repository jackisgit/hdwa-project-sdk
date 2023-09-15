package com.hdwa.sdk.service;

import com.hdwa.sdk.entity.repository.DataContainer;
import com.hdwa.sdk.entity.repository.RepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author abao
 * @since 2023/8/30
 * 加载数据入口
 */
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

    @Value("${project.id}")
    private String projectId;

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
     * 加载数据入口
     */
    public void loadDataMain() {
        RepositoryImpl repository = new RepositoryImpl();
        //加载物理世界数据
        physicalWorldService.loadPhysicalWorldData(repository);
        //加载IBMS物理世界数据
        ibmsPhysicalWorldService.loadIbmsPhysicalWorldData(repository);
        //加载IBMS逻辑编组数据
        // TODO: 2023/9/14 还没有编组数据 
        //ibmsLogicalGroupService.loadLogicalGroupData(repository);
        //加载点位数据
        pointService.loadPointData(repository);
        //加载报警数据
        alarmService.loadAlarmData(repository);
        //加载接口数据
        configApiService.loadConfigData(repository);
        //保存到数据容器
        DataContainer.projectMap.put(projectId, repository);
    }
}
