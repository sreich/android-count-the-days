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
import sreich.countthedays.DayCounterAdapter.DayViewHolder
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by sreich on 10/9/16.
 */

//class ContentAdapter(private val items: List<ContentItem>, private val listener: ContentAdapter.OnItemClickListener) : RecyclerView.Adapter<ContentAdapter.ViewHolder>() {
//
//    interface OnItemClickListener {
//        fun onItemClick(item: ContentItem)
//    }
//
//    val itemCount: Int
//        get() = items.size
//
//    internal class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
//        private val name: TextView
//        private val image: ImageView
//
//        init {
//            name = itemView.findViewById(R.id.name) as TextView
//            image = itemView.findViewById(R.id.image) as ImageView
//        }
//
//        fun bind(item: ContentItem, listener: OnItemClickListener) {
//            name.setText(item.name)
//            Picasso.with(itemView.getContext()).load(item.imageUrl).into(image)
//            itemView.setOnClickListener(object : View.OnClickListener() {
//                fun onClick(v: View) {
//                    listener.onItemClick(item)
//                }
//            })
//        }
//    }
//}
class DayCounterAdapter(context: Context,
                        val counterList: MutableList<DayCounter>,
                        private val listener: DayCounterAdapter.OnItemClickListener) : RecyclerView.Adapter<DayViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(item: DayCounter)
    }

//    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {

//    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(counterList[position], listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val itemView = LayoutInflater.from(parent!!.context).inflate(R.layout.list_item_daycounter, parent, false)
        return DayViewHolder(itemView)
    }

    override fun getItemCount() = counterList.size

    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getItemId(position: Int) = position.toLong()

    class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var nameTextView: TextView = itemView.findViewById(R.id.name_text_view) as TextView
        var dateTextView: TextView = itemView.findViewById(R.id.date_text_view) as TextView

        fun bind(counter: DayCounter, listener: OnItemClickListener) {
            nameTextView.text = counter.name

            val dateTime = counter.dateTime

            val period = Period(dateTime, DateTime.now(), PeriodType.yearMonthDay())

            dateTextView.text = dateViewText(period)
        }

        fun dateViewText(period: Period): String {
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

            return finalDateText
        }
    }
}