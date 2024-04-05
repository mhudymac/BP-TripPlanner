package kmp.android.trip.di

import kmp.android.trip.ui.search.SearchViewModel
import kmp.android.trip.ui.create.CreateViewModel
import kmp.android.trip.ui.detail.DetailViewModel
import kmp.android.trip.ui.edit.EditViewModel
import kmp.android.trip.ui.list.ListViewModel
import kmp.android.trip.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val tripModule = module {
    viewModel { SearchViewModel(get(), get(), get()) }
    viewModel { CreateViewModel(get(), get(), get()) }
    viewModel { ListViewModel(get()) }
    viewModel { DetailViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { EditViewModel(get(), get(), get()) }
}