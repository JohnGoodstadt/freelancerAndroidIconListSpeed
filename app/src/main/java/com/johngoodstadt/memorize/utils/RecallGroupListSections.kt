package com.johngoodstadt.memorize.utils

import android.net.Uri
import com.johngoodstadt.memorize.Libraries.*
import com.johngoodstadt.memorize.models.SectionHeader
import com.johngoodstadt.memorize.models.RecallItem
import com.johngoodstadt.memorize.models.ReadyForReviewTableViewCell
import com.johngoodstadt.memorize.models.RecallGroup
import java.util.*

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 */
object RecallGroupInSections {

    /**
     * An array of sample (dummy) items.
     */
    val ITEMS: MutableList<Any> = ArrayList()

    private val COUNT = 8

    init {

        calcItems()

    }

    fun calcItems(){

        ITEMS.clear()

//        val recallGroups = Constants.GlobalVariables.groups

        val recallGroups = Constants.GlobalVariables.groups.sortedBy { it.title }

        for (recallGroup in recallGroups){
            ITEMS.add(SectionHeader(recallGroup.UID,recallGroup.title.toUpperCase()))
            var setup_count=0
            var learning_count=0
            var due_count=0
            var phrases_count=0
            var initials = "" //either url or initials

            //TODO inefficient filter from global busesNoOrderFromDB
//            val recallItemsForThisGroup = Constants.GlobalVariables.busesNoOrderFromDB.filter { it.busDepotUID == recallGroup.UID }
            val recallItemsForThisGroup = recallGroup.itemList
            val main_group_title = chooseGroupTitle(recallGroup)

            val subheading_title = groupSummarySubTitle(recallItemsForThisGroup)


            var image_uri:Uri? = null
            if (!recallGroup.itemList.isEmpty()){
                recallGroup.itemList.first().let{
                    val filenameThumbnail = LibraryFilesystem.getFileNameThumbnailImage(it.intUID)
                    if (LibraryFilesystem.exists(filenameThumbnail) ){
                        image_uri = LibraryFilesystem.getUriFromFilename(filenameThumbnail)
                    }else{
                        initials = LibraryJava.getBest2LetterTitle(recallGroup.title)
                    }
                }

            }else{
                //do initials here

                //if (Constants.whichApp.isRunning == Constants.whichApp.target.music) {
                    initials = LibraryJava.getBest2LetterTitle(recallGroup.title)
                //}
            }

                var typicalIntUID = 0
                if (recallItemsForThisGroup.count() > 0){
                    typicalIntUID = recallItemsForThisGroup.first().intUID
                }

                if (recallGroup.stepNumber != RecallGroup.PIECE_STEP_NUMBER_ENUM.step_number2){
                    for (ri in recallItemsForThisGroup){
                        phrases_count++
                        if (ri.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateInDepotSetup)
                            setup_count++
                        if (ri.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateInDepotLearning)
                            learning_count++
                        if (ri.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravelling)
                            due_count++
                        if (ri.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravellingOverdue)
                            due_count++
                    }
                }


            if (recallGroup.stepNumber == RecallGroup.PIECE_STEP_NUMBER_ENUM.step_number2){
                val i = ReadyForReviewTableViewCell(
                    recallGroup.busDepotID,
                    recallGroup.title,
                    heading = main_group_title,
                    subheading = subheading_title,
                    row_1 ="",
                    row_2 = "",
                    row_3 = "",
                    image_path = image_uri,
                    UID = recallGroup.UID,
                    initials = initials
                )
                ITEMS.add(i)
            }else{
                val i = ReadyForReviewTableViewCell(
                    recallGroup.busDepotID,
                    recallGroup.title,
                    heading = main_group_title,
                    subheading = subheading_title,
                    row_1 = setup_count.toString() + " in setup",
                    row_2 = learning_count.toString() + " learning",
                    row_3 = due_count.toString() + " due now",
                    image_path = image_uri,
                    UID = recallGroup.UID,
                    initials = initials
                )
                ITEMS.add(i)
            }

           // print("debug")



        }

    }


}
