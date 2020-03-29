package com.johngoodstadt.memorize.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.johngoodstadt.memorize.models.RecallGroup
import com.johngoodstadt.memorize.models.RecallItem

@Dao
interface RecallGroupDAO
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveRecallGroup(rg: RecallGroup)

    @Query(value = "SELECT * from BusDepot")
    fun getAllRecallGroups() : LiveData<List<RecallGroup>>

    @Query(value = "SELECT * from Bus WHERE BusDepotUID = :BusDepotUID")
    fun getRecallItemsForGroup( BusDepotUID: String) : List<RecallItem>


    @Query(value = "SELECT * from BusDepot")
    fun getAllRecallGroupsAsList() : List<RecallGroup>

    @Update
    suspend fun update(rg: RecallGroup)

    @Delete
    suspend fun delete(rg: RecallGroup)
}