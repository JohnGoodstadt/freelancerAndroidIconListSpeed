package com.johngoodstadt.memorize.activities

//import kotlinx.android.synthetic.yoga.alert_dialog_get_group_name.view.*


//import kotlinx.android.synthetic.student.alert_dialog_get_group_name.view.*
import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.CountDownTimer
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.analytics.FirebaseAnalytics
import com.johngoodstadt.memorize.BuildConfig
import com.johngoodstadt.memorize.FRED
import com.johngoodstadt.memorize.Libraries.LibraryFilesystem
import com.johngoodstadt.memorize.Libraries.LibraryJava
import com.johngoodstadt.memorize.Libraries.LibraryJava.DatabaseExists
import com.johngoodstadt.memorize.Libraries.checkWritePermission
import com.johngoodstadt.memorize.R
import com.johngoodstadt.memorize.database.AppDatabase
import com.johngoodstadt.memorize.database.MainViewModel
import com.johngoodstadt.memorize.fragments.RecallGroupsTabFragment
import com.johngoodstadt.memorize.fragments.TodayTabFragment
import com.johngoodstadt.memorize.fragments.TodayTabFragment.OnListFragmentInteractionListener
import com.johngoodstadt.memorize.models.*

import com.johngoodstadt.memorize.ui.main.SectionsPagerAdapter
import com.johngoodstadt.memorize.utils.Constants
import com.johngoodstadt.memorize.utils.Constants.ALARM_KEY_DAILY
import com.johngoodstadt.memorize.utils.Constants.ARG_PARAM_UID
import com.johngoodstadt.memorize.utils.Constants.ARG_PARAM_busDepotUID
import com.johngoodstadt.memorize.utils.Constants.GROUP_NAME_KEY
import com.johngoodstadt.memorize.utils.Constants.MAIN_TAB_GROUPS
import com.johngoodstadt.memorize.utils.Constants.MAIN_TAB_TODAY
import com.johngoodstadt.memorize.utils.Constants.NOTIF_KEY_ID
import com.johngoodstadt.memorize.utils.RecallGroupInSections
import com.johngoodstadt.memorize.utils.TodayItemsList
import com.johngoodstadt.memorize.viewmodels.TodayListViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.alert_dialog_get_group_name.view.*
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.math.abs

//import sun.jvm.hotspot.utilities.IntArray


