package com.johngoodstadt.memorize.fragments.PromptAsImageWizard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.johngoodstadt.memorize.database.AppDatabase
import com.johngoodstadt.memorize.database.RecallItemRepository
import com.johngoodstadt.memorize.models.RecallItem
import kotlinx.coroutines.launch

// Class extends AndroidViewModel and requires application as a parameter.
class PromptAsImageWizard3ViewModel(application: Application) : AndroidViewModel(application) {

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

    /**
     * The implementation of insert() in the database is completely hidden from the UI.
     * Room ensures that you're not doing any long running operations on
     * the main thread, blocking the UI, so we don't need to handle changing Dispatchers.
     * ViewModels have a coroutine scope based on their lifecycle called
     * viewModelScope which we can use here.
     */
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