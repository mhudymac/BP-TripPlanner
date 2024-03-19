package kmp.shared.data.source

import kmp.shared.infrastructure.local.BookEntity
import kotlinx.coroutines.flow.Flow

internal interface BookLocalSource {
    suspend fun getAll(): Flow<List<BookEntity>>
    suspend fun updateOrInsert(items: List<BookEntity>)
}