class MainActivity : AppCompatActivity(), RecallGroupsTabFragment.OnListFragmentInteractionListener,

    OnListFragmentInteractionListener {

    var plusDisabled: Boolean = false
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    //test coroutines
    private val coroutineScope = CoroutineScope(Dispatchers.IO)


    //The view Model for the DB
    private lateinit var mainViewModel: MainViewModel
    var tableRefreshTimer = Timer()


//    constructor() : super()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContentView(R.layout.activity_main)


        if(BuildConfig.DEBUG){
            println("DEBUG version")
        }else{
            println("RELEASE version")
        }


        //TODO: Temp call as bitmap icons need permission
        if (checkWritePermission(this)) {
          println("Already has permission")
        } else {

            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                Constants.RequestCodes.REQUEST_WRITE_STORAGE_PERMISSION
            )
        }

        setUpUI()
        setUpEvents()


        checkBuildFlavor()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)


        println("in MAIN ACTIVITY.onCreate()")


        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)


        var initial_tab = MAIN_TAB_GROUPS
        val UID = intent?.getStringExtra(NOTIF_KEY_ID)


        if (UID != null && UID == ALARM_KEY_DAILY){
            //This is NOT first load - but notification call

            println(UID)
            initial_tab = MAIN_TAB_TODAY
            view_pager.currentItem = initial_tab
        }else{
            //TODO: assumes this is first load - not notification call

            //TODO: Could check for userDefaults here?


            if (Constants.whichApp.isRunning === Constants.whichApp.target.legaldemo) {

                if (DatabaseExists(applicationContext) == false){ //first run
                    println("===> RUNNING LEGAL AND NO DB - so copy it it now")
                    LibraryJava.DatabaseCopier(applicationContext)
                }
            }
            //Load all data from sqlite
            loadFromLocal(initial_tab)
        }



    }

    private fun doStartupTasks() {

        //TODO: Freelancer write first icon
        val green_circle = BitmapFactory.decodeResource(getResources(), R.drawable.green_circle)
        LibraryFilesystem.createImageThumbnail(12345,green_circle)

        startJAVABroadcastTimer(10)


    }



    override fun onAttachFragment(fragment: Fragment) {

        super.onAttachFragment(fragment)

        if (fragment is TodayTabFragment) {
            fragment.refreshAdapter()
            println("TodayFragment")
        }else if (fragment is RecallGroupsTabFragment) {
            println("PiecesFragment")
        }

    }
    //region fragments
    override fun onListFragmentInteraction(item: RecallItemRowItem) {




    }

    override fun onListFragmentInteraction(item: ReadyForReviewTableViewCell) {



        if (Constants.whichApp.isRunning == Constants.whichApp.target.music) {

            val intent = Intent(this@MainActivity, MusicPieceActivity::class.java)
            intent.putExtra(ARG_PARAM_busDepotUID, item.UID)

            startActivityForResult(intent, Constants.RequestCodes.REQUEST_GROUP)

        } else {
            val intent = Intent(this@MainActivity, RecallGroupActivity::class.java)
            intent.putExtra(ARG_PARAM_busDepotUID, item.UID)

            startActivityForResult(intent, Constants.RequestCodes.REQUEST_GROUP)




        }


    }

    //endregion

    private val mMessageReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            val msg = intent?.getStringExtra("message")
            Toast.makeText(this@MainActivity, "Refresh on MainActivity", Toast.LENGTH_SHORT).show()

            Log.e("broadcast", "MainActivity.onReceive()")


            refreshDataAndView()



        }

    }


    private fun checkBuildFlavor() {
        Log.d("app id", BuildConfig.APPLICATION_ID)


//        if(BuildConfig.APPLICATION_ID.equals("com.johngoodstadt.memorize.free")){
        if (Constants.whichApp.isRunning == Constants.whichApp.target.music) {
            Log.e("music", "this is free version")
        } else if (Constants.whichApp.isRunning == Constants.whichApp.target.yoga) {
            Log.e("YOGA", "this is YOGA version")
        } else {
            Log.e("music", "this is paid version")

        }

        Log.d("product flavor", BuildConfig.FLAVOR)
    }



    //region timer
    private fun startJAVABroadcastTimer(interval:Long) {

        var millis = interval * 1000

        this.tableRefreshTimer = Timer()

        //Set the schedule function
        tableRefreshTimer.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    sendNotification()
                }
            },
            10, millis //10 is a delay for async things op finish at start up
        )


    }

    private fun startBroadcastTimer() {

        object : CountDownTimer(1000000, 10000) {

            override fun onTick(millisUntilFinished: Long) {
                Log.e("refresh", "onTick")

                sendNotification()
            }

            override fun onFinish() {


                sendNotification()


            }
        }.start()
    }
    private fun sendNotification() {

        val intent = Intent(Constants.BROADCAST_RECEIVER_INTENT)
        intent.putExtra("message", "refresh from Main Activity")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)


    }
    override fun onResume() {
        super.onResume()

        println("in MAIN ACTIVITY.onResume()")

        LocalBroadcastManager.getInstance(this).registerReceiver(
            mMessageReceiver,
            IntentFilter(Constants.BROADCAST_RECEIVER_INTENT)
        )
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)

    }
    //endregion


    private fun setUpUI() {

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        view_pager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(view_pager)




        tabs.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

                if (tab.position == 0){
                    RecallGroupInSections.calcItems()
                    refreshGroupList()
                }else{
                    TodayItemsList.calcItems()
                    refreshTodayList()
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}

        })

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)



        if (requestCode == Constants.RequestCodes.REQUEST_PIECE_ADDED && resultCode == Activity.RESULT_OK) {

            Log.w("fred", "MainActivity.onActivityResult()")

            data?.let {
                val group_name = it.getStringExtra(GROUP_NAME_KEY)


                addGroupToMemoryAndLocal(group_name)

                refreshDataAndView()



            }


        }else{
            refreshDataAndView() //was missing some cases - so just always refresh

        }


    }

    private fun refreshDataAndView() {



        if (view_pager.currentItem == MAIN_TAB_GROUPS) {
            //data
            RecallGroupInSections.calcItems()
            //view
            refreshGroupList()
        }else{
            //data
            TodayItemsList.calcItems()
            //view
            refreshTodayList()
        }

    }

    // add to global memory
    // add to local back up
    private fun addGroupToMemoryAndLocal(group_name: String) {

//        Log.e("BUG","group :" + group_name)

        val rg = RecallGroup(group_name)

        Constants.GlobalVariables.groups.add(rg)

        mainViewModel.insert(rg)
    }

    private fun setUpEvents() {
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                //Log.d("position", position.toString())
                DisableEnablePlus(position)

            }

            override fun onPageSelected(position: Int) {
                DisableEnablePlus(position)

               when (position){
                   0 ->{
                       refreshGroupList()
                   }
                   1 -> {
                       refreshTodayList()
                   }
               }


            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
        add.setOnClickListener {


            if (!plusDisabled) {

                if (Constants.whichApp.isRunning == Constants.whichApp.target.music) {
                    val intent = Intent(this@MainActivity, AddPieceActivity::class.java)
                    startActivityForResult(intent, Constants.RequestCodes.REQUEST_PIECE_ADDED)
                }else{
                    //Inflate the dialog with custom view


                    val dialog_title = "Add Group"
                    val message = "Add a new group name that you can add asanas to"

                    val mDialogView = LayoutInflater.from(this)
                        .inflate(R.layout.alert_dialog_get_group_name, null)
                    //AlertDialogBuilder
                    val mBuilder = AlertDialog.Builder(this)
                        .setView(mDialogView)
                        .setTitle(dialog_title)
                        .setMessage(message)

                    val mAlertDialog = mBuilder.show()
                    //login button click of custom layout
                    mDialogView.dialogOKButton.setOnClickListener {
                        //dismiss dialog
                        mAlertDialog.dismiss()
                        //get text from EditTexts of custom layout
                        val groupName = mDialogView.dialogNameEt.text.toString()//.toUpperCase()
                        val recallGroup = RecallGroup(groupName)

                        mainViewModel.insert(recallGroup)

                        Constants.GlobalVariables.groups.add(RecallGroup(groupName))
                        RecallGroupInSections.calcItems()


                    }
                    //cancel button click of custom layout
                    mDialogView.dialogCancelBtn.setOnClickListener {
                        mAlertDialog.dismiss()
                    }
                }



            }


        }
        settings.setOnClickListener {

            Toast.makeText(this, "Settings button pressed",Toast.LENGTH_SHORT).show()



        }
    }



    fun DisableEnablePlus(position: Int) {
        if (position == 1) {
            add.setImageResource(R.drawable.ic_action_add_disabled)
            plusDisabled = true
        } else {
            add.setImageResource(R.drawable.ic_action_add)
            plusDisabled = false
        }
    }


        suspend fun loadFromLocal_coroutines() = withContext(Dispatchers.IO) {

//TODO: if this called not on first load but from notif then do I need to load again?
            val db = AppDatabase.getDatabase(application)


            db.let { appdb ->
                Log.e("THREAD", "RECALL ITEM - LOADING")


                //1. read groups
                Constants.GlobalVariables.groups.clear()
                appdb?.recallGroupDAO()?.getAllRecallGroupsAsList()?.forEach()
                {
                    Log.e("Group", "UID:${it.UID}, title: ${it.title} ")
                    Constants.GlobalVariables.groups.add(it)
                }

                //2. read items
                var counter = 1
                Constants.GlobalVariables.busesNoOrderFromDB.clear()
                appdb?.recallItemDAO()?.getAllRecallItemsAsList()?.forEach()
                {


                    if (counter <= 10){
                        Log.e("Items", "${it.UID} : ${it.title}, ${it.intUID} : groupUID: ${it.busDepotUID}")
                        counter++
                    }


                    Constants.GlobalVariables.busesNoOrderFromDB.add(it)

                }



                Log.e(
                    "Fetch Records",
                    "group count: ${Constants.GlobalVariables.groups.count()}"
                )
                Log.e(
                    "Fetch Records",
                    "item  count: ${Constants.GlobalVariables.busesNoOrderFromDB.count()}"
                )

                Log.e("Fetch Records", "End of async load loadFromLocal_coroutines()" )
                Log.e("Fetch Records", Thread.currentThread().id.toString() )


                val lookupDict =  Constants.GlobalVariables.groups.associate { Pair(it.UID, it) }.toMap()


                //3. add items to correct groups
                for (ri in Constants.GlobalVariables.busesNoOrderFromDB){
                    if (ri.busDepotUID.isEmpty() == false){

                        val key = ri.busDepotUID

                        val rg = lookupDict.get(key)

                        rg?.let {
                            ri.recallGroup = it //ri points to group
                            it.addItem(ri)      //rg points to ri
                        }
                    }
                }

                if ( Constants.whichApp.isRunning == Constants.whichApp.target.music ){

                    val list = RecallGroup.moveToOtherListAllBusesAllCombinedPhrases(Constants.GlobalVariables.busesNoOrderFromDB)
                    Constants.GlobalVariables.busesNoOrderFromDB = ArrayList(list)


                }


                if ( Constants.whichApp.isRunning == Constants.whichApp.target.music ){
                    for (rg in Constants.GlobalVariables.groups){
                        rg.markCurrentStep()
                    }
                }





            }




        }



        fun loadFromLocal(whichTab:Int) {

            Log.e("ASNC", "launching")
            coroutineScope.launch(Dispatchers.IO) {
                loadFromLocal_coroutines()

                Log.e("ASYNC", "loaded")
                Log.e("ASYNC", "BEFORE runOnUiThread" )
                Log.e("ASYNC", Thread.currentThread().id.toString() )
                runOnUiThread {
                    Log.e("ASYNC", "AFTER  runOnUiThread" )
                    Log.e("ASYNC", Thread.currentThread().id.toString() )
                    sendNotification() //debugger can crash here due to not on main thread
                    view_pager.currentItem = whichTab

                    doStartupTasks() //DB i has been loaded when comes here


                }


            }

        }




    fun Drawable.toBitmap(): Bitmap {
        if (this is BitmapDrawable) {
            return bitmap
        }

        val width = if (bounds.isEmpty) intrinsicWidth else bounds.width()
        val height = if (bounds.isEmpty) intrinsicHeight else bounds.height()

        return Bitmap.createBitmap(width.nonZero(), height.nonZero(), Bitmap.Config.ARGB_8888).also {
            val canvas = Canvas(it)
            setBounds(0, 0, canvas.width, canvas.height)
            draw(canvas)
        }
    }

    private fun Int.nonZero() = if (this <= 0) 1 else this

    private fun refreshTodayList() {
        val list = supportFragmentManager.fragments
        for (f in list) {
            if (f is TodayTabFragment) {
                f.refreshAdapter()
                break
            }
        }
    }

    private fun refreshGroupList() {
        val list = supportFragmentManager.fragments
        for (f in list) {
            if (f is RecallGroupsTabFragment) {
                f.refreshAdapter()
                break
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.RequestCodes.REQUEST_STORAGE_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

            } else {

                println("Permission Denied")
            }
        } else if (requestCode == Constants.RequestCodes.REQUEST_WRITE_STORAGE_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

            } else {
                println("Permission Denied")
            }
        }
    }
}