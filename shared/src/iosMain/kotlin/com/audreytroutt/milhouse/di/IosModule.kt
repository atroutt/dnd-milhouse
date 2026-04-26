package com.audreytroutt.milhouse.di

import com.audreytroutt.milhouse.data.db.DatabaseDriverFactory
import org.koin.dsl.module

val iosModule = module {
    single { DatabaseDriverFactory() }
}
