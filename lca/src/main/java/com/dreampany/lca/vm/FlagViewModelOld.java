/*
package com.dreampany.lca.vm;


import android.app.Application;
import com.dreampany.frame.data.enums.UiState;
import com.dreampany.frame.misc.AppExecutors;
import com.dreampany.frame.misc.ResponseMapper;
import com.dreampany.frame.misc.RxMapper;
import com.dreampany.frame.misc.SmartMap;
import com.dreampany.frame.misc.exception.ExtraException;
import com.dreampany.frame.misc.exception.MultiException;
import com.dreampany.frame.ui.adapter.SmartAdapter;
import com.dreampany.frame.util.DataUtil;
import com.dreampany.frame.vm.BaseViewModel;
import com.dreampany.lca.data.enums.CoinSource;
import com.dreampany.lca.data.model.Coin;
import com.dreampany.lca.data.model.Currency;
import com.dreampany.lca.data.source.pref.Pref;
import com.dreampany.lca.data.source.repository.CoinRepository;
import com.dreampany.lca.ui.model.CoinItem;
import com.dreampany.lca.ui.model.UiTask;
import com.dreampany.network.NetworkManager;
import com.dreampany.network.data.model.Network;
import hugo.weaving.DebugLog;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

*/
/**
 * Created by Hawladar Roman on 5/31/2018.
 * BJIT Group
 * hawladar.roman@bjitgroup.com
 *//*

public class FlagViewModelOld extends BaseViewModel<Coin, CoinItem, UiTask<Coin>> {

    private static final boolean OPEN = true;

    private final NetworkManager network;
    private final Pref pref;
    private final CoinRepository repo;
    private SmartAdapter.Callback<CoinItem> uiCallback;
    private Disposable updateDisposable;

    @Inject
    FlagViewModelOld(Application application,
                     RxMapper rx,
                     AppExecutors ex,
                     ResponseMapper rm,
                     NetworkManager network,
                     Pref pref,
                     CoinRepository repo) {
        super(application, rx, ex, rm);
        this.network = network;
        this.pref = pref;
        this.repo = repo;
        network.observe(this::onResult, true);
    }

    @Override
    public void clear() {
        network.deObserve(this::onResult, true);
        this.uiCallback = null;
        removeUpdateDisposable();
        super.clear();
    }

    void onResult(Network... networks) {
        UiState state = UiState.OFFLINE;
        for (Network network : networks) {
            if (network.isConnected()) {
                state = UiState.ONLINE;
                //getEx().postToUi(this::updateItem, 2000L);
            }
        }
        UiState finalState = state;
        //getEx().postToUiSmartly(() -> updateUiState(finalState));
    }

    public void setUiCallback(SmartAdapter.Callback<CoinItem> callback) {
        this.uiCallback = callback;
    }

    public void removeUpdateDisposable() {
        removeSubscription(updateDisposable);
    }

    public void refresh(boolean onlyUpdate, boolean withProgress) {
        if (onlyUpdate) {
            update(withProgress);
            return;
        }
        loads(true, withProgress);
    }

    @DebugLog
    public void loads(boolean fresh, boolean withProgress) {
        if (!OPEN) {
            return;
        }
        if (!preLoads(fresh)) {
            return;
        }
        Currency currency = Currency.USD;
        Disposable disposable = getRx()
                .backToMain(getFlagItemsRx(currency))
                .doOnSubscribe(subscription -> postProgress(true))
                .subscribe(
                        result -> postResult(result, withProgress),
                        error -> postFailureMultiple(new MultiException(error, new ExtraException()))
                );
        addMultipleSubscription(disposable);
    }

    public void update(boolean withProgress) {
        if (!OPEN) {
            return;
        }
        if (hasDisposable(updateDisposable)) {
            return;
        }
        Currency currency = Currency.USD;
        updateDisposable = getRx()
                .backToMain(getVisibleItemsIfRx(currency))
                .subscribe(result -> postResult(result, withProgress), this::postFailure);
        addSubscription(updateDisposable);
    }

    public void toggle(Coin coin) {
        if (!OPEN) {
            return;
        }
        if (hasSingleDisposable()) {
            return;
        }
        Disposable disposable = getRx()
                .backToMain(toggleImpl(coin))
                .subscribe(this::postFlag, this::postFailure);
        addSingleSubscription(disposable);
    }

    */
/** private api *//*

    private Maybe<List<CoinItem>> getFlagItemsRx(Currency currency) {
        return Maybe.fromCallable(() -> {
            List<CoinItem> result = new ArrayList<>();
            List<Coin> real = repo.getFlags();
            if (real == null) {
                real = new ArrayList<>();
            }
            List<CoinItem> ui = uiCallback.getItems();
            for (Coin coin : real) {
                CoinItem item = getItem(coin);
                item.setFlagged(true);
                result.add(item);
            }

            if (!DataUtil.isEmpty(ui)) {
                for (CoinItem item : ui) {
                    if (!real.contains(item.getItem())) {
                        item.setFlagged(false);
                        result.add(item);
                    }
                }
            }

            Timber.v("Flag Result in VM %d", result.size());
            return result;
        });
    }

    private Maybe<List<CoinItem>> getVisibleItemsIfRx(Currency currency) {
        return Maybe.fromCallable(() -> getVisibleItemsIf(currency));
    }

    private List<CoinItem> getVisibleItemsIf(Currency currency) {
        if (uiCallback == null) {
            return null;
        }
        List<CoinItem> items = uiCallback.getVisibleItems();
        if (!DataUtil.isEmpty(items)) {
            List<String> symbols = new ArrayList<>();
            for (CoinItem item : items) {
                symbols.add(item.getItem().getSymbol());
            }
            items = null;
            if (!DataUtil.isEmpty(symbols)) {
                String[] result = DataUtil.toStringArray(symbols);
                List<Coin> coins = repo.getItems(CoinSource.CMC, result, currency);
                if (!DataUtil.isEmpty(coins)) {
                    items = getItems(coins);
                }
            }
        }
        return items;
    }

    private CoinItem getItem(Coin coin) {
        SmartMap<Long, CoinItem> map = getUiMap();
        CoinItem item = map.get(coin.getId());
        if (item == null) {
            item = CoinItem.getSimpleItem(coin);
            map.put(coin.getId(), item);
        }
        item.setItem(coin);
        adjustFlag(coin, item);
        return item;
    }

    private List<CoinItem> getItems(List<Coin> result) {
        List<Coin> coins = new ArrayList<>(result);
        List<Coin> ranked = new ArrayList<>();
        for (Coin coin : coins) {
            if (coin.getRank() > 0) {
                ranked.add(coin);
            }
        }
        Collections.sort(ranked, (left, right) -> left.getRank() - right.getRank());
        coins.removeAll(ranked);
        coins.addAll(0, ranked);

        List<CoinItem> items = new ArrayList<>(coins.size());
        for (Coin coin : coins) {
            CoinItem item = getItem(coin);
            items.add(item);
        }
        return items;
    }

    private void adjustFlag(Coin coin, CoinItem item) {
        boolean flagged = repo.isFlagged(coin);
        item.setFlagged(flagged);
    }

    //todo need to improve for flowable and completable working
    private Flowable<CoinItem> toggleImpl(Coin coin) {
        return Flowable.fromCallable(() -> {
            repo.toggleFlag(coin);
            CoinItem item = getItem(coin);
            return item;
        });
    }
}
*/
