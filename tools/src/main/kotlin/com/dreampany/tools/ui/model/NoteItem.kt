package com.dreampany.tools.ui.model

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dreampany.frame.data.model.Base
import com.dreampany.frame.ui.model.BaseItem
import com.dreampany.frame.util.AndroidUtil
import com.dreampany.frame.util.DataUtilKt
import com.dreampany.frame.util.DisplayUtil
import com.dreampany.frame.util.FrescoUtil
import com.dreampany.tools.R
import com.dreampany.tools.data.enums.ApkType
import com.dreampany.tools.data.model.Apk
import com.dreampany.tools.data.model.Note
import com.dreampany.tools.misc.Constants
import com.dreampany.tools.ui.adapter.ApkAdapter
import com.dreampany.tools.ui.adapter.NoteAdapter
import com.dreampany.tools.ui.enums.UiAction
import com.facebook.drawee.view.SimpleDraweeView
import com.github.nikartm.button.FitButton
import de.hdodenhof.circleimageview.CircleImageView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import jp.shts.android.library.TriangleLabelView
import java.io.Serializable
import java.lang.ref.WeakReference


/**
 * Created by roman on 2019-08-03
 * Copyright (c) 2019 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
class NoteItem private constructor(
    item: Note, @LayoutRes layoutId: Int = Constants.Default.INT
) : BaseItem<Note, NoteItem.ViewHolder, String>(item, layoutId) {

    companion object {
        fun getItem(item: Note): NoteItem {
            return NoteItem(item, R.layout.item_none)
        }
    }

    override fun createViewHolder(
        view: View,
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>
    ): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun filter(constraint: String?): Boolean {
        return false
    }

    class ViewHolder(
        view: View,
        adapter: FlexibleAdapter<*>
    ) : BaseItem.ViewHolder(view, adapter) {

        private val height: Int

        private var adapter: NoteAdapter
        private var textTitle: AppCompatTextView
        private var textDescription: AppCompatTextView

        init {
            this.adapter = adapter as NoteAdapter
            height = getSpanHeight(this.adapter.getSpanCount(), this.adapter.getItemOffset())

            textTitle = view.findViewById(R.id.text_name)
            textDescription = view.findViewById(R.id.text_size)



            view.setOnClickListener {
                this.adapter.click?.onClick(item = this.adapter.getItem(adapterPosition), action =  UiAction.OPEN)
            }
/*            view.setOnLongClickListener {
                this.adapter.click?.onClick(this.adapter.getItem(adapterPosition)!!)
                true
            }*/
        }

        override fun <VH : BaseItem.ViewHolder, T : Base, S : Serializable, I : BaseItem<T, VH, S>> bind(
            position: Int,
            item: I
        ) {
            val uiItem = item as NoteItem
            val item = uiItem.item

            textTitle.text = item.title
            textDescription.text = item.description
        }
    }
}