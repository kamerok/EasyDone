package easydone.feature.feed

import androidx.core.view.isVisible
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_task.*


internal fun taskDelegate(listener: (String) -> Unit) =
    adapterDelegateLayoutContainer<TaskUiModel, Any>(R.layout.item_task) {
        itemView.setOnClickListener { item.run { listener(this.id) } }

        bind {
            titleView.text = item.title
            descriptionView.isVisible = item.hasDescription
        }
    }

internal fun headerDelegate() =
    adapterDelegateLayoutContainer<FeedHeader, Any>(R.layout.item_header) {
        bind { titleView.text = item.title }
    }