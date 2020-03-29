package com.johngoodstadt.memorize.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.johngoodstadt.memorize.Libraries.Converters
import com.johngoodstadt.memorize.models.RecallGroup
import com.johngoodstadt.memorize.models.RecallItem
import kotlinx.coroutines.CoroutineScope


@Database (entities = [(RecallItem::class),(RecallGroup::class)],version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun recallItemDAO(): RecallItemDAO
    abstract fun recallGroupDAO(): RecallGroupDAO



    companion object {
        @Volatile
        private var appDatabaseInstance: AppDatabase? = null



        public open fun getDatabase(context: Context,
                                    scope: CoroutineScope
        ): AppDatabase? {
            if (appDatabaseInstance == null) {
                synchronized(AppDatabase::class.java) {
                    if (appDatabaseInstance == null) {
                        appDatabaseInstance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase::class.java, "memorize.db"
                        ).addMigrations(MIGRATION_1_2,MIGRATION_2_1)
                            .build()
                    }
                }
            }
            return appDatabaseInstance
        }

        public open fun getDatabase(context: Context
        ): AppDatabase? {
            if (appDatabaseInstance == null) {
                synchronized(AppDatabase::class.java) {
                    if (appDatabaseInstance == null) {
                        appDatabaseInstance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase::class.java, "memorize.db"
                        ).addMigrations(MIGRATION_1_2,MIGRATION_2_1)
                            .build()
                    }
                }
            }
            return appDatabaseInstance
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                Log.w("DB","migrating from 1 to 3")
                //database.execSQL("CREATE TABLE `Fruit` (`id` INTEGER, `name` TEXT, " +
                //       "PRIMARY KEY(`id`))")
            }
        }
        val MIGRATION_2_1 = object : Migration(2, 1) {
            override fun migrate(database: SupportSQLiteDatabase) {
                Log.w("DB","migrating from 2 to 1")
                //database.execSQL("CREATE TABLE `Fruit` (`id` INTEGER, `name` TEXT, " +
                //       "PRIMARY KEY(`id`))")
            }
        }
    }


}