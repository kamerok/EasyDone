package easydone.feature.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import easydone.core.domain.DomainRepository
import easydone.feature.feed.FeedFragment
import easydone.feature.feed.FeedNavigator
import easydone.feature.feed.FeedViewModel


class CustomFragmentFactory(
    private val domainRepository: DomainRepository,
    private val onEditTask: (String) -> Unit
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment =
        when (className) {
                FeedFragment::class.java.name -> {
                    FeedFragment { fragment ->
                        ViewModelProvider(
                            fragment,
                            object : AbstractSavedStateViewModelFactory(fragment, null) {
                                override fun <T : ViewModel?> create(
                                    key: String,
                                    modelClass: Class<T>,
                                    handle: SavedStateHandle
                                ): T =
                                    FeedViewModel(
                                        domainRepository,
                                        object : FeedNavigator {
                                            override fun navigateToTask(id: String) {
                                                onEditTask(id)
                                            }
                                        }
                                    ) as T
                            }).get()
                    }
                }
                else -> super.instantiate(classLoader, className)
            }

}
