package sreich.countthedays

import android.app.Activity
import android.app.Activity.*
import android.app.DatePickerDialog
import android.app.DatePickerDialog.*
import android.icu.text.DateFormat
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import java.util.*

class NewCounterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_new_counter)
        val toolbar = findViewById(R.id.toolbar) as Toolbar

        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val nameText = findViewById(R.id.nameText) as EditText
        nameText.setText(intent.getStringExtra("name"))

        val currentCalendar = intent.getSerializableExtra("calendar") as Calendar

        val dateDialog = DatePickerDialog(this, OnDateSetListener { datePicker, year, month, day ->
            datePicked(datePicker, year, month, day)
        }, currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DATE))

        val dateText = findViewById(R.id.dateText) as EditText
        dateText.setOnClickListener {
            dateDialog.show()
        }

        val okButton = findViewById(R.id.ok) as Button
        okButton.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }

        val cancelButton = findViewById(R.id.cancel) as Button
        cancelButton.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun datePicked(datePicker: DatePicker, year: Int, month: Int, day: Int) {
    }

}
