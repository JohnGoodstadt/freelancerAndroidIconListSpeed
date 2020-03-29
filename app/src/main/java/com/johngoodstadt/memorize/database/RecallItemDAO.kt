package com.johngoodstadt.memorize.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.johngoodstadt.memorize.models.Person
import com.johngoodstadt.memorize.models.RecallItem
import com.johngoodstadt.memorize.utils.Constants


@Dao
interface RecallItemDAO
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveRecallItem(ri: RecallItem)

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun savePerson(p: Person)


    @Query(value = "SELECT * from Bus")
    fun getAllRecallItems() : LiveData<List<RecallItem>>

    @Query(value = "SELECT * from Bus")
    fun getAllRecallItemsAsList() : List<RecallItem>

    //TODO: is this used?
    @Query(value = "SELECT * from Bus WHERE BusDepotUID = :BusDepotUID")
    fun getRecallItemsForGroup( BusDepotUID: String) : List<RecallItem>

    @Query(value = "SELECT * from Bus WHERE UID = :UID")
    fun getRecallItem( UID: String) :RecallItem


    @Delete
    suspend fun delete(recallItem: RecallItem)

    @Update
    suspend fun update(recallItem: RecallItem)

    @Query("UPDATE Bus SET journeyState = :state WHERE UID = :UID")
    suspend fun updateProperty(state: Int, UID: String)

    @Query("UPDATE Bus SET title = :title WHERE UID = :UID")
    suspend fun updateProperty(title: String, UID: String)

    @Query("UPDATE Bus SET title = :title WHERE UID = :UID")
    suspend fun updateTitleProperty(title: String, UID: String)

    @Query("UPDATE Bus SET words = :words WHERE UID = :UID")
    suspend fun updateWordsProperty(words: String, UID: String)
}