package com.example.finalproject.data.dao

import androidx.room.*
import com.example.finalproject.data.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {

    @Query("SELECT * FROM search_history ORDER BY searched_at DESC LIMIT 20")
    fun getAllHistory(): Flow<List<SearchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: SearchHistoryEntity)

    @Query("DELETE FROM search_history")
    suspend fun clearAllHistory()

    @Query("SELECT * FROM search_history WHERE query LIKE '%' || :query || '%' ORDER BY searched_at DESC LIMIT 5")
    fun searchHistory(query: String): Flow<List<SearchHistoryEntity>>
}
