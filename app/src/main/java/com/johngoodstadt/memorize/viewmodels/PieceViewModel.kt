package com.johngoodstadt.memorize.viewmodels

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class PieceViewModel @Inject constructor(): ViewModel() {
    val composer: ObservableField<String> = ObservableField()
    val piece: ObservableField<String> = ObservableField()
    val movement: ObservableField<String> = ObservableField()


}
