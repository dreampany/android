package com.dreampany.lca.data.misc;

import com.dreampany.frame.misc.SmartCache;
import com.dreampany.frame.misc.SmartMap;
import com.dreampany.frame.util.DataUtil;
import com.dreampany.frame.util.TimeUtil;
import com.dreampany.lca.api.cc.model.CcExchange;
import com.dreampany.lca.data.model.Exchange;
import com.dreampany.lca.misc.ExchangeAnnote;

import javax.inject.Inject;

/**
 * Created by Hawladar Roman on 5/31/2018.
 * BJIT Group
 * hawladar.roman@bjitgroup.com
 */
public class ExchangeMapper {

    private final SmartMap<Long, Exchange> map;
    private final SmartCache<Long, Exchange> cache;

    @Inject
    ExchangeMapper(@ExchangeAnnote SmartMap<Long, Exchange> map,
                   @ExchangeAnnote SmartCache<Long, Exchange> cache) {
        this.map = map;
        this.cache = cache;
    }

    public Exchange toExchange(CcExchange in) {
        if (in == null) {
            return null;
        }
        long id = DataUtil.getSha512(in.getExchange(), in.getFromSymbol(), in.getToSymbol());
        Exchange out = map.get(id);
        if (out == null) {
            out = new Exchange();
            map.put(id, out);
        }
        out.setId(id);
        out.setTime(TimeUtil.currentTime());
        out.setExchange(in.getExchange());
        out.setFromSymbol(in.getFromSymbol());
        out.setToSymbol(in.getToSymbol());
        out.setVolume24h(in.getVolume24h());
        out.setVolume24hTo(in.getVolume24hTo());
        return out;
    }
}
