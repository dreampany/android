package com.dreampany.tube.data.source.room

import com.dreampany.tube.data.model.Category
import com.dreampany.tube.data.source.api.CategoryDataSource
import com.dreampany.tube.data.source.mapper.CategoryMapper
import com.dreampany.tube.data.source.room.dao.CategoryDao

/**
 * Created by roman on 1/7/20
 * Copyright (c) 2020 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
class CategoryRoomDataSource(
    private val mapper: CategoryMapper,
    private val dao: CategoryDao
) : CategoryDataSource {
    override suspend fun isFavorite(input: Category): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun toggleFavorite(input: Category): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getFavorites(): List<Category>? {
        TODO("Not yet implemented")
    }

    override suspend fun put(input: Category): Long {
        TODO("Not yet implemented")
    }

    override suspend fun put(inputs: List<Category>): List<Long>? {
        TODO("Not yet implemented")
    }

    override suspend fun get(id: String): Category? {
        TODO("Not yet implemented")
    }

    override suspend fun gets(): List<Category>? {
        TODO("Not yet implemented")
    }

    override suspend fun gets(regionCode: String): List<Category>? {
        TODO("Not yet implemented")
    }

    override suspend fun gets(ids: List<String>): List<Category>? {
        TODO("Not yet implemented")
    }

    override suspend fun gets(offset: Long, limit: Long): List<Category>? {
        TODO("Not yet implemented")
    }

    @Throws
    override suspend fun deleteAll() = dao.deleteAll()
}