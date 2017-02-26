package sreich.countthedays

import android.app.Activity
import android.app.Activity.*
import android.app.DatePickerDialog
import android.app.DatePickerDialog.*
import android.app.TimePickerDialog
import android.content.Intent
import android.icu.text.DateFormat
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import com.mcxiaoke.koi.ext.find
import com.mcxiaoke.koi.ext.onTextChange
import org.joda.time.DateTime
import org.joda.time.DateTimeUtils
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.DateTimeParser
import java.util.*

class NewCounterActivity : AppCompatActivity() {

    lateinit var newDateTime: DateTime
    lateinit var dateButton: Button
    lateinit var timeButton: Button
    lateinit var dateTime: DateTime

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityInit()

        val nameText = find<EditText>(R.id.nameText)
        nameText.setText(intent.getStringExtra("name"))
        nameText.addTextChangedListener(TextChangedListener())

        // null along with other values if we're creating a new entry.
        // else it'll be the existing data that we're editing
        dateTime = intent.getSerializableExtra("dateTime") as? DateTime ?: DateTime.now()//.minusDays(5)

        val dateDialog = DatePickerDialog(this, OnDateSetListener { datePicker, year, month, day ->
            dateTime = datePicked(datePicker, year, month + 1, day)
            dateButton.text = formatDate(dateTime)

        }, dateTime.year, dateTime.monthOfYear - 1, dateTime.dayOfMonth)

        dateButton = find<Button>(R.id.dateButton).apply {
            setOnClickListener {
                dateDialog.show()
            }

            text = formatDate(dateTime)
        }

        //todo can probably clean these up and make them more clear...with kt wrappers of them
        val timeDialog = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            dateTime = dateTime.withTime(hourOfDay, minute, 0, 0)
            timeButton.text = formatTime(dateTime)
        }, dateTime.hourOfDay, dateTime.minuteOfHour, false)

        timeButton = find<Button>(R.id.timeButton).apply {
            setOnClickListener {
                timeDialog.show()
            }

            text = formatTime(dateTime)
        }

        val okButton = find<Button>(R.id.ok)
        okButton.setOnClickListener {
            val data = Intent()
            data.putExtra("name", nameText.text.toString())
            data.putExtra("dateTime", dateTime)

            setResult(RESULT_OK, data)
            finish()
        }

        val cancelButton = find<Button>(R.id.cancel)
        cancelButton.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }


    //fixme: create kt wrapper, see koi's onTextChanged
    inner class TextChangedListener : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            val okButton = find<Button>(R.id.ok)
            okButton.isEnabled = !s.isNullOrEmpty()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
    }

    private fun formatTime(dateTime: DateTime): String {
        return dateTime.toString(DateTimeFormat.shortTime())
    }

    private fun formatDate(dateTime: DateTime): String = dateTime.toString(DateTimeFormat.shortDate())

    private fun activityInit() {
        setContentView(R.layout.activity_new_counter)
        val toolbar = find<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun datePicked(datePicker: DatePicker, year: Int, month: Int, day: Int) =
            DateTime(year, month, day, 0, 0)
}

