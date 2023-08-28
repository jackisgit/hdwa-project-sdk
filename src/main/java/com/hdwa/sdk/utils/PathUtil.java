package com.hdwa.sdk.utils;

import com.alibaba.fastjson.JSONArray;
import com.hdwa.sdk.entity.exception.ExceptionItem;
import com.hdwa.sdk.entity.repository.RepositoryBase;
import com.hdwa.sdk.entity.scene.SceneDataObject;
import com.hdwa.sdk.entity.scene.SceneDataValue;
import com.hdwa.sdk.entity.scene.SceneObject;
import com.hdwa.sdk.entity.scene.SceneProperty;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PathUtil {

    public static List<Object> getByPath(Object parentData, String path) throws Exception {
        String[] splits = path.split("'");
        int splits_index = 0;
        // 查询目标可以是value_object或者value_array
        List<Object> tmpList = new CopyOnWriteArrayList<Object>();
        tmpList.add(parentData);
        for (int i = splits_index; i < splits.length; i++) {
            String split = splits[i];
            List<Object> tmpListInner = new CopyOnWriteArrayList<Object>();
            for (Object tmp : tmpList) {
                if (tmp instanceof SceneObject) {
                    SceneObject soInner = (SceneObject) tmp;
                    for (SceneProperty spTmp : soInner.propertyList) {
                        if (spTmp.propertyName.equals(split)) {
                            tmpListInner.add(spTmp);
                        }
                    }
                } else if (tmp instanceof SceneProperty) {
                    SceneProperty spInner = (SceneProperty) tmp;
                    if (spInner.propertyValueType.equals("static") && spInner.propertyValueSchema.equals("JSONArray")) {
                        int index_ = split.indexOf('=');
                        if (index_ != -1) {
                            String propertyName = split.substring(0, index_);
                            String propertyValue = split.substring(index_ + 1);
                            for (SceneObject soInner2 : spInner.static_array) {
                                boolean match = false;
                                for (SceneProperty spInner2 : soInner2.propertyList) {
                                    if (spInner2.propertyName.equals(propertyName) && spInner2.propertyValueType.equals("static")
                                            && spInner2.static_value.equals(propertyValue)) {
                                        match = true;
                                        break;
                                    }
                                }
                                if (match) {
                                    tmpListInner.add(soInner2);
                                }
                            }
                        } else {
                            for (SceneObject soInner2 : spInner.static_array) {
                                boolean match = false;
                                for (SceneProperty spInner2 : soInner2.propertyList) {
                                    if ((spInner2.propertyName.equals("名称") || spInner2.propertyName.equals("id"))
                                            && spInner2.propertyValueType.equals("static") && spInner2.static_value.equals(split)) {
                                        match = true;
                                        break;
                                    }
                                }
                                if (match) {
                                    tmpListInner.add(soInner2);
                                }
                            }
                        }
                    } else if (spInner.propertyValueType.equals("custom")) {
                        for (SceneProperty spInner2 : spInner.custom_object.propertyList) {
                            if (spInner2.propertyName.equals(split)) {
                                tmpListInner.add(spInner2);
                                break;
                            }
                        }
                    }
                }
            }
            tmpList = tmpListInner;
        }
        return tmpList;
    }

    public static String getPropertyPath(RepositoryBase Repository, Object spInner) throws ExceptionItem {
        if (spInner == null) {
            return null;
        }

        Object parentData = spInner;
        List<Object> tmpList = new CopyOnWriteArrayList<Object>();
        List<Integer> tmpIndexList = new CopyOnWriteArrayList<Integer>();
        while (true) {
            if (parentData == null) {
                break;
            }
            tmpList.add(parentData);
            Integer soIndex = null;
            if (parentData instanceof SceneProperty) {
                SceneProperty tmpProperty = (SceneProperty) parentData;
                if (Repository.attachproperty2host.containsKey(tmpProperty)) {
                    parentData = Repository.attachproperty2host.get(tmpProperty);
                } else if (Repository.property2customobject.containsKey(tmpProperty)) {
                    parentData = Repository.customobject2host.get(Repository.property2customobject.get(tmpProperty));
                } else if (Repository.property2staticobject.containsKey(tmpProperty)) {
                    parentData = Repository.property2staticobject.get(tmpProperty);
                } else {
                    throw new ExceptionItem(PathUtil.getPropertyPath(Repository, spInner), "getPropertyPath error", null);
                }
            } else if (parentData instanceof SceneObject) {
                SceneObject tmpObject = (SceneObject) parentData;
                if (Repository.staticobject2host.containsKey(tmpObject)) {
                    parentData = Repository.staticobject2host.get(tmpObject);
                    soIndex = Repository.staticobject2index.get(tmpObject);
                } else if (parentData == Repository.sceneObject) {
                    break;
                } else {
                    throw new ExceptionItem(PathUtil.getPropertyPath(Repository, spInner), "getPropertyPath error", null);
                }
            }
            tmpIndexList.add(soIndex);
        }

        StringBuffer sb = new StringBuffer();
        for (int i = tmpList.size() - 1; i >= 0; i--) {
            Object obj = tmpList.get(i);
            if (obj instanceof SceneProperty) {
                SceneProperty ss = (SceneProperty) obj;
                if (sb.length() > 0) {
                    sb.append("-");
                }
                sb.append(ss.propertyName);
            } else if (obj instanceof SceneObject) {
                Integer soIndex = tmpIndexList.get(i);
                if (soIndex != null) {
                    sb.append("[" + soIndex + "]");
                }
            }
        }
        return sb.toString();
    }

    public static String getDataPath(Object sv) throws Exception {
        String result = getDataPath(sv, new JSONArray());
        return result;
    }

    public static String getDataPath(Object sv, JSONArray pathArray) throws Exception {
        List<Object> list = new CopyOnWriteArrayList<Object>();
        {
            Object tmp = sv;
            while (tmp != null) {
                list.add(0, tmp);
                if (tmp instanceof SceneDataValue) {
                    SceneDataValue tmpData = (SceneDataValue) tmp;
                    tmp = tmpData.parentObjectData;
                } else if (tmp instanceof SceneDataObject) {
                    SceneDataObject tmpData = (SceneDataObject) tmp;
                    tmp = tmpData.parentObjectData != null ? tmpData.parentObjectData : tmpData.parentArrayData;
                } else {
                    throw new Exception();
                }
            }
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 1; i < list.size(); i++) {
            Object tmp = list.get(i);
            if (tmp instanceof SceneDataValue) {
                SceneDataValue tmpData = (SceneDataValue) tmp;
                sb.append((sb.length() > 0 ? "." : "") + tmpData.myPropertyName);
                pathArray.add(tmpData.myPropertyName);
            } else if (tmp instanceof SceneDataObject) {
                SceneDataObject tmpData = (SceneDataObject) tmp;
                if (tmpData.myPropertyName == null) {
                    SceneDataValue sdvInner = null;
                    for (String keyName : KeywordUtil.keyProperty) {
                        if (tmpData.get(keyName) != null) {
                            sdvInner = tmpData.get(keyName);
                            break;
                        }
                    }
                    if (sdvInner != null) {
                        sb.append((sb.length() > 0 ? "." : "") + "[" + (sdvInner.value_prim == null ? "null" : sdvInner.value_prim.value) + "]");
                        pathArray.add(sdvInner.value_prim == null ? "null" : sdvInner.value_prim.value);
                    }
                } else {
                    sb.append((sb.length() > 0 ? "." : "") + tmpData.myPropertyName);
                    pathArray.add(tmpData.myPropertyName);
                }
            }
        }
        return sb.toString();
    }

}
