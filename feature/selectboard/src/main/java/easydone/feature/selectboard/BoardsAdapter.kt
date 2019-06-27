package easydone.feature.selectboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kamer.selectboard.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_board.*


internal class BoardsAdapter(
    private val listener: (String) -> Unit
) : RecyclerView.Adapter<BoardsAdapter.ViewHolder>() {

    private var data = mutableListOf<BoardUiModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_board,
                parent,
                false
            ), listener
        )

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(data[position])

    fun setData(data: List<BoardUiModel>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    class ViewHolder(
        override val containerView: View,
        private val listener: (String) -> Unit
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        private var item: BoardUiModel? = null

        init {
            containerView.setOnClickListener { item?.run { listener(this.id) } }
        }

        fun bind(item: BoardUiModel) {
            this.item = item

            nameView.text = item.name
        }

    }
}