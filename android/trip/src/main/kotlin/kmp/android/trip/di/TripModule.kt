package kmp.android.trip.di

import kmp.android.trip.screens.edit.EditViewModel
import kmp.android.trip.screens.detail.DetailViewModel
import kmp.android.trip.screens.list.ListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val tripModule = module {
    viewModel { EditViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { ListViewModel(get(), get(), get(),get()) }
    viewModel { DetailViewModel(get(), get(), get(), get()) }
}