package kmp.android.shared.di

import kmp.android.shared.ErrorMessageProviderImpl
import kmp.shared.base.error.ErrorMessageProvider
import org.koin.dsl.module

val androidSharedModule = module {
    single<ErrorMessageProvider> { ErrorMessageProviderImpl(get()) }
}
