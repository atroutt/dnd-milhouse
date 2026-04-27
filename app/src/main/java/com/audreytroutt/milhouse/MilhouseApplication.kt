package com.audreytroutt.milhouse

import android.app.Application
import com.audreytroutt.milhouse.di.androidModule
import com.audreytroutt.milhouse.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MilhouseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MilhouseApplication)
            modules(appModule, androidModule)
        }
    }
}
