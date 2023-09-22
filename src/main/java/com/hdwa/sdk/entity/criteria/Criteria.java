package com.hdwa.sdk.entity.criteria;

import com.hdwa.sdk.entity.scene.SceneDataObject;
import com.hdwa.sdk.entity.scene.SceneDataValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author abao
 * @since 2023/9/20
 */
public class Criteria {

    public static class CriteriaDefault extends CriteriaBase {
        public Map<String, List<MatchBase>> column2MatchList = new HashMap<>(16);

        public CriteriaDefault() {
            this.type = "default";
        }

        public boolean match(SceneDataObject item) {
            for (String column : column2MatchList.keySet()) {
                List<MatchBase> matchList = column2MatchList.get(column);
                SceneDataValue sdv = item.get(column);
                for (MatchBase match : matchList) {
                    if (!match.match(sdv)) {
                        return false;
                    }
                }
            }
            return true;
        }
    }


    public static class CriteriaOr extends CriteriaBase {
        public List<CriteriaBase> criteriaList;

        public CriteriaOr() {
            this.type = "or";
        }

        public boolean match(SceneDataObject item) {
            for (CriteriaBase criteria : this.criteriaList) {
                if (criteria.match(item)) {
                    return true;
                }
            }
            return false;
        }
    }


    public static class CriteriaNot extends CriteriaBase {
        public CriteriaBase criteria;

        public CriteriaNot() {
            this.type = "not";
        }

        public boolean match(SceneDataObject item) {
            return !criteria.match(item);
        }
    }

    public static class CriteriaAnd extends CriteriaBase {
        public List<CriteriaBase> criteriaList;

        public CriteriaAnd() {
            this.type = "and";
        }

        public boolean match(SceneDataObject item) {
            for (CriteriaBase criteria : this.criteriaList) {
                if (!criteria.match(item)) {
                    return false;
                }
            }
            return true;
        }
    }

}
