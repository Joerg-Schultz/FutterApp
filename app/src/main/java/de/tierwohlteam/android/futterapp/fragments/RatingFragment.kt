package de.tierwohlteam.android.futterapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import de.tierwohlteam.android.futterapp.R
import de.tierwohlteam.android.futterapp.adapters.RatingsViewPagerAdapter
import de.tierwohlteam.android.futterapp.databinding.AddRatingFragmentBinding
import de.tierwohlteam.android.futterapp.databinding.RatingFragmentBinding
import de.tierwohlteam.android.futterapp.databinding.ShowRatingsFragmentBinding
import de.tierwohlteam.android.futterapp.viewModels.RatingViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class RatingFragment: Fragment(R.layout.rating_fragment) {
    private val ratingViewModel: RatingViewModel by activityViewModels()
    private var _binding: RatingFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = RatingFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewPager2 = view.findViewById<ViewPager2>(R.id.rating_pager_container)

        val fragmentTitleList: Map<String,Fragment> = mapOf(
            getString(R.string.addRating) to AddRatingFragment(),
            getString(R.string.showRatings) to ShowRatingsFragment(),
        )
        viewPager2.adapter = RatingsViewPagerAdapter(this.childFragmentManager, lifecycle,
            ArrayList(fragmentTitleList.values)
        )

        val tabLayout = view.findViewById<TabLayout>(R.id.rating_tablayout)
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.text = fragmentTitleList.keys.toTypedArray()[position]
        }.attach()
    }
}

class AddRatingFragment: Fragment() {
    private var _binding: AddRatingFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddRatingFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }
}

class ShowRatingsFragment: Fragment() {
    private var _binding: ShowRatingsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ShowRatingsFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }
}