package sreich.countthedays

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.view.GestureDetectorCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.format.DateUtils
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import com.fatboyindustrial.gsonjodatime.Converters
import com.github.salomonbrys.kotson.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.internal.Streams.write
import com.google.gson.reflect.TypeToken
import org.joda.time.DateTime
import org.joda.time.LocalDate
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {
    var counterList = mutableListOf<DayCounter>()

    var editingIndex = -1

    val deprecatedGson = Converters.registerDateTime(GsonBuilder()).create()!!
    val gson = GsonBuilder()/*.registerTypeAdapter<DateTime> {

        write {
            beginArray()
            value(it.toString())
            endArray()
        }
        //deserialize { it.json. }

        read {
            beginArray()
            val dateTime = nextString()
            endArray()

            DateTime.parse(dateTime)
        }
    }*/.registerTypeAdapter<DayCounter> {
        write {
            beginArray()
            value(it.name)
            value(it.dateTime.toString())
            endArray()
        }

        read {
            beginArray()
            val name = nextString()
            val dateTime = nextString()
            endArray()

            DayCounter(name = name, dateTime = DateTime(dateTime))
        }
    }.create()

    lateinit var adapter: DayCounterAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val createNewFab = findViewById(R.id.fab) as FloatingActionButton
        createNewFab.setOnClickListener(FabCreateNewClickListener())

        loadSettingsData()

        val listView = findViewById(R.id.listview) as RecyclerView
        registerForContextMenu(listView)

        listView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        adapter = DayCounterAdapter(context = this, counterList = counterList)
        listView.adapter = adapter
        listView.layoutManager = LinearLayoutManager(applicationContext)
        listView.itemAnimator = DefaultItemAnimator()

        listView.addOnItemTouchListener(RecyclerTouchListener(applicationContext, listView, ListClickListener()))
    }

    /**
     * data for testing and first time startup
     */
    private fun sampleData(): MutableList<DayCounter> {
        val now = DateTime.now()

        val list = mutableListOf(
                Pair(i18n(R.string.sampleData1),
                     now.minusYears(0).minusMonths(0).minusDays(1).minusHours(0).minusMinutes(0)),
                Pair(i18n(R.string.sampleData2),
                     now.minusYears(0).minusMonths(0).minusDays(24).minusHours(0).minusMinutes(0)),
                Pair(i18n(R.string.sampleData3),
                     now.minusYears(0).minusMonths(0).plusDays(24).minusHours(0).minusMinutes(0)),
                Pair(i18n(R.string.sampleData4),
                     now.minusYears(0).minusMonths(3).minusDays(0).minusHours(0).minusMinutes(0)),
                Pair(i18n(R.string.sampleData5),
                     now.minusYears(1).minusMonths(3).minusDays(0).minusHours(0).minusMinutes(0)),
                Pair(i18n(R.string.sampleData6),
                     now.minusYears(0).minusMonths(3).minusDays(24).minusHours(0).minusMinutes(0)),
                Pair(i18n(R.string.sampleData7),
                     now.minusYears(1).minusMonths(3).minusDays(10).minusHours(0).minusMinutes(0)),
                Pair(i18n(R.string.sampleData8),
                     now.minusYears(1).minusMonths(3).minusDays(10).minusHours(10).minusMinutes(52)))

        val newCounterList = mutableListOf<DayCounter>()
        list.forEach { (first, second) ->
            newCounterList.add(DayCounter(name = first, dateTime = second))
        }

        return newCounterList
    }

    enum class ActivityRequest(val value: Int) {
        EditListItem(0),
        CreateListItem(1)
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)

        if (v!!.id == R.id.listview) {
            menuInflater.inflate(R.menu.menu_list, menu)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.clear_all -> {
                val builder = AlertDialog.Builder(this)

                builder.setTitle(i18n(R.string.confirmDialogTitle))
                builder.setMessage(i18n(R.string.confirmDialogPrompt))

                builder.setPositiveButton(i18n(R.string.confirmDialogYes)) { dialog, _ ->
                    counterList.clear()

                    adapter.notifyDataSetChanged()

                    dialog.dismiss()
                }

                builder.setNegativeButton(i18n(R.string.confirmDialogNo)) { dialog, _ -> dialog.dismiss() }

                val alert = builder.create()
                alert.show()

                true
            }

            R.id.settings -> {

                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivity(intent)

                true
            }

            else -> super.onContextItemSelected(item)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val position = selectedItem
        return when (item.itemId) {
            R.id.delete -> {
                //delete this one
                counterList.removeAt(position)

                adapter.notifyDataSetChanged()
                true
            }

            R.id.reset -> {
                val counter = counterList[position]
                counter.dateTime = DateTime.now()

                adapter.notifyDataSetChanged()
                true
            }

            else -> super.onContextItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ActivityRequest.EditListItem.value -> if (resultCode == RESULT_OK) {
                val counterToUpdate = counterList[editingIndex]

                val newName = data!!.getStringExtra("name")
                val newDateTime = data.getSerializableExtra("dateTime") as DateTime
                counterToUpdate.apply {
                    name = newName
                    dateTime = newDateTime
                }

                adapter.notifyDataSetChanged()

                saveSettingsJson()
            }

            ActivityRequest.CreateListItem.value -> if (resultCode == RESULT_OK) {
                //add the new item

                val name = data!!.getStringExtra("name")
                val dateTime = data.getSerializableExtra("dateTime") as DateTime
                val newCounter = DayCounter(name = name, dateTime = dateTime)

                counterList.add(newCounter)

                saveSettingsJson()
            }
        }
    }

    /**
     * 1.0 - not in json file, but in shared preferences (bad decision)
     * 1.1 - first time it is in json settings file, everything stored here
     */
    private val saveFileVersion: String = "1.1"

    val settingsSaveLocation by lazy { applicationContext.filesDir!! }

    object Settings {
        val settingsFileName = "count-the-days-settings"
        val settingsFileExtension = ".json"
    }

    val file = File(settingsSaveLocation, "${Settings.settingsFileName}${Settings.settingsFileExtension}")

    fun saveSettingsJson() {
        val counterListJson = gson.toJsonTree(counterList)

        val settingsJson: JsonObject = jsonObject(
                "saveVersion" to saveFileVersion,
                "counters" to counterListJson)
        file.writeText(settingsJson.toString())
    }

    /**
     * either loads the saved data, or fills it with sample data
     * if it's all empty
     */
    private fun loadSettingsData() {
        //we do not yet ever clear sharedprefs..it's our backup for now, in
        //case a rollout screws things up
        //todo in the future, delete this..maybe after a few versions
        // once we know we're in the claer
        val prefs = getPreferences(MODE_PRIVATE)
        val deprecatedJson = prefs.getString("counter-list-json", null)

        //conversion from 1.0 data format when we stored it in sharedprefs
        //so convert it to json and clear it, write save file to json
        if (deprecatedJson != null && !file.exists()) {
            //we only perform the 1.0 -> 1.1 upgrade if the json output doesn't exist
            // (so it only runs once)
            //since shared prefs is migrated from after this first run, and kept until we
            //decide to (versions later), delete them safely.
            upgradeSaveDataFrom1_0(deprecatedJson, prefs)
        }

        loadSettingsJson()
    }

    private fun loadSettingsJson() {
        val fileText = file.readText()

        val fileJsonElement = JsonParser().parse(fileText)

        val versionString = fileJsonElement["saveVersion"].asString

        val counterJson = fileJsonElement["counters"]
        val loadedCounterList = gson.fromJson<MutableList<DayCounter>>(counterJson)

        counterList = loadedCounterList
    }

    /**
     * version 1.0, we stored data in shared preferences and used
     * gson jodatime deserialization. we can't use kotson here from what
     * i could tell..seems like the output json isn't that great from version
     * 1.0...
     *
     * going forward it's just better to not use sharedprefs, as i have to export
     * to/from file, so why not just store config as json file directly and reuse
     * the code?
     *
     * plus to import/export sharedprefs to a json file that'd probably be pretty horrible
     */
    private fun upgradeSaveDataFrom1_0(deprecatedJson: String, prefs: SharedPreferences) {
        counterList = deprecatedGson.fromJson<MutableList<DayCounter>>(deprecatedJson)

        //clear out sharedpreferences, we're likely never using this again.
        //fixme for now import each and every time, TEMPORARY ONLY FOR ME
//        val edit = prefs.edit()
        //       edit.clear()
        //      edit.apply()

        //write current settings to new save file
        saveSettingsJson()
    }

    var selectedItem = -1

    inner class FabCreateNewClickListener : View.OnClickListener {
        override fun onClick(view: View) {
            val intent = Intent(this@MainActivity, NewCounterActivity::class.java)
            startActivityForResult(intent, ActivityRequest.CreateListItem.value)
        }
    }

    inner class ListClickListener : RecyclerTouchListener.ClickListener {
        override fun onClick(view: View, position: Int) {
            val intent = Intent(this@MainActivity, NewCounterActivity::class.java)
            editingIndex = position
            val counter = counterList[position]

            intent.putExtra("name", counter.name)
            intent.putExtra("dateTime", counter.dateTime)

            startActivityForResult(intent, ActivityRequest.EditListItem.value)
        }

        override fun onLongClick(view: View, position: Int) {
            selectedItem = position
            openContextMenu(view)
        }
    }
}


data class DayCounter(var name: String, var dateTime: DateTime)
