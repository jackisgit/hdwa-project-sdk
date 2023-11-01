package com.hdwa.sdk.entity.criteria;

import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.entity.scene.DataObject;
import com.hdwa.sdk.entity.scene.DataValue;
import com.hdwa.sdk.utils.DataUtil;

import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author abao
 * @since 2023/9/20
 */
public class Match {

    public static class MatchStartwith extends MatchBase {
        public boolean pass;
        public String value;
        private boolean change = false;

        public MatchStartwith(Object value, boolean change) {
            if (value instanceof JSONObject) {
                JSONObject valueObject = (JSONObject) value;
                if (valueObject.containsKey("pass")) {
                    this.pass = true;
                    return;
                }
            }

            if (value instanceof String) {
                this.value = (String) value;
            }
            this.change = change;
        }

        public boolean match(DataValue item) {
            if (this.pass) {
                return true;
            }

            String itemValue = null;
            if (item != null) {
                itemValue = (String) (item.valuePrim.value);
            }

            if (itemValue == null) {
                return false;
            }

            return itemValue.toLowerCase().startsWith(this.value.toLowerCase());
        }

        public boolean change() {
            return this.change;
        }
    }


    public static class MatchRegex extends MatchBase {
        public boolean pass;
        public String value;
        Pattern regex;
        private boolean change = false;

        public MatchRegex(Object value, boolean change) {
            if (value instanceof JSONObject) {
                JSONObject valueObject = (JSONObject) value;
                if (valueObject.containsKey("pass")) {
                    this.pass = true;
                    return;
                }
            }

            if (value instanceof String) {
                this.value = (String) value;
            }
            if (this.value != null) {
                regex = Pattern.compile(this.value);
            }
            this.change = change;
        }

        public boolean match(DataValue item) {
            if (this.pass) {
                return true;
            }

            Object itemValue = null;
            if (item != null) {
                itemValue = (item.valuePrim.value);
            }

            if (itemValue == null) {
                return false;
            }

            return regex.matcher(itemValue.toString()).matches();
        }

        public boolean change() {
            return this.change;
        }
    }

    public static class MatchNotin extends MatchBase {
        public boolean pass;
        public HashSet<Object> value;
        private final boolean change;

        public MatchNotin(HashSet<Object> value, boolean change) {
            this.value = value;
            this.change = change;
        }

        public boolean match(DataValue item) {
            if (this.pass) {
                return true;
            }

            Object itemValue = null;
            if (item != null) {
                itemValue = (item.valuePrim.value);
            }
            itemValue = DataUtil.primitive_normalize(itemValue);

            return !this.value.contains(itemValue);
        }

        public boolean change() {
            return this.change;
        }
    }

    public static class MatchNe extends MatchBase {
        public boolean pass;
        public Object value;
        private boolean change = false;

        public MatchNe(Object value, boolean change) {
            if (value instanceof JSONObject) {
                JSONObject valueObject = (JSONObject) value;
                if (valueObject.containsKey("pass")) {
                    this.pass = true;
                    return;
                }
            }

            this.value = value;
            this.change = change;
        }

        public boolean match(DataValue item) {
            if (this.pass) {
                return true;
            }

            Object itemValue = null;
            if (item != null) {
                itemValue = (item.valuePrim.value);
            }

            if (itemValue == null && value == null) {
                return false;
            }
            if (itemValue == null) {
                return false;
            }
            if (value == null) {
                return true;
            }

            return MatchUtil.match("ne", itemValue, value);
        }

        public boolean change() {
            return this.change;
        }
    }


    public static class MatchLte extends MatchBase {
        public boolean pass;
        public Object value;
        private boolean change = false;

        public MatchLte(Object value, boolean change) {
            if (value instanceof JSONObject) {
                JSONObject valueObject = (JSONObject) value;
                if (valueObject.containsKey("pass")) {
                    this.pass = true;
                    return;
                }
            }

            this.value = value;
            this.change = change;
        }

        public boolean match(DataValue item) {
            if (this.pass) {
                return true;
            }

            Object itemValue = null;
            if (item != null) {
                itemValue = (item.valuePrim.value);
            }

            if (itemValue == null && value == null) {
                return true;
            }
            if (itemValue == null) {
                return true;
            }
            if (value == null) {
                return false;
            }

            return MatchUtil.match("lte", itemValue, value);
        }

        public boolean change() {
            return this.change;
        }
    }


    public static class MatchLt extends MatchBase {
        public boolean pass;
        public Object value;
        private boolean change = false;

