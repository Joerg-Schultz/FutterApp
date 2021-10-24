package de.tierwohlteam.android.futterapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.tierwohlteam.android.futterapp.databinding.FoodItemBinding
import de.tierwohlteam.android.futterapp.models.Food
import de.tierwohlteam.android.futterapp.others.iconFoodTypeHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class FoodListAdapter: RecyclerView.Adapter<FoodListAdapter.FoodViewHolder>() {

    // generate a diff list to update only changed items in the RecView
    private val diffCallback = object : DiffUtil.ItemCallback<Food>(){
        override fun areItemsTheSame(oldItem: Food, newItem: Food): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: Food, newItem: Food): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }
    private val differ = AsyncListDiffer(this, diffCallback)
    fun submitList(list: List<Food>) = differ.submitList(list)


    inner class FoodViewHolder(val binding: FoodItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodListAdapter.FoodViewHolder {
        val binding = FoodItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodListAdapter.FoodViewHolder, position: Int) {
        val food = differ.currentList[position]
        holder.binding.apply {
            tvFoodname.text = food.name
            val icon = iconFoodTypeHelper(food.group, imageGroupFoodlist.context)
            if (icon != null) {
                imageGroupFoodlist.setImageDrawable(icon)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}