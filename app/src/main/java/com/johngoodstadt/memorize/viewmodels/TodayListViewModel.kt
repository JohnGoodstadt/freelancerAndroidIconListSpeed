package com.johngoodstadt.memorize.viewmodels

import android.net.Uri
import android.text.format.DateUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.johngoodstadt.memorize.Libraries.LibraryFilesystem
import com.johngoodstadt.memorize.Libraries.LibraryJava
import com.johngoodstadt.memorize.Libraries.MyApplication
import com.johngoodstadt.memorize.Libraries.updateOverdueJourneyStateIfNecessary
import com.johngoodstadt.memorize.R
import com.johngoodstadt.memorize.models.RecallItem
import com.johngoodstadt.memorize.models.RecallItemRowItem
import com.johngoodstadt.memorize.models.SectionHeader
import com.johngoodstadt.memorize.utils.Constants
import java.util.*
import javax.inject.Inject

class TodayListViewModel @Inject constructor() : ViewModel() {

    var todayRecallItemList: MutableLiveData<ArrayList<Any>> = MutableLiveData<ArrayList<Any>>()


     fun getStaticDataForMain() {

        if (Constants.GlobalVariables.busesNoOrderFromDB.isEmpty()){
            return //on app load can call before async load
        }

        val resources = MyApplication.getAppContext().resources

        val listItems: ArrayList<Any> = ArrayList()

        var due_header_added = false
        var overdue_header_added = false


        //IOS let tableviewSectionsModel = LocalLibrarySort.sortByStateThenDateToTVSections(activeTodayOnly)
        var travellingItems = Constants.GlobalVariables.busesNoOrderFromDB.filter { it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravelling || it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravellingOverdue }
        val sortedItems = travellingItems.sortedWith(compareBy({ it.journeyState }, { it.nextEventTime }))

        updateOverdueJourneyStateIfNecessary(sortedItems)

        sortedItems.forEach(){


                if (it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravelling && DateUtils.isToday(it.nextEventTime.getTime()) ) {
                    if (DateUtils.isToday(it.nextEventTime.getTime())) {
                        if (!due_header_added) {
                            due_header_added = true
                            listItems.add(SectionHeader(it.UID, resources.getString(R.string.section_title_not_ready)))

                        }
                        var image_uri: Uri? = null
                        var initials = "" //use either url or initials

                        val filename = LibraryFilesystem.getFileNameThumbnailImage(it.intUID)
                        if (LibraryFilesystem.exists(filename)) {
                            image_uri = LibraryFilesystem.getUriFromFilename(filename)
                        }else{
                            if (Constants.whichApp.isRunning == Constants.whichApp.target.music) {
                                initials = LibraryJava.getSuffixLetter(it.title)
                            }else{
                                initials = LibraryJava.getBest2LetterTitle(it.title)
                            }
                        }

                        var summaryTitleLabel = LibraryJava.assignStateLabel(it);// "The Heading" //it.heading
                        var debugLabel = it.currentStopTitle() + " " + it.currentBusStopNumber + " of " + it.stopCount; //"The Sub Heading"// it.subhe ading



                        listItems.add(RecallItemRowItem(1,it.UID,it.busDepotUID,  it.journeyState, it.title,summaryTitleLabel, debugLabel ,"Time",image_uri,initials))

                    }

                }else  if ( it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravellingOverdue) {
                    if (!overdue_header_added) {
                        overdue_header_added = true
                        listItems.add(SectionHeader(it.UID, resources.getString(R.string.section_title_ready)))

                    }
                    var image_uri: Uri? = null
                    var initials = "" //use either url or initials

                    val filename = LibraryFilesystem.getFileNameThumbnailImage(it.intUID)
                    if (LibraryFilesystem.exists(filename)) {
                        image_uri = LibraryFilesystem.getUriFromFilename(filename)
                    }else{
                        if (Constants.whichApp.isRunning == Constants.whichApp.target.music) {
                            initials = LibraryJava.getSuffixLetter(it.title)
                        }else{
                            initials = LibraryJava.getBest2LetterTitle(it.title)
                        }
                    }
                    var summaryTitleLabel = LibraryJava.assignStateLabel(it);// "The Heading" //it.heading
                    var debugLabel = it.currentStopTitle() + " " + it.currentBusStopNumber + " of " + it.stopCount; //"The Sub Heading"// it.subhe ading



                    listItems.add(RecallItemRowItem(1,it.UID,it.busDepotUID,  it.journeyState, it.title,summaryTitleLabel, debugLabel ,"Time",image_uri,initials))

                }



        }

        todayRecallItemList.postValue(listItems)

    }

