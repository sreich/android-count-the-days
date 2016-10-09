package sreich.countthedays

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
        fab.setOnClickListener { view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show() }

        val stringList = listOf("test1", "test2", "test3", "test4")

        val listView = findViewById(R.id.listview) as ListView
        listView.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, stringList)

        listView.onItemClickListener = AdapterView.OnItemClickListener() { adapterView: AdapterView<*>, view: View, position: Int, id: Long ->

        }
    }

}
