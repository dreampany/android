package com.dreampany.cast.injector.ui

import com.dreampany.cast.ui.activity.LaunchActivity
import com.dreampany.cast.ui.activity.NavigationActivity
import com.dreampany.cast.ui.activity.ToolsActivity
import com.dreampany.frame.misc.ActivityScope
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
    @ContributesAndroidInjector(modules = [HomeModule::class, MoreModule::class])
    abstract fun navigationActivity(): NavigationActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [SettingsModule::class, LicenseModule::class, AboutModule::class])
    abstract fun toolsActivity(): ToolsActivity
}