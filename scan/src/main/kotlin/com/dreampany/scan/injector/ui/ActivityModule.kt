package com.dreampany.scan.injector.ui

import com.dreampany.frame.misc.ActivityScope
import com.dreampany.scan.ui.activity.LaunchActivity
import com.dreampany.scan.ui.activity.NavigationActivity
import com.dreampany.scan.ui.activity.ToolsActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


/**
 * Created by Hawladar Roman on 5/23/2018.
 * Dreampany Ltd
 * dreampanymail@gmail.com
 */
@Module
abstract class ActivityModule {
    @ActivityScope
    @ContributesAndroidInjector
    abstract fun launchActivity(): LaunchActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [HomeModule::class, FlagModule::class, MoreModule::class])
    abstract fun navigationActivity(): NavigationActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [AboutModule::class, SettingsModule::class])
    abstract fun toolsActivity(): ToolsActivity
}