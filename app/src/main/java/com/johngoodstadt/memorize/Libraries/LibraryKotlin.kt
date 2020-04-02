package com.johngoodstadt.memorize.Libraries

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.format.DateUtils
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.johngoodstadt.memorize.R
import com.johngoodstadt.memorize.models.RecallItem

import com.johngoodstadt.memorize.utils.Constants
import com.johngoodstadt.memorize.utils.writeTextOnDrawable
import com.johngoodstadt.memorize.viewmodels.TodayListViewModel
import java.io.ByteArrayOutputStream
import java.util.*


fun getItemFromRecallGroupUID(busDepotUID: String?, recallItemUID: String?): RecallItem? {

    val recallGroups = Constants.GlobalVariables.groups.filter { it.UID == busDepotUID }


    if (recallGroups.count() > 0) {
        val recallGroup = recallGroups.first()

        return recallGroup.getItem(recallItemUID)

    }

    return null //only null return
}
fun getItemFromAllRecallItems(UID: String?): RecallItem? {

    var returnValue:RecallItem? = null

    val recallItems = Constants.GlobalVariables.busesNoOrderFromDB.filter { it.UID == UID }


    if (recallItems.count() > 0) {
        returnValue = recallItems.first()
    }

    return returnValue
}
fun updateOverdueJourneyStateIfNecessary(list:List<RecallItem>): Boolean {

    var returnValue = false

    for (ri in list){
        if (ri.isBusWaitingOverdue()) {

            ri.journeyState = RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravellingOverdue
            //TODO: cannot update DB here - Do i need to or is this cal only?
            returnValue = true
        }
    }

    return returnValue

}

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
fun listAlarms(context: Context) {
    val am =  context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    var aci = am.nextAlarmClock
    if (aci == null){
        println("No Alarm Clocks")
        return
    }

    while (aci != null) {
        Log.d("Alarm", aci.showIntent.toString())
        Log.d("Alarm", String.format("Trigger time: %d", aci.triggerTime))
        aci = am.nextAlarmClock
    }

}


fun getNotficationItems()  : List<RecallItem> {

    return Constants.GlobalVariables.busesNoOrderFromDB.filter { it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravelling || it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravellingOverdue }

}
fun iconAsInitialsIfNecessary(image_uri: Uri?, initials: String?): Uri? {

    var returnValue = image_uri

    if (image_uri == null && initials.isNullOrEmpty() == false) {

        //TODO: Freelancer speed test - comment and uncomment this line
        return returnValue


        val icon =  MyApplication.getAppContext().writeTextOnDrawable(R.drawable.green_circle, initials)
        returnValue = getImageUriFromBitmap(MyApplication.getAppContext(), icon.bitmap)





        //TODO: speed test
//        return returnVaue

    }else{
        return returnValue
    }

    return returnValue
}
private fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri?{

    //var image_uri: Uri?

    val bytes = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)

    val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)

//TODO: temp perfmance line
//    if (LibraryFilesystem.exists(path)) {
//        return null
//    }

    if (path == null){
        Log.e("","WARNING: path to initial image is null")
        return null
    }


    return Uri.parse(path.toString())
}

fun launchTomorrowNotificationNow(context:Context){


    val list = TodayListViewModel.getTodayDataRI()
    if(list.count() == 0){
        println("----> No items to recall today")
        return
    }




    val titles:Triple<String,String,String> = titlesForDailySummary(context, list)

    println("======> titles getting ready for daily ${titles.first},${titles.second} <======")




}


