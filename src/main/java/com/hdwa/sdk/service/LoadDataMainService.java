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
    public boolean downLoadDataMain() {
        boolean flag;
        flag = physicalWorldService.downLoadPhysicalWorldData();
        if (!flag) {
            return false;
        }
        flag = ibmsPhysicalWorldService.downLoadIbmsPhysicalWorldData();
        if (!flag) {
            return false;
        }
        flag = ibmsLogicalGroupService.downLoadLogicalGroupData();
        if (!flag) {
            return false;
        }
        //本地点位控制数据不影响系统运行
        pointService.downLoadPoint();
        flag = configApiService.downLoadConfig();
        return flag;
    }

    /**
     * 先下载后加载数据全流程
     */
    public boolean main() {
        boolean flag = downLoadDataMain();
        if (!flag) {
            return false;
        }
        return loadDataMain();
    }


    /**
     * 下载逻辑编组数据后加载数据流程
     */
    public boolean logicGroupMain() {
        boolean flag = ibmsLogicalGroupService.downLoadLogicalGroupData();
        if (!flag) {
            return false;
        }
        return loadDataMain();
    }

    /**
     * 下载接口数据后加载数据流程
     */
    public boolean logicApiMain() {
        boolean flag = configApiService.downLoadConfig();
        if (!flag) {
            return false;
        }
        return loadDataMain();
    }

    /**
     * 加载数据入口
     */
    public boolean loadDataMain() {
        log.warn("************加载数据************");
        RepositoryImpl repository = new RepositoryImpl();
        RepositoryImpl repositoryOld = DataContainer.projectMap.get(BaseDecConstant.CURRENT_PROJECT_ID);
        boolean flag;
        try {
            //加载物理世界数据
            flag = physicalWorldService.loadPhysicalWorldData(repository);
            if (!flag) {
                return false;
            }
            //加载IBMS物理世界数据
            flag = ibmsPhysicalWorldService.loadObjectData(repository);
            if (!flag) {
                return false;
            }
            //加载IBMS逻辑编组数据
            flag = ibmsLogicalGroupService.loadGroupData(repository);
            if (!flag) {
                return false;
            }
            //加载点位数据，加载失败不影响系统运行
            pointService.loadPointData(repository);
            //加载报警数据
            flag = alarmService.loadAlarmData(repository);
            if (!flag) {
                return false;
            }
            //构建依赖
            flag = repository.refreshDependency();
            if (!flag) {
                return false;
            }
            //加载接口数据
            flag = configApiService.loadConfigData(repository);
            if (!flag) {
                return false;
            }
            //加载到数据容器
            DataContainer.projectMap.put(BaseDecConstant.CURRENT_PROJECT_ID, repository);
            //关闭老的计算线程
            if (repositoryOld != null) {
                repositoryOld.threadStop();
            }
            //启动新的计算线程
            repository.threadStart();
            return true;
        } catch (Exception e) {
            log.error("********加载数据入口异常", e);
            return false;
        }
    }


    /**
     * 更新点位过滤数据
     */
    public boolean updatePoint() {
        boolean flag = pointService.downLoadPoint();
        if (!flag) {
            return false;
        }
        return loadDataMain();

    }
}
