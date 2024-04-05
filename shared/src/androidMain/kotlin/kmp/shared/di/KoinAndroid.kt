package kmp.shared.di

import android.content.Context
import com.google.android.gms.location.LocationServices
import io.ktor.client.engine.android.Android
import kmp.shared.domain.controller.LocationController
import kmp.shared.infrastructure.local.DriverFactory
import kmp.shared.system.Config
import kmp.shared.system.ConfigImpl
import kmp.shared.system.Log
import kmp.shared.system.Logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.dsl.module

actual val platformModule = module {
    single<Config> { ConfigImpl() }
    single { DriverFactory(get()) }
    single<Logger> { Log }
    single { Android.create() }
    single<LocationController> {
        LocationController(
            context = get(),
            locationProvider = LocationServices.getFusedLocationProviderClient(get<Context>()),
        )
    }
}
