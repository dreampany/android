package com.dreampany.tools.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.dreampany.framework.data.enums.Action
import com.dreampany.framework.data.enums.Subtype
import com.dreampany.framework.data.enums.Type
import com.dreampany.framework.data.model.Response
import com.dreampany.framework.misc.ActivityScope
import com.dreampany.framework.misc.exception.EmptyException
import com.dreampany.framework.misc.exception.ExtraException
import com.dreampany.framework.misc.exception.MultiException
import com.dreampany.framework.ui.adapter.SmartAdapter
import com.dreampany.framework.ui.enums.UiState
import com.dreampany.framework.ui.fragment.BaseMenuFragment
import com.dreampany.framework.ui.listener.OnVerticalScrollListener
import com.dreampany.framework.ui.model.UiTask
import com.dreampany.framework.util.TextUtil
import com.dreampany.framework.util.ViewUtil
import com.dreampany.tools.R
import com.dreampany.tools.data.misc.RelatedQuizRequest
import com.dreampany.tools.data.model.Quiz
import com.dreampany.tools.data.model.RelatedQuiz
import com.dreampany.tools.databinding.*
import com.dreampany.tools.misc.Constants
import com.dreampany.tools.ui.adapter.QuizOptionAdapter
import com.dreampany.tools.ui.model.QuizItem
import com.dreampany.tools.ui.model.QuizOptionItem
import com.dreampany.tools.ui.model.RelatedQuizItem
import com.dreampany.tools.ui.vm.RelatedQuizViewModel
import cz.kinst.jakub.view.StatefulLayout
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

