package easydone.feature.feed

import androidx.core.view.isVisible
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_task.*


fun taskDeletage(listener: (String) -> Unit) =
    adapterDelegateLayoutContainer<TaskUiModel, Any>(R.layout.item_task) {
        itemView.setOnClickListener { item.run { listener(this.id) } }

        bind {
            titleView.text = item.title
            descriptionView.isVisible = item.hasDescription
        }
    }