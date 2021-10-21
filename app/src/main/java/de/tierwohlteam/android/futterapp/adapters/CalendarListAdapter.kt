package de.tierwohlteam.android.futterapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.tierwohlteam.android.futterapp.databinding.CalendarItemBinding
import de.tierwohlteam.android.futterapp.databinding.RatingItemBinding
import de.tierwohlteam.android.futterapp.models.Rating
import de.tierwohlteam.android.futterapp.viewModels.StatisticsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class CalendarListAdapter: RecyclerView.Adapter<CalendarListAdapter.CalendarViewHolder>() {

    // generate a diff list to update only changed items in the RecView
    private val diffCallback = object : DiffUtil.ItemCallback<StatisticsViewModel.CalendarEntry>(){
        override fun areItemsTheSame(oldItem: StatisticsViewModel.CalendarEntry, newItem: StatisticsViewModel.CalendarEntry): Boolean {
            return oldItem.date == newItem.date
        }
        override fun areContentsTheSame(oldItem: StatisticsViewModel.CalendarEntry, newItem: StatisticsViewModel.CalendarEntry): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }
    private val differ = AsyncListDiffer(this, diffCallback)
    fun submitList(list: List<StatisticsViewModel.CalendarEntry>) = differ.submitList(list)


    inner class CalendarViewHolder(val binding: CalendarItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarListAdapter.CalendarViewHolder {
        val binding = CalendarItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return CalendarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarListAdapter.CalendarViewHolder, position: Int) {
        val calendarEntry = differ.currentList[position]
        holder.binding.tvCalendarDate.text = calendarEntry.date.toString()
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}

