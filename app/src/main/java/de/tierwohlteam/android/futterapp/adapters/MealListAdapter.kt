package de.tierwohlteam.android.futterapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.tierwohlteam.android.futterapp.databinding.MealItemBinding
import de.tierwohlteam.android.futterapp.models.Food
import de.tierwohlteam.android.futterapp.models.Ingredient
import de.tierwohlteam.android.futterapp.models.Meal
import de.tierwohlteam.android.futterapp.viewModels.MealViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class MealListAdapter(private val allFoods: List<Food>) :
    RecyclerView.Adapter<MealListAdapter.GoalViewHolder>() {

    // generate a diff list to update only changed items in the RecView
    private val diffCallback = object : DiffUtil.ItemCallback<Meal>(){
        override fun areItemsTheSame(oldItem: Meal, newItem: Meal): Boolean {
            return oldItem.feeding.id == newItem.feeding.id
        }
        override fun areContentsTheSame(oldItem: Meal, newItem: Meal): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }
    private val differ = AsyncListDiffer(this, diffCallback)
    fun submitList(list: List<Meal>) = differ.submitList(list)


    inner class GoalViewHolder(val binding: MealItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealListAdapter.GoalViewHolder {
        val binding = MealItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return GoalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealListAdapter.GoalViewHolder, position: Int) {
        val meal = differ.currentList[position]
        val timeStamp = meal.feeding.time
        val dayOfWeek = timeStamp.dayOfWeek
        val day = timeStamp.dayOfMonth
        val month = timeStamp.monthNumber
        val hour = timeStamp.hour
        val min = timeStamp.minute
        val dateString = "$dayOfWeek $day.$month $hour:$min"
        holder.binding.tvDate.text = dateString
        val table = holder.binding.mealTableLayout
        for (ingredient in meal.ingredients) {
            val row = TableRow(table.context)
            Log.d("FOOD", "ingredient foodID = ${ingredient.foodID}")
            val food = allFoods.firstOrNull() { it.id == ingredient.foodID }
            val cellGroup = TextView(table.context)
            cellGroup.text = food?.group?.toString() ?: "No Group"
            row.addView(cellGroup, 0)
            val cellIngredient = TextView(table.context)
            cellIngredient.text = food?.name ?: "No name"
            row.addView(cellIngredient, 1)
            val cellGram = TextView(table.context)
            cellGram.text = ingredient.gram.toString()
            row.addView(cellGram, 2)
            table.addView(row)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}