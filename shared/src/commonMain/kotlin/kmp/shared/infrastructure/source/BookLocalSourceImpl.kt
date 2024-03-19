package kmp.shared.infrastructure.source

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kmp.shared.data.source.BookLocalSource
import kmp.shared.infrastructure.local.BookEntity
import kmp.shared.infrastructure.local.BookQueries
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.coroutineContext

internal class BookLocalSourceImpl(
    private val queries: BookQueries,
) : BookLocalSource {
    override suspend fun getAll(): Flow<List<BookEntity>> {
        return queries.getAllBooks().asFlow().mapToList(coroutineContext)
    }

    override suspend fun updateOrInsert(items: List<BookEntity>) {
        queries.deleteAllBooks()
        items.forEach(queries::insertOrReplace)
    }
}
