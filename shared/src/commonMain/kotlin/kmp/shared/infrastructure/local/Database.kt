package kmp.shared.infrastructure.local

import app.cash.sqldelight.db.SqlDriver
import kmp.Database

/**
 * This is an expect class that represents a factory for creating SQL drivers.
 * It is expected to have a platform-specific implementation.
 */
internal expect class DriverFactory {
    /**
     * This function is used to create a SQL driver.
     * It is expected to have a platform-specific implementation.
     *
     * @param dbName The name of the database for which to create the driver.
     * @return A SqlDriver object for the specified database.
     */
    fun createDriver(dbName: String): SqlDriver
}

/**
 * This function is used to create a Database object.
 * It uses a DriverFactory to create a SQL driver for a database named "trip.db" and creates a Database object with this driver.
 *
 * @param driverFactory The factory to create the SQL driver.
 * @return A Database object for the "trip.db" database.
 */
internal fun createDatabase(driverFactory: DriverFactory): Database =
    Database(driverFactory.createDriver("trip.db"))
