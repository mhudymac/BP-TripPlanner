package kmp.android.trip.di

import kmp.android.trip.vm.SearchViewModel
import kmp.android.trip.vm.CreateViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val tripModule = module {
    viewModel { SearchViewModel( get() ) }
    viewModel { CreateViewModel() }
}