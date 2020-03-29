package com.johngoodstadt.memorize.models

import javax.inject.Inject

data class Piece @Inject constructor(
    var composer: String?, var piece: String?,
    var movement: String?
)
