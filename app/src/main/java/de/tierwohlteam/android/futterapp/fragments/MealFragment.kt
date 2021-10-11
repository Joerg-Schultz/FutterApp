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
import de.tierwohlteam.android.futterapp.databinding.AddMealFragmentBinding
import de.tierwohlteam.android.futterapp.databinding.AddRatingFragmentBinding
import de.tierwohlteam.android.futterapp.databinding.MealFragmentBinding
import de.tierwohlteam.android.futterapp.viewModels.MealViewModel
import de.tierwohlteam.android.futterapp.viewModels.RatingViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

class MealFragment: Fragment(R.layout.meal_fragment) {
    private var _binding: MealFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = MealFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewPager2 = view.findViewById<ViewPager2>(R.id.meal_pager_container)

        val fragmentTitleList: Map<String,Fragment> = mapOf(
            getString(R.string.addMeal) to AddMealFragment(),
        )
        viewPager2.adapter = RatingsViewPagerAdapter(this.childFragmentManager, lifecycle,
            ArrayList(fragmentTitleList.values)
        )

        val tabLayout = view.findViewById<TabLayout>(R.id.meal_tablayout)
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.text = fragmentTitleList.keys.toTypedArray()[position]
        }.attach()
    }
}
@ExperimentalCoroutinesApi
class AddMealFragment : Fragment(R.layout.add_meal_fragment) {
    private var _binding: AddMealFragmentBinding? = null
    private val binding get() = _binding!!

    private val mealViewModel: MealViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddMealFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }


}

