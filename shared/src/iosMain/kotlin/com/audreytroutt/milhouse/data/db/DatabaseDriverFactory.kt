package com.audreytroutt.milhouse.data.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.audreytroutt.milhouse.db.MilhouseDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver =
        NativeSqliteDriver(MilhouseDatabase.Schema, "milhouse.db")
}
