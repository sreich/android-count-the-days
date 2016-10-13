package sreich.countthedays

import android.app.Activity
import android.app.Activity.*
import android.app.DatePickerDialog
import android.app.DatePickerDialog.*
import android.content.Intent
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
import org.joda.time.DateTime
import org.joda.time.DateTimeUtils
import org.joda.time.format.DateTimeParser
import java.util.*

class NewCounterActivity : AppCompatActivity() {

    lateinit var dateTime: DateTime
    lateinit var newDateTime: DateTime
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityInit()

        val nameText = findViewById(R.id.nameText) as EditText
        nameText.setText(intent.getStringExtra("name"))

        // null along with other values if we're creating a new entry.
        // else it'll be the existing data that we're editing
        dateTime = intent.getSerializableExtra("dateTime") as? DateTime ?: DateTime.now()//.minusDays(5)

        val dateDialog = DatePickerDialog(this, OnDateSetListener { datePicker, year, month, day ->
            datePicked(datePicker, year, month, day)
        }, dateTime.year, dateTime.monthOfYear - 1, dateTime.dayOfMonth)

        val dateText = findViewById(R.id.dateText) as EditText
        dateText.setOnClickListener {
            dateDialog.show()
        }

        val okButton = findViewById(R.id.ok) as Button
        okButton.setOnClickListener {
            val data = Intent()
            data.putExtra("name", nameText.text.toString())
            data.putExtra("dateTime", dateTime)

            setResult(RESULT_OK, data)
            finish()
        }

        val cancelButton = findViewById(R.id.cancel) as Button
        cancelButton.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun activityInit() {
        setContentView(R.layout.activity_new_counter)
        val toolbar = findViewById(R.id.toolbar) as Toolbar

        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun datePicked(datePicker: DatePicker, year: Int, month: Int, day: Int) =
            DateTime(year, month, day, 0, 0)
}

