package com.dreampany.pair.data.source.auth

import com.dreampany.pair.data.mapper.Mappers
import com.dreampany.pair.data.model.User
import com.dreampany.pair.data.source.api.RegistrationDataSource
import com.google.firebase.auth.FirebaseAuth
import durdinapps.rxfirebase2.RxFirebaseAuth

/**
 * Created by roman on 14/3/20
 * Copyright (c) 2020 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
class RegistrationAuthDataSource
constructor(
    val mappers: Mappers
) : RegistrationDataSource {
    @Throws
    override suspend fun register(
        email: String,
        password: String,
        name: String
    ): User? {
        return doRegister(email, password, name)
    }

    override suspend fun save(user: User): Long {
        TODO("Not yet implemented")
    }

    @Throws
    private fun doRegister(
        email: String,
        password: String,
        name: String
    ): User? {
        val auth = FirebaseAuth.getInstance()
        val fbUser =
            RxFirebaseAuth.createUserWithEmailAndPassword(auth, email, password).blockingGet().user
        return if (fbUser != null) mappers.getUser(fbUser, name) else null
    }

}