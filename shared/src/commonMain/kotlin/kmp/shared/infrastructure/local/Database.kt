package kmp.shared.infrastructure.local

import app.cash.sqldelight.db.SqlDriver
import kmp.Database

internal expect class DriverFactory {
    fun createDriver(dbName: String): SqlDriver
}

internal fun createDatabase(driverFactory: DriverFactory): Database =
    Database(driverFactory.createDriver("kmp.db"))
