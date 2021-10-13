package de.tierwohlteam.android.futterapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import de.tierwohlteam.android.futterapp.R
import de.tierwohlteam.android.futterapp.adapters.RatingsListAdapter
import de.tierwohlteam.android.futterapp.adapters.RatingsViewPagerAdapter
import de.tierwohlteam.android.futterapp.databinding.AddRatingFragmentBinding
import de.tierwohlteam.android.futterapp.databinding.RatingFragmentBinding
import de.tierwohlteam.android.futterapp.databinding.ShowRatingsFragmentBinding
import de.tierwohlteam.android.futterapp.others.Status
import de.tierwohlteam.android.futterapp.viewModels.RatingViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@InternalCoroutinesApi
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

@ExperimentalCoroutinesApi
class AddRatingFragment: Fragment() {
    private var _binding: AddRatingFragmentBinding? = null
    private val binding get() = _binding!!

    private val ratingViewModel: RatingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddRatingFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        binding.fabAddRating.setOnClickListener {
            val rating = binding.ratingStars.rating
            val comment = binding.tiRating.text.toString()
            lifecycleScope.launch {
                ratingViewModel.insertRating(rating,comment)
            }
        }
    }

    private fun subscribeToObservers() {
        //Did the insert work?
        lifecycleScope.launch {
            ratingViewModel.insertRatingFlow.collect {
                it.getContentIfNotHandled()?.let { result ->
                    when (result.status) {
                        Status.ERROR -> {
                            Snackbar.make(
                                binding.root,
                                result.message ?: "Konnte Rating nicht speichern",
                                Snackbar.LENGTH_LONG
                            ).setAnchorView(R.id.fab_addRating)
                                .show()
                        }
                        Status.SUCCESS -> {
                            Snackbar.make(
                                binding.root,
                                "Rating gespeichert",
                                Snackbar.LENGTH_LONG
                            ).setAnchorView(R.id.fab_addRating)
                                .show()
                        }
                        else -> { /* NO-OP */ }
                    }
                }
            }
        }
    }
}

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class ShowRatingsFragment: Fragment() {
    private val ratingViewModel: RatingViewModel by activityViewModels()

    private var _binding: ShowRatingsFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var ratingsListAdapter: RatingsListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ShowRatingsFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvRatings.apply {
            ratingsListAdapter = RatingsListAdapter()
            adapter = ratingsListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        lifecycleScope.launchWhenStarted {
            ratingViewModel.allRatings.collect { result ->
                when (result.status) {
                    Status.LOADING -> {
                        binding.pBRatinglist.visibility = View.VISIBLE
                    }
                    Status.SUCCESS -> {
                        binding.pBRatinglist.visibility = View.GONE
                        result.data?.let { list ->
                        ratingsListAdapter.submitList(list.sortedByDescending { it.timeStamp }) }
                    }
                    else -> { /* NO-OP */ }
                }
            }
        }
    }

}