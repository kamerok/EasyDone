package easydone.feature.feed

import android.os.Bundle
import android.view.View
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.fragment_feed.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class FeedFragment(
    private val viewModelProvider: (Fragment) -> FeedViewModel
) : Fragment(R.layout.fragment_feed) {

    private val viewModel: FeedViewModel by lazy { viewModelProvider(this) }

    private val adapter by lazy { FeedAdapter { viewModel.onTaskClick(it) } }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.setOnApplyWindowInsetsListener { v, insets ->
            v.updatePadding(top = insets.systemWindowInsetTop)
            recyclerView.updatePadding(bottom = insets.systemWindowInsetBottom)
            insets
        }
        recyclerView.adapter = adapter

        viewModel.getData()
            .onEach { adapter.items = it }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

}
