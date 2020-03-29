package com.johngoodstadt.memorize

import com.johngoodstadt.memorize.Libraries.MyApplication
import com.johngoodstadt.memorize.di.AppComponent
import com.johngoodstadt.memorize.di.DaggerAppComponent

class BaseApplication: MyApplication() {
    private var appComponent: AppComponent? = null

    override fun onCreate() {
        super.onCreate()
        appComponent= createAppComponent()

    }

    fun createAppComponent(): AppComponent {
        return DaggerAppComponent.create()
    }


    fun getAppComponent(): AppComponent? {
        return appComponent
    }
}