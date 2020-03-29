package com.johngoodstadt.memorize.viewmodels

import android.net.Uri
import android.util.Log
import androidx.databinding.ObservableInt
//import androidx.core.content.res.TypedArrayUtils.getString
//import android.provider.Settings.Global.getString
//import android.content.context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.johngoodstadt.memorize.Libraries.LibraryFilesystem
import com.johngoodstadt.memorize.Libraries.LibraryJava
import com.johngoodstadt.memorize.Libraries.MyApplication
import com.johngoodstadt.memorize.Libraries.updateOverdueJourneyStateIfNecessary
import com.johngoodstadt.memorize.R
import com.johngoodstadt.memorize.database.MusicPieceViewModel
import com.johngoodstadt.memorize.models.*
import com.johngoodstadt.memorize.utils.Constants
//import jdk.nashorn.internal.objects.NativeDate.getTime
import java.util.*
import javax.inject.Inject


class RecallGroupItemListViewModel @Inject constructor() : ViewModel() {

    lateinit var DB: MusicPieceViewModel
    lateinit var checked: Set<RecallItem>
    lateinit var unchecked: Set<RecallItem>
    //PART 1
    var buttonSelected: ObservableInt = ObservableInt(0)

    public lateinit var recallGroup: RecallGroup

    fun buttonClicked(position:Int){
        buttonSelected.set(position)
        when (position) {
            1 -> step1ButtonPressed()
            2 -> step2ButtonPressed()
            3 -> step3ButtonPressed()
        }
    }

    private fun step1ButtonPressed() {

        recallGroup.goToStep1()
        DB.let {
            for (ri in recallGroup.itemList){
                DB.update(ri)
            }
            DB.update(recallGroup)
            getPhraseListforPiece(recallGroup)
        }
    }


    private fun step2ButtonPressed() {

//        recallGroup.goToStep2()
//        DB.let {
//            // there will only be 1 phrase
//            DB.update(recallGroup.itemList.first())
//            DB.update(recallGroup)
//            getPhraseListforPiece(recallGroup)
//        }


    }
//TODO: not used
    private fun step3ButtonPressed() {
        Log.d("memorize","polish clicked")

        //recallGroup.goToStep3()
        DB.let {

        }

    }


    //TRY NO LIVE DATA