   companion object{
       fun getTodayData()  : ArrayList<RecallItemRowItem>{


           //val resources = MyApplication.getAppContext().resources
           val listItems: ArrayList<RecallItemRowItem> = ArrayList()

           //val finalList: ArrayList<RecallItemRowItem> = ArrayList()

           if (Constants.GlobalVariables.busesNoOrderFromDB.isEmpty()){
               return listItems
           }



           var travellingItems = Constants.GlobalVariables.busesNoOrderFromDB.filter { it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravelling || it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravellingOverdue }
           val sortedItems = travellingItems.sortedWith(compareBy({ it.journeyState }, { it.nextEventTime }))

           updateOverdueJourneyStateIfNecessary(sortedItems)

           sortedItems.forEach(){

               if (it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravelling && DateUtils.isToday(it.nextEventTime.getTime()) ) {


                       var image_uri: Uri? = null
                       var initials = "" //either url or initials
                       val filename = LibraryFilesystem.getFileNameThumbnailImage(it.intUID)
                       if (LibraryFilesystem.exists(filename)) {
                           image_uri = LibraryFilesystem.getUriFromFilename(filename)
                       }
                       var summaryTitleLabel = LibraryJava.assignStateLabel(it);// "The Heading" //it.heading
                       var debugLabel = it.currentStopTitle() + " " + it.currentBusStopNumber + " of " + it.stopCount; //"The Sub Heading"// it.subhe ading


                       listItems.add(RecallItemRowItem(1,it.UID,it.busDepotUID,  it.journeyState, it.title,summaryTitleLabel, debugLabel ,"Time",image_uri,initials))




               }else  if ( it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravellingOverdue) {

                       var image_uri: Uri? = null
                       var initials = "" //either url or initials
                       val filename = LibraryFilesystem.getFileNameThumbnailImage(it.intUID)
                       if (LibraryFilesystem.exists(filename)) {
                           image_uri = LibraryFilesystem.getUriFromFilename(filename)
                       }
                       var summaryTitleLabel = LibraryJava.assignStateLabel(it);// "The Heading" //it.heading
                       var debugLabel = it.currentStopTitle() + " " + it.currentBusStopNumber + " of " + it.stopCount; //"The Sub Heading"// it.subhe ading



                       listItems.add(RecallItemRowItem(1,it.UID,it.busDepotUID,  it.journeyState, it.title,summaryTitleLabel, debugLabel ,"Time",image_uri,initials))


               }
           }

           return listItems

       }
       fun getTodayDataRI()  : ArrayList<RecallItem>{


           //val resources = MyApplication.getAppContext().resources
           val listItems: ArrayList<RecallItem> = ArrayList()

           //val finalList: ArrayList<RecallItemRowItem> = ArrayList()

           if (Constants.GlobalVariables.busesNoOrderFromDB.isEmpty()){
               return listItems
           }



           var travellingItems = Constants.GlobalVariables.busesNoOrderFromDB.filter { it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravelling || it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravellingOverdue }
           val sortedItems = travellingItems.sortedWith(compareBy({ it.journeyState }, { it.nextEventTime }))

           updateOverdueJourneyStateIfNecessary(sortedItems)

           sortedItems.forEach(){

               if (it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravelling && DateUtils.isToday(it.nextEventTime.getTime()) ) {

                   listItems.add(it)

               }else  if ( it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravellingOverdue) {

                   listItems.add(it)

               }
           }

           return listItems

       }
   }



}