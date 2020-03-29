package com.johngoodstadt.memorize.di

import com.johngoodstadt.memorize.activities.AddPieceActivity
import com.johngoodstadt.memorize.activities.MusicPieceActivity
import dagger.Component

@Component
interface AppComponent {

    fun inject(pieceActivity: AddPieceActivity)
    fun inject(pieceDetailActivity: MusicPieceActivity)
}