        public MatchLt(Object value, boolean change) {
            if (value instanceof JSONObject) {
                JSONObject valueObject = (JSONObject) value;
                if (valueObject.containsKey("pass")) {
                    this.pass = true;
                    return;
                }
            }

            this.value = value;
            this.change = change;
        }

        public boolean match(DataValue item) {
            if (this.pass) {
                return true;
            }

            Object itemValue = null;
            if (item != null) {
                itemValue = (item.valuePrim.value);
            }

            if (itemValue == null && value == null) {
                return false;
            }
            if (itemValue == null) {
                return true;
            }
            if (value == null) {
                return false;
            }

            return MatchUtil.match("lt", itemValue, value);
        }

        public boolean change() {
            return this.change;
        }
    }


    public static class MatchIn extends MatchBase {
        public boolean pass;
        public HashSet<Object> value;
        private final boolean change;

        public MatchIn(HashSet<Object> value, boolean change) {
            this.value = value;
            this.change = change;
        }

        public boolean match(DataValue item) {
            if (this.pass) {
                return true;
            }

            Object itemValue = null;
            if (item != null && item.valuePrim != null) {
                itemValue = (item.valuePrim.value);
            }
            itemValue = DataUtil.primitive_normalize(itemValue);

            return this.value.contains(itemValue);
        }

        public boolean change() {
            return this.change;
        }
    }


    public static class MatchGte extends MatchBase {
        public boolean pass;
        public Object value;
        private boolean change = false;

        public MatchGte(Object value, boolean change) {
            if (value instanceof JSONObject) {
                JSONObject valueObject = (JSONObject) value;
                if (valueObject.containsKey("pass")) {
                    this.pass = true;
                    return;
                }
            }

            this.value = value;
            this.change = change;
        }

        public boolean match(DataValue item) {
            if (this.pass) {
                return true;
            }

            Object itemValue = null;
            if (item != null) {
                itemValue = (item.valuePrim.value);
            }

            if (itemValue == null && value == null) {
                return true;
            }
            if (itemValue == null) {
                return false;
            }
            if (value == null) {
                return true;
            }

            return MatchUtil.match("gte", itemValue, value);
        }

        public boolean change() {
            return this.change;
        }
    }

    public static class MatchGt extends MatchBase {
        public boolean pass;
        public Object value;
        private boolean change = false;

        public MatchGt(Object value, boolean change) {
            if (value instanceof JSONObject) {
                JSONObject valueObject = (JSONObject) value;
                if (valueObject.containsKey("pass")) {
                    this.pass = true;
                    return;
                }
            }

            this.value = value;
            this.change = change;
        }

        public boolean match(DataValue item) {
            if (this.pass) {
                return true;
            }

            Object itemValue = null;
            if (item != null) {
                itemValue = (item.valuePrim.value);
            }

            if (itemValue == null && value == null) {
                return false;
            }
            if (itemValue == null) {
                return false;
            }
            if (value == null) {
                return true;
            }

            return MatchUtil.match("gt", itemValue, value);
        }

        public boolean change() {
            return this.change;
        }
    }


    public static class MatchE extends MatchBase {
        public boolean pass;
        public Object value;
        private boolean change = false;

        public MatchE(Object value, boolean change) {
            if (value instanceof JSONObject) {
                JSONObject valueObject = (JSONObject) value;
                if (valueObject.containsKey("pass")) {
                    this.pass = true;
                    return;
                }
            }

            this.value = value;
            this.change = change;
        }

        public boolean match(DataValue item) {
            if (this.pass) {
                return true;
            }

            Object itemValue = null;
            if (item != null) {
                itemValue = (item.valuePrim.value);
            }

            if (itemValue == null && value == null) {
                return true;
            }
            if (itemValue == null) {
                return false;
            }
            if (value == null) {
                return false;
            }

            return MatchUtil.match("e", itemValue, value);
        }

        public boolean change() {
            return this.change;
        }
    }

    public static class MatchContain extends MatchBase {
        public boolean pass;
        public String value;
        private boolean change = false;

        public MatchContain(Object value, boolean change) {
            if (value instanceof JSONObject) {
                JSONObject valueObject = (JSONObject) value;
                if (valueObject.containsKey("pass")) {
                    this.pass = true;
                    return;
                }
            }

            if (value instanceof String) {
                this.value = (String) value;
            }
            this.change = change;
        }

        public boolean match(DataValue item) {
            if (this.pass) {
                return true;
            }

            String itemValue = null;
            if (item != null) {
                itemValue = (String) (item.valuePrim.value);
            }

            if (itemValue == null) {
                return false;
            }

            return itemValue.toLowerCase().contains(this.value.toLowerCase());
        }

