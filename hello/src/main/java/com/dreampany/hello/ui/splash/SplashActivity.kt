package com.dreampany.hello.ui.splash

import android.os.Bundle
import com.dreampany.framework.misc.exts.open
import com.dreampany.framework.ui.activity.InjectActivity
import com.dreampany.hello.R
import com.dreampany.hello.ui.auth.AuthActivity
import com.dreampany.hello.ui.home.activity.HomeActivity
import kotlinx.coroutines.Runnable

/**
 * Created by roman on 3/10/20
 * Copyright (c) 2020 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
class SplashActivity : InjectActivity() {

    override val layoutRes: Int = R.layout.splash_activity

    override fun onStartUi(state: Bundle?) {
        initUi()
        ex.postToUi(Runnable { nextScreen() })
    }

    override fun onStopUi() {

    }

    private fun initUi() {
        //vm = ViewModelProvider(this, factory).get(AuthViewModel::class.java)
    }

    private fun nextScreen() {
        open(AuthActivity::class, true)
/*        if (vm.isJoinPressed()) {
            if (vm.isLoggedIn()) {
                open(HomeActivity::class, true)
            } else if (vm.isLoggedOut()) {
                open(LoginActivity::class, true)
            } else {
                open(AuthActivity::class, true)
            }
        } else {
            open(TutorialActivity::class, true)
        }*/
    }
}