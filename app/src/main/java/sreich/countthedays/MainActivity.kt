package sreich.countthedays

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show()
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        val counterList = mutableListOf<DayCounter>()
        repeat(50) {
                counterList.add(DayCounter("name number $it"))
        }

        val listView = findViewById(R.id.listview) as ListView
        listView.adapter = DayCounterAdapter(this, counterList)

        listView.onItemClickListener = AdapterView.OnItemClickListener() { adapterView: AdapterView<*>, view: View, position: Int, id: Long ->
            startActivity(Intent(this, NewCounterActivity::class.java))
        }
    }

}

class DayCounter(val name: String) {

}
