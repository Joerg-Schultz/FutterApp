package de.tierwohlteam.android.futterapp.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import de.tierwohlteam.android.futterapp.R
import de.tierwohlteam.android.futterapp.adapters.FoodListAdapter
import de.tierwohlteam.android.futterapp.adapters.MealComponentListAdapter
import de.tierwohlteam.android.futterapp.adapters.MealListAdapter
import de.tierwohlteam.android.futterapp.adapters.MealViewPagerAdapter
import de.tierwohlteam.android.futterapp.databinding.AddMealFragmentBinding
import de.tierwohlteam.android.futterapp.databinding.MealFragmentBinding
import de.tierwohlteam.android.futterapp.databinding.ShowFoodFragmentBinding
import de.tierwohlteam.android.futterapp.databinding.ShowMealsFragmentBinding
import de.tierwohlteam.android.futterapp.models.Food
import de.tierwohlteam.android.futterapp.models.FoodType
import de.tierwohlteam.android.futterapp.others.Status
import de.tierwohlteam.android.futterapp.others.defaultGram
import de.tierwohlteam.android.futterapp.others.gramSteps
import de.tierwohlteam.android.futterapp.others.translate
import de.tierwohlteam.android.futterapp.viewModels.MealViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@ExperimentalCoroutinesApi
class MealFragment @Inject constructor(
    private val foodListAdapter: FoodListAdapter
): Fragment(R.layout.meal_fragment) {
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
            getString(R.string.showMeals) to ShowMealsFragment(),
            getString(R.string.food) to ShowFoodFragment(foodListAdapter),
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
    private lateinit var foodList: List<Food>

    private var _binding: AddMealFragmentBinding? = null
    private val binding get() = _binding!!

    private var currentFoodType: FoodType? = null
    private var currentFoodName = ""
    private var currentGram = 0

    private val mealViewModel: MealViewModel by activityViewModels()
    private lateinit var mealComponentListAdapter: MealComponentListAdapter

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
            mealComponentListAdapter = MealComponentListAdapter()
            adapter = mealComponentListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvMeal)

        binding.btnAddingredient.setOnClickListener {
            selectGroup(it.context)
            // set ingredient
            // set grams
        }
        binding.btnSavemeal.setOnClickListener {
            lifecycleScope.launch {
                mealViewModel.saveMeal()
            }
        }
        lifecycleScope.launchWhenStarted {
            mealViewModel.ingredientList.collect {
                mealComponentListAdapter.submitList(it)
            }
        }
        lifecycleScope.launchWhenStarted {
            mealViewModel.allFoods.collect { result ->
                if (result.status == Status.SUCCESS) foodList = result.data!!
            }
        }

        lifecycleScope.launchWhenStarted {
            mealViewModel.latestMeal.collect{ result ->
                if (result.status == Status.SUCCESS) {
                    mealViewModel.emptyIngredientList()
                    val ingredients = result.data?.ingredients ?: emptyList()
                    for (ingredient in ingredients) {  //data CAN bes null here
                        val food = foodList.first {it.id == ingredient.foodID}
                        mealViewModel.addIngredient(food.group, food.name, ingredient.gram)
                    }
                }

            }
        }

        lifecycleScope.launchWhenStarted {
            mealViewModel.insertMealFlow.collect { result ->
                val resource = result.getContentIfNotHandled()
                if (resource != null) {
                    when (resource.status) {
                        Status.SUCCESS -> {
                            Snackbar.make(binding.root,"Inserted Meal", Snackbar.LENGTH_LONG).show()
                        }
                        Status.ERROR -> {
                            Snackbar.make(binding.root,"Could not insert Meal", Snackbar.LENGTH_LONG).show()
                        }
                        else -> { /* NO-OP */ }
                    }
                }
            }
        }
    }

    private val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(
        0,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }


        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val pos = viewHolder.adapterPosition
            when(direction){
                ItemTouchHelper.RIGHT -> mealViewModel.deleteIngredient(pos)
                //ItemTouchHelper.LEFT -> mealViewModel.deleteIngredient(pos)
            }
        }

        override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            return super.getSwipeDirs(recyclerView, viewHolder)
        }

    }


    private fun selectGroup(context: Context) {
        val foodGroups = FoodType.values().map { it.translate(context) }.toTypedArray()
        val foodMap = FoodType.values().associateWith { it.translate(context) }
        val checkedItem = foodGroups.indexOf(resources.getString(R.string.others))
        currentFoodType = FoodType.OTHERS
        MaterialAlertDialogBuilder(context)
            .setTitle(resources.getString(R.string.select_group))
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
                currentFoodType = null
            }
            .setPositiveButton(resources.getString(R.string.next)) { dialog, which ->
                selectIngredient(context)
            }
            .setSingleChoiceItems(foodMap.values.toTypedArray(), checkedItem) { dialog, which ->
                currentFoodType  = foodMap.filterValues { it == foodMap.values.toTypedArray()[which] }.keys.first()
            }
            .show()
    }

    private fun selectIngredient(context: Context) {
        val foodNameInput = EditText(context)
        foodNameInput.apply {
            setHint("new food")
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setPadding(20, 20, 20, 20)
        }

        val foodNames = foodList.filter { it.group == currentFoodType }
            .map { it.name }
            .toTypedArray()
        MaterialAlertDialogBuilder(context)
            .setTitle(resources.getString(R.string.select_ingredient))
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
                currentFoodName = ""
            }
            .setPositiveButton(resources.getString(R.string.next)) { dialog, which ->
                if (!foodNameInput.text.isNullOrBlank()) {
                    currentFoodName = foodNameInput.text.toString()
                }
                selectGram(context)
            }
            .setSingleChoiceItems(foodNames, -1) { dialog, which ->
                currentFoodName = foodNames[which]
            }
            .setView(foodNameInput)
            .show()
    }

    private fun selectGram(context: Context) {
        val numberPicker = NumberPicker(context)
        val gramSteps = currentFoodType?.gramSteps() ?: (0..200 step 10).toList()
        var currentSelection = currentFoodType?.defaultGram() ?: 10
        numberPicker.apply {
            wrapSelectorWheel = true
            minValue = 0
            maxValue = gramSteps.lastIndex
            displayedValues = gramSteps.map { it.toString() }.toTypedArray()
            value = gramSteps.indexOfFirst { it == currentSelection }
            setOnValueChangedListener { picker, oldVal, newVal ->
                currentSelection = gramSteps[newVal]
            }
        }
        AlertDialog.Builder(context)
            .setTitle("Grams")
            .setPositiveButton("OK") { dialog, which->
                currentGram = currentSelection
                if( currentFoodType != null) {
                    mealViewModel.addIngredient(currentFoodType!!, currentFoodName, currentGram)
                }
            }
            .setNeutralButton("Cancel") { dialog, which ->
                currentGram = 0
            }
            .setView(numberPicker)
            .show()
    }
}

