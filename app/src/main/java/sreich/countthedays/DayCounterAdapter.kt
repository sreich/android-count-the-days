package sreich.countthedays

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

/**
 * Created by sreich on 10/9/16.
 */

class DayCounterAdapter(context: Context, val counterList: List<DayCounter>) : BaseAdapter() {
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getItem(position: Int) = counterList.get(position)

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = counterList.size

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.list_item_daycounter, parent, false)

        return rowView
    }
}