fun doesRouteHitToday(ri:RecallItem): Boolean {

    var returnValue = false

    if(isBusActive(ri)){
        return returnValue
    }

    if (ri.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravellingOverdue) {
        return true
    }

    if (ri.stops == null){
        if (ri.schemeTitles.length > 0) {
            ri.stops = ri.schemeTitles.split(",").toTypedArray()
        }
    }

    for (interval in ri.stops){
        val dateInterval = LibraryJava.StringToDateInterval(interval, ri.goTime)

        if (DateUtils.isToday(dateInterval.time)) {
            returnValue = true //only true exit
            break
        }


        if (isThisDateLaterThanToday(dateInterval) ) {
            break
        }
    }

    return returnValue
}
fun doesRouteHitTomorrow(ri:RecallItem): Boolean {

    var returnValue = false

    if(isBusActive(ri)){
        return returnValue
    }

    if (ri.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravellingOverdue) {
        return true
    }

    if (ri.stops == null){
        if (ri.schemeTitles.length > 0) {
            ri.stops = ri.schemeTitles.split(",").toTypedArray()
        }
    }

    for (interval in ri.stops){
        val dateInterval = LibraryJava.StringToDateInterval(interval, ri.goTime)

        if (LibraryJava.isDateTomorrow(dateInterval)) {
            returnValue = true //only true exit
            break
        }


        if (isThisDateLaterThanTomorrow(dateInterval) ) {
            break
        }
    }

    return returnValue
}
fun isThisDateLaterThanToday(date:Date): Boolean {

    var dt = Date()
    val c = Calendar.getInstance()
    c.time = dt

    val today = c.time

    if (today.after(date)) {
        return true
    }

    return false

}
fun isThisDateLaterThanTomorrow(date:Date): Boolean {

    var dt = Date()
    val c = Calendar.getInstance()
    c.time = dt
    c.add(Calendar.DATE, 1)


    val tomorrow = c.time

    if (tomorrow.after(date)) {
        return true
    }

    return false

}
fun getTomorrowAt8(): Date {

    // today
    val date: Calendar = GregorianCalendar()


//    //TODO: remove this debug line
//    date.add(GregorianCalendar.SECOND, 10)
//    return date.time

    date[Calendar.HOUR_OF_DAY] = 8
    date[Calendar.MINUTE] = 0
    date[Calendar.SECOND] = 0


    // tomorrow
    date.add(Calendar.DAY_OF_MONTH, 1)
    return date.time
}
fun isBusActive(ri:RecallItem): Boolean {

    var returnValue = false

    if (ri.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateAtStop || ri.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravellingOverdue || ri.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravelling )  {
        returnValue = true
    }


    return returnValue
}
fun titlesForDailySummary(context: Context,phrases:ArrayList<RecallItem>): Triple<String,String,String>{



//    var thisCategory = RecallItemCategory_Enum.RecallItemCategoryWords
//    if (phrases.isEmpty() == false){
//        thisCategory = phrases.first().busCategory
//    }

    //default
    var title = "${phrases.count()} items are due for recall today."
    if(phrases.count() == 1){
        title = "1 item is due for recall today."
    }

    if (Constants.whichApp.isRunning === Constants.whichApp.target.music) {
        title = "${phrases.count()} phrases are due for recall today."
        if(phrases.count() == 1){
            title = "1 phrase is due for recall today."
        }
    }else if (Constants.whichApp.isRunning === Constants.whichApp.target.legaldemo) {
        title = "${phrases.count()} cases are due for recall today."
        if(phrases.count() == 1){
            title = "1 case is due for recall today."
        }
    }else if (Constants.whichApp.isRunning === Constants.whichApp.target.yoga) {
        title = "${phrases.count()} poses are due for recall today."
        if(phrases.count() == 1){
            title = "1 pose is due for recall today."
        }
    }

    var countOverdue = 0
    var countNotOverdue = 0
    var uniqueGroupNames:MutableSet<String> = mutableSetOf<String>()

    for (ri in phrases){
        if (ri.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravellingOverdue){
            countOverdue++
        }else{
            countNotOverdue++
        }


        ri.recallGroup?.let {rg ->
            uniqueGroupNames.add(rg.title)
        }
    }

    var subtitle = ""
    if (countOverdue > 0 && countNotOverdue > 0) {
        subtitle = "${countOverdue} overdue, ${countNotOverdue} ready for prompting"
    }else if (countOverdue > 0) {
        subtitle = "${countOverdue} are overdue."
        if (countOverdue == 1) {
            subtitle = "1 item is overdue."
        }
    }else {
        subtitle = "${countNotOverdue} are ready for prompting."
        if (countNotOverdue == 1) {
            subtitle = "1 item ready for prompting."
        }
    }



    var body = ""
    var first = true

    for (group in uniqueGroupNames) {

//    }

//    val group = uniqueGroupNames.iterator()
//    while (group.hasNext()) {
        if (first) {
            body = "${group}"
            first = false
        }else{
            body += "${body},${group}"
        }
    }

    return Triple(title,subtitle,body)




}

fun checkWritePermission(thisActivity: Activity): Boolean {

    if (ContextCompat.checkSelfPermission(thisActivity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED) {

        // Permission is not granted
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            return false
        } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(thisActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                Constants.MY_PERMISSIONS_REQUEST_READ_CONTACTS)

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
            return true
        }
    } else {
        // Permission has already been granted
        return true
    }

}