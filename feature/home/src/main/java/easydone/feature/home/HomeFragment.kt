package easydone.feature.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.kamer.home.R
import easydone.core.domain.DomainRepository
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

    private lateinit var tabs: List<Tab>
    private lateinit var fragmentFactory: (Int) -> Fragment
    private lateinit var domainRepository: DomainRepository
    private lateinit var navigator: HomeNavigator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        domainRepository.refresh()
        addTaskView.setOnClickListener { navigator.navigateToCreate() }
        refreshView.setOnClickListener { domainRepository.refresh() }
        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItem(position: Int): Fragment = fragmentFactory(position)

            override fun getItemCount(): Int = tabs.size
        }
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                bottomNavigationView.selectedItemId = position
            }
        })
        bottomNavigationView.setBackgroundColor(Color.WHITE)
        bottomNavigationView.menu.apply {
            tabs.forEachIndexed { index, tab ->
                add(Menu.NONE, index, Menu.NONE, "Home$index").setIcon(android.R.drawable.ic_delete)
            }
        }
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            viewPager.currentItem = item.itemId
            true
        }
    }

    data class Dependencies(
        val tabs: List<Tab>,
        val fragmentFactory: (Int) -> Fragment,
        val domainRepository: DomainRepository,
        val navigator: HomeNavigator
    )

    companion object {
        fun create(dependencies: Dependencies): Fragment = HomeFragment().apply {
            tabs = dependencies.tabs
            fragmentFactory = dependencies.fragmentFactory
            domainRepository = dependencies.domainRepository
            navigator = dependencies.navigator
        }
    }

}
