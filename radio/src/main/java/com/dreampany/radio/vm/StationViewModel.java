package com.dreampany.radio.vm;

import android.app.Application;

import com.dreampany.frame.misc.AppExecutors;
import com.dreampany.frame.misc.ResponseMapper;
import com.dreampany.frame.misc.RxMapper;
import com.dreampany.frame.ui.vm.BaseViewModel;
import com.dreampany.network.NetworkManager;
import com.dreampany.radio.data.model.Demo;
import com.dreampany.radio.ui.model.StationItem;
import com.dreampany.radio.ui.model.UiTask;

import javax.inject.Inject;

/**
 * Created by Hawladar Roman on 6/29/2018.
 * BJIT Group
 * hawladar.roman@bjitgroup.com
 */
public class StationViewModel extends BaseViewModel<Demo, StationItem, UiTask<Demo>> {

    private final NetworkManager network;

    @Inject
    StationViewModel(Application application,
                     RxMapper rx,
                     AppExecutors ex,
                     ResponseMapper rm,
                     NetworkManager network) {
        super(application, rx, ex, rm);
        this.network = network;
    }
}