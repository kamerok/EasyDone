package easydone.feature.feed

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter


internal class FeedAdapter(
    listener: (String) -> Unit
) : AsyncListDifferDelegationAdapter<Any>(Callback) {

    init {
        delegatesManager
            .addDelegate(taskDelegate(listener))
            .addDelegate(headerDelegate())
    }

}

private object Callback : DiffUtil.ItemCallback<Any>() {
    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean = when {
        oldItem is TaskUiModel && newItem is TaskUiModel -> oldItem.id == newItem.id
        else -> oldItem == newItem
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean = oldItem == newItem
}