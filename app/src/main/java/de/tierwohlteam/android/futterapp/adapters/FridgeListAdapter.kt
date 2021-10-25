package de.tierwohlteam.android.futterapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.tierwohlteam.android.futterapp.R
import de.tierwohlteam.android.futterapp.databinding.FoodItemBinding
import de.tierwohlteam.android.futterapp.databinding.FridgeItemBinding
import de.tierwohlteam.android.futterapp.models.Food
import de.tierwohlteam.android.futterapp.models.Pack
import de.tierwohlteam.android.futterapp.models.PacksInFridge
import de.tierwohlteam.android.futterapp.others.icon

class FridgeListAdapter(val amountSelector: ((pack: Pack, context: Context) -> Unit)? = null): RecyclerView.Adapter<FridgeListAdapter.FridgeViewHolder>() {

    // generate a diff list to update only changed items in the RecView
    private val diffCallback = object : DiffUtil.ItemCallback<PacksInFridge>(){
        override fun areItemsTheSame(oldItem: PacksInFridge, newItem: PacksInFridge): Boolean {
            return oldItem.pack.food.id == newItem.pack.food.id &&
                    oldItem.pack.size == newItem.pack.size &&
                    oldItem.amount == newItem.amount
        }
        override fun areContentsTheSame(oldItem: PacksInFridge, newItem: PacksInFridge): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }
    private val differ = AsyncListDiffer(this, diffCallback)
    fun submitList(list: List<PacksInFridge>) = differ.submitList(list)

    inner class FridgeViewHolder(val binding: FridgeItemBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FridgeListAdapter.FridgeViewHolder {
        val binding = FridgeItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return FridgeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FridgeListAdapter.FridgeViewHolder, position: Int) {
        val packs = differ.currentList[position]
        holder.binding.apply {
            val icon = packs.pack.food.group.icon(fridgeImageGroup.context)
            if (icon != null) {
                fridgeImageGroup.setImageDrawable(icon)
            }
            tvFridgeFoodname.text = packs.pack.food.name
            val gramString = "${packs.pack.size} gr"
            tvFridgeGram.text = gramString
            val amountString = "${packs.amount} ${tvFridgeAmount.context.getString(R.string.pack)}"
            tvFridgeAmount.text = amountString
        }
        if(amountSelector != null) {
            holder.itemView.setOnClickListener { view ->
                amountSelector.invoke(packs.pack, view.context)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}
