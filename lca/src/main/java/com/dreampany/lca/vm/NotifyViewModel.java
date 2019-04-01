package com.dreampany.lca.vm;

import android.app.Application;
import android.view.TextureView;

import com.dreampany.frame.api.notify.NotifyManager;
import com.dreampany.frame.misc.AppExecutors;
import com.dreampany.frame.misc.ResponseMapper;
import com.dreampany.frame.misc.RxMapper;
import com.dreampany.frame.misc.exception.EmptyException;
import com.dreampany.frame.util.DataUtil;
import com.dreampany.frame.util.NumberUtil;
import com.dreampany.frame.util.TextUtil;
import com.dreampany.lca.R;
import com.dreampany.lca.app.App;
import com.dreampany.lca.data.enums.CoinSource;
import com.dreampany.lca.data.model.Coin;
import com.dreampany.lca.data.model.CoinAlert;
import com.dreampany.lca.data.model.Currency;
import com.dreampany.lca.data.model.Price;
import com.dreampany.lca.data.model.Quote;
import com.dreampany.lca.data.source.pref.Pref;
import com.dreampany.lca.data.source.repository.ApiRepository;
import com.dreampany.lca.data.source.repository.CoinAlertRepository;
import com.dreampany.lca.data.source.repository.PriceRepository;
import com.dreampany.lca.misc.Constants;
import com.dreampany.lca.misc.CurrencyFormatter;
import com.dreampany.lca.ui.activity.NavigationActivity;
import com.dreampany.lca.ui.model.CoinAlertItem;
import com.dreampany.lca.ui.model.CoinItem;
import com.dreampany.network.manager.NetworkManager;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import hugo.weaving.DebugLog;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.functions.Function;
import timber.log.Timber;

/**
 * Created by Hawladar Roman on 7/22/2018.
 * Dreampany Ltd
 * dreampanymail@gmail.com
 */
@Singleton
public class NotifyViewModel {

    private final Application application;
    private final RxMapper rx;
    private final Pref pref;
    private final ApiRepository repo;
    private final PriceRepository priceRepo;
    private final CoinAlertRepository alertRepo;
    private final CurrencyFormatter formatter;
    private final NotifyManager notify;

    private final Map<Coin, Price> prices;

    private boolean switching = true;

    @Inject
    NotifyViewModel(Application application,
                    RxMapper rx,
                    AppExecutors ex,
                    ResponseMapper rm,
                    NetworkManager network,
                    Pref pref,
                    ApiRepository repo,
                    PriceRepository priceRepo,
                    CoinAlertRepository alertRepo,
                    CurrencyFormatter formatter) {
        this.application = application;
        this.rx = rx;
        this.pref = pref;
        this.repo = repo;
        this.priceRepo = priceRepo;
        this.alertRepo = alertRepo;
        this.formatter = formatter;
        this.notify = new NotifyManager(application);
        prices = Maps.newConcurrentMap();
        //disposables = new CompositeDisposable();
    }

    public void clear() {
        //disposables.dispose();
    }

    public void notifyIf() {
/*        if (hasDisposable()) {
            //return;
        }*/
        Timber.v("notifyIf Processing");
        Currency currency = pref.getCurrency(Currency.USD);
        if (switching) {
            Maybe<List<CoinItem>> maybe = getProfitableItemsRx(currency);
            if (maybe != null) {
                rx
                        .backToMain(maybe)
                        .subscribe(result -> postResultCoins(currency, result), this::postFailed);
            } else {
                Timber.e("getProfitableItemsRx is Null");
            }
        } else {
            Maybe<List<CoinAlertItem>> maybe = getAlertItemsRx(currency);
            if (maybe != null) {
                rx
                        .backToMain(maybe)
                        .subscribe(this::postResultAlerts, this::postFailed);
            } else {
                Timber.e("getAlertItemsRx is Null");
            }
        }
        switching = !switching;

    }

    private Maybe<List<CoinItem>> getProfitableItemsRx(Currency currency) {
        return Maybe.create(emitter -> {
            int coinCount = repo.getCoinCount();
            int resultMax = coinCount > Constants.Limit.COIN_PAGE ? coinCount : Constants.Limit.COIN_PAGE;

            int listStart = (resultMax == Constants.Limit.COIN_PAGE) ? 0 : NumberUtil.nextRand((resultMax - Constants.Limit.COIN_PAGE) + 1);
            int listLimit = Constants.Limit.COIN_PAGE;
            List<CoinItem> result = repo
                    .getItemsIfRx(CoinSource.CMC, listStart, listLimit, currency)
                    .flatMap((Function<List<Coin>, MaybeSource<List<CoinItem>>>) coins -> getProfitableItemsRx(currency, coins))
                    .blockingGet();

            if (emitter.isDisposed()) {
                throw new IllegalStateException();
            }
            if (DataUtil.isEmpty(result)) {
                emitter.onError(new EmptyException());
            } else {
                emitter.onSuccess(result);
            }
        });
    }

