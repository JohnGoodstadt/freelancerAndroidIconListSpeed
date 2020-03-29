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
object ItemsInGroupList {



    fun calcItems(recallGroup:RecallGroup) : List<Any>{

        val resources = MyApplication.getAppContext().resources

        var ITEMS: MutableList<Any> = ArrayList()


        if (recallGroup.itemList.isEmpty()){
            return ITEMS
        }

        var recallItemsInGroup = recallGroup.itemList

        var setupItems = recallItemsInGroup.filter { it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateInDepotSetup }
        var learningItems = recallItemsInGroup.filter { it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateInDepotLearning }
        var travellingItems = recallItemsInGroup.filter { it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravelling || it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravellingOverdue }
        var waitingForOthers = recallItemsInGroup.filter { it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateWaitingForOthers }

        setupItems = setupItems.sortedBy { it.title }
        learningItems = learningItems.sortedBy { it.title }
        travellingItems = travellingItems.sortedBy { it.title }
        waitingForOthers = waitingForOthers.sortedBy { it.title }

//        val finalList: ArrayList<Any> = ArrayList()
        var image_uri: Uri?
        var initials = "" //either url or initials

        //Do overdue
        updateOverdueJourneyStateIfNecessary(travellingItems)
        var travellingItemsNotOverdue = recallItemsInGroup.filter { it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravelling  }
        var travellingItemsOverdue = recallItemsInGroup.filter { it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravellingOverdue }

        travellingItemsOverdue.forEach(){
            if (it.UID == travellingItemsOverdue.first().UID ){
                ITEMS.add(SectionHeader(recallGroup.UID, resources.getString(R.string.section_title_ready)))
            }


            image_uri = null//intUID_to_thumbnailUri(it.intUID,getBest2LetterTitle(it.title))
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

            var titleForLine = it.title
            if (Constants.whichApp.isRunning == Constants.whichApp.target.music) {
                titleForLine = determineTitleFromStepNumber(it, titleForLine)
            }else{
                if (it.phrase.isEmpty() == false){
                    titleForLine = it.phrase
                }
            }



            ITEMS.add(RecallItemRowItem(1,it.UID, it.busDepotUID, it.journeyState, titleForLine,summaryTitleLabel, debugLabel ,"Time",image_uri,initials))
        }
        travellingItemsNotOverdue.forEach(){
            if (it.UID == travellingItemsNotOverdue.first().UID ){
                ITEMS.add(SectionHeader(recallGroup.UID,  resources.getString(R.string.section_title_not_ready)  ))
            }


            image_uri = null//intUID_to_thumbnailUri(it.intUID,getBest2LetterTitle(it.title))
            val filename = LibraryFilesystem.getFileNameThumbnailImage(it.intUID)
            if (LibraryFilesystem.exists(filename)) {
                image_uri = LibraryFilesystem.getUriFromFilename(filename)
            }else{
                // initials = LibraryJava.getSuffixLetter(it.title)
                if (Constants.whichApp.isRunning == Constants.whichApp.target.music) {
                    initials = LibraryJava.getSuffixLetter(it.title)
                }else{
                    initials = LibraryJava.getBest2LetterTitle(it.title)
                }
            }

            var titleForLine = it.title
            if (Constants.whichApp.isRunning == Constants.whichApp.target.music) {
                titleForLine = determineTitleFromStepNumber(it, titleForLine)
            }else{
                if (it.phrase.isEmpty() == false){
                    titleForLine = it.phrase
                }
            }

            var summaryTitleLabel = LibraryJava.assignStateLabel(it);// "The Heading" //it.heading
            var debugLabel = it.currentStopTitle() + " " + it.currentBusStopNumber + " of " + it.stopCount; //"The Sub Heading"// it.subhe ading



            ITEMS.add(RecallItemRowItem(1,it.UID, it.busDepotUID, it.journeyState, titleForLine,summaryTitleLabel, debugLabel ,"Time",image_uri,initials))
        }

        setupItems.forEach(){
            if (it.UID == setupItems.first().UID ){
                ITEMS.add(SectionHeader(recallGroup.UID, resources.getString(R.string.section_title_setup)))
            }

            image_uri = null

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


            var titleForLine = it.title
            if (Constants.whichApp.isRunning == Constants.whichApp.target.music) {
                titleForLine = determineTitleFromStepNumber(it, titleForLine)
            }

            ITEMS.add(OneLineTableViewCell(1,it.UID,it.busDepotUID, it.journeyState, titleForLine,"Time",image_uri,initials))
        }



        learningItems.forEach(){
            if (it.UID == learningItems.first().UID ){
                ITEMS.add(SectionHeader(recallGroup.UID, resources.getString(R.string.section_title_learning)))
            }

//            image_uri = intUID_to_thumbnailUri(it.intUID,getBest2LetterTitle(it.title))
            image_uri = null//intUID_to_thumbnailUri(it.intUID,getBest2LetterTitle(it.title))
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

            var titleForLine = it.title
            if (Constants.whichApp.isRunning == Constants.whichApp.target.music) {
                titleForLine = determineTitleFromStepNumber(it, titleForLine)
            }

            ITEMS.add(OneLineTableViewCell(1,it.UID, it.busDepotUID, it.journeyState, titleForLine,"Time",image_uri,initials))
        }



        //music only
        waitingForOthers.forEach(){

            //only for music app
            if (Constants.whichApp.isRunning == Constants.whichApp.target.music) {

                if (it.UID == waitingForOthers.first().UID ){
                    ITEMS.add(SectionHeader(recallGroup.UID, resources.getString(R.string.section_title_waiting_for_others)))
                }

                image_uri = null
                val filename = LibraryFilesystem.getFileNameThumbnailImage(it.intUID)
                if (LibraryFilesystem.exists(filename)) {
                    image_uri = LibraryFilesystem.getUriFromFilename(filename)
                }else{
                    initials = LibraryJava.getSuffixLetter(it.title)

                }

                val titleForLine = determineTitleFromStepNumber(it, it.title)

                var summaryTitleLabel = "Waiting for other phrases"
                ITEMS.add(WaitingForOrhersTableViewCell(1,it.UID, it.busDepotUID, it.journeyState, titleForLine,summaryTitleLabel,"",image_uri,initials))

            }

        }

        return ITEMS

    }
    private fun determineTitleFromStepNumber(
        it: RecallItem,
        titleForLine: String
    ): String {
        var titleForLine1 = titleForLine
        it.recallGroup.let {
            if (it.stepNumber == RecallGroup.PIECE_STEP_NUMBER_ENUM.step_number2) {
                titleForLine1 = LibraryJava.dropLetterSuffixFromTitle(titleForLine1)
            }
        }
        return titleForLine1
    }

}
