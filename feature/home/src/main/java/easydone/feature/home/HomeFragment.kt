package easydone.feature.home

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.kamer.home.R
import easydone.core.domain.DomainRepository
import easydone.core.domain.Synchronizer
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

    private lateinit var tabs: List<Tab>
    private lateinit var fragmentFactory: (Int) -> Fragment
    private lateinit var repository: DomainRepository
    private lateinit var synchronizer: Synchronizer
    private lateinit var navigator: HomeNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        synchronizer.initiateSync()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        addTaskView.setOnClickListener { navigator.navigateToCreate() }
        refreshLayout.setOnRefreshListener {
            synchronizer.initiateSync()
            refreshLayout.isRefreshing = false
        }
        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItem(position: Int): Fragment = fragmentFactory(position)

            override fun getItemCount(): Int = tabs.size
        }
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                bottomNavigationView.selectedItemId = position
            }

            override fun onPageScrollStateChanged(state: Int) {
                refreshLayout.isEnabled = state == ViewPager2.SCROLL_STATE_IDLE
            }
        })
        bottomNavigationView.setBackgroundColor(Color.WHITE)
        bottomNavigationView.menu.apply {
            tabs.forEachIndexed { index, tab ->
                add(Menu.NONE, index, Menu.NONE, getString(tab.nameRes))
                    .setIcon(tab.iconRes)
            }
        }
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            viewPager.currentItem = item.itemId
            true
        }
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) =
        inflater.inflate(R.menu.home_toolbar, menu)

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_settings -> {
            navigator.navigateToSettings()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    data class Dependencies(
        val tabs: List<Tab>,
        val fragmentFactory: (Int) -> Fragment,
        val repository: DomainRepository,
        val synchronizer: Synchronizer,
        val navigator: HomeNavigator
    )

    companion object {
        fun create(dependencies: Dependencies): Fragment = HomeFragment().apply {
            tabs = dependencies.tabs
            fragmentFactory = dependencies.fragmentFactory
            repository = dependencies.repository
            synchronizer = dependencies.synchronizer
            navigator = dependencies.navigator
        }
    }

}
