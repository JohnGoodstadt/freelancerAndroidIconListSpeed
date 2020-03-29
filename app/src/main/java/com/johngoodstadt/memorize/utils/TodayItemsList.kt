package com.johngoodstadt.memorize.utils

import android.net.Uri
import android.text.format.DateUtils
import com.johngoodstadt.memorize.Libraries.*
import com.johngoodstadt.memorize.R
import com.johngoodstadt.memorize.models.*
import java.util.*

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 */
object TodayItemsList {

    /**
     * An array of sample (dummy) items.
     */
    val ITEMS: MutableList<Any> = ArrayList()

    private val COUNT = 8

    init {

        calcItems()

    }

    fun calcItems(){

        val resources = MyApplication.getAppContext().resources

        ITEMS.clear()

        if (Constants.GlobalVariables.busesNoOrderFromDB.isEmpty()){
            return
        }


        val allTravellingItems = Constants.GlobalVariables.busesNoOrderFromDB.filter { it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravelling || it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravellingOverdue }

        updateOverdueJourneyStateIfNecessary(allTravellingItems)

        val travelling = allTravellingItems.filter { it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravelling }
        val overdue = allTravellingItems.filter { it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravellingOverdue }
        val travellingSorted = travelling.sortedWith(compareBy({ it.nextEventTime }))
        val overdueSorted = overdue.sortedWith(compareBy({ it.nextEventTime }))

        travellingSorted.forEach(){

            if (it.UID == travellingSorted.first().UID ){
                ITEMS.add(SectionHeader(it.UID, resources.getString(R.string.section_title_not_ready)))
            }

            var image_uri: Uri? = null
            var initials = "" //either url or initials
            val filename = LibraryFilesystem.getFileNameThumbnailImage(it.intUID)
            if (LibraryFilesystem.exists(filename)) {
                image_uri = LibraryFilesystem.getUriFromFilename(filename)
            }else{
                initials = LibraryJava.getBest2LetterTitle(it.title)
            }
            var summaryTitleLabel = LibraryJava.assignStateLabel(it);// "The Heading" //it.heading
            var debugLabel = it.currentStopTitle() + " " + it.currentBusStopNumber + " of " + it.stopCount; //"The Sub Heading"// it.subhe ading


            ITEMS.add(RecallItemRowItem(1,it.UID,it.busDepotUID,  it.journeyState, it.title,summaryTitleLabel, debugLabel ,"Time",image_uri,initials))

        }

        overdueSorted.forEach(){
            if (it.UID == overdueSorted.first().UID ){
                ITEMS.add(SectionHeader(it.UID, resources.getString(R.string.section_title_ready)))
            }
            var image_uri: Uri? = null
            var initials = "" //either url or initials
            val filename = LibraryFilesystem.getFileNameThumbnailImage(it.intUID)
            if (LibraryFilesystem.exists(filename)) {
                image_uri = LibraryFilesystem.getUriFromFilename(filename)
            }else{
                initials = LibraryJava.getBest2LetterTitle(it.title)
            }
            var summaryTitleLabel = LibraryJava.assignStateLabel(it);// "The Heading" //it.heading
            var debugLabel = it.currentStopTitle() + " " + it.currentBusStopNumber + " of " + it.stopCount; //"The Sub Heading"// it.subhe ading



            ITEMS.add(RecallItemRowItem(1,it.UID,it.busDepotUID,  it.journeyState, it.title,summaryTitleLabel, debugLabel ,"Time",image_uri,initials))

        }


    }


}