    fun getItemListforPiece(recallGroup: RecallGroup) : ArrayList<Any>{

        val resources = MyApplication.getAppContext().resources

        val finalList: ArrayList<Any> = ArrayList()

        if (recallGroup.itemList.isEmpty()){
            return finalList
        }

        var recallItemsInGroup = recallGroup.itemList //Constants.GlobalVariables.busesNoOrderFromDB.filter { it.busDepotUID == recallGroup }

        var setupItems = recallItemsInGroup.filter { it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateInDepotSetup }
        var learningItems = recallItemsInGroup.filter { it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateInDepotLearning }
        var travellingItems = recallItemsInGroup.filter { it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravelling || it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravellingOverdue }
        var waitingForOthers = recallItemsInGroup.filter { it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateWaitingForOthers }

        setupItems = setupItems.sortedBy { it.title }
        learningItems = learningItems.sortedBy { it.title }
        travellingItems = travellingItems.sortedBy { it.title }
        waitingForOthers = waitingForOthers.sortedBy { it.title }


        var image_uri: Uri?
        var initials = "" //either url or initials

        setupItems.forEach(){
            if (it.UID == setupItems.first().UID ){
                finalList.add(SectionHeader(recallGroup.UID, resources.getString(R.string.section_title_setup)))
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

            finalList.add(OneLineTableViewCell(1,it.UID,it.busDepotUID, it.journeyState, titleForLine,"Time",image_uri,initials))
        }



        learningItems.forEach(){
            if (it.UID == learningItems.first().UID ){
                finalList.add(SectionHeader(recallGroup.UID, resources.getString(R.string.section_title_learning)))
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

            finalList.add(OneLineTableViewCell(1,it.UID, it.busDepotUID, it.journeyState, titleForLine,"Time",image_uri,initials))
        }

        //Do overdue
        updateOverdueJourneyStateIfNecessary(travellingItems)
        var travellingItemsNotOverdue = recallItemsInGroup.filter { it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravelling  }
        var travellingItemsOverdue = recallItemsInGroup.filter { it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravellingOverdue }

        travellingItemsOverdue.forEach(){
            if (it.UID == travellingItemsOverdue.first().UID ){
                finalList.add(SectionHeader(recallGroup.UID, resources.getString(R.string.section_title_ready)))
            }


            image_uri = null//intUID_to_thumbnailUri(it.intUID,getBest2LetterTitle(it.title))
            val filename = LibraryFilesystem.getFileNameThumbnailImage(it.intUID)
            if (LibraryFilesystem.exists(filename)) {
                image_uri = LibraryFilesystem.getUriFromFilename(filename)
            }else{
                initials = LibraryJava.getSuffixLetter(it.title)
            }

            var summaryTitleLabel = LibraryJava.assignStateLabel(it);// "The Heading" //it.heading
            var debugLabel = it.currentStopTitle() + " " + it.currentBusStopNumber + " of " + it.stopCount; //"The Sub Heading"// it.subhe ading

            var titleForLine = it.title
            if (Constants.whichApp.isRunning == Constants.whichApp.target.music) {
                titleForLine = determineTitleFromStepNumber(it, titleForLine)
            }


            finalList.add(RecallItemRowItem(1,it.UID, it.busDepotUID, it.journeyState, titleForLine,summaryTitleLabel, debugLabel ,"Time",image_uri,initials))
        }
        travellingItemsNotOverdue.forEach(){
            if (it.UID == travellingItemsNotOverdue.first().UID ){
                finalList.add(SectionHeader(recallGroup.UID,  resources.getString(R.string.section_title_not_ready)  ))
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
            }

            var summaryTitleLabel = LibraryJava.assignStateLabel(it);// "The Heading" //it.heading
            var debugLabel = it.currentStopTitle() + " " + it.currentBusStopNumber + " of " + it.stopCount; //"The Sub Heading"// it.subhe ading



            finalList.add(RecallItemRowItem(1,it.UID, it.busDepotUID, it.journeyState, titleForLine,summaryTitleLabel, debugLabel ,"Time",image_uri,initials))
        }

        waitingForOthers.forEach(){

            //only for music app
            if (Constants.whichApp.isRunning == Constants.whichApp.target.music) {

                if (it.UID == waitingForOthers.first().UID ){
                    finalList.add(SectionHeader(recallGroup.UID, resources.getString(R.string.section_title_waiting_for_others)))
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
                finalList.add(WaitingForOrhersTableViewCell(1,it.UID, it.busDepotUID, it.journeyState, titleForLine,summaryTitleLabel,"",image_uri,initials))

            }

        }

        return finalList

    }


    //PART 2

    var recallItemList: MutableLiveData<ArrayList<Any>> = MutableLiveData<ArrayList<Any>>()


    fun getPhraseListforPiece(recallGroup: RecallGroup) {

        val resources = MyApplication.getAppContext().resources

        if (recallGroup.itemList.isEmpty()){
            return
        }

        var recallItemsInGroup = recallGroup.itemList //Constants.GlobalVariables.busesNoOrderFromDB.filter { it.busDepotUID == recallGroup }

        var setupItems = recallItemsInGroup.filter { it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateInDepotSetup }
        var learningItems = recallItemsInGroup.filter { it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateInDepotLearning }
        var travellingItems = recallItemsInGroup.filter { it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravelling || it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravellingOverdue }
        var waitingForOthers = recallItemsInGroup.filter { it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateWaitingForOthers }

        setupItems = setupItems.sortedBy { it.title }
        learningItems = learningItems.sortedBy { it.title }
        travellingItems = travellingItems.sortedBy { it.title }
        waitingForOthers = waitingForOthers.sortedBy { it.title }

        val finalList: ArrayList<Any> = ArrayList()
        var image_uri: Uri?
        var initials = "" //either url or initials

        setupItems.forEach(){
            if (it.UID == setupItems.first().UID ){
                finalList.add(SectionHeader(recallGroup.UID, resources.getString(R.string.section_title_setup)))
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

            finalList.add(OneLineTableViewCell(1,it.UID,it.busDepotUID, it.journeyState, titleForLine,"Time",image_uri,initials))
        }



        learningItems.forEach(){
            if (it.UID == learningItems.first().UID ){
                finalList.add(SectionHeader(recallGroup.UID, resources.getString(R.string.section_title_learning)))
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

            finalList.add(OneLineTableViewCell(1,it.UID, it.busDepotUID, it.journeyState, titleForLine,"Time",image_uri,initials))
        }

        //Do overdue
        updateOverdueJourneyStateIfNecessary(travellingItems)
        var travellingItemsNotOverdue = recallItemsInGroup.filter { it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravelling  }
        var travellingItemsOverdue = recallItemsInGroup.filter { it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravellingOverdue }

        travellingItemsOverdue.forEach(){
            if (it.UID == travellingItemsOverdue.first().UID ){
                finalList.add(SectionHeader(recallGroup.UID, resources.getString(R.string.section_title_ready)))
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



            finalList.add(RecallItemRowItem(1,it.UID, it.busDepotUID, it.journeyState, titleForLine,summaryTitleLabel, debugLabel ,"Time",image_uri,initials))
        }
        travellingItemsNotOverdue.forEach(){
            if (it.UID == travellingItemsNotOverdue.first().UID ){
                finalList.add(SectionHeader(recallGroup.UID,  resources.getString(R.string.section_title_not_ready)  ))
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



            finalList.add(RecallItemRowItem(1,it.UID, it.busDepotUID, it.journeyState, titleForLine,summaryTitleLabel, debugLabel ,"Time",image_uri,initials))
        }

        //music only
        waitingForOthers.forEach(){

            //only for music app
            if (Constants.whichApp.isRunning == Constants.whichApp.target.music) {

                if (it.UID == waitingForOthers.first().UID ){
                    finalList.add(SectionHeader(recallGroup.UID, resources.getString(R.string.section_title_waiting_for_others)))
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
                finalList.add(WaitingForOrhersTableViewCell(1,it.UID, it.busDepotUID, it.journeyState, titleForLine,summaryTitleLabel,"",image_uri,initials))

            }

        }

        return recallItemList.postValue(finalList)

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