package kmp.shared.di

import com.russhwolf.settings.Settings
import kmp.Database
import kmp.shared.data.repository.DistanceRepositoryImpl
import kmp.shared.data.repository.PlaceRepositoryImpl
import kmp.shared.data.repository.TripRepositoryImpl
import kmp.shared.data.repository.PhotoRepositoryImpl
import kmp.shared.data.source.DistanceLocalSource
import kmp.shared.data.source.PhotoLocalSource
import kmp.shared.data.source.PlaceLocalSource
import kmp.shared.data.source.PlaceRemoteSource
import kmp.shared.data.source.TripLocalSource
import kmp.shared.domain.repository.DistanceRepository
import kmp.shared.domain.repository.PhotoRepository
import kmp.shared.domain.repository.PlaceRepository
import kmp.shared.domain.repository.TripRepository
import kmp.shared.domain.usecase.place.SaveDistancesUseCase
import kmp.shared.domain.usecase.place.SaveDistancesUseCaseImpl
import kmp.shared.domain.usecase.location.GetLocationFlowUseCase
import kmp.shared.domain.usecase.location.GetLocationFlowUseCaseImpl
import kmp.shared.domain.usecase.location.GetLocationUseCase
import kmp.shared.domain.usecase.location.GetLocationUseCaseImpl
import kmp.shared.domain.usecase.photos.GetPhotosByTripUseCase
import kmp.shared.domain.usecase.photos.GetPhotosByTripUseCaseImpl
import kmp.shared.domain.usecase.photos.GetPhotosUseCase
import kmp.shared.domain.usecase.photos.GetPhotosUseCaseImpl
import kmp.shared.domain.usecase.photos.RemovePhotoByUriUseCase
import kmp.shared.domain.usecase.photos.RemovePhotoByUriUseCaseImpl
import kmp.shared.domain.usecase.photos.SavePhotoUseCase
import kmp.shared.domain.usecase.photos.SavePhotoUseCaseImpl
import kmp.shared.domain.usecase.place.DeletePlaceByIdUseCase
import kmp.shared.domain.usecase.place.DeletePlaceByIdUseCaseImpl
import kmp.shared.domain.usecase.place.GetPlaceByLocationUseCase
import kmp.shared.domain.usecase.place.GetPlaceByLocationUseCaseImpl
import kmp.shared.domain.usecase.place.SearchPlacesUseCase
import kmp.shared.domain.usecase.place.SearchPlacesUseCaseImpl
import kmp.shared.domain.usecase.place.SearchPlacesWithBiasUseCase
import kmp.shared.domain.usecase.place.SearchPlacesWithBiasUseCaseImpl
import kmp.shared.domain.usecase.place.UpdatePhotoUrlUseCase
import kmp.shared.domain.usecase.place.UpdatePhotoUrlUseCaseImpl
import kmp.shared.domain.usecase.trip.DeleteTripUseCase
import kmp.shared.domain.usecase.trip.DeleteTripUseCaseImpl
import kmp.shared.domain.usecase.trip.GetCompletedTripsWithoutPlacesUseCase
import kmp.shared.domain.usecase.trip.GetCompletedTripsWithoutPlacesUseCaseImpl
import kmp.shared.domain.usecase.trip.GetUncompletedTripsWithoutPlacesUseCase
import kmp.shared.domain.usecase.trip.GetUncompletedTripsWithoutPlacesUseCaseImpl
import kmp.shared.domain.usecase.trip.GetNearestTripUseCase
import kmp.shared.domain.usecase.trip.GetNearestTripUseCaseImpl
import kmp.shared.domain.usecase.trip.GetTripUseCase
import kmp.shared.domain.usecase.trip.GetTripUseCaseImpl
import kmp.shared.domain.usecase.trip.OptimiseTripUseCase
import kmp.shared.domain.usecase.trip.OptimiseTripUseCaseImpl
import kmp.shared.domain.usecase.trip.RemoveTripUseCase
import kmp.shared.domain.usecase.trip.RemoveTripUseCaseImpl
import kmp.shared.domain.usecase.trip.RepeatTripUseCase
import kmp.shared.domain.usecase.trip.RepeatTripUseCaseImpl
import kmp.shared.domain.usecase.trip.SaveTripUseCase
import kmp.shared.domain.usecase.trip.SaveTripUseCaseImpl
import kmp.shared.domain.usecase.trip.SaveTripWithoutIdUseCase
import kmp.shared.domain.usecase.trip.SaveTripWithoutIdUseCaseImpl
import kmp.shared.domain.usecase.trip.UpdateDistancesUseCase
import kmp.shared.domain.usecase.trip.UpdateDistancesUseCaseImpl
import kmp.shared.domain.usecase.trip.UpdateOnlyTripDetailsUseCase
import kmp.shared.domain.usecase.trip.UpdateOnlyOnlyTripDetailsUseCaseImpl
import kmp.shared.infrastructure.local.createDatabase
import kmp.shared.infrastructure.remote.maps.MapsClient
import kmp.shared.infrastructure.remote.maps.MapsService
import kmp.shared.infrastructure.remote.places.PlacesClient
import kmp.shared.infrastructure.remote.places.PlaceService
import kmp.shared.infrastructure.source.DistanceLocalSourceImpl
import kmp.shared.infrastructure.source.PhotoLocalSourceImpl
import kmp.shared.infrastructure.source.PlaceLocalSourceImpl
import kmp.shared.infrastructure.source.PlaceRemoteSourceImpl
import kmp.shared.infrastructure.source.TripLocalSourceImpl
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.qualifier.named
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
    single(named("PlacesClient")) { PlacesClient.init(get(), get(), get()) }
    single(named("GeocodingClient")) { MapsClient.init(get(), get(), get()) }
    single { Settings() }

    // Trip UseCases
    factory<SaveTripUseCase> { SaveTripUseCaseImpl(get(), get(), get()) }
    factory<SaveTripWithoutIdUseCase> { SaveTripWithoutIdUseCaseImpl(get(),get(),get(), get()) }
    factory<GetUncompletedTripsWithoutPlacesUseCase> { GetUncompletedTripsWithoutPlacesUseCaseImpl(get()) }
    factory<GetCompletedTripsWithoutPlacesUseCase> { GetCompletedTripsWithoutPlacesUseCaseImpl(get()) }
    factory<GetTripUseCase> { GetTripUseCaseImpl(get(), get()) }
    factory<GetNearestTripUseCase> { GetNearestTripUseCaseImpl(get(), get(), get(), get()) }
    factory<RemoveTripUseCase> { RemoveTripUseCaseImpl(get()) }
    factory<UpdateOnlyTripDetailsUseCase> { UpdateOnlyOnlyTripDetailsUseCaseImpl(get()) }
    factory<DeleteTripUseCase> { DeleteTripUseCaseImpl(get(), get(), get(), get()) }
    factory<RepeatTripUseCase> { RepeatTripUseCaseImpl(get(), get(), get(), get()) }
    factory<OptimiseTripUseCase> { OptimiseTripUseCaseImpl(get(), get()) }
    factory<UpdateDistancesUseCase> { UpdateDistancesUseCaseImpl(get(), get()) }

    // Place UseCases
    factory<SearchPlacesUseCase> { SearchPlacesUseCaseImpl(get()) }
    factory<SearchPlacesWithBiasUseCase> { SearchPlacesWithBiasUseCaseImpl(get()) }
    factory<UpdatePhotoUrlUseCase> { UpdatePhotoUrlUseCaseImpl(get()) }
    factory<GetLocationFlowUseCase> { GetLocationFlowUseCaseImpl(get()) }
    factory<GetLocationUseCase> { GetLocationUseCaseImpl(get()) }
    factory<GetPlaceByLocationUseCase> { GetPlaceByLocationUseCaseImpl(get(), get()) }
    factory<DeletePlaceByIdUseCase> { DeletePlaceByIdUseCaseImpl(get()) }

    // Distance UseCases
    factory<SaveDistancesUseCase> { SaveDistancesUseCaseImpl(get(), get()) }

    // Photo UseCases
    factory<SavePhotoUseCase> { SavePhotoUseCaseImpl(get()) }
    factory<GetPhotosUseCase> { GetPhotosUseCaseImpl(get()) }
    factory<GetPhotosByTripUseCase> { GetPhotosByTripUseCaseImpl(get()) }
    factory<RemovePhotoByUriUseCase> { RemovePhotoByUriUseCaseImpl(get()) }

    // Repositories
    single<PlaceRepository> { PlaceRepositoryImpl(get(), get()) }
    single<TripRepository> { TripRepositoryImpl(get()) }
    single<PhotoRepository> { PhotoRepositoryImpl(get()) }
    single<DistanceRepository> { DistanceRepositoryImpl(get()) }

    // Sources
    single<PlaceRemoteSource> { PlaceRemoteSourceImpl(get(), get()) }
    single<PlaceLocalSource> { PlaceLocalSourceImpl(get()) }
    single<TripLocalSource> { TripLocalSourceImpl(get()) }
    single<PhotoLocalSource> { PhotoLocalSourceImpl(get()) }
    single<DistanceLocalSource> { DistanceLocalSourceImpl(get()) }

    // DAOs


    // Http Services
    single { PlaceService(get(named("PlacesClient"))) }
    single { MapsService(get(named("GeocodingClient"))) }

    // Database
    single { createDatabase(get()) }
    single { get<Database>().tripQueries }
    single { get<Database>().placeQueries }
    single { get<Database>().photosQueries }
    single { get<Database>().distanceQueries }



}

expect val platformModule: Module
