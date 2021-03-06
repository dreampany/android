package com.dreampany.play2048.vm;

import android.app.Application;

import com.dreampany.play2048.app.App;
import com.dreampany.play2048.ui.model.DemoItem;
import com.dreampany.framework.api.notify.NotifyManager;
import com.dreampany.framework.misc.AppExecutors;
import com.dreampany.framework.misc.ResponseMapper;
import com.dreampany.framework.misc.RxMapper;
import com.dreampany.network.NetworkManager;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;


import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
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
    private final NotifyManager notify;
    private Disposable disposable;

    @Inject
    NotifyViewModel(Application application,
                    RxMapper rx,
                    AppExecutors ex,
                    ResponseMapper rm,
                    NetworkManager network) {
        this.application = application;
        this.rx = rx;
        this.notify = new NotifyManager();
    }

    public void clear() {
        if (hasDisposable()) {
            disposable.dispose();
        }
    }

    public void notifyIf() {
        if (true) {
            return;
        }
        if (hasDisposable()) {
            //return;
        }
        Timber.v("Processing");
        this.disposable = rx.backToMain(getDemoItems())
                .subscribe(this::postResult, this::postFailed);
    }

    private Flowable<List<DemoItem>> getDemoItems() {
        return null;
    }


    private boolean hasDisposable() {
        return disposable != null && !disposable.isDisposed();
    }


    private void postResult(List<DemoItem> items) {
        App app = (App) application;
        if (app.isVisible()) {
            //return;
        }
        Timber.v("Visible %s", app.isVisible());
/*        String title = TextUtil.getString(application, R.string.app_name);
        String message;
        if (!DataUtil.isEmpty(items)) {
            message = TextUtil.getString(app, R.string.profitable_coins, items.size());
        } else {
            message = TextUtil.getString(app, R.string.profitable_coins_motto);
        }
        notify.showNotification(application, title, message, NavigationActivity.class);*/
    }


    private void postFailed(Throwable error) {

    }
}
