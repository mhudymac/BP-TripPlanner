package kmp.android.di

import android.content.Context
import androidx.activity.ComponentActivity
import com.google.firebase.analytics.FirebaseAnalytics
import kmp.android.gallery.di.galleryModule
import kmp.android.home.di.homeModule
import kmp.android.search.di.searchModule
import kmp.android.shared.di.androidSharedModule
import kmp.android.trip.di.tripModule
import kmp.shared.di.initKoin
import org.koin.dsl.module

fun ComponentActivity.initDependencyInjection() {
    initKoin {
        val contextModule = module { // Provide Android Context
            factory<ComponentActivity> { this@initDependencyInjection }
            factory<Context> { this@initDependencyInjection }
            single { FirebaseAnalytics.getInstance(get()) }
        }

        modules(
            contextModule,
            androidSharedModule,
            tripModule,
            searchModule,
            homeModule,
            galleryModule
        )
    }
}
