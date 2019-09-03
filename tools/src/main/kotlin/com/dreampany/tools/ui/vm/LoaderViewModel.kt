package com.dreampany.tools.ui.vm

import android.app.Application
import com.dreampany.frame.data.enums.Action
import com.dreampany.frame.data.enums.State
import com.dreampany.frame.data.enums.Subtype
import com.dreampany.frame.data.enums.Type
import com.dreampany.frame.data.misc.StoreMapper
import com.dreampany.frame.data.model.Store
import com.dreampany.frame.data.source.repository.StoreRepository
import com.dreampany.frame.misc.*
import com.dreampany.frame.ui.model.UiTask
import com.dreampany.frame.util.AndroidUtil
import com.dreampany.frame.util.DataUtil
import com.dreampany.frame.util.DataUtilKt
import com.dreampany.frame.ui.vm.BaseViewModel
import com.dreampany.network.manager.NetworkManager
import com.dreampany.tools.data.misc.LoadRequest
import com.dreampany.tools.data.misc.WordMapper
import com.dreampany.tools.data.model.Load
import com.dreampany.tools.data.model.Word
import com.dreampany.tools.data.source.pref.Pref
import com.dreampany.tools.data.source.pref.WordPref
import com.dreampany.tools.data.source.repository.WordRepository
import com.dreampany.tools.misc.Constants
import com.dreampany.tools.ui.model.LoadItem
import com.dreampany.tools.ui.model.WordItem
import com.dreampany.translation.data.source.repository.TranslationRepository
import kotlinx.coroutines.Runnable
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * Created by Roman-372 on 8/19/2019
 * Copyright (c) 2019 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
