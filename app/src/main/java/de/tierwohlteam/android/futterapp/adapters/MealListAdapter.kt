package de.tierwohlteam.android.futterapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.tierwohlteam.android.futterapp.databinding.MealItemBinding
import de.tierwohlteam.android.futterapp.databinding.RatingItemBinding
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
        holder.binding.tvMeal.text = meal.feeding.time.toString()
    }
}