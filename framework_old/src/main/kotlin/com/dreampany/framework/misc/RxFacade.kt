package com.dreampany.framework.misc

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by roman on 2019-07-09
 * Copyright (c) 2019 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
@Singleton
class RxFacade @Inject constructor() {

    fun io(): Scheduler {
        return Schedulers.io()
    }

    fun compute(): Scheduler {
        return Schedulers.computation()
    }

    fun ui(): Scheduler {
        return AndroidSchedulers.mainThread()
    }
}