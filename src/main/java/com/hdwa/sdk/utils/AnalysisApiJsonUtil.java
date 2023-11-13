package com.hdwa.sdk.utils;


import com.hdwa.sdk.entity.repository.RepositoryBase;
import com.hdwa.sdk.entity.scene.DataObjectBase;
import com.hdwa.sdk.entity.scene.DataProperty;

import java.util.HashMap;


/**
 * @author abao
 * @since 2023/8/1
 * 解析接口对象和属性
 */
public class AnalysisApiJsonUtil {

    /**
     * 解析入口
     *
     * @param repository
     */
    public static void analysisMain(RepositoryBase repository) {
        repository.customobject2host = new HashMap<>(16);
        repository.attachproperty2host = new HashMap<>(16);
        repository.property2customobject = new HashMap<>(16);
        repository.p2walker1 = new HashMap<>(16);
        repository.p2walker2 = new HashMap<>(16);
        analysisObject(repository, null, repository.dataObjectBase, false, -1);
    }

    /**
     * 解析对象
     *
     * @param repository
     * @param parentProperty
     * @param object
     * @param soIsSai
     * @param soIndex
     */
    private static void analysisObject(RepositoryBase repository, DataProperty parentProperty, DataObjectBase object, boolean soIsSai, int soIndex) {
        if (parentProperty != null) {
            if (soIsSai) {
                repository.staticobject2host.put(object, parentProperty);
                repository.staticobject2index.put(object, soIndex);
            } else {
                repository.customobject2host.put(object, parentProperty);
            }
        }
        for (DataProperty sp : object.propertyList) {
            analysisProperty(repository, object, null, sp, soIsSai);
        }
    }

    /**
     * 解析属性
     *
     * @param repository
     * @param parentObject
     * @param parentProperty
     * @param property
     * @param soIsSai
     */
    private static void analysisProperty(RepositoryBase repository, DataObjectBase parentObject, DataProperty parentProperty, DataProperty property, boolean soIsSai) {
        if (parentObject != null) {
            if (soIsSai) {
                repository.property2staticobject.put(property, parentObject);
            } else {
                repository.property2customobject.put(property, parentObject);
            }
        }
        if (parentProperty != null) {
            repository.attachproperty2host.put(property, parentProperty);
        }
        if (property.queryAttached != null) {
            for (DataProperty spInner : property.queryAttached) {
                analysisProperty(repository, null, property, spInner, false);
            }
        }
        if (property.customObject != null) {
            analysisObject(repository, property, property.customObject, false, -1);
        }
        if (property.staticArray != null) {
            for (int soIndex = 0; soIndex < property.staticArray.length; soIndex++) {
                DataObjectBase object = property.staticArray[soIndex];
                analysisObject(repository, property, object, true, soIndex);
            }
        }
    }
}
