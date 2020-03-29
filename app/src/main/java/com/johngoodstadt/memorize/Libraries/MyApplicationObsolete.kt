package com.johngoodstadt.memorize.Libraries

import android.app.Application
import android.content.Context

open class MyApplicationObsolete : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    companion object {
        var appContext: Context? = null
            private set
    }
}