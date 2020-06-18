package com.dreampany.tools.inject.ui.news

import com.dreampany.framework.inject.annote.ActivityScope
import com.dreampany.tools.ui.news.activity.NewsActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by roman on 14/6/20
 * Copyright (c) 2020 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
@Module
abstract class NewsModule {
    @ActivityScope
    @ContributesAndroidInjector(modules = [ArticlesModule::class])
    abstract fun news(): NewsActivity
}