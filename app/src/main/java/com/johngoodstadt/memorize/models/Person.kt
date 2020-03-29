package com.johngoodstadt.memorize.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "Person")
//class Person(name: String, dbid: Int, UID:String) {
//class Person (title:String,dbid:Int,UID:String,hidden:Boolean,application:Application) {
class Person (title:String,dbid:Int,UID:String,hidden:Boolean) {
    //@Ignore private val coroutineScope = CoroutineScope(Dispatchers.IO)
    //@Ignore private var recallItemDAO:RecallItemDAO
    //var db:AppDatabase? = null
    //var application:Application = null
    //private lateinit var personViewModel: PersonViewModel

    init{
        //this.recallItemDAO = AppDatabase.getDatabase(application)!!.recallItemDAO()
    }

//
//    constructor() {
////
//        //this.recallItemDAO = AppDatabase.getDatabase(application)!!.recallItemDAO()
////
//    }


    var title: String = ""

        set(value) {
            field = value
            //updatePropertyLocal("title","value",UID)

            //this.recallItemDAO.updateProperty(title,UID)

            //coroutineScope.launch(Dispatchers.IO) {

                println("updating title")
                //recallItemDAO.updateProperty(title,UID)
                println("updated  title")

            //}

        }





//        coroutineScope.launch(Dispatchers.IO) {
//            recallItemDAO.updateProperty(dbid, UID)
//
//        }
//    }



//    var dbid: Int by Delegates.observable(dbid) { _, old, new ->
//        println("dbid changed from $old to $new")
//    }

    var dbid: Int = 0
        set(value) {
            println("dbid changed from $dbid to $value")
            field = value
           // updatePropertyLocal("dbid",value.toString(),UID)
        }

    @PrimaryKey var UID:String = UUID.randomUUID().toString()


    var hidden: Boolean = false
        set(value) {
            println("hidden changed from $hidden to $value")
            field = value
           // updatePropertyLocal("hidden",value.toString(),UID)
    }

//    var isLinked: Boolean = false
//        set(value) {
//            println("hidden changed from $isLinked to $value")
//            field = value
//            //updatePropertyLocal("isLinked",value.toString(),UID)
//        }

//    @Ignore var stepNumber: Int = 1
//
//
//    @Ignore var itemList: List<RecallItem>  = emptyList()
}

/*
data class RecallGroup(var title: String = "",
                       var dbid: Int = 1,
                       @PrimaryKey var UID:String = UUID.randomUUID().toString(),
                       var hidden:Boolean = false,
                       var isLinked:Boolean = false,
                       var isGroupMerged:Boolean = false,
                       var stepNumber:Int = 1,
                       @Ignore var itemList:List<RecallItem> = emptyList()


)
 */