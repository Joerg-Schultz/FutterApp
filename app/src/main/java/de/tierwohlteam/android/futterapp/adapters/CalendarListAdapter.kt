package de.tierwohlteam.android.futterapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.tierwohlteam.android.futterapp.databinding.CalendarItemBinding
import de.tierwohlteam.android.futterapp.models.Food
import de.tierwohlteam.android.futterapp.models.Meal
import de.tierwohlteam.android.futterapp.models.Rating
import de.tierwohlteam.android.futterapp.others.translate
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
        addHeader(calendarEntry, holder)
        var meals = calendarEntry.meals
        meals.sortBy { it.feeding.time }
        var ratings = calendarEntry.ratings
        ratings.sortBy { it.timeStamp }
        val table = holder.binding.tableCalendarItem
        while (meals.isNotEmpty() || ratings.isNotEmpty()) {
            when {
                meals.isEmpty() -> {
                    ratings.forEach { addRatingToTable(it, table) }
                    ratings = mutableListOf()
                }
                ratings.isEmpty() -> {
                    meals.forEach { addMealToTable(it, table) }
                    meals = mutableListOf()
                }
                meals.first().feeding.time <= ratings.first().timeStamp -> {
                    addMealToTable(meals.first(),table)
                    meals.removeFirst()
                }
                else -> {
                    addRatingToTable(ratings.first(),table)
                    ratings.removeFirst()
                }
            }
        }
    }

    private fun addHeader(
        calendarEntry: StatisticsViewModel.CalendarEntry,
        holder: CalendarViewHolder
    ) {
        val timeStamp = calendarEntry.date
        val dayOfWeek = timeStamp.dayOfWeek.translate(holder.binding.tvCalendarDate.context, short = true)
        val day = timeStamp.dayOfMonth
        val month = timeStamp.monthNumber
        val dateString = "$dayOfWeek $day.$month"
        holder.binding.tvCalendarDate.text = dateString
        val avgRating = calendarEntry.ratings.map { it.value }.average().toFloat()
        if (avgRating.isNaN()) {
            holder.binding.ratingBarCalendarItem.visibility = View.GONE
        }
        else {
            holder.binding.ratingBarCalendarItem.rating = avgRating
        }
    }

    private fun addRatingToTable(rating: Rating, table: TableLayout) {
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
    private fun addMealToTable(meal: Meal, table: TableLayout) {
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

