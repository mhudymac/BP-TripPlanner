package kmp.shared.di

import com.russhwolf.settings.Settings
import kmp.Database
import kmp.shared.data.repository.PlaceRepositoryImpl
import kmp.shared.data.repository.TripRepositoryImpl
import kmp.shared.data.source.PlaceLocalSource
import kmp.shared.data.source.PlaceRemoteSource
import kmp.shared.data.source.TripLocalSource
import kmp.shared.domain.repository.PlaceRepository
import kmp.shared.domain.repository.TripRepository
import kmp.shared.domain.usecase.place.SearchPlacesUseCase
import kmp.shared.domain.usecase.place.SearchPlacesUseCaseImpl
import kmp.shared.domain.usecase.place.SearchPlacesWithBiasUseCase
import kmp.shared.domain.usecase.place.SearchPlacesWithBiasUseCaseImpl
import kmp.shared.domain.usecase.place.UpdatePhotoUrlUseCase
import kmp.shared.domain.usecase.place.UpdatePhotoUrlUseCaseImpl
import kmp.shared.domain.usecase.trip.GetAllTripsWithoutPlacesUseCase
import kmp.shared.domain.usecase.trip.GetAllTripsWithoutPlacesUseCaseImpl
import kmp.shared.domain.usecase.trip.GetNearestTripUseCase
import kmp.shared.domain.usecase.trip.GetNearestTripUseCaseImpl
import kmp.shared.domain.usecase.trip.GetTripUseCase
import kmp.shared.domain.usecase.trip.GetTripUseCaseImpl
import kmp.shared.domain.usecase.trip.SaveTripUseCase
import kmp.shared.domain.usecase.trip.SaveTripUseCaseImpl
import kmp.shared.infrastructure.local.createDatabase
import kmp.shared.infrastructure.remote.HttpClient
import kmp.shared.infrastructure.remote.PlaceService
import kmp.shared.infrastructure.source.PlaceLocalSourceImpl
import kmp.shared.infrastructure.source.PlaceRemoteSourceImpl
import kmp.shared.infrastructure.source.TripLocalSourceImpl
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(appDeclaration: KoinAppDeclaration = {}): KoinApplication {
    val koinApplication = startKoin {
        appDeclaration()
        modules(commonModule, platformModule)
    }

    // Dummy initialization logic, making use of appModule declarations for demonstration purposes.
    val koin = koinApplication.koin
    val doOnStartup =
        koin.getOrNull<() -> Unit>() // doOnStartup is a lambda which is implemented in Swift on iOS side
    doOnStartup?.invoke()

    return koinApplication
}

private val commonModule = module {

    // General
    single { "AIzaSyCm23vlG0GFw6CE9U94peD-2HaSzkPZhGk" }
    single { HttpClient.init(get(), get(), get()) }
    single { Settings() }

    // UseCases
    factory<SearchPlacesUseCase> { SearchPlacesUseCaseImpl(get()) }
    factory<SearchPlacesWithBiasUseCase> { SearchPlacesWithBiasUseCaseImpl(get()) }
    factory<UpdatePhotoUrlUseCase> { UpdatePhotoUrlUseCaseImpl(get()) }

    factory<SaveTripUseCase> { SaveTripUseCaseImpl(get(),get()) }
    factory<GetAllTripsWithoutPlacesUseCase> { GetAllTripsWithoutPlacesUseCaseImpl(get()) }
    factory<GetTripUseCase> { GetTripUseCaseImpl(get(), get()) }
    factory<GetNearestTripUseCase> { GetNearestTripUseCaseImpl(get(), get()) }


    // Repositories
    single<PlaceRepository> { PlaceRepositoryImpl(get(), get()) }
    single<TripRepository> { TripRepositoryImpl(get()) }

    // Sources
    single<PlaceRemoteSource> { PlaceRemoteSourceImpl(get()) }
    single<PlaceLocalSource> { PlaceLocalSourceImpl(get()) }
    single<TripLocalSource> { TripLocalSourceImpl(get()) }

    // DAOs


    // Http Services
    single { PlaceService(get()) }

    // Database
    single { createDatabase(get()) }
    single { get<Database>().tripQueries }
    single { get<Database>().placeQueries }


}

expect val platformModule: Module
