package de.tierwohlteam.android.futterapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TableRow
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.tierwohlteam.android.futterapp.databinding.CalendarItemBinding
import de.tierwohlteam.android.futterapp.models.Food
import de.tierwohlteam.android.futterapp.models.Meal
import de.tierwohlteam.android.futterapp.models.Rating
import de.tierwohlteam.android.futterapp.viewModels.StatisticsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class CalendarListAdapter: RecyclerView.Adapter<CalendarListAdapter.CalendarViewHolder>() {
    lateinit var foodList: List<Food>

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
        val avgRating = calendarEntry.ratings.map { it.value }.average().toFloat()
        if (avgRating.isNaN()) {
            holder.binding.ratingBarCalendarItem.visibility = View.GONE
        }
        else {
            holder.binding.ratingBarCalendarItem.rating = avgRating
        }
        var meals = calendarEntry.meals
        meals.sortBy { it.feeding.time }
        var ratings = calendarEntry.ratings
        ratings.sortBy { it.timeStamp }
        while (meals.isNotEmpty() || ratings.isNotEmpty()) {
            when {
                meals.isEmpty() -> {
                    ratings.forEach { addRatingToTable(it, holder) }
                    ratings = mutableListOf()
                }
                ratings.isEmpty() -> {
                    meals.forEach { addMealToTable(it, holder) }
                    meals = mutableListOf()
                }
                meals.first().feeding.time <= ratings.first().timeStamp -> {
                    addMealToTable(meals.first(),holder)
                    meals.removeFirst()
                }
                else -> {
                    addRatingToTable(ratings.first(),holder)
                    ratings.removeFirst()
                }
            }
        }
    }

    private fun addRatingToTable(rating: Rating, holder: CalendarViewHolder) {
        val table = holder.binding.tableCalendarItem
        val row = TableRow(table.context)
        val timeString = "${rating.timeStamp.hour}:${rating.timeStamp.minute}"
        val timeCell = TextView(table.context)
        timeCell.text = timeString
        row.addView(timeCell,0)
        val starCell = RatingBar(table.context,null, android.R.attr.ratingBarStyleSmall)
        starCell.rating = rating.value
        row.addView(starCell,1)
        table.addView(row)
    }
    private fun addMealToTable(meal: Meal, holder: CalendarViewHolder) {
        val table = holder.binding.tableCalendarItem
        val row = TableRow(table.context)
        val timeString = "${meal.feeding.time.hour}:${meal.feeding.time.minute}"
        val timeCell = TextView(table.context)
        timeCell.text = timeString
        row.addView(timeCell,0)
        val foodCell = TextView(table.context)
        foodCell.text = meal.ingredients.joinToString("\n") { ingredient ->
            foodList.filter { it.id == ingredient.foodID }
                .map { it.name }
                .first().toString()
        }
        row.addView(foodCell, 1)
        table.addView(row)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}

