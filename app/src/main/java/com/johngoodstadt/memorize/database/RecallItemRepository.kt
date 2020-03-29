package com.johngoodstadt.memorize.database

import androidx.lifecycle.LiveData
import com.johngoodstadt.memorize.models.Person
import com.johngoodstadt.memorize.models.RecallItem

class RecallItemRepository(private val recallItemDao: RecallItemDAO) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allWords: LiveData<List<RecallItem>> = recallItemDao.getAllRecallItems()

    suspend fun insert(ri: RecallItem) {
        recallItemDao.saveRecallItem(ri)
    }
//    suspend fun insert(p: Person) {
//        recallItemDao.savePerson(p)
//    }
    suspend fun delete(ri: RecallItem) {
        recallItemDao.delete(ri)
    }
    suspend fun update(ri: RecallItem) {
        recallItemDao.update(ri)
    }
    suspend fun updateTitleProperty(title: String,UID: String) {
        recallItemDao.updateTitleProperty(title,UID)
    }
    suspend fun updateWordsProperty(words: String,UID: String) {
        recallItemDao.updateWordsProperty(words,UID)
    }
}