package de.tierwohlteam.android.futterapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.tierwohlteam.android.futterapp.databinding.MealItemBinding
import de.tierwohlteam.android.futterapp.models.Meal

class MealListAdapter: RecyclerView.Adapter<MealListAdapter.GoalViewHolder>() {

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
            // TODO get food info from repository
            val row = TableRow(table.context)
            val cellGroup = TextView(table.context)
            cellGroup.text = "bla"
            row.addView(cellGroup, 0)
            val cellIngredient = TextView(table.context)
            cellIngredient.text = "blub"
            row.addView(cellIngredient, 1)
            val cellGram = TextView(table.context)
            cellGram.text = "250"
            row.addView(cellGram, 2)
            table.addView(row)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}