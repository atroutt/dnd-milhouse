package com.audreytroutt.milhouse.di

import com.audreytroutt.milhouse.data.db.DatabaseDriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {
    single { DatabaseDriverFactory(androidContext()) }
}
