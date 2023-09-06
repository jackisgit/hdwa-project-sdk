package com.hdwa.sdk.entity.criteria;


import com.hdwa.sdk.entity.scene.SceneDataObject;
import com.hdwa.sdk.entity.scene.SceneDataValue;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CriteriaDefault extends CriteriaBase {
    public Map<String, List<MatchBase>> column2MatchList = new ConcurrentHashMap<String, List<MatchBase>>();

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
