package com.johngoodstadt.memorize.viewmodels

import android.net.Uri
//import androidx.core.content.res.TypedArrayUtils.getString
//import android.provider.Settings.Global.getString
//import android.content.context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.johngoodstadt.memorize.Libraries.LibraryFilesystem
import com.johngoodstadt.memorize.Libraries.LibraryJava
import com.johngoodstadt.memorize.Libraries.MyApplication
import com.johngoodstadt.memorize.R
import com.johngoodstadt.memorize.database.MusicPieceViewModel
import com.johngoodstadt.memorize.models.*
import com.johngoodstadt.memorize.utils.Constants
//import jdk.nashorn.internal.objects.NativeDate.getTime
import java.util.*
import javax.inject.Inject


class PolishingItemListViewModel @Inject constructor() : ViewModel() {

    lateinit var DB: MusicPieceViewModel
    lateinit var checked: Set<RecallItem>
    lateinit var unchecked: Set<RecallItem>
    //PART 1


    public lateinit var recallGroup: RecallGroup


    //TRY NO LIVE DATA

    fun getItemListforPiece(recallGroup: RecallGroup) : ArrayList<Any>{

        val resources = MyApplication.getAppContext().resources

        val finalList: ArrayList<Any> = ArrayList()

        if (recallGroup.itemList.isEmpty()){
            return finalList
        }

        //All items - hidden or not
        var recallItemsInGroup = recallGroup.itemList
        recallItemsInGroup.addAll(recallGroup.otheritemList)


        val allItems = recallItemsInGroup.sortedBy { it.title }



        var image_uri: Uri?
        var initials = "" //either url or initials


        allItems.forEach(){
            if (it.UID == allItems.first().UID ){
                finalList.add(SectionHeader(recallGroup.UID, resources.getString(R.string.section_title_all_phrases)))
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

            finalList.add(CheckedTableViewCell(1,it.UID,it.busDepotUID, it.journeyState, titleForLine,false,image_uri,initials))
        }




        return finalList

    }


    //PART 2

    var recallItemList: MutableLiveData<ArrayList<Any>> = MutableLiveData<ArrayList<Any>>()


    fun getItemListforPolishedItems(items: ArrayList<CheckedRecallItems>) {

        val resources = MyApplication.getAppContext().resources

        val finalList: ArrayList<Any> = ArrayList()



        //All items - hidden or not
        var recallItemsInGroup = items//recallGroup.itemList + recallGroup.otheritemList
//        recallItemsInGroup.addAll(recallGroup.otheritemList)


        val allItems = recallItemsInGroup.sortedBy { it.title }



        var image_uri: Uri?
        var initials = "" //either url or initials


        allItems.forEach(){
            if (it.UID == allItems.first().UID ){
                finalList.add(SectionHeader("1", resources.getString(R.string.section_title_all_phrases)))
            }

            image_uri = null

            val filename = LibraryFilesystem.getFileNameThumbnailImage(it.intUID)
            if (LibraryFilesystem.exists(filename)) {
                image_uri = LibraryFilesystem.getUriFromFilename(filename)
            }else{

                if (Constants.whichApp.isRunning == Constants.whichApp.target.music) {
                    initials = LibraryJava.getSuffixLetter(it.title)
                }
            }

            finalList.add(CheckedTableViewCell(1,it.UID,"99999", it.journeyState,  it.title,it.checked,image_uri,initials))
        }

        return recallItemList.postValue(finalList)

    }
    fun getItemListforPieceLiveData(recallGroup: RecallGroup) {

        val resources = MyApplication.getAppContext().resources

        val finalList: ArrayList<Any> = ArrayList()



        //All items - hidden or not
        var recallItemsInGroup = recallGroup.itemList + recallGroup.otheritemList
//        recallItemsInGroup.addAll(recallGroup.otheritemList)


        val allItems = recallItemsInGroup.sortedBy { it.title }



        var image_uri: Uri?
        var initials = "" //either url or initials


        allItems.forEach(){
            if (it.UID == allItems.first().UID ){
                finalList.add(SectionHeader(recallGroup.UID, resources.getString(R.string.section_title_all_phrases)))
            }

            image_uri = null

            val filename = LibraryFilesystem.getFileNameThumbnailImage(it.intUID)
            if (LibraryFilesystem.exists(filename)) {
                image_uri = LibraryFilesystem.getUriFromFilename(filename)
            }else{

                if (Constants.whichApp.isRunning == Constants.whichApp.target.music) {
                    initials = LibraryJava.getSuffixLetter(it.title)
                }
            }


            var titleForLine = it.title

            var checked = true
            if (it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateWaitingForOthers){
                checked = false
            }
            finalList.add(CheckedTableViewCell(1,it.UID,it.busDepotUID, it.journeyState, titleForLine,checked,image_uri,initials))
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