package de.tierwohlteam.android.futterapp.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import de.tierwohlteam.android.futterapp.R
import de.tierwohlteam.android.futterapp.adapters.FridgeListAdapter
import de.tierwohlteam.android.futterapp.adapters.FridgeViewPagerAdapter
import de.tierwohlteam.android.futterapp.adapters.MealViewPagerAdapter
import de.tierwohlteam.android.futterapp.databinding.FillFridgeFragmentBinding
import de.tierwohlteam.android.futterapp.databinding.FridgeFragmentBinding
import de.tierwohlteam.android.futterapp.databinding.MealFragmentBinding
import de.tierwohlteam.android.futterapp.databinding.ShowFridgeFragmentBinding
import de.tierwohlteam.android.futterapp.models.Food
import de.tierwohlteam.android.futterapp.models.FoodType
import de.tierwohlteam.android.futterapp.models.Pack
import de.tierwohlteam.android.futterapp.others.Status
import de.tierwohlteam.android.futterapp.viewModels.FridgeViewModel
import de.tierwohlteam.android.futterapp.viewModels.MealViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

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
            getString(R.string.addPacks) to FillFridgeFragment(),
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

        binding.rvShowfridge.apply {
            fridgeListAdapter = FridgeListAdapter()
            adapter = fridgeListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvShowfridge)

        lifecycleScope.launchWhenStarted {
            fridgeViewModel.content.collect {
                when (it.status) {
                    Status.LOADING -> binding.pBShowfridge.visibility = View.VISIBLE
                    Status.SUCCESS -> {
                        binding.pBShowfridge.visibility = View.GONE
                        fridgeListAdapter.submitList(it.data!!)
                    }
                    else -> { /* NO-OP */ }
                }
            }
        }
    }
    private val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(
        0,
        ItemTouchHelper.RIGHT // or ItemTouchHelper.LEFT,
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
                ItemTouchHelper.RIGHT -> fridgeViewModel.deleteOnePack(pos)
            }
        }

        override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            return super.getSwipeDirs(recyclerView, viewHolder)
        }
    }
}

@ExperimentalCoroutinesApi
class FillFridgeFragment: Fragment(R.layout.fill_fridge_fragment) {
    private var _binding: FillFridgeFragmentBinding? = null
    private val binding get() = _binding!!

    private val fridgeViewModel: FridgeViewModel by activityViewModels()

    private lateinit var fridgeListAdapter: FridgeListAdapter
    private val mealViewModel: MealViewModel by activityViewModels()

    private lateinit var foodList: List<Food>

    private var currentFoodType: FoodType? = null
    private var currentFoodName = ""
    private var currentGram = 0
    private var currentAmount = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FillFridgeFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            mealViewModel.allFoods.collect { result ->
                if (result.status == Status.SUCCESS) foodList = result.data!!
            }
        }
        binding.rvFillfridge.apply {
            fridgeListAdapter = FridgeListAdapter { pack: Pack, context: Context -> amountSelector(pack, context) }
            adapter = fridgeListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        lifecycleScope.launchWhenStarted {
            fridgeViewModel.contentWithEmpty.collect {
                when (it.status) {
                    Status.LOADING -> binding.pBFillfridge.visibility = View.VISIBLE
                    Status.SUCCESS -> {
                        binding.pBFillfridge.visibility = View.GONE
                        fridgeListAdapter.submitList(it.data!!)
                    }
                    else -> { /* NO-OP */ }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            fridgeViewModel.insertPacksFlow.collect { result ->
                val resource = result.getContentIfNotHandled()
                if (resource != null) {
                    when (resource.status) {
                        Status.SUCCESS -> {
                            Snackbar.make(binding.root,"Inserted Packs", Snackbar.LENGTH_LONG).show()
                        }
                        Status.ERROR -> {
                            Snackbar.make(binding.root,"Could not insert Packs", Snackbar.LENGTH_LONG).show()
                        }
                        else -> { /* NO-OP */ }
                    }
                }
            }
        }

        binding.fabFillFridge.setOnClickListener {
            newPack(it.context)
        }
    }

    // this is mainly a copy from mealfragment
    // TODO can I merge this?
    private fun newPack(context: Context) {
        val foodGroups = FoodType.values().map { translateFoodType(it) }.toTypedArray()
        val foodMap = FoodType.values().associateWith { translateFoodType(it) }
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
        val gramSteps = (250..2000 step 250).toList()
        var currentSelection = 500
        numberPicker.apply {
            wrapSelectorWheel = true
            minValue = 0
            maxValue = gramSteps.lastIndex
            displayedValues = gramSteps.map { it.toString() }.toTypedArray()
            value = gramSteps[1]
            setOnValueChangedListener { picker, oldVal, newVal ->
                currentSelection = gramSteps[newVal]
            }
        }
        AlertDialog.Builder(context)
            .setTitle("Grams")
            .setPositiveButton("OK") { dialog, which->
                currentGram = currentSelection
                selectAmount(context)
            }
            .setNeutralButton("Cancel") { dialog, which ->
                currentGram = 0
            }
            .setView(numberPicker)
            .show()
    }

    fun amountSelector(pack: Pack, context: Context) {
        currentFoodName = pack.food.name
        currentFoodType = pack.food.group
        currentGram = pack.size
        selectAmount(context)
    }
    private fun selectAmount(context: Context) {
        val numberPicker = NumberPicker(context)
        var currentSelection = 1
        numberPicker.apply {
            wrapSelectorWheel = true
            minValue = 1
            maxValue = 10
            value = 1
            setOnValueChangedListener { picker, oldVal, newVal ->
                currentSelection = newVal
            }
        }
        AlertDialog.Builder(context)
            .setTitle("Amount")
            .setPositiveButton("OK") { dialog, which->
                currentAmount = currentSelection
               if( currentAmount != 0 && currentFoodType != null) {
                    lifecycleScope.launch {
                        fridgeViewModel.addToFridge(currentFoodType!!, currentFoodName, currentGram, currentAmount)
                    }
                }
            }
            .setNeutralButton("Cancel") { dialog, which ->
                currentAmount = 0
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

