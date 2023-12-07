package com.Yogify.birthdayreminder.util

import java.text.SimpleDateFormat
import java.util.Date

class utils {

    companion object {

        const val DATE_dd_MMM_yyyy = "dd-MMM-yyyy"
        const val DATE_dd_MMMM = "dd-MMMM"
        fun getDateToLong(value: String, formate: String): Date {
            var formatter = SimpleDateFormat(formate)
            var date: Date = formatter.parse(value)
            return date
        }


        fun datetoFormate(date: Date): String {
            val sdf = SimpleDateFormat(DATE_dd_MMMM)
            return sdf.format(date.time)

        }
    }

}