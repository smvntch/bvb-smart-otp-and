package com.bvb.sotp.util

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import com.bvb.sotp.helper.PreferenceHelper
import java.util.*


class LanguageUtils(context: Context) {
    /* ------------------------------------- */
    private var myLocale: Locale? = null
    private val lang = "vi"
    // Lưu ngôn ngữ đã cài đặt
    fun saveLocale(lang: String, activity: Activity) {
        val preferenceHelper = PreferenceHelper(activity)
        preferenceHelper.setLang(lang)
        loadLocale(activity)
    }

    // Load lại ngôn ngữ đã lưu và thay đổi chúng
    fun loadLocale(activity: Activity): String {
        val preferenceHelper = PreferenceHelper(activity)

        val language = preferenceHelper.getLang()

        println("---------lang-"+language)
//        changeLang(language, activity)

        return language
    }

    // method phục vụ cho việc thay đổi ngôn ngữ.
    fun changeLang(lang: String, activity: Activity) {
        if (lang.equals("", ignoreCase = true))
            return

        myLocale = Locale(lang)
        saveLocale(lang, activity)
        Locale.setDefault(myLocale)
        val config = Configuration()
        config.locale = myLocale
        activity.baseContext.resources.updateConfiguration(config,
                activity.baseContext.resources.displayMetrics)
//        activity.onConfigurationChanged(config)


    }


    fun getSystemLocaleLegacy(config: Configuration): Locale {
        return config.locale
    }

    @TargetApi(Build.VERSION_CODES.N)
    fun getSystemLocale(config: Configuration): Locale {
        return config.getLocales().get(0)
    }

    fun setSystemLocaleLegacy(config: Configuration, locale: Locale) {
        config.locale = locale
    }

    @TargetApi(Build.VERSION_CODES.N)
    fun setSystemLocale(config: Configuration, locale: Locale) {
        config.setLocale(locale)
    }


}