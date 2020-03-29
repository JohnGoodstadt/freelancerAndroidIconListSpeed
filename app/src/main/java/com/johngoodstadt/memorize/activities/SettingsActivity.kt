package com.johngoodstadt.memorize.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.johngoodstadt.memorize.R
import com.johngoodstadt.memorize.adapters.SettingsRecyclerViewAdapter
import com.johngoodstadt.memorize.models.RecallItem
import com.johngoodstadt.memorize.models.Settings
import com.johngoodstadt.memorize.models.SettingsSections
import com.johngoodstadt.memorize.utils.Constants
import com.johngoodstadt.memorize.utils.interfaces.ItemClicked
import kotlinx.android.synthetic.main.activity_settings.*
import java.util.*

class SettingsActivity : AppCompatActivity(),ItemClicked {



    var headers:Array<String> = arrayOf()

    override fun showMessage(msg: String?) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()

       // val headers = resources.getStringArray(R.array.settings_header)

        when(msg){
            headers.get(6) ->{
//                print("Alarm")
                val intent = Intent(this@SettingsActivity, AlarmActivity::class.java)
                startActivity(intent)
            }
            headers.last() ->{
//                print("Notif")
                execNotification()
            }
        }


    }

    val ITEMS: MutableList<Any> = ArrayList()
    val DRAWABLE_ARR: IntArray = Constants.SETTINGS_DRAWABLE_ARR



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setUpData()
        setUpUI()
        setUpEvents()
    }

    private fun setUpEvents() {
        back.setOnClickListener {
            finish()
        }
    }

    private fun setUpUI() {

        with(settings_rv) {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            adapter = SettingsRecyclerViewAdapter(
                ITEMS,
                this@SettingsActivity
            )
        }
    }

    private fun setUpData() {

        val sections = arrayOf("User Settings","Developer Settings")
        this.headers = arrayOf(
            "Logged Out",
            "Timings",
            "Support",
            "You Tube - Instructions",
            "7 Day Activity",
            getSummary(),
            "Alarm Tester",
            "Notification Tester"
        )
        val sub_headers = arrayOf(
            "You are not logged in",
            "Manage timings - (20 minutes,1 hour, 1 day etc)",
            "Go to Facebook Messenger to ask any question. Are you stuck? Don\\'t know what to do next? Chat to me.",
            "Full Usage explained by Alexa and Brian",
            "Displays what you have done in last 7 days",
            getSummaryDetail(),
            "Test that Alarms work",
            "Send out a notification"
        )

        val sectionsOld = resources.getStringArray(R.array.settings_sections)
        val headersOld = resources.getStringArray(R.array.settings_header)
        val sub_headersOld = resources.getStringArray(R.array.settings_sub_header)


        setData(sections, headers, sub_headers)
    }
    private fun  getSummary(): String {


        var groupTitle = if (Constants.GlobalVariables.groups.count() == 1) "Group" else "Groups"
        if (Constants.whichApp.isRunning === Constants.whichApp.target.music) {
            groupTitle = if (Constants.GlobalVariables.groups.count() == 1) "Piece" else "Pieces"
        }

        var busTitle = if (Constants.GlobalVariables.groups.count() == 1) "Memory" else "Memories"
        if (Constants.whichApp.isRunning === Constants.whichApp.target.music) {
            busTitle = if (Constants.GlobalVariables.groups.count() == 1) "Phrase" else "Phrases"
        }else if (Constants.whichApp.isRunning === Constants.whichApp.target.legaldemo) {
            busTitle = if (Constants.GlobalVariables.groups.count() == 1) "Case" else "Cases"
        }




        //val groups = Constants.GlobalVariables.groups

        return "${Constants.GlobalVariables.groups.count()} ${groupTitle}"
    }
    private fun  getSummaryDetail(): String {

        var busTitle = if (Constants.GlobalVariables.groups.count() == 1) "Memory" else "Memories"
        if (Constants.whichApp.isRunning === Constants.whichApp.target.music) {
            busTitle = if (Constants.GlobalVariables.groups.count() == 1) "Phrase" else "Phrases"
        }else if (Constants.whichApp.isRunning === Constants.whichApp.target.legaldemo) {
            busTitle = if (Constants.GlobalVariables.groups.count() == 1) "Case" else "Cases"
        }
        val arraySetup = Constants.GlobalVariables.busesNoOrderFromDB.filter() { it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateInDepotSetup }.count()
        val arrayLearning = Constants.GlobalVariables.busesNoOrderFromDB.filter() { it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateInDepotLearning }.count()
        val activeCount = Constants.GlobalVariables.busesNoOrderFromDB.filter() { it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravelling || it.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravellingOverdue  }.count()

        return "${Constants.GlobalVariables.busesNoOrderFromDB.count()} ${busTitle}\n\t${arraySetup} in Setup\n\t${arrayLearning} Learning\n\t${activeCount} Active"
    }
    fun setData(
        sections: Array<String>,
        headers: Array<String>,
        subHeaders: Array<String>
    ) {
        val count = headers.size
        ITEMS.add(SettingsSections(section = sections[0]))
        for (i in 0..count - 1) {

            if (i == 6) {
                ITEMS.add(SettingsSections(section = sections[1]))

            }
            ITEMS.add(
                Settings(
                    header = headers[i],
                    sub_heading = subHeaders[i],
                    image_url = DRAWABLE_ARR[i]
                )
            )


        }
    }

    private fun execNotification(){


    }



}