    private Maybe<List<CoinAlertItem>> getAlertItemsRx(Currency currency) {
        return alertRepo
                .getItemsRx()
                .flatMap((Function<List<CoinAlert>, MaybeSource<List<CoinAlertItem>>>) alerts -> getAlertItemsRx(currency, alerts));
    }

    private Maybe<List<CoinItem>> getProfitableItemsRx(Currency currency, List<Coin> result) {
        return Flowable.fromIterable(result)
                .filter(this::isProfitable)
                .map(coin -> {
                    CoinItem item = CoinItem.getSimpleItem(coin, currency);
                    item.setFormatter(formatter);
                    return item;
                })
                .toList()
                .toMaybe();
    }

    private Maybe<List<CoinAlertItem>> getAlertItemsRx(Currency currency, List<CoinAlert> result) {
        return Flowable.fromIterable(result)
                .filter(this::isAlertable)
                .map(alert -> {
                    Coin coin = repo.getItemIf(CoinSource.CMC, alert.getSymbol(), currency);
                    return CoinAlertItem.getItem(coin, alert);
                }).toList()
                .toMaybe();
    }

    private void postResultCoins(Currency currency, List<CoinItem> items) {
        App app = (App) application;
        if (app.isVisible()) {
            //return;
        }

        String message;
        if (!DataUtil.isEmpty(items)) {
            CoinItem profitable = items.get(0);
            for (int index = 1; index < items.size(); index++) {
                Quote profitableQuote = profitable.getItem().getQuote(currency);
                Quote nextQuote = items.get(index).getItem().getQuote(currency);
                if (nextQuote.getDayChange() > profitableQuote.getDayChange()) {
                    profitable = items.get(index);
                }
            }
            Coin coin = profitable.getItem();
            double price = coin.getQuote(currency).getPrice();
            double dayChange = coin.getQuote(currency).getDayChange();
            message = formatter.formatPrice(coin.getSymbol(), coin.getName(), price, dayChange, currency);
        } else {
            message = TextUtil.getString(app, R.string.profitable_coins_motto);
        }
        String title = TextUtil.getString(app, R.string.notify_title_profit);
        notify.showNotification(title, message, R.drawable.ic_notification, NavigationActivity.class);
    }

    private void postResultAlerts(List<CoinAlertItem> items) {
        App app = (App) application;
        if (app.isVisible()) {
            //return;
        }
        if (DataUtil.isEmpty(items)) {
            return;
        }
        StringBuilder message = new StringBuilder();
        for (CoinAlertItem item : items) {
            if (message.length() > 0) {
                message.append(Constants.Sep.COMMA_SPACE);
            }
            Coin coin = item.getCoin();
            CoinAlert alert = item.getItem();
            message.append(item.getCoin().getSymbol());
            if (alert.hasPriceUp()) {
                message
                        .append(Constants.Sep.SPACE)
                        .append(Constants.Sep.UP)
                        .append(alert.getPriceUp());
            }
            if (alert.hasPriceDown()) {
                message
                        .append(Constants.Sep.SPACE)
                        .append(Constants.Sep.DOWN)
                        .append(alert.getPriceDown());
            }
        }
        String title = TextUtil.getString(app, R.string.notify_title_price_alert);
        notify.showNotification(
                title,
                message.toString(),
                R.drawable.ic_notification,
                Constants.Notify.ALERT_ID,
                Constants.Notify.ALERT_CHANNEL_ID,
                NavigationActivity.class);
    }

    @DebugLog
    private void postFailed(Throwable error) {

    }

    private boolean isProfitable(Coin coin) {
        Quote quote = coin.getQuote(Currency.USD);
        return quote.getDayChange() >= 0;
    }

    private boolean isAlertable(CoinAlert alert) {
        Coin coin = repo.getItemIf(CoinSource.CMC, alert.getSymbol(), Currency.USD);
        if (coin == null) {
            return false;
        }
        Quote quote = coin.getQuote(Currency.USD);
        if (alert.hasPriceUp() && quote.getPrice() > alert.getPriceUp()) {
            return true;
        }
        if (alert.hasPriceDown() && quote.getPrice() > alert.getPriceDown()) {
            return true;
        }
        return false;
    }
}
