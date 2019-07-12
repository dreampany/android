package com.dreampany.translation.data.source.firestore

import com.dreampany.firebase.RxFirebaseFirestore
import com.dreampany.network.manager.NetworkManager
import com.dreampany.translation.data.misc.TextTranslationMapper
import com.dreampany.translation.data.model.TextTranslation
import com.dreampany.translation.data.source.api.TranslationDataSource
import com.dreampany.translation.misc.Constants
import io.reactivex.Maybe
import javax.inject.Singleton

/**
 * Created by Roman-372 on 7/4/2019
 * Copyright (c) 2019 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
@Singleton
class FirestoreTranslationDataSource
constructor(
    val network: NetworkManager,
    val mapper: TextTranslationMapper,
    val firestore: RxFirebaseFirestore
) : TranslationDataSource {
    override fun isReady(target: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun ready(target: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val TRANSLATIONS = Constants.FirebaseKey.TRANSLATIONS

    override fun isExistsRx(input: String, source: String, target: String): Maybe<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isExists(input: String, source: String, target: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItem(input: String, source: String, target: String): TextTranslation {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isEmpty(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isEmptyRx(): Maybe<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCountRx(): Maybe<Int> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isExists(t: TextTranslation?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItem(id: String?): TextTranslation {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemRx(input: String, source: String, target: String): Maybe<TextTranslation> {
        return firestore.getItemRx(
            TRANSLATIONS,
            mapper.toId(input, source, target),
            TextTranslation::class.java
        )
    }

    override fun isExistsRx(t: TextTranslation?): Maybe<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun putItem(t: TextTranslation): Long {
        val id = mapper.toId(t)
        val error = firestore.setItemRx(TRANSLATIONS, id, t).blockingGet()
        return if (error == null) { 0L } else -1L
    }

    override fun putItemRx(t: TextTranslation): Maybe<Long> {
        return Maybe.fromCallable { putItem(t) }
    }

    override fun putItems(ts: MutableList<TextTranslation>?): MutableList<Long> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun putItemsRx(ts: MutableList<TextTranslation>?): Maybe<MutableList<Long>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(t: TextTranslation?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteRx(t: TextTranslation?): Maybe<Int> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(ts: MutableList<TextTranslation>?): MutableList<Long> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteRx(ts: MutableList<TextTranslation>?): Maybe<MutableList<Long>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemRx(id: String?): Maybe<TextTranslation> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItems(): MutableList<TextTranslation> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemsRx(): Maybe<MutableList<TextTranslation>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItems(limit: Int): MutableList<TextTranslation> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemsRx(limit: Int): Maybe<MutableList<TextTranslation>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}