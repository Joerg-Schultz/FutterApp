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
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect

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
    private var currentFoodType: FoodType? = null
    private var currentFoodName = ""
    private var currentGram = 0

    private val mealViewModel: MealViewModel by activityViewModels()
    private lateinit var mealListAdapter: MealListAdapter
    private val ingredientList: MutableStateFlow<List<MealComponent>> = MutableStateFlow(emptyList())

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
        binding.btnAddingredient.setOnClickListener {
            selectGroup(it.context)
            // set ingredient
            // set grams
        }
        binding.btnSavemeal.setOnClickListener {
            Snackbar.make(
                binding.root,
                "Selected Group: $currentFoodType; Name: $currentFoodName; Gram: $currentGram",
                Snackbar.LENGTH_LONG
            ).show()
        }
        lifecycleScope.launchWhenStarted {
            ingredientList.collect {
                mealListAdapter.submitList(it)
            }
        }
    }
    private fun selectGroup(context: Context) {
        val foodGroups = FoodType.values().map { translateFoodType(it) }.toTypedArray()
        val foodMap = FoodType.values().associateWith { translateFoodType(it) }
        val checkedItem = foodGroups.indexOf(resources.getString(R.string.others))
        currentFoodType = FoodType.OTHERS
        MaterialAlertDialogBuilder(context)
            .setTitle(resources.getString(R.string.select_group))
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
                currentFoodType = null
            }
            .setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
                selectIngredient(context)
            }
            .setSingleChoiceItems(foodMap.values.toTypedArray(), checkedItem) { dialog, which ->
                currentFoodType  = foodMap.filterValues { it == foodMap.values.toTypedArray()[which] }.keys.first()
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
                if( currentFoodType != null) {
                    val newComponent = MealComponent(currentFoodType!!, currentFoodName, null, currentGram)
                    ingredientList.value = ingredientList.value + newComponent
                }
            }
            .setNeutralButton("Cancel") { dialog, which ->
                currentGram = 0
            }
            .setView(numberPicker)
            .show()
    }

    fun translateFoodType(type: FoodType): String =
        when (type) {
            FoodType.MEAT -> resources.getString(R.string.meat)
            FoodType.CARBS -> resources.getString(R.string.carbs)
            FoodType.VEGGIES_COOKED -> resources.getString(R.string.veggiesCooked)
            FoodType.VEGGIES_RAW -> resources.getString(R.string.veggiesRaw)
            FoodType.OTHERS -> resources.getString((R.string.others))
        }
}

