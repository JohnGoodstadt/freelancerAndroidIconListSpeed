package com.johngoodstadt.memorize.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.johngoodstadt.memorize.models.RecallGroup
import com.johngoodstadt.memorize.models.RecallItem
import kotlinx.coroutines.launch

// Class extends AndroidViewModel and requires application as a parameter.
class PersonViewModel(application: Application) : AndroidViewModel(application) {

    // The ViewModel maintains a reference to the repository to get data.
    private val recallItemRepository: RecallItemRepository


    init {

        val recallItemDAO = AppDatabase.getDatabase(application, viewModelScope)!!.recallItemDAO()
        recallItemDAO.let {
            recallItemRepository = RecallItemRepository(it)
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

    fun updateProperty(ri: RecallItem) = viewModelScope.launch {
        recallItemRepository.update(ri)
    }
}