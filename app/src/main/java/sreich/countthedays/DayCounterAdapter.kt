package sreich.countthedays

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

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
        var convertView2: View? = null
        convertView2 = inflater.inflate(R.layout.list_item_daycounter, parent, false)
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

        convertView2.toString()

        val a = convertView2.findViewById(R.id.name_text_view)
        val b = a as TextView

        holder!!.nameTextView = convertView2.findViewById(R.id.name_text_view) as TextView
        holder!!.dateTextView = convertView2.findViewById(R.id.time_text_view) as TextView
        val nameTextView = holder!!.nameTextView

        val dayCounter = getItem(position)
        nameTextView.text = dayCounter.name

        val dateTextView = holder.dateTextView
        dateTextView.text = "test date text"

        return convertView2
    }

    private class DayViewHolder {
        lateinit var nameTextView: TextView
        lateinit var dateTextView: TextView
    }
}