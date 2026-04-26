package com.audreytroutt.milhouse.data.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.audreytroutt.milhouse.db.MilhouseDatabase

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver =
        AndroidSqliteDriver(MilhouseDatabase.Schema, context, "milhouse.db")
}
