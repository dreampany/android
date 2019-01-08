package com.dreampany.word.injector.ui

import com.dreampany.word.ui.fragment.FirstFragment
import com.dreampany.word.ui.fragment.ThirdFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector


/**
 * Created by Hawladar Roman on 6/29/2018.
 * BJIT Group
 * hawladar.roman@bjitgroup.com
 */
@Module
abstract class ThirdModule {
    @ContributesAndroidInjector
    abstract fun thirdFragment(): ThirdFragment;
}