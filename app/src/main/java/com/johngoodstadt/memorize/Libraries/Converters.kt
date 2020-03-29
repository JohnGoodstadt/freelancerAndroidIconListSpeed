package com.johngoodstadt.memorize.Libraries

import androidx.room.TypeConverter
import com.johngoodstadt.memorize.models.RecallItem
import com.johngoodstadt.memorize.models.RecallItem.ANSWERED_ENUM
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: String?): Date? {

        return value?.let {
            if (it.isEmpty() ){
                Date()
            }else{
                Extensions.dateFromString(it)
            }


        }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): String? {


        return Extensions.dateForDB(date)
    }

//    @TypeConverter
//    fun fromIntToJourneyStateEnum(value: Int): RecallItem.ITEM_JOURNEY_STATE_ENUM? {
//
//        //val returnValue = RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateInDepotSetup
//
//        return value?.let {
//
//
//
//            RecallItem.getJourneyStateInt(value)
//
//
//
//        }
//    }
    @TypeConverter
    fun fromJourneyState(value: RecallItem.ITEM_JOURNEY_STATE_ENUM) = value.ordinal

    @TypeConverter
    fun toJourneyState(value: Int) = enumValues<RecallItem.ITEM_JOURNEY_STATE_ENUM>()[value]

    @TypeConverter
    fun toBusCategory(value: Int) = enumValues<RecallItem.RecallItemCategory_Enum>()[value]

    @TypeConverter
    fun fromBusCategory(value: RecallItem.RecallItemCategory_Enum) = value.ordinal


    @TypeConverter
    fun fromAnsweredState(value: RecallItem.ANSWERED_ENUM) = value.ordinal

    @TypeConverter
    fun toAnsweredState(value: Int) = enumValues<RecallItem.ANSWERED_ENUM>()[value]


//    @TypeConverter
//    fun toJourneyState(value: Integer): RecallItem.ITEM_JOURNEY_STATE_ENUM {
//
//        //val returnValue = RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateInDepotSetup
//
//        return value?.let {
//
//
//
//            RecallItem.getJourneyStateInt(value)
//
//
//
//        }
//    }
}