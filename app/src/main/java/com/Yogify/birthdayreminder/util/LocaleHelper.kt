package com.Yogify.birthdayreminder.util

import android.annotation.TargetApi
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import androidx.preference.PreferenceManager
import java.util.Locale


class LocaleHelper {
    companion object {

        var ENGLISH_LANGUAGE="en"
        var HINDI_LANGUAGE="hi"

        private val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"
        fun setLocale(context: Context): Context {
            var language = getLocale(context)
            if (language.equals(ENGLISH_LANGUAGE)) language = HINDI_LANGUAGE else language = ENGLISH_LANGUAGE
            persist(context, language)
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                updateResources(context, language)
            } else updateResourcesLegacy(context, language)
        }

        private fun persist(context: Context, language: String) {
            val preferences: SharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context)
            val editor = preferences.edit()
            editor.putString(SELECTED_LANGUAGE, language)
            editor.apply()
        }

        fun getLocale(context: Context): String {
            val preferences: SharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context)
            val language = preferences.getString(SELECTED_LANGUAGE, "en")!!
            return language
        }

        private fun updateResources(context: Context, language: String): Context {
            val locale = Locale(language)
            Locale.setDefault(locale)
            val configuration: Configuration = context.getResources().getConfiguration()
            configuration.setLocale(locale)
            configuration.setLayoutDirection(locale)
            return context.createConfigurationContext(configuration)
        }


        private fun updateResourcesLegacy(context: Context, language: String): Context {
            val locale = Locale(language)
            Locale.setDefault(locale)
            val resources: Resources = context.getResources()
            val configuration: Configuration = resources.getConfiguration()
            configuration.locale = locale
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                configuration.setLayoutDirection(locale)
            }
            resources.updateConfiguration(configuration, resources.getDisplayMetrics())
            return context
        }
    }
}