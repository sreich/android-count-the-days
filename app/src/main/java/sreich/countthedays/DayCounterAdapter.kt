package sreich.countthedays

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.mcxiaoke.koi.ext.find
import org.joda.time.*
import sreich.countthedays.DayCounterAdapter.DayViewHolder
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by sreich on 10/9/16.
 */
class DayCounterAdapter(context: Context) : RecyclerView.Adapter<DayViewHolder>() {
    var counterList = mutableListOf<DayCounter>()

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(counterList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_daycounter, parent, false)
        return DayViewHolder(itemView)
    }

    override fun getItemCount() = counterList.size

    override fun getItemId(position: Int) = position.toLong()

    class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var nameTextView: TextView = itemView.find<TextView>(R.id.name_text_view)
        var dateTextView: TextView = itemView.find<TextView>(R.id.date_text_view)

        fun bind(counter: DayCounter) {
            nameTextView.text = counter.name

            val dateTime = counter.dateTime
            val period = Period(dateTime, DateTime.now(), PeriodType.yearMonthDay())

            dateTextView.text = dateViewText(period)
        }

        //todo this needs redone, but it works for now..
        fun dateViewText(period: Period): String {
            // Negative timespan means the date is in the future
            val future = period.years < 0 || period.months < 0 || period.days < 0

            // Remove the negative sign
            // TODO: Use builtin math function to calculate absolute value
            val years = if(period.years < 0) {-1*period.years} else {period.years}
            val months = if(period.months < 0) {-1*period.months} else {period.months}
            val days = if(period.days < 0) {-1*period.days} else {period.days}

            val yearsString = when {
                years == 1 -> "$years year, "
                years > 0 || years < 0 -> "$years years, "
                else -> ""
            }

            val monthsString = when {
                months == 1 -> "$months month, "
                months > 0 || months < 0 -> "$months months, "
                else -> ""
            }

            val daysString = when {
                days == 1 -> "$days day"
                days > 0 || days < 0 -> "$days days"
                else -> ""
            }

            // Hacky solution to calculate the total amount of days
            // TODO: Use buildin functions in order to calculate the exact day-difference
            var totalDays = ( 365 * years + 30 * months + days )

            val finalDateText = if (days == 0 && months == 0 && years == 0) {
                "now"
            } else {
                // Build the displayed string
                if (future == true) {
                    // The date is in the future
                    "in $yearsString$monthsString$daysString (~$totalDays days)"
                }
                else {
                    // The date was in the past
                    "$yearsString$monthsString$daysString ago (~$totalDays days)"
                }
            }

            return finalDateText
        }
    }
}