@ExperimentalCoroutinesApi
class ShowMealsFragment: Fragment(R.layout.show_meals_fragment) {
    private var _binding: ShowMealsFragmentBinding? = null
    private val binding get() = _binding!!

    private val mealViewModel: MealViewModel by activityViewModels()
    private lateinit var mealListAdapter: MealListAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ShowMealsFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenStarted {
            mealViewModel.allFoods.collect { result ->
                when (result.status) {
                    Status.SUCCESS -> {
                        binding.rvMeals.apply {
                            mealListAdapter = MealListAdapter(result.data!!)
                            adapter = mealListAdapter
                            layoutManager = GridLayoutManager(requireContext(),1)
                            //layoutManager = GridLayoutManager(requireContext(),2)
                        }
                    }
                    else -> { /* NO-OP */ }
                }
            }
        }


        lifecycleScope.launchWhenStarted {
            mealViewModel.allMeals.collect { result ->
                when (result.status) {
                    Status.LOADING -> {
                        binding.pBMeallist.visibility = View.VISIBLE
                    }
                    Status.SUCCESS -> {
                        binding.pBMeallist.visibility = View.GONE
                        result.data?.let { list ->
                            mealListAdapter.submitList(list.sortedByDescending { it.feeding.time }) }
                    }
                    else -> { /* NO-OP */ }
                }
            }
        }
    }
}

@ExperimentalCoroutinesApi
class ShowFoodFragment @Inject constructor(
    private val foodListAdapter: FoodListAdapter
): Fragment(R.layout.show_food_fragment) {
    private var _binding: ShowFoodFragmentBinding? = null
    private val binding get() = _binding!!

    private val mealViewModel: MealViewModel by activityViewModels()
    //private lateinit var foodListAdapter: FoodListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ShowFoodFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvFoodlist.apply {
            //foodListAdapter = FoodListAdapter()
            adapter = foodListAdapter
            //layoutManager = LinearLayoutManager(requireContext())
            layoutManager = GridLayoutManager(requireContext(),2)
        }
        lifecycleScope.launchWhenStarted {
            mealViewModel.allFoods.collect { result ->
                when (result.status) {
                    Status.SUCCESS -> foodListAdapter.submitList(result.data!!.sortedBy { it.name })
                    else -> { /* NO-OP */ }
                }
            }
        }
    }
}