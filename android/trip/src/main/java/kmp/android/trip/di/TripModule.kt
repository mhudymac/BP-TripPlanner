package kmp.android.trip.di

import kmp.android.trip.vm.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val tripModule = module {
    viewModel { SearchViewModel( get() ) }
}