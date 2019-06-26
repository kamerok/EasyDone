package easydone.feature.todo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_task.*


internal class TodoAdapter(
    private val listener: (String) -> Unit
) : RecyclerView.Adapter<TodoAdapter.ViewHolder>() {

    private var data = mutableListOf<TodoTaskUiModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false), listener)

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(data[position])

    fun setData(data: List<TodoTaskUiModel>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    class ViewHolder(
        override val containerView: View,
        private val listener: (String) -> Unit
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        private var item: TodoTaskUiModel? = null

        init {
            containerView.setOnClickListener { item?.run { listener(this.id) } }
        }

        fun bind(item: TodoTaskUiModel) {
            this.item = item

            titleView.text = item.title
        }

    }
}