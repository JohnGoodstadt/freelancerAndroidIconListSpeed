package com.johngoodstadt.memorize.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.johngoodstadt.memorize.Libraries.LibraryFilesystem
import com.johngoodstadt.memorize.R
import com.johngoodstadt.memorize.fragments.CRUDLocalRecallItemViewModel
import com.johngoodstadt.memorize.fragments.PieceActionListDialogFragment
import com.johngoodstadt.memorize.models.PieceDetailBottom
import com.johngoodstadt.memorize.models.RecallGroup
import com.johngoodstadt.memorize.models.RecallItem
//import com.johngoodstadt.memorize.notifications.NotificationHelper
import com.johngoodstadt.memorize.utils.Constants
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_learn.*


class LearnNowActivity : AppCompatActivity(), PieceActionListDialogFragment.Listener {

    //passed in
    private var recallItemUID: String? = null
    private var busDepotUID: String? = null
    private lateinit var recallItem: RecallItem

    private var prompt_image_uri: Uri? = null
    private var main_image_uri: Uri? = null

    private var bottomlist: ArrayList<PieceDetailBottom> = ArrayList()

    //iVars
    private lateinit var localViewModel: CRUDLocalRecallItemViewModel
    private var try_now_image: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




        recallItemUID = intent.getStringExtra(Constants.ARG_PARAM_UID).guard { return }
        busDepotUID = intent.getStringExtra(Constants.ARG_PARAM_busDepotUID).guard { return }

        val recallItems = Constants.GlobalVariables.busesNoOrderFromDB.filter { it.UID == recallItemUID }
        if (recallItems.count() > 0 ){
            this.recallItem = recallItems.first()
            Log.e("===> item UID",this.recallItem.title + " " + this.recallItem.UID)
        }


        setContentView(R.layout.activity_learn)
        localViewModel = ViewModelProviders.of(this).get(CRUDLocalRecallItemViewModel::class.java)

        val orientation = resources.configuration.orientation
        if (savedInstanceState != null) {
           // try_now_image = savedInstanceState.getBoolean("try_now_image", true)
        }

        localViewModel = ViewModelProviders.of(this).get(CRUDLocalRecallItemViewModel::class.java)


        if (recallItem.recallGroup != null){
            recallItem.recallGroup.let {
                if (recallItem.recallGroup.stepNumber == RecallGroup.PIECE_STEP_NUMBER_ENUM.step_number2){
                    //TODO if it does not exist what to do
                    main_image_uri = LibraryFilesystem.getUriFromFirstScorePage(it.UID)
                }else{
                    main_image_uri = LibraryFilesystem.readMainImage(recallItem.intUID)
                }
            }
        }

        prompt_image_uri = LibraryFilesystem.readPromptImage(recallItem.intUID)

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            setUpUI()
            setUpEvents()
            setImagePortrait()
        } else {
            setImageLandscape()
        }
