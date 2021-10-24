package de.tierwohlteam.android.futterapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.tierwohlteam.android.futterapp.R
import de.tierwohlteam.android.futterapp.databinding.MealComponentItemBinding
import de.tierwohlteam.android.futterapp.models.FoodType
import de.tierwohlteam.android.futterapp.others.iconFoodTypeHelper
import de.tierwohlteam.android.futterapp.others.translateFoodTypeHelper
import de.tierwohlteam.android.futterapp.viewModels.MealViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class MealComponentListAdapter: RecyclerView.Adapter<MealComponentListAdapter.GoalViewHolder>() {

    // generate a diff list to update only changed items in the RecView
    private val diffCallback = object : DiffUtil.ItemCallback<MealViewModel.MealComponent>(){
        override fun areItemsTheSame(oldItem: MealViewModel.MealComponent, newItem: MealViewModel.MealComponent): Boolean {
            return oldItem.foodGroup == newItem.foodGroup && oldItem.foodName == newItem.foodName
        }
        override fun areContentsTheSame(oldItem: MealViewModel.MealComponent, newItem: MealViewModel.MealComponent): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }
    private val differ = AsyncListDiffer(this, diffCallback)
    fun submitList(list: List<MealViewModel.MealComponent>) = differ.submitList(list)


    inner class GoalViewHolder(val binding: MealComponentItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealComponentListAdapter.GoalViewHolder {
        val binding = MealComponentItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return GoalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealComponentListAdapter.GoalViewHolder, position: Int) {
        val component = differ.currentList[position]
        holder.binding.apply {
            tvIngredient.text = component.foodName
            val gramString = "${component.gram} gr"
            tvGram.text = gramString
            val icon = iconFoodTypeHelper(component.foodGroup, imageGroup.context)
            if(icon != null) {
                imageGroup.setImageDrawable(icon)
            }
        }
        val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
        holder.itemView.layoutParams = params
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
