package de.tierwohlteam.android.futterapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.benasher44.uuid.Uuid
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import de.tierwohlteam.android.futterapp.R
import de.tierwohlteam.android.futterapp.adapters.MealListAdapter
import de.tierwohlteam.android.futterapp.adapters.MealViewPagerAdapter
import de.tierwohlteam.android.futterapp.databinding.AddMealFragmentBinding
import de.tierwohlteam.android.futterapp.databinding.MealFragmentBinding
import de.tierwohlteam.android.futterapp.models.FoodType
import de.tierwohlteam.android.futterapp.viewModels.MealViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
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
        viewPager2.adapter = MealViewPagerAdapter(this.childFragmentManager, lifecycle,
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

    inner class MealComponent(
        val foodGroup: FoodType,
        val foodName: String,
        val foodID: Uuid?,
        val gram: Int
    ) {

    }

    private val mealViewModel: MealViewModel by activityViewModels()
    private lateinit var mealListAdapter: MealListAdapter
    private val ingredientList = mutableListOf<MealComponent>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddMealFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvMeal.apply {
            mealListAdapter = MealListAdapter()
            adapter = mealListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        mealListAdapter.submitList(ingredientList)
        binding.btnAddingredient.setOnClickListener {
            selectGroup(it.context)
            // set ingredient
            // set grams
        }
    }
    private fun selectGroup(context: Context) {
        val singleItems = listOf(R.string.meat, R.string.carbs, R.string.veggies, R.string.others)
            .map { resources.getString(it) }.toTypedArray()
        val checkedItem = 0
        MaterialAlertDialogBuilder(context)
            .setTitle(resources.getString(R.string.select_group))
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
                // Respond to neutral button press
            }
            .setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
                // Respond to positive button press
                selectIngredient(context)
            }
            // Single-choice items (initialized with checked item)
            .setSingleChoiceItems(singleItems, checkedItem) { dialog, which ->
                val selectedGroup = singleItems[which]
            }
            .show()
    }

    private fun selectIngredient(context: Context) {
        val singleItems = listOf("eins", "zwei", "drei")
            .toTypedArray()
        val checkedItem = 0
        MaterialAlertDialogBuilder(context)
            .setTitle(resources.getString(R.string.select_ingredient))
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
                // Respond to neutral button press
            }
            .setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
                // Respond to positive button press
            }
            // Single-choice items (initialized with checked item)
            .setSingleChoiceItems(singleItems, checkedItem) { dialog, which ->
                val selectedGroup = singleItems[which]
            }
            .show()
    }
}