//        setUpBottomSheet()

    }

    private fun setUpUI() {

        pageTitleTextView.text = this.recallItem.title


    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
       // outState?.putBoolean("try_now_image", try_now_image)

    }


    fun setImagePortrait() {
        if (try_now_image) {
            btn_try.text = getString(R.string.try_it_now)
            learnImageView.setImageURI(main_image_uri)
        } else {
            btn_try.text = getString(R.string.view)
            learnImageView.setImageURI(prompt_image_uri)
        }
    }

    fun setImageLandscape() {
        if (try_now_image) {
            learnImageView.setImageURI(main_image_uri)
        } else {
            learnImageView.setImageURI(prompt_image_uri)

        }
    }


    private fun setUpEvents() {
        back.setOnClickListener {
            setResult(RESULT_OK, intent);//if came from wizard 3 - learn now then back (refresh on music piece activity)
             finish()
        }
        btn_later.setOnClickListener {
            setResult(RESULT_OK, intent); //if came from wizard 3 - learn now then later (refresh on music piece activity)
            finish()
        }
        btn_learnt.setOnClickListener {

            if (recallItem.canIGo()){

                vibrateForSuccess()

                recallItem.Go()
                localViewModel.updateProperties(recallItem)

                //NotificationHelper.addNewAlarm(this,recallItem) //actually just an alarm

                setResult(RESULT_OK, intent);
            }

            finish()
        }
        nav_settings.setOnClickListener { showBottomSheet() }
        btn_try.setOnClickListener {
            if (try_now_image) {
                btn_try.text = getString(R.string.view)
                learnImageView.setImageURI(prompt_image_uri)
                try_now_image = false
            } else {
                btn_try.text = getString(R.string.try_it_now)
                learnImageView.setImageURI(main_image_uri)
                try_now_image = true

            }
        }

        learnImageView.setOnClickListener {
            val intent = Intent(this@LearnNowActivity, ViewImageActivity::class.java)
            val filename = LibraryFilesystem.getFileNameLargeFirstBusID(recallItem.intUID)
            intent.putExtra("filename", filename)

            startActivityForResult(intent, Constants.RequestCodes.REQUEST_VIEW_IMAGE)
        }

    }


    private fun showBottomSheet() {
//        val actionDialog = PieceActionListDialogFragment.newInstance(
//            bottomlist,
//            getString(R.string.manage_phrases)
//        )
//        actionDialog.show(supportFragmentManager, "learn_bottom_sheet")

        var bottomlist:ArrayList<PieceDetailBottom> = ArrayList()

        bottomlist.add(PieceDetailBottom(Constants.MenuIDs.VIEW_IMAGE, Constants.MenuTitles.VIEW_IMAGE, false))
        bottomlist.add(PieceDetailBottom(Constants.MenuIDs.REPLACE_IMAGE, Constants.MenuTitles.REPLACE_IMAGE, false))
        bottomlist.add(PieceDetailBottom(Constants.MenuIDs.CROP, Constants.MenuTitles.CROP, false))
        bottomlist.add(PieceDetailBottom(Constants.MenuIDs.SET_UP, Constants.MenuTitles.SET_UP, true))



        val actionDialog= PieceActionListDialogFragment.newInstance(bottomlist,getString(R.string.manage_images))
        actionDialog.show(supportFragmentManager,"learn_bottom_sheet")

    }

//    private fun setUpBottomSheet() {
////        val bottom_array = resources.getStringArray(R.array.learn_bottom_sheet)
////
////
////
////        for ((index, title) in bottom_array.withIndex()) {
////            bottomlist.add(PieceDetailBottom(index, title, false))
////        }
//
//
//
//
//    }

    override fun onPieceActionClicked(bottomModel: PieceDetailBottom) {

        //val bottom_array_keys = resources.getStringArray(R.array.learn_bottom_sheet_keys)
        when (bottomModel.id) {
            Constants.MenuIDs.CROP -> {


                val uri = LibraryFilesystem.readMainImage(recallItem.intUID)
                CropImage.activity(uri).start(this);

            }
            Constants.MenuIDs.REPLACE_IMAGE -> {
                CropImage.activity().start(this);
            }
            Constants.MenuIDs.VIEW_IMAGE -> {

                val intent = Intent(this@LearnNowActivity, ViewImageActivity::class.java)
                val filename = LibraryFilesystem.getFileNameLargeFirstBusID(recallItem.intUID)
                intent.putExtra("filename", filename)
                startActivityForResult(intent, Constants.RequestCodes.REQUEST_VIEW_IMAGE)






            }
            Constants.MenuIDs.SET_UP -> {
                recallItem.moveStart()
                localViewModel.updateProperties(recallItem)

                setResult(Activity.RESULT_OK)
                finish()
            }
        }


//        when (bottomModel.id) {
//            0 -> {
//                Log.w("menu","View in full screen")
//            }
//            1 -> {
//                Log.w("menu","Replace from camera")
//            }
//            2 -> {
//                Log.w("menu","Replace from library")
//            }
//            3 -> {
//                Log.w("menu","Crop phrase")
//            }
//            4 -> {
//                Log.w("menu","Setup again")
//                //this.recallItem.journeyState = ITEM_JOURNEY_STATE_ENUM.JourneyStateInDepotSetup
//
//                recallItem.moveStart()
//                localViewModel.updateProperties(recallItem)
//
//
//            }
//        }
//
//        Toast.makeText(this, bottomModel.title, Toast.LENGTH_SHORT).show()

    }
    private fun vibrateForSuccess() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) { // Vibrator availability checking
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        500,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                ) // New vibrate method for API Level 26 or higher
            } else {
                vibrator.vibrate(500) // Vibrate method for below API Level 26
            }
        }
    }
}
