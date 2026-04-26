package com.audreytroutt.milhouse

import com.audreytroutt.milhouse.di.appModule
import com.audreytroutt.milhouse.di.iosModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(appModule, iosModule)
    }
}
