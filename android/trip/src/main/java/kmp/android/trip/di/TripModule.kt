package kmp.android.trip.di

import kmp.android.trip.vm.SearchViewModel
import kmp.android.trip.vm.CreateViewModel
import kmp.android.trip.vm.ListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val tripModule = module {
    viewModel { SearchViewModel( get(), get() ) }
    viewModel { CreateViewModel( get() ) }
    viewModel { ListViewModel( get() ) }
}