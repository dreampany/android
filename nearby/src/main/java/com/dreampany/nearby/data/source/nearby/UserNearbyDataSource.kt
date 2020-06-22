package com.dreampany.nearby.data.source.nearby

import android.content.Context
import android.os.UserManager
import com.dreampany.nearby.data.model.User
import com.dreampany.nearby.data.source.api.UserDataSource
import com.dreampany.network.nearby.NearbyManager
import com.google.android.gms.nearby.connection.Strategy
import java.util.*

/**
 * Created by roman on 21/6/20
 * Copyright (c) 2020 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
class UserNearbyDataSource(
   private val context : Context,
   private  val mapper : UserManager,
   private val nearby : NearbyManager
) : UserDataSource {

    private val callbacks: MutableSet<UserDataSource.Callback>

    init {
        callbacks = Collections.synchronizedSet(HashSet<UserDataSource.Callback>())
    }

    override fun register(callback: UserDataSource.Callback) {
        callbacks.add(callback)
    }

    override fun unregister(callback: UserDataSource.Callback) {
        callbacks.remove(callback)
    }

    override fun startNearby(
        strategy: Strategy,
        serviceId: Long,
        user: User
    ) {
        nearby.init(strategy, serviceId, user.id.toLong(), null)
        nearby.start()
    }

    override fun stopNearby() {
        nearby.stop()
    }

    override suspend fun isFavorite(input: User): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun toggleFavorite(input: User): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getFavorites(): List<User>? {
        TODO("Not yet implemented")
    }

    override suspend fun put(input: User): Long {
        TODO("Not yet implemented")
    }

    override suspend fun put(inputs: List<User>): List<Long>? {
        TODO("Not yet implemented")
    }

    override suspend fun get(id: String): User? {
        TODO("Not yet implemented")
    }

    override suspend fun gets(): List<User>? {
        TODO("Not yet implemented")
    }

    override suspend fun gets(ids: List<String>): List<User>? {
        TODO("Not yet implemented")
    }

    override suspend fun gets(offset: Long, limit: Long): List<User>? {
        TODO("Not yet implemented")
    }
}