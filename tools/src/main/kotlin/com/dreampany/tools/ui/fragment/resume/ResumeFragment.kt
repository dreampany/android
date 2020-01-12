package com.dreampany.tools.ui.fragment.resume

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dreampany.framework.api.session.SessionManager
import com.dreampany.framework.data.enums.Action
import com.dreampany.framework.data.enums.State
import com.dreampany.framework.data.model.Response
import com.dreampany.framework.misc.ActivityScope
import com.dreampany.framework.ui.enums.UiState
import com.dreampany.framework.ui.fragment.BaseMenuFragment
import com.dreampany.framework.ui.listener.TextChangeListener
import com.dreampany.framework.ui.model.UiTask
import com.dreampany.framework.util.*
import com.dreampany.tools.R
import com.dreampany.tools.data.model.Note
import com.dreampany.tools.data.model.Resume
import com.dreampany.tools.databinding.FragmentNoteBinding
import com.dreampany.tools.databinding.FragmentResumeBinding
import com.dreampany.tools.misc.Constants
import com.dreampany.tools.ui.model.NoteItem
import com.dreampany.tools.ui.model.ResumeItem
import com.dreampany.tools.ui.vm.note.NoteViewModel
import com.dreampany.tools.ui.vm.resume.ResumeViewModel
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by roman on 2020-01-12
 * Copyright (c) 2020 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
@ActivityScope
class ResumeFragment
@Inject constructor() :
    BaseMenuFragment() {

    @Inject
    internal lateinit var factory: ViewModelProvider.Factory
    @Inject
    internal lateinit var session: SessionManager
    private lateinit var bind: FragmentResumeBinding

    private lateinit var vm: ResumeViewModel
    private var edited: Boolean = false
    private var saved: Boolean = false

    override fun getLayoutId(): Int {
        return R.layout.fragment_resume
    }

    override fun getMenuId(): Int {
        return R.menu.menu_resume
    }

    override fun getScreen(): String {
        return Constants.resume(context!!)
    }

    override fun onStartUi(state: Bundle?) {
initUi()
    }

    override fun onStopUi() {
        vm.updateUiState(uiState = UiState.HIDE_PROGRESS)
    }

    override fun onMenuCreated(menu: Menu, inflater: MenuInflater) {
        super.onMenuCreated(menu, inflater)

        val editItem = findMenuItemById(R.id.item_edit)
        val doneItem = findMenuItemById(R.id.item_done)
        MenuTint.colorMenuItem(
            ColorUtil.getColor(context!!, R.color.material_white),
            null, editItem, doneItem
        )

        val editing = isEditing()
        editItem?.isVisible = !editing
        doneItem?.isVisible = editing
    }

    private fun initUi() {
        val uiTask = getCurrentTask<UiTask<Note>>() ?: return
        val titleRes =
            if (uiTask.action == Action.ADD) R.string.title_add_note else R.string.title_edit_note

        setTitle(titleRes)
        bind = super.binding as FragmentResumeBinding

        vm = ViewModelProvider(this, factory).get(ResumeViewModel::class.java)
        vm.observeUiState(this, Observer { this.processUiState(it) })
        vm.observeOutput(this, Observer { this.processSingleResponse(it) })

        val note = uiTask.input

 /*       note?.title?.run { noteTitle = this }
        note?.description?.run { noteDescription = this }

        bind.inputEditTitle.addTextChangedListener(object : TextChangeListener() {
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!DataUtilKt.isEquals(noteTitle, s?.toString())) {
                    edited = true
                }
                noteTitle = s.toString()
            }

        })
        bind.inputEditDescription.addTextChangedListener(object : TextChangeListener() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!DataUtilKt.isEquals(noteDescription, s?.toString())) {
                    edited = true
                }
                noteDescription = s.toString()
            }
        })
        resolveUi()
        if (uiTask.action == Action.EDIT || uiTask.action == Action.VIEW) {
            note?.run {
                request(id = this.id, action = Action.GET, progress = true)
            }
        }*/
    }

    private fun isEditing(): Boolean {
        val uiTask = getCurrentTask<UiTask<Resume>>(false)
        val action = uiTask?.action ?: Action.DEFAULT
        return action == Action.EDIT || action == Action.ADD
    }

    private fun processUiState(response: Response.UiResponse) {
        Timber.v("UiState %s", response.uiState.name)
        when (response.uiState) {
            UiState.SHOW_PROGRESS -> if (!bind.layoutRefresh.isRefreshing()) {
                bind.layoutRefresh.setRefreshing(true)
            }
            UiState.HIDE_PROGRESS -> if (bind.layoutRefresh.isRefreshing()) {
                bind.layoutRefresh.setRefreshing(false)
            }
        }
    }

    fun processSingleResponse(response: Response<ResumeItem>) {
        if (response is Response.Progress<*>) {
            val result = response as Response.Progress<*>
            vm.processProgress(result.state, result.action, result.loading)
        } else if (response is Response.Failure<*>) {
            val result = response as Response.Failure<*>
            vm.processFailure(result.state, result.action, result.error)
        } else if (response is Response.Result<*>) {
            val result = response as Response.Result<ResumeItem>
            processSuccess(result.state, result.action, result.data)
        }
    }

    private fun processSuccess(state: State, action: Action, item: ResumeItem) {
        if (action == Action.ADD || action == Action.EDIT) {
            NotifyUtil.showInfo(getParent()!!, getString(R.string.dialog_saved_note))
            AndroidUtil.hideSoftInput(getParent()!!)
            saved = true
            //ex.postToUi(Runnable { forResult(saved) }, 500L)
            hasBackPressed()
            return
        }
        if (isEditing()) {
            //bind.inputEditTitle.setText(item.item.title)
            //bind.inputEditDescription.setText(item.item.description)
        } else {
            //bind.textTitle.setText(item.item.title)
            //bind.textDescription.setText(item.item.description)
        }
        ex.postToUi(Runnable {
            vm.updateUiState(state, action, UiState.EXTRA)
        }, 500L)
        if (state == State.DIALOG) {

        }
    }
}