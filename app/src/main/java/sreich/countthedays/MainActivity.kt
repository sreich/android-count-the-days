package sreich.countthedays

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.format.DateUtils
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import org.joda.time.DateTime
import java.util.*

class MainActivity : AppCompatActivity() {

    val counterList = mutableListOf<DayCounter>()
    var editingIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener(CreateNewClickListener())

        loadData()

        val listView = findViewById(R.id.listview) as ListView
        listView.adapter = DayCounterAdapter(this, counterList)

        listView.onItemClickListener = ItemClickListener()
    }

    /**
     * data for testing
     */
    private fun loadData() {
        val now = DateTime.now()

        //fixme off by one? not using last index in the view!!
        val a = mutableListOf<DateTime>().apply {
            add(now.minusYears(0).minusMonths(0).minusDays(24).minusHours(0).minusMinutes(0))
            add(now.minusYears(0).minusMonths(3).minusDays(0).minusHours(0).minusMinutes(0))
            add(now.minusYears(1).minusMonths(3).minusDays(0).minusHours(0).minusMinutes(0))
            add(now.minusYears(0).minusMonths(3).minusDays(24).minusHours(0).minusMinutes(0))
            add(now.minusYears(1).minusMonths(3).minusDays(10).minusHours(0).minusMinutes(0))
            add(now.minusYears(1).minusMonths(3).minusDays(10).minusHours(10).minusMinutes(52))
        }

        a.forEachIndexed { i, dateTime ->
            counterList.add(DayCounter(name = "test name number: $i", dateTime = dateTime))
        }
    }

    val EDIT_LIST_ITEM_REQUEST = 0

    //edit the list item
    inner class ItemClickListener : OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            editingIndex = position
            val intent = Intent(this@MainActivity, NewCounterActivity::class.java)

            val currentCounter = counterList[position]
            intent.putExtra("name", currentCounter.name)


            intent.putExtra("dateTime", currentCounter.dateTime)

            startActivityForResult(intent, EDIT_LIST_ITEM_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == EDIT_LIST_ITEM_REQUEST) {
            if (resultCode == RESULT_OK) {
                val counterToUpdate = counterList[editingIndex]

                val name = data!!.getStringExtra("name")
                counterToUpdate.name = name
            }
        }
    }

    inner class CreateNewClickListener : View.OnClickListener {
        override fun onClick(view: View) {
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show()
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
        }
    }
}

class DayCounter(var name: String, val dateTime: DateTime) {
}
