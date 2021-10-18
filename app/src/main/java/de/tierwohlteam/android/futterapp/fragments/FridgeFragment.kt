package de.tierwohlteam.android.futterapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import de.tierwohlteam.android.futterapp.R
import de.tierwohlteam.android.futterapp.adapters.FridgeListAdapter
import de.tierwohlteam.android.futterapp.adapters.FridgeViewPagerAdapter
import de.tierwohlteam.android.futterapp.adapters.MealViewPagerAdapter
import de.tierwohlteam.android.futterapp.databinding.FridgeFragmentBinding
import de.tierwohlteam.android.futterapp.databinding.MealFragmentBinding
import de.tierwohlteam.android.futterapp.databinding.ShowFridgeFragmentBinding
import de.tierwohlteam.android.futterapp.others.Status
import de.tierwohlteam.android.futterapp.viewModels.FridgeViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
class FridgeFragment: Fragment(R.layout.fridge_fragment) {
    private var _binding: FridgeFragmentBinding? = null
    private val binding get() =_binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FridgeFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewPager2 = view.findViewById<ViewPager2>(R.id.fridge_pager_container)

        val fragmentTitleList: Map<String,Fragment> = mapOf(
            getString(R.string.fridge) to ShowFridgeFragment(),
            //getString(R.string.addPacks) to FillFridgeFragment(),
        )
        viewPager2.adapter = FridgeViewPagerAdapter(this.childFragmentManager, lifecycle,
            ArrayList(fragmentTitleList.values)
        )

        val tabLayout = view.findViewById<TabLayout>(R.id.fridge_tablayout)
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.text = fragmentTitleList.keys.toTypedArray()[position]
        }.attach()
    }
}

@ExperimentalCoroutinesApi
class ShowFridgeFragment: Fragment(R.layout.show_fridge_fragment) {
    private var _binding: ShowFridgeFragmentBinding? = null
    private val binding get() =_binding!!

    private val fridgeViewModel: FridgeViewModel by activityViewModels()

    private lateinit var fridgeListAdapter: FridgeListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = ShowFridgeFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvFridge.apply {
            fridgeListAdapter = FridgeListAdapter()
            adapter = fridgeListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        lifecycleScope.launchWhenStarted {
            fridgeViewModel.content.collect {
                when (it.status) {
                    Status.LOADING -> binding.pBFridge.visibility = View.VISIBLE
                    Status.SUCCESS -> {
                        binding.pBFridge.visibility = View.GONE
                        fridgeListAdapter.submitList(it.data!!.filter { packs -> packs.amount > 0 })
                    }
                    else -> { /* NO-OP */ }
                }
            }
        }
    }
}

