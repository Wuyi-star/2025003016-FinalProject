package com.example.finalproject.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.finalproject.data.dao.BookDao
import com.example.finalproject.data.dao.SearchHistoryDao
import com.example.finalproject.data.entity.BookEntity
import com.example.finalproject.data.entity.SearchHistoryEntity

@Database(
    entities = [BookEntity::class, SearchHistoryEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun bookDao(): BookDao
    abstract fun searchHistoryDao(): SearchHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * 数据库迁移：版本 1 -> 2
         * 新增 search_history 表和 books 表的 is_favorite 字段
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 创建搜索历史表
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `search_history` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `query` TEXT NOT NULL,
                        `searched_at` INTEGER NOT NULL
                    )
                """)
                // 为 books 表添加 is_favorite 列（带默认值 false）
                database.execSQL("""
                    ALTER TABLE `books` ADD COLUMN `is_favorite` INTEGER NOT NULL DEFAULT 0
                """)
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "book_collector_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
