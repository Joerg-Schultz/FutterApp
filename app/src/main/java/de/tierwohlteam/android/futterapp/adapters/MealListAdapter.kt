package de.tierwohlteam.android.futterapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.tierwohlteam.android.futterapp.databinding.MealItemBinding
import de.tierwohlteam.android.futterapp.fragments.AddMealFragment
import de.tierwohlteam.android.futterapp.fragments.MealFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class MealListAdapter: RecyclerView.Adapter<MealListAdapter.GoalViewHolder>() {

    // generate a diff list to update only changed items in the RecView
    private val diffCallback = object : DiffUtil.ItemCallback<AddMealFragment.MealComponent>(){
        override fun areItemsTheSame(oldItem: AddMealFragment.MealComponent, newItem: AddMealFragment.MealComponent): Boolean {
            return oldItem.foodGroup == newItem.foodGroup && oldItem.foodName == newItem.foodName
        }
        override fun areContentsTheSame(oldItem: AddMealFragment.MealComponent, newItem: AddMealFragment.MealComponent): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }
    private val differ = AsyncListDiffer(this, diffCallback)
    fun submitList(list: List<AddMealFragment.MealComponent>) = differ.submitList(list)


    inner class GoalViewHolder(val binding: MealItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealListAdapter.GoalViewHolder {
        val binding = MealItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return GoalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealListAdapter.GoalViewHolder, position: Int) {
        val component = differ.currentList[position]
        holder.binding.apply {
            // TODO translate FoodType AddMealFragment().translateFoodType
            tvGroup.text = component.foodGroup.toString()
            tvIngredient.text = component.foodName
            tvGram.text = component.gram.toString()
        }
        val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
        holder.itemView.layoutParams = params
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
