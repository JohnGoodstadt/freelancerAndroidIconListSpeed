package com.johngoodstadt.memorize.viewmodels

import android.util.Log
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import com.johngoodstadt.memorize.models.RecallGroup
import com.johngoodstadt.memorize.models.RecallItem

class PieceDetailVM: ViewModel() {
    var buttonSelected: ObservableInt = ObservableInt(0)

    public lateinit var recallGroup: RecallGroup

    fun buttonClicked(position:Int){
        buttonSelected.set(position)
        when (position) {
            0 -> callPhrases()
            2 -> callPiece() //??? 2
            3 -> callPolish()
        }
    }

    private fun callPolish() {
        Log.d("memorize","polish clicked")

        //recallGroup.

    }

    private fun callPiece() {
        Log.d("memorize","piece clicked")
        recallGroup.goToStep2()

    }

    private fun callPhrases() {
        Log.d("memorize","phraase clicked")

    }
}