package com.kamer.inbox

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_task.*


internal class InboxAdapter(
    private val listener: (String) -> Unit
) : RecyclerView.Adapter<InboxAdapter.ViewHolder>() {

    private var data = mutableListOf<InboxTaskUiModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false), listener)

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(data[position])

    fun setData(data: List<InboxTaskUiModel>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    class ViewHolder(
        override val containerView: View,
        private val listener: (String) -> Unit
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        private var item: InboxTaskUiModel? = null

        init {
            containerView.setOnClickListener { item?.run { listener(this.id) } }
        }

        fun bind(item: InboxTaskUiModel) {
            this.item = item

            titleView.text = item.title
        }

    }
}