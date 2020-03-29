package com.johngoodstadt.memorize.models

import android.util.Log
import com.johngoodstadt.memorize.Libraries.MyApplication
import com.johngoodstadt.memorize.database.AppDatabase
import com.johngoodstadt.memorize.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


open class ExtensionRecallGroup {

    companion object {

        fun moveToOtherListAllBusesAllCombinedPhrases( recallItemsNoOrder:List<RecallItem> ) : List<RecallItem> {


            var returnValue =  ArrayList<RecallItem>()

            var bigListUID =  ArrayList<String>()
            // var bigListBus : [RecallItem] = []

            //for each combined representative bus
            //1. get linkedUIDs (remove self)
            //2. add to total list
            //3. convert to list of buses
            //4. remove each from global list
            //        var listOfCombinedPhrasesUID = [[String]]()

            for (ri in recallItemsNoOrder){

                //NOTE: this can be step2 - all merged to 'A'  or Step 4 combined phrases
                if (ri.linkedPhrases.isEmpty() == false){//we have a combined list of phrases - step 2
                    val lstValues: List<String> = ri.linkedPhrases.split(",").map { it -> it.trim() }
                    bigListUID.addAll(lstValues)
                }else{
                    //quick check on invalid combinations
                    //if linkedPhrases is empty then 'ExtraInfo' should also be empty and if not and also not the same as title - can fix it up here
                    //linkedPhrases is empty
                    //TODO what is extraInfo for?
//                    if (ri.extraInfo.isEmpty() == false) {
//                        if (ri.extraInfo != ri.title) {
//                            ri.title = ri.extraInfo
//                            ri.extraInfo = "" //clear out
//                            print("! SAVED USER FROM INVALID COMBINED PHRASES ERROR !")
//                        }
//                    }
                }
            }



            if (bigListUID.isEmpty()){
                return recallItemsNoOrder
            }


            //for step 2 processing in muisc assign to other list
            val itemsToMove = recallItemsNoOrder.filter { it.linkedPhrases.isEmpty() &&  it.UID in bigListUID }

            for (ri in itemsToMove){
                if (ri.recallGroup != null){
                    ri.recallGroup.let { rg ->
                        rg.otheritemList.add(ri)
                        rg.itemList.remove(ri)
                    }
                }

            }





            val projection = itemsToMove.map { it.UID }
            val list = recallItemsNoOrder.filter { it.UID !in projection }



            return list
        }
        fun removeFromAllBusesAllCombinedPhrases( recallItemsNoOrder:List<RecallItem> ) : List<RecallItem> {


            var returnValue =  ArrayList<RecallItem>()

            var bigListUID =  ArrayList<String>()
            // var bigListBus : [RecallItem] = []

            //for each combined representative bus
            //1. get linkedUIDs (remove self)
            //2. add to total list
            //3. convert to list of buses
            //4. remove each from global list
            //        var listOfCombinedPhrasesUID = [[String]]()

            for (ri in recallItemsNoOrder){

                //NOTE: this can be step2 - all merged to 'A'  or Step 4 combined phrases
                if (ri.linkedPhrases.isEmpty() == false){//we have a combined list of phrases - step 2
                    val lstValues: List<String> = ri.linkedPhrases.split(",").map { it -> it.trim() }
                    bigListUID.addAll(lstValues)
                }else{
                    //quick check on invalid combinations
                    //if linkedPhrases is empty then 'ExtraInfo' should also be empty and if not and also not the same as title - can fix it up here
                    //linkedPhrases is empty
                    if (ri.extraInfo.isEmpty() == false) {
                        if (ri.extraInfo != ri.title) {
                            ri.title = ri.extraInfo
                            ri.extraInfo = "" //clear out
                            print("! SAVED USER FROM INVALID COMBINED PHRASES ERROR !")
                        }
                    }
                }
            }



            if (bigListUID.isEmpty()){
                return recallItemsNoOrder
            }

            //let listFilteredEmpty = recallItemsNoOrder.filter { $0.linkedPhrases.isEmpty }
            //NOTE also filter out NON Empty rows
            //val itemsToRemove9 = recallItemsNoOrder.filter{ $0.linkedPhrases.isEmpty && bigListUID.contains($0.UID) }

            val itemsToRemove = recallItemsNoOrder.filter { it.linkedPhrases.isEmpty() &&  it.UID in bigListUID }

//            val referenceIds = itemsToRemove.distinctBy { it.UID }.toSet()
//            recallItemsNoOrder.filter  { it.UID !in referenceIds }

            val projection = itemsToRemove.map { it.UID }
            val list = recallItemsNoOrder.filter { it.UID !in projection }


//            val set1 = recallItemsNoOrder.toSet()
//            val set2 = itemsToRemove.toSet()
//
//            returnValue = set1.subtract(itemsToRemove)

           return list
        }

        //TODO: does not work from RecallGroup class!!
        fun getRowsFromDBRoom(groupUID:String){
            val coroutineScope = CoroutineScope(Dispatchers.IO)

            Log.e("Items", "in getRowsFromDBRoom()")


            coroutineScope.launch(Dispatchers.IO){
                getRowsFromDBRoomInternal(groupUID)
            }
        }
        suspend fun getRowsFromDBRoomInternal(groupUID:String) = withContext(Dispatchers.IO) {
            {

                Log.e("Items", "in getRowsFromDBRoomInternal()")


                val db = AppDatabase.getDatabase(MyApplication.getAppContext())


                db.let { appdb ->
                    appdb?.recallGroupDAO()?.getRecallItemsForGroup(groupUID)?.forEach()
                    {


                        Log.e("Items", "${it.UID} : ${it.title}, ${it.intUID} : groupUID: ${it.busDepotUID}")


                    }

                }





            }
//        fun getRowsFromDBCoroutine(rg:RecallGroup){
//
//
//
//            val coroutineScope = CoroutineScope(Dispatchers.IO)
//            coroutineScope.launch(Dispatchers.IO) {
//
//
//                loadFromLocal_coroutines(rg)
//
//                Log.e("ASYNC", "loaded")
//                Log.e("ASYNC", "BEFORE runOnUiThread" )
//                Log.e("ASYNC", Thread.currentThread().getId().toString() )
//                runOnUiThread {
//                    Log.e("ASYNC", "AFTER  runOnUiThread" )
//                    Log.e("ASYNC", Thread.currentThread().getId().toString() )
//                }
//
//
//            }
//        }
//        suspend fun loadFromLocal_coroutines(rg:RecallGroup) = withContext(Dispatchers.IO) {
//
//
//            val db = AppDatabase.getDatabase(application)
//
//
//            db.let { appdb ->
//                Log.e("THREAD", "RECALL ITEM - LOADING")
//
//
//                //1. read groups
//                appdb?.recallGroupDAO()?.getAllRecallGroupsAsList()?.forEach()
//                {
//                    Log.e("Group", "UID:${it.UID}, title: ${it.title} ")
//                    Constants.GlobalVariables.groups.add(it)
//                }
//
//                //2. read items
//                var counter = 1
//                appdb?.recallItemDAO()?.getAllRecallItemsAsList()?.forEach()
//                {
//
//
//                    if (counter <= 50){
//                        Log.e("Items", "${it.UID} : ${it.title}, ${it.intUID} : groupUID: ${it.busDepotUID}")
//                        counter++
//                    }
//
//
//                    Constants.GlobalVariables.busesNoOrderFromDB.add(it)
//
//                }
//                if ( Constants.whichApp.isRunning == Constants.whichApp.target.music ){
//
//                    val list = RecallGroup.removeFromAllBusesAllCombinedPhrases(Constants.GlobalVariables.busesNoOrderFromDB)
//                    Constants.GlobalVariables.busesNoOrderFromDB = ArrayList(list)
//
//
//                }
//
//
//                Log.e(
//                    "Fetch Records",
//                    "group count: ${Constants.GlobalVariables.groups.count()}"
//                )
//                Log.e(
//                    "Fetch Records",
//                    "item  count: ${Constants.GlobalVariables.busesNoOrderFromDB.count()}"
//                )
//
//                Log.e("Fetch Records", "End of async load loadFromLocal_coroutines()" )
//                Log.e("Fetch Records", Thread.currentThread().getId().toString() )
//
//
//                val lookupDict =  Constants.GlobalVariables.groups.associate { Pair(it.UID, it) }.toMap()
//
//
//                //3. add items to correct groups
//                for (ri in Constants.GlobalVariables.busesNoOrderFromDB){
//                    if (ri.busDepotUID.isEmpty() == false){
//
//                        val key = ri.busDepotUID
//
//                        val rg = lookupDict.get(key)
//
//                        rg?.let {
//                            ri.recallGroup = it //ri points to group
//                            it.addItem(ri)      //rg points to ri
//                        }
//                    }
//                }
//
//
//                if ( Constants.whichApp.isRunning == Constants.whichApp.target.music ){
//                    for (rg in Constants.GlobalVariables.groups){
//                        rg.markCurrentStep()
//                    }
//                }
//
//            }
//
//
//
//
//        }
        }

    }
}
