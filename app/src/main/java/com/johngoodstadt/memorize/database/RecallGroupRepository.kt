package com.johngoodstadt.memorize.database

import androidx.lifecycle.LiveData
import com.johngoodstadt.memorize.models.RecallGroup
import com.johngoodstadt.memorize.models.RecallItem

class RecallGroupRepository(private val recallGroupDAO: RecallGroupDAO) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allGroups: LiveData<List<RecallGroup>> = recallGroupDAO.getAllRecallGroups()

    suspend fun insert(ri: RecallGroup) {
        recallGroupDAO.saveRecallGroup(ri)
    }
    suspend fun update(rg: RecallGroup) {
        recallGroupDAO.update(rg)
    }
    suspend fun delete(rg: RecallGroup) {
        recallGroupDAO.delete(rg)
    }
}