package com.hdwa.sdk.service;

import com.hdwa.sdk.entity.repository.RepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
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

    RepositoryImpl repository = new RepositoryImpl();


    /**
     * 加载数据入口
     */
    public Object loadDataMain() {
        //这里是为了方便调试，如果已经加载过物理世界数据 就不在加载了
        if (repository.classArray.set.size() == 0) {
            //加载物理世界数据
            physicalWorldService.loadPhysicalWorldData(repository);
        }
        if (repository.ZKTObjectArrayDic.size() == 0) {
            //加载IBMS物理世界数据
            ibmsPhysicalWorldService.loadIbmsPhysicalWorldData(repository);
        }

        if (repository.IBMSArrayDic.size() == 0) {
            //加载IBMS逻辑编组数据
            ibmsLogicalGroupService.loadLogicalGroupData(repository);
        }

        if (repository.InfoPointListArray.set.size() == 0) {
            //加载点位数据
            pointService.loadPointData(repository);
        }


        return "ok";
    }
}
