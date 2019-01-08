package com.dreampany.lca.injector.ui

import com.dreampany.frame.misc.FragmentScope
import com.dreampany.lca.ui.fragment.IcoFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector


/**
 * Created by Hawladar Roman on 5/25/2018.
 * BJIT Group
 * hawladar.roman@bjitgroup.com
 */

@Module
abstract class IcoModule {
    @FragmentScope
    @ContributesAndroidInjector(modules = [LiveIcoModule::class, UpcomingIcoModule::class, FinishedIcoModule::class])
    abstract fun icoFragment(): IcoFragment;
}