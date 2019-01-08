package com.dreampany.lca.api.iwl.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Created by Hawladar Roman on 6/22/2018.
 * BJIT Group
 * hawladar.roman@bjitgroup.com
 */
public class LiveIcoResponse extends Response<Map<String, List<ApiIco>>> {
    public LiveIcoResponse(@JsonProperty(JSON_ICO_PROPERTY) Map<String, List<ApiIco>> ico) {
        super(ico);
    }

    public List<ApiIco> getIcos() {
        return ico.get(JSON_LIVE_ICO_PROPERTY);
    }
}
