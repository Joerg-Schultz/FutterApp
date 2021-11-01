package de.tierwohlteam.android.futterapp.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import de.tierwohlteam.android.futterapp.adapters.FoodListAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class FutterAppFragmentFactory @Inject constructor(
    private val foodListAdapter: FoodListAdapter
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className) {
            MealFragment::class.java.name -> MealFragment(foodListAdapter)
            ShowFoodFragment::class.java.name -> ShowFoodFragment(foodListAdapter)
            else -> super.instantiate(classLoader, className)
        }
    }
}