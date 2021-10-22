package de.tierwohlteam.android.futterapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import de.tierwohlteam.android.futterapp.R
import de.tierwohlteam.android.futterapp.adapters.CalendarListAdapter
import de.tierwohlteam.android.futterapp.adapters.RatingsListAdapter
import de.tierwohlteam.android.futterapp.adapters.RatingsViewPagerAdapter
import de.tierwohlteam.android.futterapp.adapters.StatisticsViewPagerAdapter
import de.tierwohlteam.android.futterapp.databinding.AddRatingFragmentBinding
import de.tierwohlteam.android.futterapp.databinding.RatingFragmentBinding
import de.tierwohlteam.android.futterapp.databinding.ShowCalendarFragmentBinding
import de.tierwohlteam.android.futterapp.databinding.StatisticsFragmentBinding
import de.tierwohlteam.android.futterapp.others.Status
import de.tierwohlteam.android.futterapp.viewModels.RatingViewModel
import de.tierwohlteam.android.futterapp.viewModels.StatisticsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class StatisticsFragment: Fragment(R.layout.statistics_fragment) {
    //private val statisticsViewModel: StatisticsViewModel by activityViewModels()
    private var _binding: StatisticsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = StatisticsFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewPager2 = view.findViewById<ViewPager2>(R.id.statistics_pager_container)

        val fragmentTitleList: Map<String, Fragment> = mapOf(
            getString(R.string.calendar) to ShowCalendarFragment(),
        )
        viewPager2.adapter = StatisticsViewPagerAdapter(this.childFragmentManager, lifecycle,
            ArrayList(fragmentTitleList.values)
        )

        val tabLayout = view.findViewById<TabLayout>(R.id.statistics_tablayout)
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.text = fragmentTitleList.keys.toTypedArray()[position]
        }.attach()
    }
}


@ExperimentalCoroutinesApi
class ShowCalendarFragment: Fragment() {
    private var _binding: ShowCalendarFragmentBinding? = null
    private val binding get() = _binding!!

    private val statisticsViewModel: StatisticsViewModel by activityViewModels()
    private lateinit var calendarListAdapter: CalendarListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ShowCalendarFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvCalendar.apply {
            calendarListAdapter = CalendarListAdapter()
            adapter = calendarListAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
        lifecycleScope.launchWhenStarted {
            statisticsViewModel.allEntries.collect { result ->
                when (result.status) {
                    Status.LOADING -> {
                        binding.pBCalendar.visibility = View.VISIBLE
                    }
                    Status.SUCCESS -> {
                        binding.pBCalendar.visibility = View.GONE
                        result.data?.let { list ->
                            calendarListAdapter.submitList(list.sortedByDescending { it.date }) }
                    }
                    else -> { /* NO-OP */ }
                }
            }
        }

        statisticsViewModel.getEntries()

    }
}