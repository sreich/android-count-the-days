package sreich.countthedays

import android.app.DatePickerDialog
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import android.widget.DatePicker

class NewCounterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_new_counter)

        val toolbar = findViewById(R.id.toolbar) as Toolbar

        setSupportActionBar(toolbar)


        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val dateDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
            datePicked(datePicker, year, month, day)
        }, 0, 0, 0)


        val button = findViewById(R.id.button2) as Button
        button.setOnClickListener {
            dateDialog.show()
        }

    }

    private fun datePicked(datePicker: DatePicker, year: Int, month: Int, day: Int) {
        TODO()
    }

}
