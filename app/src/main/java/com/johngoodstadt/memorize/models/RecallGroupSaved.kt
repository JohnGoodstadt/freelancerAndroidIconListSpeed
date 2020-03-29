package com.johngoodstadt.memorize.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*

data class RecallGroupSaved2(val uid: Int,
                        var header: String?

)



@Entity(tableName = "BusDepot")
data class RecallGroupSaved(

    //values to assign - latest Android App
    var title: String = "",

    //Default values
    var busDepotID: Int = 1,
    var hidden:Boolean = false,

    var updated_date: Date? = Date(),
    var inserted_date: Date? = Date(),

    @PrimaryKey var UID:String = UUID.randomUUID().toString(),
    var recordName: String = "",
    var recordChangeTag: String = "",

    @Ignore var isLinked:Boolean = false,
    @Ignore var isGroupMerged:Boolean = false,
    @Ignore var stepNumber:Int = 1,
    @Ignore var itemList:List<RecallItem> = emptyList()


){}
