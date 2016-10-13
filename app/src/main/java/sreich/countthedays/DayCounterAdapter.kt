package sreich.countthedays

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import org.joda.time.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by sreich on 10/9/16.
 */

class DayCounterAdapter(context: Context, val counterList: List<DayCounter>) : BaseAdapter() {
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getItem(position: Int) = counterList[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = counterList.size

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var holder: DayViewHolder? = null
        var view: View? = null
        view = inflater.inflate(R.layout.list_item_daycounter, parent, false)
//
//        if (convertView == null) {
//            convertView2 = inflater.inflate(R.layout.list_item_daycounter, parent, false)
//
//            val holder = DayViewHolder()
//            holder.nameTextView = convertView2.findViewById(R.id.name_text_view) as TextView
//            holder.dateTextView = convertView2.findViewById(R.id.time_text_view) as TextView
//
//            assert (holder != null)
//            assert(convertView2 != null)
//            convertView2.setTag(holder)
//        } else {
//            holder = convertView.tag as DayViewHolder
//        }
        holder = DayViewHolder()

        holder.nameTextView = view.findViewById(R.id.name_text_view) as TextView
        holder.dateTextView = view.findViewById(R.id.time_text_view) as TextView
        val nameTextView = holder.nameTextView

        val dayCounter = getItem(position)
        nameTextView.text = dayCounter.name

        val dateTextView = holder.dateTextView

        val dateTime = dayCounter.dateTime

        val period = Period(dateTime, DateTime.now(), PeriodType.yearMonthDay())
        val years = period.years
        val months = period.months
        val days = period.days

        val yearsString = when {
            years == 1 -> "$years year, "
            years > 0 -> "$years years, "
            else -> ""
        }

        val monthsString = when {
            months == 1 -> "$months month, "
            months > 0 -> "$months months, "
            else -> ""
        }

        val daysString = when {
            days == 1 -> "$days day"
            days > 0 -> "$days days"
            else -> ""
        }
        val finalDateText = if (days == 0 && months == 0 && years == 0) {
            "now"
        } else {
            "$yearsString$monthsString$daysString"
        }

        dateTextView.text = finalDateText
        return view
    }

    private class DayViewHolder {
        lateinit var nameTextView: TextView
        lateinit var dateTextView: TextView
    }
}