package com.dreampany.common.ui.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.dreampany.common.misc.extension.bindInflater
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by roman on 3/3/20
 * Copyright (c) 2020 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
abstract class BaseAdapter<T, VH : BaseAdapter.ViewHolder<T, VH>>(listener: Any?) :
    RecyclerView.Adapter<VH>(), Filterable {

    interface OnItemClickListener<T> {
        fun onItemClick(item: T)

        fun onChildItemClick(view: View, item: T)
    }

    private var items: MutableList<T>
    private var filtered: MutableList<T>
    protected var listener: OnItemClickListener<T>? = null

    init {
        items = arrayListOf<T>()
        filtered = arrayListOf<T>()
        if (listener is OnItemClickListener<*>) {
            this.listener = listener as OnItemClickListener<T>
        }
    }

    protected fun viewType(item: T): Int = 0

    @LayoutRes
    protected abstract fun layoutId(viewType: Int): Int

    protected abstract fun createViewHolder(bind: ViewDataBinding, viewType: Int): VH

    protected abstract fun filters(constraint: CharSequence): Boolean

    override fun getItemCount(): Int = filtered.size

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position) ?: return 0
        return viewType(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val layoutId = layoutId(viewType)
        return createViewHolder(layoutId.bindInflater(parent), viewType)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        getItem(position)?.run {
            holder.bindView(this, position)
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                if (constraint.isEmpty()) {
                    filtered = items
                } else {
                    val result = items.filter { filters(constraint)}.toMutableList()
                    filtered = result
                }
                val result = FilterResults()
                result.values = filtered
                return result
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                filtered = results.values as MutableList<T>
                notifyDataSetChanged()
            }
        }
    }

    fun isEmpty(): Boolean = itemCount == 0

    fun isFirst(item: T): Boolean = isFirst(getPosition(item))

    fun isLast(item: T): Boolean = isLast(getPosition(item))

    fun isMiddle(item: T): Boolean = isMiddle(getPosition(item))

    fun isFirst(position: Int): Boolean = position == 0

    fun isLast(position: Int): Boolean = position == itemCount - 1

    fun isMiddle(position: Int): Boolean = position > 0 && position < itemCount - 1

    open fun getItem(position: Int): T? =
        if (!isValidPosition(position)) null else filtered[position]

    open fun getPosition(item: T): Int = filtered.indexOf(item)

    open fun addAll(items: List<T>) {
        for (room in items) {
            add(room)
        }
    }

    open fun addAll(items: List<T>, notify: Boolean) {
        for (room in items) {
            add(room, notify.not())
        }
        if (notify) notifyDataSetChanged()
    }

    open fun addAll(
        items: List<T>,
        comparator: Comparator<T>?
    ) {
        for (room in items) {
            add(room, comparator)
        }
    }

    open fun add(item: T, notify: Boolean = false) {
        val position = getPosition(item)
        if (position == -1) {
            filtered.add(item)
            if (notify)
                notifyItemInserted(itemCount - 1)
        } else {
            filtered[position] = item
            if (notify)
                notifyItemChanged(position)
        }
    }

    open fun add(item: T, comparator: Comparator<T>?) {
        var position = getPosition(item)
        if (position == -1) {
            position = calculatePositionFor(item, comparator)
            //Timber.v("Calculated Position %d", position)
            performInsert(position, listOf(item), true)
            /*items.add(item);
            notifyItemInserted(getItemCount() - 1);*/
        } else {
            filtered[position] = item
            notifyItemChanged(position)
        }
    }

    open fun add(position: Int, item: T) {
        filtered.add(position, item)
        notifyItemInserted(position)
    }

    open fun removeAt(position: Int): T {
        val item = filtered.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, filtered.size)
        return item
    }

    open fun clearAll() {
        filtered.clear()
        notifyDataSetChanged()
    }

    open fun notifyItemChanged(item: T) {
        val position = getPosition(item)
        notifyItemChanged(position)
    }

    protected open fun calculatePositionFor(item: T, comparator: Comparator<T>?): Int {
        if (comparator == null) return 0
        val sortedList: MutableList<T> = ArrayList(filtered)
        if (!sortedList.contains(item)) sortedList.add(item)
        Collections.sort(sortedList, comparator)
        return Math.max(0, sortedList.indexOf(item))
    }

    private fun performInsert(
        position: Int,
        items: List<T>,
        notify: Boolean
    ) {
        var position = position
        val itemCount = itemCount
        if (position < itemCount) {
            this.filtered.addAll(position, items)
        } else {
            this.filtered.addAll(items)
            position = itemCount
        }
        // Notify range addition
        if (notify) {
            //Timber.v("addItems on position=%s itemCount=%s", position, items.size)
            notifyItemRangeInserted(position, items.size)
        }
    }

    private fun isValidPosition(position: Int): Boolean = position >= 0 && position < itemCount

    abstract class ViewHolder<T, VH : RecyclerView.ViewHolder>
    protected constructor(bind: ViewDataBinding) : RecyclerView.ViewHolder(bind.root) {
        protected val context: Context get() = itemView.context

        abstract fun bindView(item: T, position: Int)
    }
}