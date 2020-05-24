package com.dreampany.tools.data.source.wifi.api

import com.dreampany.tools.data.model.wifi.Wifi

/**
 * Created by roman on 23/5/20
 * Copyright (c) 2020 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
interface WifiDataSource {
    @Throws
    suspend fun gets(): List<Wifi>?
}