//package kmp.shared.infrastructure.source
//
//import app.cash.sqldelight.coroutines.asFlow
//import app.cash.sqldelight.coroutines.mapToList
//import kmp.shared.data.source.UserLocalSource
//import kmp.shared.data.source.UserPagingRequest
//import kmp.shared.infrastructure.local.UserCache
//import kmp.shared.infrastructure.local.UserCacheQueries
//import kmp.shared.infrastructure.local.UserEntity
//import kmp.shared.infrastructure.local.UserQueries
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.IO
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.distinctUntilChanged
//import kotlinx.coroutines.flow.drop
//import kotlinx.coroutines.flow.map
//
//internal class UserLocalSourceImpl(
//    private val userQueries: UserQueries,
//    private val userCacheQueries: UserCacheQueries,
//) : UserLocalSource {
//
//    override fun getUsers(): Flow<List<UserEntity>> {
//        return userQueries.getAllUsers().asFlow().mapToList(Dispatchers.IO)
//    }
//
//    override suspend fun getUser(id: String): UserEntity? =
//        userQueries
//            .getUser(id)
//            .executeAsOneOrNull()
//
//    override suspend fun updateOrCreate(userEntity: UserEntity) =
//        userQueries.insertOrReplace(userEntity)
//
//    override suspend fun updateOrCreate(entities: List<UserEntity>) {
//        userQueries.deleteAllUsers()
//        entities.forEach(userQueries::insertOrReplace)
//    }
//
//    override suspend fun replaceCacheWith(users: List<UserCache>) = userCacheQueries.transaction {
//        userCacheQueries.deleteCache()
//        users.forEach(userCacheQueries::insertOrReplace)
//    }
//
//    override suspend fun gePagingCache(paging: UserPagingRequest): Flow<List<UserCache>> =
//        userCacheQueries
//            .getUsersPaginated(paging.limit.toLong(), paging.offset.toLong())
//            .asFlow()
//            .mapToList(Dispatchers.IO)
//
//    override suspend fun getPagingCacheCount(): Long =
//        userCacheQueries.getUserCount().executeAsOne()
//
//    override suspend fun updatePagingCache(users: List<UserCache>) {
//        userCacheQueries.transaction {
//            users.forEach(userCacheQueries::insertOrReplace)
//        }
//    }
//
//    override suspend fun onPagingCacheChanged(): Flow<Unit> = userCacheQueries
//        .getCache()
//        .asFlow()
//        .mapToList(Dispatchers.IO)
//        .distinctUntilChanged()
//        .map { }
//        .drop(1)
//}
