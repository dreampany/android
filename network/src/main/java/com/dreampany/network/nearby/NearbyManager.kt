package com.dreampany.network.nearby

import android.Manifest
import android.content.Context
import com.dreampany.network.misc.hasPermission
import com.dreampany.network.misc.hasPlayService
import com.dreampany.network.nearby.core.NearbyApi
import com.dreampany.network.nearby.core.Packets.Companion.dataPacket
import com.dreampany.network.nearby.model.Id
import com.google.android.gms.nearby.connection.Strategy
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by roman on 20/6/20
 * Copyright (c) 2020 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
@Singleton
class NearbyManager
@Inject constructor(
    context: Context
) : NearbyApi(context) {

    private lateinit var strategy: Strategy
    private var serviceId: Long = 0L
    private var peerId: Long = 0L
    private var peerData: ByteArray? = null

    @Throws(Throwable::class)
    fun init(strategy: Strategy, serviceId: Long, peerId: Long, peerData: ByteArray?) : Boolean {
        synchronized(guard) {
            this.strategy = strategy
            this.serviceId = serviceId
            this.peerId = peerId

            if (!context.hasPlayService) {
                throw IllegalStateException("Google Play service is not available!")
            }

            if (!context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                throw IllegalStateException("Location permission is missing!")
            }

            inited = true
        }
        return inited
    }

    fun register(callback: NearbyApi.Callback) {
        callbacks.add(callback)
    }

    fun unregister(callback: NearbyApi.Callback) {
        callbacks.remove(callback)
    }

    fun start() {
        synchronized(guard) {
            super.start(strategy, serviceId, peerId)
        }
    }

    override fun stop() {
        synchronized(guard) {
            super.stop()
        }
    }

    fun send(id: Id, data: ByteArray, timeout : Long) {
        synchronized(guard) {
            val packet = data.dataPacket
            sendPacket(id, packet, timeout)
        }
    }

}