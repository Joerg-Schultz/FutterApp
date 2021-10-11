package de.tierwohlteam.android.futterapp.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.benasher44.uuid.Uuid
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
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
    private var currentFoodGroup = ""
    private var currentFoodName = ""
    private var currentGram = 0

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
        binding.btnSavemeal.setOnClickListener {
            Snackbar.make(
                binding.root,
                "Selected Group: $currentFoodGroup; Name: $currentFoodName; Gram: $currentGram",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }
    private fun selectGroup(context: Context) {
        val foodGroups = listOf(R.string.meat, R.string.carbs, R.string.veggies, R.string.others)
            .map { resources.getString(it) }.toTypedArray()
        val checkedItem = 0
        currentFoodGroup = foodGroups[checkedItem]
        MaterialAlertDialogBuilder(context)
            .setTitle(resources.getString(R.string.select_group))
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
                currentFoodGroup = ""
            }
            .setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
                selectIngredient(context)
            }
            .setSingleChoiceItems(foodGroups, checkedItem) { dialog, which ->
                currentFoodGroup = foodGroups[which]
            }
            .show()
    }

    private fun selectIngredient(context: Context) {
        val foodNames = listOf("eins", "zwei", "drei")
            .toTypedArray()
        val checkedItem = 0
        currentFoodName = foodNames[checkedItem]
        MaterialAlertDialogBuilder(context)
            .setTitle(resources.getString(R.string.select_ingredient))
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
                currentFoodName = ""
            }
            .setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
                selectGram(context)
            }
            .setSingleChoiceItems(foodNames, checkedItem) { dialog, which ->
                currentFoodName = foodNames[which]
            }
            .show()
    }

    private fun selectGram(context: Context) {
        val numberPicker = NumberPicker(context)
        val gramSteps = (50..500 step 50).toList()
        var currentSelection = 250
        numberPicker.apply {
            wrapSelectorWheel = true
            minValue = 0
            maxValue = gramSteps.lastIndex
            displayedValues = gramSteps.map { it.toString() }.toTypedArray()
            value = gramSteps.lastIndex / 2
            setOnValueChangedListener { picker, oldVal, newVal ->
                currentSelection = gramSteps[newVal]
            }
        }
        AlertDialog.Builder(context)
            .setTitle("Grams")
            .setPositiveButton("OK") { dialog, which->
                currentGram = currentSelection
                val newComponent = MealComponent(FoodType.MEAT, currentFoodName, null, currentGram)
            }
            .setNeutralButton("Cancel") { dialog, which ->
                currentGram = 0
            }
            .setView(numberPicker)
            .show()
    }
}

