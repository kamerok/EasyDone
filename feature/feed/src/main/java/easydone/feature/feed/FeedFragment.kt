package easydone.feature.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import easydone.core.domain.DomainRepository


class FeedFragment : Fragment() {

    private lateinit var repository: DomainRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_feed, container, false)

    data class Dependencies(
        val repository: DomainRepository
    )

    companion object {
        fun create(dependencies: Dependencies): Fragment = FeedFragment().apply {
            repository = dependencies.repository
        }
    }

}