class LoaderViewModel
@Inject constructor(
    application: Application,
    rx: RxMapper,
    ex: AppExecutors,
    rm: ResponseMapper,
    private val network: NetworkManager,
    private val pref: Pref,
    private val wordPref: WordPref,
    private val storeMapper: StoreMapper,
    private val storeRepo: StoreRepository,
    private val mapper: WordMapper,
    private val repo: WordRepository,
    private val translationRepo: TranslationRepository,
    @Favorite private val favorites: SmartMap<String, Boolean>
) : BaseViewModel<Load, LoadItem, UiTask<Load>>(application, rx, ex, rm) {

    private val commonWords = mutableListOf<Word>()
    private val alphaWords = mutableListOf<Word>()
    private var commonLoading = false
    private var alphaLoading = false

    fun request(request: LoadRequest) {
        when (request.type) {
            Type.WORD -> {
                requestWord(request)
            }
        }

    }

    private fun requestWord(request: LoadRequest) {
        when (request.action) {
            Action.LOAD -> {
                loadWords(request)
            }
            Action.SYNC -> {
                ex.postToNetwork(Runnable {
                    syncWord(request)
                })
            }
        }
    }


    /*First Layer*/
    private fun loadWords(request: LoadRequest) {
        if (!wordPref.isCommonLoaded() && !commonLoading) {
            ex.postToNetwork(Runnable {
                commonLoading = true
                loadCommons(request)
                commonLoading = false
                request(request)
            })
            return
        }
        if (!wordPref.isAlphaLoaded() && !alphaLoading) {
            ex.postToNetwork(Runnable {
                alphaLoading = true
                loadAlphas(request)
                alphaLoading = false
            })
        }
    }

    private fun syncWord(request: LoadRequest) {
        val rawStore = storeRepo.getItem(Type.WORD, Subtype.DEFAULT, State.RAW)
        rawStore?.run {
            Timber.v("Sync Word/.. %s", this.toString())
            var item = mapper.getItem(this, repo)
            item?.run {
                val uiItem = getUiItem(request, this)
            }
        }
    }


    /*Second Layer*/
    private fun loadCommons(request: LoadRequest) {
        buildCommonWords()

        var current = storeRepo.getCountByType(Type.WORD, Subtype.DEFAULT, State.RAW)
        val load = Load(current = current, total = current)
        val item = LoadItem.getItem(load)
        ex.postToUi(Runnable { postResult(request.action, item) })

        val last = wordPref.getLastWord()
        val lastIndex = if (last != null) commonWords.indexOf(last) else -1

        if (lastIndex > 0) {
            DataUtil.removeFirst(commonWords, lastIndex + 1)
        }

        while (!commonWords.isEmpty()) {
            val words = DataUtilKt.takeFirst(commonWords, Constants.Count.WORD_PAGE)
            if (words.isNullOrEmpty()) {
                continue
            }
            var resultOf = repo.putItems(words)
            if (DataUtil.isEqual(words, resultOf)) {
                val states = ArrayList<Store>()
                words.forEach { word ->
                    states.add(Store(word.id, Type.WORD, Subtype.DEFAULT, State.RAW))
                }
                resultOf = storeRepo.putItems(states)
            }

            if (DataUtil.isEqual(words, resultOf)) {
                val lastWord = DataUtil.pullLast(words)
                wordPref.setLastWord(lastWord)
                current = storeRepo.getCountByType(Type.WORD, Subtype.DEFAULT, State.RAW)
                load.current = current
                load.total = current

                Timber.v("%d Last Common Word = %s", current, lastWord!!.id)
                ex.postToUi(Runnable { postResult(request.action, item) })
                AndroidUtil.sleep(100)
            }
        }
        if (commonWords.isEmpty()) {
            wordPref.commitCommonLoaded()
            wordPref.clearLastWord()
        }
    }

    private fun loadAlphas(request: LoadRequest) {
        buildAlphaWords()
        var current = storeRepo.getCountByType(Type.WORD, Subtype.DEFAULT, State.RAW)
        val load = Load(current = current, total = current)
        val item = LoadItem.getItem(load)
        ex.postToUi(Runnable { postResult(request.action, item) })

        val last = wordPref.getLastWord()
        val lastIndex = if (last != null) alphaWords.indexOf(last) else -1

        if (lastIndex > 0) {
            DataUtil.removeFirst(alphaWords, lastIndex + 1)
        }

        while (!alphaWords.isEmpty()) {
            val words = DataUtilKt.takeFirst(alphaWords, Constants.Count.WORD_PAGE)
            if (words.isNullOrEmpty()) {
                continue
            }
            var resultOf = repo.putItems(words)
            if (DataUtil.isEqual(words, resultOf)) {
                val states = ArrayList<Store>()
                words.forEach { word ->
                    states.add(Store(word.id, Type.WORD, Subtype.DEFAULT, State.RAW))
                }
                resultOf = storeRepo.putItems(states)
            }

            if (DataUtil.isEqual(words, resultOf)) {
                val lastWord = DataUtil.pullLast(words)
                wordPref.setLastWord(lastWord)
                current = storeRepo.getCountByType(Type.WORD, Subtype.DEFAULT, State.RAW)
                load.current = current
                load.total = current

                Timber.v("%d Last Alpha Word = %s", current, lastWord!!.id)
                ex.postToUi(Runnable { postResult(request.action, item) })
                AndroidUtil.sleep(100)
            }
        }
        if (alphaWords.isEmpty()) {
            wordPref.commitAlphaLoaded()
            wordPref.clearLastWord()
        }
    }

    private fun buildCommonWords() {
        if (commonWords.size != Constants.Count.WORD_COMMON) {
            val words = repo.getCommonItems()
            commonWords.clear()
            commonWords.addAll(words!!)
        }
    }

    private fun buildAlphaWords() {
        if (alphaWords.size != Constants.Count.WORD_ALPHA) {
            val words = repo.getAlphaItems()
            alphaWords.clear()
            alphaWords.addAll(words!!)
        }
    }

    private fun getUiItem(request: LoadRequest, item: Word): WordItem {
        var uiItem: WordItem? = mapper.getUiItem(item.id)
        if (uiItem == null) {
            uiItem = WordItem.getItem(item)
        }
        uiItem.item = item
        adjustTranslate(request, uiItem)
        return uiItem
    }

    private fun adjustTranslate(request: LoadRequest, item: WordItem) {
        var translation: String? = null
        if (request.translate) {
            if (item.hasTranslation(request.target)) {
                translation = item.getTranslationBy(request.target)
            } else {
                val textTranslation =
                    translationRepo.getItem(request.source!!, request.target!!, item.item.id)
                textTranslation?.let {
                    Timber.v("Translation %s - %s", request.id, it.output)
                    item.addTranslation(request.target!!, it.output)
                    translation = it.output
                }
            }
        }
        item.translation = translation
    }
}