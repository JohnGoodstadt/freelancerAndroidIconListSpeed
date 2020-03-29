package com.johngoodstadt.memorize.Libraries

import android.net.Uri
import com.johngoodstadt.memorize.R
import com.johngoodstadt.memorize.models.RecallGroup
import com.johngoodstadt.memorize.models.RecallItem
import com.johngoodstadt.memorize.tools.toBitmap
import com.johngoodstadt.memorize.utils.Constants
import com.johngoodstadt.memorize.utils.writeTextOnDrawable


fun chooseGroupTitle(rg: RecallGroup): String {

    var returnValue = "No Entries"


    val entryCount = rg.itemList.count()
    if (rg.itemList.isNotEmpty()) {
        val typicalBus = rg.itemList.first()

        when (Constants.whichApp.isRunning) {
            Constants.whichApp.target.yoga -> {
                if (entryCount == 1) {
                    returnValue = "1 Asana"
                } else {
                    returnValue = "${entryCount} Asanas"
                }
            }
            Constants.whichApp.target.music -> {
                if (rg.stepNumber == RecallGroup.PIECE_STEP_NUMBER_ENUM.step_number2){

                    if (entryCount == 1) returnValue = "1 Piece" else returnValue = "${entryCount} Pieces"

                }else{

                    if (entryCount == 1) returnValue = "1 Phrase" else returnValue = "${entryCount} Phrases"

                }

            }
            else -> {
                if (entryCount == 1) {
                    returnValue = "1 Memory"
                } else {
                    returnValue = "${entryCount} Memories"
                }
            }
        }


    }


    return returnValue
}


fun groupSummarySubTitle(items: List<RecallItem>): String {

    var returnValue = "Not Active"


    if (items.isNotEmpty()) {
        returnValue = "Active"
    }



    return returnValue
}
///1. get thumbnail filename
///2. if it exists return URI
///3. if it does not exist write initials and return URI
public fun intUID_to_thumbnailUri(intUID: Int, text:String): Uri? {


    return null;

    val filename = LibraryFilesystem.getFileNameThumbnailImage(intUID)
    if (LibraryFilesystem.exists(filename)) {

        return LibraryFilesystem.getUriFromFilename(filename)

    } else {

        val new_drawable =
            MyApplication.getAppContext().writeTextOnDrawable(R.drawable.green_circle, text)
        LibraryFilesystem.writeImageFileToDIRECTORY_DOWNLOADS(new_drawable.toBitmap(), filename)

        return LibraryFilesystem.getUriFromFilename(filename)
    }


}
fun filename_to_thumbnailUri_of_ImageOr2CharInitials(filename: String, text:String): Uri? {

    return null;

    if (LibraryFilesystem.exists(filename)) {
        return LibraryFilesystem.getUriFromFilename(filename)
    } else {

        val new_drawable = MyApplication.getAppContext().writeTextOnDrawable(R.drawable.green_circle, text)
        LibraryFilesystem.writeImageFileToDIRECTORY_DOWNLOADS(new_drawable.toBitmap(), filename)

        return LibraryFilesystem.getUriFromFilename(filename)
    }


}
public fun write_text_to_thumbnailUri(intUID: Int, text:String) {

    val filename = LibraryFilesystem.getFileNameThumbnailImage(intUID)


    val new_drawable = MyApplication.getAppContext().writeTextOnDrawable(R.drawable.green_circle, text)
    LibraryFilesystem.writeImageFileToDIRECTORY_DOWNLOADS(new_drawable.toBitmap(), filename)





}