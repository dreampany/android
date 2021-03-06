package com.dreampany.framework.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

/**
 * Created by roman on 31/5/20
 * Copyright (c) 2020 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
@Singleton
class WorkerInjectFactory
@Inject
constructor(
    private val factories: Map<Class<out ListenableWorker>, @JvmSuppressWildcards Provider<IWorkerFactory<out ListenableWorker>>>
) : WorkerFactory() {

    override fun createWorker(context: Context, workerClassName: String, params: WorkerParameters): ListenableWorker? {
        val entry = factories.entries.find { Class.forName(workerClassName).isAssignableFrom(it.key) }
        val factory = entry?.value ?: factories.entries.first().value//throw IllegalArgumentException("could not find worker: $workerClassName")
        return factory.get().create(context, params)
    }
}