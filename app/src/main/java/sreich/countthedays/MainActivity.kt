package sreich.countthedays

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.format.DateUtils
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import com.fatboyindustrial.gsonjodatime.Converters
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.joda.time.DateTime
import java.util.*

class MainActivity : AppCompatActivity() {

    var counterList = mutableListOf<DayCounter>() //loadSave()

    var editingIndex = -1

    lateinit var gson: Gson

    lateinit var adapter: DayCounterAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        gson = Converters.registerDateTime(GsonBuilder()).create()

        val createNewFab = findViewById(R.id.fab) as FloatingActionButton
        createNewFab.setOnClickListener(CreateNewClickListener())

        counterList = loadSave()

        //debugLoadData()

        val listView = findViewById(R.id.listview) as ListView
        registerForContextMenu(listView)
        adapter = DayCounterAdapter(this, counterList)
        listView.adapter = adapter

        listView.onItemClickListener = ItemClickListener()
    }

    /**
     * data for testing
     */
    private fun debugLoadData() {
        val now = DateTime.now()

        //fixme off by one? not using last index in the view!!
        val a = mutableListOf<DateTime>().apply {
            add(now.minusYears(0).minusMonths(0).minusDays(1).minusHours(0).minusMinutes(0))
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

    enum class ActivityRequest(val value: Int) {
        EditListItem(0),
        CreateListItem(1)
    }

    //edit the list item
    inner class ItemClickListener : OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            editingIndex = position
            val intent = Intent(this@MainActivity, NewCounterActivity::class.java)

            val currentCounter = counterList[position]
            intent.putExtra("name", currentCounter.name)

            intent.putExtra("dateTime", currentCounter.dateTime)

            startActivityForResult(intent, ActivityRequest.EditListItem.value)
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)

        if (v!!.id == R.id.listview) {
            //menuInflater.inflate(R.menu.menu_list, menu)
            for (menuItem in resources.getStringArray(R.array.menu)) {
                menu!!.add(Menu.NONE, 0, 0, menuItem)
            }
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        when (item.itemId) {
            R.id.delete -> {
                //delete this one
                counterList.removeAt(info.position)
                adapter.notifyDataSetChanged()
                return true
            }

            else -> return super.onContextItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ActivityRequest.EditListItem.value -> if (resultCode == RESULT_OK) {
                val counterToUpdate = counterList[editingIndex]

                val name = data!!.getStringExtra("name")
                val dateTime = data.getSerializableExtra("dateTime") as DateTime
                counterToUpdate.name = name
                counterToUpdate.dateTime = dateTime
            }

            ActivityRequest.CreateListItem.value -> if (resultCode == RESULT_OK) {
                //add the new item

                val name = data!!.getStringExtra("name")
                val dateTime = data.getSerializableExtra("dateTime") as DateTime
                val newCounter = DayCounter(name = name, dateTime = dateTime)

                counterList.add(newCounter)

                saveChanges()
            }
        }
    }

    private fun loadSave(): MutableList<DayCounter> {
        val prefs = getPreferences(MODE_PRIVATE)
        val json = prefs.getString("counter-list-json", null) ?: return mutableListOf()
        Log.d("daycounter", "loading: $json")

        val list = gson.fromJson<MutableList<DayCounter>>(json, object : TypeToken<MutableList<DayCounter>>() {}.type)

        return list
    }

    private fun saveChanges() {
        val prefs = getPreferences(MODE_PRIVATE)
        val edit = prefs.edit()

        val json = gson.toJson(counterList)

        edit.putString("counter-list-json", json)
        Log.d("daycounter", "saving: $json")

        edit.apply()
    }

    inner class CreateNewClickListener : View.OnClickListener {
        override fun onClick(view: View) {
            val intent = Intent(this@MainActivity, NewCounterActivity::class.java)
            startActivityForResult(intent, ActivityRequest.CreateListItem.value)
        }
    }
}

data class DayCounter(var name: String, var dateTime: DateTime)
