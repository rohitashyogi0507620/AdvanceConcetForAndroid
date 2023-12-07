package com.Yogify.birthdayreminder.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.Yogify.birthdayreminder.R
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.Calendar

class AddReminderActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reminder)

        var calendar = Calendar.getInstance()

        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setTitleText(getString(R.string.select_time))
            .setHour(calendar.get(Calendar.HOUR_OF_DAY)+1)
            .setMinute(calendar.get(Calendar.MINUTE))
            .build()
        timePicker.show(supportFragmentManager, "TIME_PICKER")

        timePicker.addOnPositiveButtonClickListener {
            val newHour: Int = timePicker.hour
            val newMinute: Int = timePicker.minute
            Toast.makeText(this, "Hour $newHour Minutes $newMinute", Toast.LENGTH_SHORT).show()

        }
        // set Max Min Date

//        val today = MaterialDatePicker.todayInUtcMilliseconds()
//        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
//
//        calendar.timeInMillis = today
//        calendar[Calendar.MONTH] = Calendar.JANUARY
//        val janThisYear = calendar.timeInMillis
//
//        calendar.timeInMillis = today
//        calendar[Calendar.MONTH] = Calendar.DECEMBER
//        val decThisYear = calendar.timeInMillis
//
//       val constraintsBuilder = CalendarConstraints.Builder().setStart(janThisYear).setEnd(decThisYear)

        var datePicker = MaterialDatePicker
            .Builder
            .datePicker()
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
//            .setCalendarConstraints(constraintsBuilder.build())
            .setTitleText(getString(R.string.select_date))
            .build()

        datePicker.show(supportFragmentManager, "DATE_PICKER")


        datePicker.addOnPositiveButtonClickListener {
            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
        }

    }
}