        public boolean change() {
            return this.change;
        }
    }

    public static class MatchArraySize extends MatchBase {
        public boolean pass;
        public Object value;
        private boolean change = false;

        public MatchArraySize(Object value, boolean change) {
            if (value instanceof JSONObject) {
                JSONObject valueObject = (JSONObject) value;
                if (valueObject.containsKey("pass")) {
                    this.pass = true;
                    return;
                }
            }

            this.value = value;
            this.change = change;
        }

        public boolean match(DataValue item) {
            if (this.pass) {
                return true;
            }

            int itemValue = 0;
            if (item != null) {
                if (item.valueArray.isSingleValueSet) {
                    itemValue = item.valueArray.singleValueSet.size();
                } else {
                    itemValue = item.valueArray.set.size();
                }
            }

            boolean result;
            if (this.value instanceof Integer) {
                int valueInt = (Integer) this.value;
                result = itemValue == valueInt;
            } else {
                result = true;
                JSONObject valueJSON = (JSONObject) this.value;
                for (String key : valueJSON.keySet()) {
                    int valueInner = (Integer) valueJSON.get(key);
                    boolean match_result = MatchUtil.match(key, itemValue, valueInner);
                    if (!match_result) {
                        result = false;
                        break;
                    }
                }
            }
            return result;
        }

        public boolean change() {
            return this.change;
        }
    }


    public static class MatchArrayNe extends MatchBase {
        public boolean pass;
        public HashSet<Object> value;
        private final boolean change;

        public MatchArrayNe(HashSet<Object> value, boolean change) {
            this.value = value;
            this.change = change;
        }

        public boolean match(DataValue item) {
            if (this.pass) {
                return true;
            }

            List<DataValue> sdvList = null;
            if (item != null && item.valueArray != null) {
                sdvList = (item.valueArray.singleValueSet);
            }

            HashSet<Object> setInner = new HashSet<>();
            if (sdvList != null) {
                for (DataValue sdvInner : sdvList) {
                    setInner.add(DataUtil.primitive_normalize(sdvInner.valuePrim.value));
                }
            }

            boolean result = false;
            for (Object itemValue : this.value) {
                if (!setInner.contains(itemValue)) {
                    result = true;
                    break;
                }
            }
            if (result) {
                return true;
            }
            for (Object itemValue : setInner) {
                if (!this.value.contains(itemValue)) {
                    result = true;
                    break;
                }
            }
            return result;
        }

        public boolean change() {
            return this.change;
        }
    }

    public static class MatchArrayIntersect extends MatchBase {
        public boolean pass;
        public HashSet<Object> value;
        private final boolean change;

        public MatchArrayIntersect(HashSet<Object> value, boolean change) {
            this.value = value;
            this.change = change;
        }

        public boolean match(DataValue item) {
            if (this.pass) {
                return true;
            }

            List<DataValue> sdvList = null;
            if (item != null && item.valueArray != null) {
                sdvList = (item.valueArray.singleValueSet);
            }

            boolean result = false;
            if (sdvList != null) {
                for (DataValue sdvInner : sdvList) {
                    Object itemValue = sdvInner.valuePrim.value;
                    itemValue = DataUtil.primitive_normalize(itemValue);
                    if (this.value.contains(itemValue)) {
                        result = true;
                        break;
                    }
                }
            }
            return result;
        }

        public boolean change() {
            return this.change;
        }
    }

    public static class MatchArrayIncluded extends MatchBase {
        public boolean pass;
        public HashSet<Object> value;
        private final boolean change;

        public MatchArrayIncluded(HashSet<Object> value, boolean change) {
            this.value = value;
            this.change = change;
        }

        public boolean match(DataValue item) {
            if (this.pass) {
                return true;
            }

            List<DataValue> sdvList = null;
            if (item != null && item.valueArray != null) {
                sdvList = (item.valueArray.singleValueSet);
            }

            boolean result = true;
            if (sdvList != null) {
                for (DataValue sdvInner : sdvList) {
                    Object itemValue = sdvInner.valuePrim.value;
                    itemValue = DataUtil.primitive_normalize(itemValue);
                    if (!this.value.contains(itemValue)) {
                        result = false;
                        break;
                    }
                }
            }
            return result;
        }

        public boolean change() {
            return this.change;
        }
    }

    public static class MatchArrayInclude extends MatchBase {
        public boolean pass;
        public HashSet<Object> value;
        private final boolean change;

