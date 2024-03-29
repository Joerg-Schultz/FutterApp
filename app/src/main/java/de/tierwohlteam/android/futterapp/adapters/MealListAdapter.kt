package de.tierwohlteam.android.futterapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TableRow
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.tierwohlteam.android.futterapp.databinding.MealItemBinding
import de.tierwohlteam.android.futterapp.models.Food
import de.tierwohlteam.android.futterapp.models.FoodType
import de.tierwohlteam.android.futterapp.models.Meal
import de.tierwohlteam.android.futterapp.others.icon
import de.tierwohlteam.android.futterapp.others.minute
import de.tierwohlteam.android.futterapp.others.translate
import kotlinx.coroutines.ExperimentalCoroutinesApi

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
        holder.setIsRecyclable(false) //TODO this kills recycling, but otherwise items are added to existing table
        val meal = differ.currentList[position]
        val timeStamp = meal.feeding.time
        val dayOfWeek = timeStamp.dayOfWeek.translate(holder.binding.tvDate.context, short = true)
        val day = timeStamp.dayOfMonth
        val month = timeStamp.monthNumber
        val hour = timeStamp.hour
        val min = timeStamp.minute.minute()
        val dateString = "$dayOfWeek $day.$month $hour:$min"
        holder.binding.tvDate.text = dateString
        val table = holder.binding.mealTableLayout
        for (ingredient in meal.ingredients) {
            val row = TableRow(table.context)
            val food = allFoods.firstOrNull() { it.id == ingredient.foodID }
            val cellGroupImage = ImageView(table.context)
            row.addView(cellGroupImage, 0)
            val icon = (food?.group ?: FoodType.OTHERS).icon(cellGroupImage.context)
            if(icon != null) {
                cellGroupImage.setImageDrawable(icon)
            }
            cellGroupImage.layoutParams.height = 70
            cellGroupImage.layoutParams.width = 70
            val cellIngredient = TextView(table.context)
            cellIngredient.text = food?.name ?: "No name"
            cellIngredient.setPadding(16,0,0,0)
            row.addView(cellIngredient, 1)
            val cellGram = TextView(table.context)
            val gramText = "${ingredient.gram} gr"
            cellGram.text = gramText
            cellGram.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
            row.addView(cellGram, 2)
            table.addView(row)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}