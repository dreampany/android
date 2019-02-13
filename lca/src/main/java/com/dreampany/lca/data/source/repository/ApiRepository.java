package com.dreampany.lca.data.source.repository;

import com.annimon.stream.Stream;
import com.dreampany.frame.data.misc.StateMapper;
import com.dreampany.frame.data.model.State;
import com.dreampany.frame.data.source.repository.StateRepository;
import com.dreampany.frame.misc.ResponseMapper;
import com.dreampany.frame.misc.RxMapper;
import com.dreampany.frame.util.DataUtil;
import com.dreampany.frame.util.TimeUtil;
import com.dreampany.lca.data.enums.CoinSource;
import com.dreampany.lca.data.enums.ItemState;
import com.dreampany.lca.data.enums.ItemSubtype;
import com.dreampany.lca.data.enums.ItemType;
import com.dreampany.lca.data.misc.CoinMapper;
import com.dreampany.lca.data.model.Coin;
import com.dreampany.lca.data.model.Currency;
import com.dreampany.lca.data.source.pref.Pref;
import com.google.common.collect.Maps;
import io.reactivex.Maybe;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Roman-372 on 2/12/2019
 * Copyright (c) 2019 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */

@Singleton
public class ApiRepository {

    private final RxMapper rx;
    private final ResponseMapper rm;
    private final Pref pref;
    private final CoinMapper coinMapper;
    private final StateMapper stateMapper;
    private final CoinRepository coinRepo;
    private final StateRepository stateRepo;
    private final Map<Coin, Boolean> flags;

    @Inject
    ApiRepository(RxMapper rx,
                  ResponseMapper rm,
                  Pref pref,
                  CoinMapper coinMapper,
                  StateMapper stateMapper,
                  CoinRepository coinRepo,
                  StateRepository stateRepo) {
        this.rx = rx;
        this.rm = rm;
        this.pref = pref;
        this.coinMapper = coinMapper;
        this.stateMapper = stateMapper;
        this.coinRepo = coinRepo;
        this.stateRepo = stateRepo;
        flags = Maps.newConcurrentMap();
    }

    public boolean hasState(Coin coin, ItemSubtype subtype, ItemState state) {
        boolean stated = stateRepo.getCount(coin.getId(), ItemType.COIN.name(), subtype.name(), state.name()) > 0;
        return stated;
    }

    public long putState(Coin coin, ItemSubtype subtype, ItemState state) {
        State s = new State(coin.getId(), ItemType.COIN.name(), subtype.name(), state.name());
        s.setTime(TimeUtil.currentTime());
        long result = stateRepo.putItem(s);
        return result;
    }

    public int removeState(Coin coin, ItemSubtype subtype, ItemState state) {
        State s = new State(coin.getId(), ItemType.COIN.name(), subtype.name(), state.name());
        s.setTime(TimeUtil.currentTime());
        int result = stateRepo.delete(s);
        return result;
    }

    public long putFlag(Coin coin) {
       long result = putState(coin, ItemSubtype.DEFAULT, ItemState.FLAG);
        return result;
    }

    public int removeFlag(Coin coin) {
        int result = removeState(coin, ItemSubtype.DEFAULT, ItemState.FLAG);
        return result;
    }

    public boolean isFlagged(Coin coin) {
        if (!flags.containsKey(coin)) {
            boolean flagged = hasState(coin, ItemSubtype.DEFAULT, ItemState.FLAG);
            flags.put(coin, flagged);
        }
        return flags.get(coin);
    }

    public boolean toggleFlag(Coin coin) {
        boolean flagged = hasState(coin, ItemSubtype.DEFAULT, ItemState.FLAG);
        if (flagged){
            removeFlag(coin);
            flags.put(coin, false);
        } else {
            putFlag(coin);
            flags.put(coin, true);
        }
        return flags.get(coin);
    }

    public List<Coin> getItemsIf(CoinSource source, String[] symbols, Currency currency) {
        return coinRepo.getItems(source, symbols, currency);
    }

    public Coin getItemIf(CoinSource source, String symbol, Currency currency) {
        return coinRepo.getItemRx(source, symbol, currency).blockingGet();
    }

    public Maybe<List<Coin>> getItemsIfRx(CoinSource source, int index, int limit, Currency currency) {
        return coinRepo.getItemsRx(source, index, limit, currency);
    }

    public List<Coin> getFlags() {
        List<State> states = stateRepo.getItems(ItemType.COIN.name(), ItemSubtype.DEFAULT.name(), ItemState.FLAG.name());
        return getItemsIf(states);
    }

    private List<Coin> getItemsIf(List<State> states) {
        if (DataUtil.isEmpty(states)) {
            return null;
        }
        List<Coin> result = new ArrayList<>(states.size());
        Stream.of(states).forEach(state -> result.add(coinMapper.toItem(state, coinRepo)));
        return result;
    }
}
