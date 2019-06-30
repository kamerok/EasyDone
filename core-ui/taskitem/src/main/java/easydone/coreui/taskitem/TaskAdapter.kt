package easydone.coreui.taskitem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_task.*


class TaskAdapter(
    private val listener: (String) -> Unit
) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    private val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<TaskUiModel>() {
        override fun areItemsTheSame(oldItem: TaskUiModel, newItem: TaskUiModel): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: TaskUiModel, newItem: TaskUiModel): Boolean =
            oldItem == newItem
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false),
            listener
        )

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(differ.currentList[position])

    fun setData(data: List<TaskUiModel>) = differ.submitList(data)

    class ViewHolder(
        override val containerView: View,
        private val listener: (String) -> Unit
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        private var item: TaskUiModel? = null

        init {
            containerView.setOnClickListener { item?.run { listener(this.id) } }
        }

        fun bind(item: TaskUiModel) {
            this.item = item

            titleView.text = item.title
            descriptionView.isVisible = item.hasDescription
        }

    }
}