        public MatchArrayInclude(HashSet<Object> value, boolean change) {
            this.value = value;
            this.change = change;
        }

        public boolean match(DataValue item) {
            if (this.pass) {
                return true;
            }

            List<DataValue> sdvList = null;
            if (item != null && item.valueArray != null) {
                sdvList = (item.valueArray.singleValueSet);
            }

            HashSet<Object> setInner = new HashSet<>();
            if (sdvList != null) {
                for (DataValue sdvInner : sdvList) {
                    setInner.add(DataUtil.primitive_normalize(sdvInner.valuePrim.value));
                }
            }

            boolean result = true;
            for (Object itemValue : this.value) {
                if (!setInner.contains(itemValue)) {
                    result = false;
                    break;
                }
            }
            return result;
        }

        public boolean change() {
            return this.change;
        }
    }


    public static class MatchArrayExclude extends MatchBase {
        public boolean pass;
        public HashSet<Object> value;
        private final boolean change;

        public MatchArrayExclude(HashSet<Object> value, boolean change) {
            this.value = value;
            this.change = change;
        }

        public boolean match(DataValue item) {
            if (this.pass) {
                return true;
            }

            List<DataValue> sdvList = null;
            if (item != null && item.valueArray != null) {
                sdvList = (item.valueArray.singleValueSet);
            }

            boolean result = true;
            if (sdvList != null) {
                for (DataValue sdvInner : sdvList) {
                    Object itemValue = sdvInner.valuePrim.value;
                    itemValue = DataUtil.primitive_normalize(itemValue);
                    if (this.value.contains(itemValue)) {
                        result = false;
                        break;
                    }
                }
            }
            return result;
        }

        public boolean change() {
            return this.change;
        }
    }


    public static class MatchArrayElemMatchExist extends MatchBase {
        public boolean pass;
        public CriteriaBase value;
        private final boolean change;

        public MatchArrayElemMatchExist(CriteriaBase value, boolean change) {
            this.value = value;
            this.change = change;
        }

        public boolean match(DataValue item) {
            if (this.pass) {
                return true;
            }

            if (item == null) {
                return false;
            }
            if (item.valueObject != null) {
                DataObject sdo = item.valueObject;
                return this.value.match(sdo);
            } else {
                boolean result = true;
                for (DataObject sdo : item.valueArray.set) {
                    boolean match_result = this.value.match(sdo);
                    if (!match_result) {
                        result = false;
                        break;
                    }
                }
                return result;
            }
        }

        public boolean change() {
            return this.change;
        }
    }


    public static class MatchArrayElemMatchAll extends MatchBase {
        public boolean pass;
        public CriteriaBase value;
        private final boolean change;

        public MatchArrayElemMatchAll(CriteriaBase value, boolean change) {
            this.value = value;
            this.change = change;
        }

        public boolean match(DataValue item) {
            if (this.pass) {
                return true;
            }

            if (item == null) {
                return false;
            }
            if (item.valueObject != null) {
                DataObject sdo = item.valueObject;
                return this.value.match(sdo);
            } else {
                boolean result = false;
                for (DataObject sdo : item.valueArray.set) {
                    boolean match_result = this.value.match(sdo);
                    if (match_result) {
                        result = true;
                        break;
                    }
                }
                return result;
            }
        }

        public boolean change() {
            return this.change;
        }
    }


    public static class MatchArrayE extends MatchBase {
        public boolean pass;
        public HashSet<Object> value;
        private boolean change;

        public MatchArrayE(HashSet<Object> value, boolean change) {
            this.value = value;
            this.change = change;
        }

        public boolean match(DataValue item) {
            if (this.pass) {
                return true;
            }

            List<DataValue> sdvList = null;
            if (item != null && item.valueArray != null) {
                sdvList = (item.valueArray.singleValueSet);
            }

            HashSet<Object> setInner = new HashSet<>(16);
            if (sdvList != null) {
                for (DataValue sdvInner : sdvList) {
                    setInner.add(DataUtil.primitive_normalize(sdvInner.valuePrim.value));
                }
            }

            boolean result = true;
            for (Object itemValue : this.value) {
                if (!setInner.contains(itemValue)) {
                    result = false;
                    break;
                }
            }
            if (!result) {
                return false;
            }
            for (Object itemValue : setInner) {
                if (!this.value.contains(itemValue)) {
                    result = false;
                    break;
                }
            }
            return result;
        }

        public boolean change() {
            return this.change;
        }
    }
}
