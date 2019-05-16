package com.dreampany.test.injector.ui

import com.dreampany.test.ui.fragment.AboutFragment
import com.dreampany.frame.misc.FragmentScope
import dagger.Module
import dagger.android.ContributesAndroidInjector


/**
 * Created by Hawladar Roman on 5/25/2018.
 * BJIT Group
 * hawladar.roman@bjitgroup.com
 */

@Module
abstract class AboutModule {
    @FragmentScope
    @ContributesAndroidInjector
    abstract fun aboutFragment(): AboutFragment;
}