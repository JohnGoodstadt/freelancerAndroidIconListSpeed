package com.johngoodstadt.memorize.fragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.johngoodstadt.memorize.database.AppDatabase
import com.johngoodstadt.memorize.database.RecallItemRepository
import com.johngoodstadt.memorize.models.RecallItem
import kotlinx.coroutines.launch

// Class extends AndroidViewModel and requires application as a parameter.
class CRUDLocalRecallItemViewModel(application: Application) : AndroidViewModel(application) {

    // The ViewModel maintains a reference to the repository to get data.
    private val recallItemRepository: RecallItemRepository


    // LiveData gives us updated words when they change.
    //val allItems: LiveData<List<RecallItem>>

    init {
        // Gets reference to WordDao from WordRoomDatabase to construct
        // the correct WordRepository.
        val recallItemDAO = AppDatabase.getDatabase(
            application,
            viewModelScope
        )!!.recallItemDAO()
        recallItemDAO.let {
            recallItemRepository =
                RecallItemRepository(it)
        }

    }


    fun insert(ri: RecallItem) = viewModelScope.launch {
        recallItemRepository.insert(ri)
    }
    fun delete(ri: RecallItem) = viewModelScope.launch {
        recallItemRepository.delete(ri)
    }
    fun update(ri: RecallItem) = viewModelScope.launch {
        recallItemRepository.update(ri)
    }

    fun updateProperties(ri: RecallItem) = viewModelScope.launch {
        recallItemRepository.update(ri)
    }
}