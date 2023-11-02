package com.hdwa.sdk.utils;

import com.alibaba.fastjson.JSONArray;
import com.hdwa.sdk.entity.exception.ExceptionItem;
import com.hdwa.sdk.entity.repository.RepositoryBase;
import com.hdwa.sdk.entity.scene.DataObject;
import com.hdwa.sdk.entity.scene.DataValue;
import com.hdwa.sdk.entity.scene.DataObjectBase;
import com.hdwa.sdk.entity.scene.DataProperty;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PathUtil {

    public static List<Object> getByPath(Object parentData, String path) {
        String[] splits = path.split("'");
        int splits_index = 0;
        // 查询目标可以是value_object或者value_array
        List<Object> tmpList = new CopyOnWriteArrayList<>();
        tmpList.add(parentData);
        for (int i = splits_index; i < splits.length; i++) {
            String split = splits[i];
            List<Object> tmpListInner = new CopyOnWriteArrayList<>();
            for (Object tmp : tmpList) {
                if (tmp instanceof DataObjectBase) {
                    DataObjectBase soInner = (DataObjectBase) tmp;
                    for (DataProperty spTmp : soInner.propertyList) {
                        if (spTmp.propertyName.equals(split)) {
                            tmpListInner.add(spTmp);
                        }
                    }
                } else if (tmp instanceof DataProperty) {
                    DataProperty spInner = (DataProperty) tmp;
                    if (spInner.propertyValueType.equals("static") && spInner.propertyValueSchema.equals("JSONArray")) {
                        int index_ = split.indexOf('=');
                        if (index_ != -1) {
                            String propertyName = split.substring(0, index_);
                            String propertyValue = split.substring(index_ + 1);
                            for (DataObjectBase soInner2 : spInner.staticArray) {
                                boolean match = false;
                                for (DataProperty spInner2 : soInner2.propertyList) {
                                    if (spInner2.propertyName.equals(propertyName) && spInner2.propertyValueType.equals("static")
                                            && spInner2.staticValue.equals(propertyValue)) {
                                        match = true;
                                        break;
                                    }
                                }
                                if (match) {
                                    tmpListInner.add(soInner2);
                                }
                            }
                        } else {
                            for (DataObjectBase soInner2 : spInner.staticArray) {
                                boolean match = false;
                                for (DataProperty spInner2 : soInner2.propertyList) {
                                    if ((spInner2.propertyName.equals("名称") || spInner2.propertyName.equals("id"))
                                            && spInner2.propertyValueType.equals("static") && spInner2.staticValue.equals(split)) {
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
                        for (DataProperty spInner2 : spInner.customObject.propertyList) {
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
            if (parentData instanceof DataProperty) {
                DataProperty tmpProperty = (DataProperty) parentData;
                if (Repository.attachproperty2host.containsKey(tmpProperty)) {
                    parentData = Repository.attachproperty2host.get(tmpProperty);
                } else if (Repository.property2customobject.containsKey(tmpProperty)) {
                    parentData = Repository.customobject2host.get(Repository.property2customobject.get(tmpProperty));
                } else if (Repository.property2staticobject.containsKey(tmpProperty)) {
                    parentData = Repository.property2staticobject.get(tmpProperty);
                } else {
                    throw new ExceptionItem(PathUtil.getPropertyPath(Repository, spInner), "getPropertyPath error", null);
                }
            } else if (parentData instanceof DataObjectBase) {
                DataObjectBase tmpObject = (DataObjectBase) parentData;
                if (Repository.staticobject2host.containsKey(tmpObject)) {
                    parentData = Repository.staticobject2host.get(tmpObject);
                    soIndex = Repository.staticobject2index.get(tmpObject);
                } else if (parentData == Repository.dataObjectBase) {
                    break;
                } else {
                    throw new ExceptionItem(PathUtil.getPropertyPath(Repository, spInner), "getPropertyPath error", null);
                }
            }
            tmpIndexList.add(soIndex);
        }

        StringBuilder sb = new StringBuilder();
        for (int i = tmpList.size() - 1; i >= 0; i--) {
            Object obj = tmpList.get(i);
            if (obj instanceof DataProperty) {
                DataProperty ss = (DataProperty) obj;
                if (sb.length() > 0) {
                    sb.append("-");
                }
                sb.append(ss.propertyName);
            } else if (obj instanceof DataObjectBase) {
                Integer soIndex = tmpIndexList.get(i);
                if (soIndex != null) {
                    sb.append("[").append(soIndex).append("]");
                }
            }
        }
        return sb.toString();
    }

    public static String getDataPath(Object sv) throws Exception {
        return getDataPath(sv, new JSONArray());
    }

    public static String getDataPath(Object sv, JSONArray pathArray) throws Exception {
        List<Object> list = new CopyOnWriteArrayList<>();
        {
            Object tmp = sv;
            while (tmp != null) {
                list.add(0, tmp);
                if (tmp instanceof DataValue) {
                    DataValue tmpData = (DataValue) tmp;
                    tmp = tmpData.parentObjectData;
                } else if (tmp instanceof DataObject) {
                    DataObject tmpData = (DataObject) tmp;
                    tmp = tmpData.parentObjectData != null ? tmpData.parentObjectData : tmpData.parentArrayData;
                } else {
                    throw new Exception();
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < list.size(); i++) {
            Object tmp = list.get(i);
            if (tmp instanceof DataValue) {
                DataValue tmpData = (DataValue) tmp;
                sb.append(sb.length() > 0 ? "." : "").append(tmpData.myPropertyName);
                pathArray.add(tmpData.myPropertyName);
            } else if (tmp instanceof DataObject) {
                DataObject tmpData = (DataObject) tmp;
                if (tmpData.myPropertyName == null) {
                    DataValue sdvInner = null;
                    for (String keyName : AttributeFilteringUtil.keyProperty) {
                        if (tmpData.get(keyName) != null) {
                            sdvInner = tmpData.get(keyName);
                            break;
                        }
                    }
                    if (sdvInner != null) {
                        sb.append(sb.length() > 0 ? "." : "").append("[").append(sdvInner.valuePrim == null ? "null" : sdvInner.valuePrim.value).append("]");
                        pathArray.add(sdvInner.valuePrim == null ? "null" : sdvInner.valuePrim.value);
                    }
                } else {
                    sb.append(sb.length() > 0 ? "." : "").append(tmpData.myPropertyName);
                    pathArray.add(tmpData.myPropertyName);
                }
            }
        }
        return sb.toString();
    }

}