/**
 * Created by roman on 2019-08-31
 * Copyright (c) 2019 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
@ActivityScope
class RelatedQuizFragment
@Inject constructor(

) : BaseMenuFragment(),
    SmartAdapter.OnUiItemClickListener<QuizOptionItem?, Action?> {

    @Inject
    internal lateinit var factory: ViewModelProvider.Factory
    private lateinit var bind: FragmentRelatedQuizBinding
    private lateinit var bindStatus: ContentTopStatusBinding
    private lateinit var bindRecycler: ContentRecyclerBinding

    private lateinit var vm: RelatedQuizViewModel
    private lateinit var adapter: QuizOptionAdapter
    private lateinit var scroller: OnVerticalScrollListener

    private lateinit var subtype: Subtype
    private var quizItem: RelatedQuizItem? = null


    override fun getLayoutId(): Int {
        return R.layout.fragment_related_quiz
    }

    override fun getTitleResId(): Int {
        val uiTask = getCurrentTask<UiTask<Quiz>>() ?: return R.string.play_quiz
        when (uiTask.subtype) {
            Subtype.SYNONYM -> return R.string.play_synonym_quiz
            Subtype.ANTONYM -> return R.string.play_antonym_quiz
            else -> return R.string.play_quiz
        }
    }

    override fun onStartUi(state: Bundle?) {
        val uiTask = getCurrentTask<UiTask<Quiz>>() ?: return
        subtype = uiTask.subtype
        initUi()
        initRecycler()
        request(action = Action.GET, single = true, progress = true)
    }

    override fun onStopUi() {
        processUiState(UiState.HIDE_PROGRESS)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.layout_parent -> {

            }
        }
    }

    override fun onClick(view: View, item: QuizOptionItem?, action: Action?) {
        item?.run {
            performAnswer(this)
        }
    }

    override fun onLongClick(view: View, item: QuizOptionItem?, action: Action?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun initUi() {
        //setTitle(R.string.home)
        bind = super.binding as FragmentRelatedQuizBinding
        bindStatus = bind.layoutTopStatus
        bindRecycler = bind.layoutRecycler

        ViewUtil.setSwipe(bind.layoutRefresh, this)

        bind.stateful.setStateView(
            UiState.DEFAULT.name,
            LayoutInflater.from(context).inflate(R.layout.item_default, null)
        )
        bind.stateful.setStateView(
            UiState.EMPTY.name,
            LayoutInflater.from(context).inflate(R.layout.item_empty, null)
        )

        vm = ViewModelProviders.of(this, factory).get(RelatedQuizViewModel::class.java)
        vm.observeUiState(this, Observer { this.processUiState(it) })
        vm.observeOutput(this, Observer { this.processSingleResponse(it) })
    }

    private fun initRecycler() {
        bind.setItems(ObservableArrayList<Any>())
        scroller = object : OnVerticalScrollListener() {}
        adapter = QuizOptionAdapter(this)
        adapter.setStickyHeaders(true)
        ViewUtil.setRecycler(
            adapter,
            bindRecycler.recycler,
            SmoothScrollLinearLayoutManager(context!!),
            FlexibleItemDecoration(context!!)
                .addItemViewType(R.layout.item_quiz_option_header, adapter.getItemOffset())
                .addItemViewType(R.layout.item_quiz_option, adapter.getItemOffset())
                .withEdge(true),
            null,
            scroller,
            null
        )
    }

    private fun processUiState(state: UiState) {
        when (state) {
            UiState.DEFAULT -> bind.stateful.setState(UiState.DEFAULT.name)
            UiState.SHOW_PROGRESS -> if (!bind.layoutRefresh.isRefreshing()) {
                bind.layoutRefresh.setRefreshing(true)
            }
            UiState.HIDE_PROGRESS -> if (bind.layoutRefresh.isRefreshing()) {
                bind.layoutRefresh.setRefreshing(false)
            }
            UiState.OFFLINE -> bindStatus.layoutExpandable.expand()
            UiState.ONLINE -> bindStatus.layoutExpandable.collapse()
            UiState.EXTRA -> processUiState(if (quizItem == null) UiState.EMPTY else UiState.CONTENT)
            UiState.SEARCH -> bind.stateful.setState(UiState.SEARCH.name)
            UiState.EMPTY -> bind.stateful.setState(UiState.SEARCH.name)
            UiState.ERROR -> {
            }
            UiState.CONTENT -> bind.stateful.setState(StatefulLayout.State.CONTENT)
        }
    }

    private fun processSingleResponse(response: Response<RelatedQuizItem>) {
        if (response is Response.Progress<*>) {
            val result = response as Response.Progress<*>
            vm.processProgress(result.loading)
        } else if (response is Response.Failure<*>) {
            val result = response as Response.Failure<*>
            processFailure(result.error)
        } else if (response is Response.Result<*>) {
            val result = response as Response.Result<RelatedQuizItem>
            processSingleSuccess(result.action, result.data)
        }
    }

    private fun processFailure(error: Throwable) {
        if (error is IOException || error.cause is IOException) {
            vm.updateUiState(UiState.OFFLINE)
        } else if (error is EmptyException) {
            vm.updateUiState(UiState.EMPTY)
        } else if (error is ExtraException) {
            vm.updateUiState(UiState.EXTRA)
        } else if (error is MultiException) {
            for (e in error.errors) {
                processFailure(e)
            }
        }
    }

    private fun processSingleSuccess(action: Action, item: RelatedQuizItem) {
        Timber.v("Result Related Quiz[%s]", item.item.id)
        quizItem = item
        val result = item.getOptionItems(context!!)
        adapter.addItems(result)
        processUiState(UiState.CONTENT)

        quizItem?.run {
            if (played()) {
                if (isWinner()) {
                    rightAnswer()
                } else {
                    wrongAnswer()
                }
            }
        }
    }

    private fun performAnswer(item: QuizOptionItem) {
        Timber.v("Select %s", item.item.id)
        if (quizItem!!.played()) {
            return
        }
        val quiz = quizItem!!.item
        val given = item.item.id
        request(action = Action.SOLVE, input = quiz, single = true, progress = true, given = given)
    }

    private fun rightAnswer() {
        bind.viewKonfetti.run {
            build()
            .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
            .setDirection(0.0, 359.0)
            .setSpeed(1f, 5f)
            .setFadeOutEnabled(true)
            .setTimeToLive(1000L)
            .addShapes(Shape.RECT, Shape.CIRCLE)
            .addSizes(Size(10))
            .setPosition(-50f, width + 50f, -50f, -50f)
            .streamFor(300, 3000L)
        }

    }

    private fun wrongAnswer() {

    }

    private fun request(
        action: Action = Action.DEFAULT,
        input: RelatedQuiz? = Constants.Default.NULL,
        single: Boolean = Constants.Default.BOOLEAN,
        progress: Boolean = Constants.Default.BOOLEAN,
        given: String? = Constants.Default.NULL
    ) {

        val request = RelatedQuizRequest(
            type = Type.QUIZ,
            subtype = subtype,
            action = action,
            input = input,
            single = single,
            progress = progress,
            given = given
        )
        vm.request(request)
    }
}