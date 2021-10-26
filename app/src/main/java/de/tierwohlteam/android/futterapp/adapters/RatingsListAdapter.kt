package de.tierwohlteam.android.futterapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.tierwohlteam.android.futterapp.databinding.RatingItemBinding
import de.tierwohlteam.android.futterapp.models.Rating
import de.tierwohlteam.android.futterapp.others.minute
import de.tierwohlteam.android.futterapp.others.translate

class RatingsListAdapter: RecyclerView.Adapter<RatingsListAdapter.GoalViewHolder>() {

    // generate a diff list to update only changed items in the RecView
    private val diffCallback = object : DiffUtil.ItemCallback<Rating>(){
        override fun areItemsTheSame(oldItem: Rating, newItem: Rating): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: Rating, newItem: Rating): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }
    private val differ = AsyncListDiffer(this, diffCallback)
    fun submitList(list: List<Rating>) = differ.submitList(list)


    inner class GoalViewHolder(val binding: RatingItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingsListAdapter.GoalViewHolder {
        val binding = RatingItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return GoalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RatingsListAdapter.GoalViewHolder, position: Int) {
        val rating = differ.currentList[position]
        holder.binding.ratingBar.rating = rating.value
        val dayOfWeek = rating.timeStamp.dayOfWeek.translate(holder.binding.tvDate.context, short = true)
        val day = rating.timeStamp.dayOfMonth
        val month = rating.timeStamp.monthNumber
        val hour = rating.timeStamp.hour
        val min = rating.timeStamp.minute.minute()
        val dateString = "$dayOfWeek $day.$month $hour:$min"
        holder.binding.tvDate.text = dateString
        holder.binding.tvDescription.text = rating.comment
        val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
        holder.itemView.layoutParams = params
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}
