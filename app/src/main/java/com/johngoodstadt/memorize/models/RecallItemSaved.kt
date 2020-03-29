package com.johngoodstadt.memorize.models

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.johngoodstadt.memorize.utils.Constants
import com.johngoodstadt.memorize.models.RecallGroup
import com.johngoodstadt.memorize.utils.ConstantsJava

import java.util.*
import java.util.concurrent.ThreadLocalRandom

//row definition for readyForPrompting
data class ReadyForReviewTableViewCell(
    val uid: Int, var title: String?, var heading: String?, var subheading: String?,
    var row_1: String?, var row_2: String?, var row_3: String?,
    var image_path: Uri?,
    var UID:String,
    val initials: String? //not changed after assignment

)
//data class Setup1LineTableViewCell(
//    val uid: Int, var title: String?,
//    var image_path: Uri?,
//    var UID:String
//)
//@Entity(tableName = "Bus")
data class RecallItemSaved  (

    //values to assign
    var title:String,
//    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    var busDepotUID:String,

    //Default values
//    @PrimaryKey(autoGenerate = true) var busID: Int = 20,
    var busID: Int = 20,
    var journeyState: Int = 0,//RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateInDepotSetup,
    var answeredState: Int = 1,
    var phrase: String = "",
    var cue: String = "",
    var busCategory: Int = 1,
    var words: String = "",
    var goTime: Date = Date(),
    var learnTime: Date = Date(),
    var prevEventTime:Date = Date(),
    var nextEventTime:Date = Date(),
    var currentBusStopNumber: Int = 0,
    var extraInfo: String = "",


    var recordName: String = "",
    var recordChangeTag: String = "",

//    @PrimaryKey()
    var UID: String = UUID.randomUUID().toString(),

    var metronome: String = "",
    var busStopTitles: String = "",
//    @ColumnInfo(typeAffinity = ColumnInfo.INTEGER)
    var busRouteUniqueID: Int = 1,

    var intUID: Int = ThreadLocalRandom.current().nextInt(1, ConstantsJava.RANDOM_UPPER_LIMIT_INT),

    var linkedPhrases: String = "",
    var schemeUID: String = "",
    var schemeTitles: String = "",
//    @Ignore
    var recallGroup: RecallGroup?,
//    @Ignore
    var recallGroupUID: String?
//    var busUID: Int = 1
){

//    constructor() : this("",
//        "",
//        0,
//        RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateInDepotSetup,
//        0, "", "", 0,"",
//        Date(),Date(),Date(),Date(),0,"",
//        "","","","","",0,
//        ThreadLocalRandom.current().nextInt(1,
//            ConstantsJava.RANDOM_UPPER_LIMIT_INT),"","","")

    var updated_date:  Date? = Date()
    var inserted_date:  Date? = Date()

}
/*
    var journeyState: Int ,
        get() = ConstantsJava.JOURNEY_STATE_IDs.JOURNEY_STATE_SETUP_ID
        set(value) = TODO(),

    var busDepotUID:String = "",
    var phrase: String = "",
    var words: String = "",
    var intUID: Int = ThreadLocalRandom.current().nextInt(1, RANDOM_UPPER_LIMIT_INT),
    var goTime: Date = Date(),
    var learnTime: Date = Date()
    var nextEventTime:Date = Date()
 */



