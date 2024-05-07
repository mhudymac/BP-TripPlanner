package kmp.android.gallery.di


import kmp.android.gallery.viewmodel.GalleryViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val galleryModule = module {
    viewModel { GalleryViewModel(get(), get(), get(), get(), get()) }
}