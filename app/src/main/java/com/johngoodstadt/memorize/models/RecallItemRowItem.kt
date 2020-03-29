package com.johngoodstadt.memorize.models

import android.net.Uri

//data class recallItemRowItemHeader(var header: String?)

data class RecallItemRowItem(
    val id: Int,
    val UID: String?,
    val busDepotUID:String?,
    var journeyState: RecallItem.ITEM_JOURNEY_STATE_ENUM,
    var title: String?,
    var heading: String?,
    var subheading: String?,
    var time: String?,
    var image_uri: Uri?,
    val initials: String? //not changed after assignment
)

data class OneLineTableViewCell(
    val id: Int,
    val UID: String?,
    val busDepotUID:String?,
    var journeyState: RecallItem.ITEM_JOURNEY_STATE_ENUM,
    var title: String?,
    var time: String?,
    var image_uri: Uri?,
    val initials: String? //not changed after assignment
)
data class CheckedTableViewCell(
    val id: Int,
    val UID: String?,
    val busDepotUID:String?,
    var journeyState: RecallItem.ITEM_JOURNEY_STATE_ENUM,
    var title: String?,
    var checked: Boolean = false,
    var image_uri: Uri?,
    val initials: String? //not changed after assignment
)
data class WaitingForOrhersTableViewCell(
    val id: Int,
    val UID: String?,
    val busDepotUID:String?,
    var journeyState: RecallItem.ITEM_JOURNEY_STATE_ENUM,
    var title: String?,
    var heading: String?,
    var subheading: String?,
    var image_uri: Uri?,
    val initials: String? //not changed after assignment
)
data class CheckedRecallItems(
    val intUID: Int,
    val UID: String?,
    var journeyState: RecallItem.ITEM_JOURNEY_STATE_ENUM,
    var title: String?,
    var checked: Boolean = false

)