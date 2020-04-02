package com.johngoodstadt.memorize.activities

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.method.ScrollingMovementMethod
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.johngoodstadt.memorize.Libraries.getItemFromRecallGroupUID
import com.johngoodstadt.memorize.R
import com.johngoodstadt.memorize.fragments.CRUDLocalRecallItemViewModel
import com.johngoodstadt.memorize.fragments.PieceActionListDialogFragment
import com.johngoodstadt.memorize.models.PieceDetailBottom
import com.johngoodstadt.memorize.models.RecallItem
//import com.johngoodstadt.memorize.notifications.NotificationHelper
import com.johngoodstadt.memorize.utils.Constants
import com.johngoodstadt.memorize.utils.Constants.ARG_PARAM_UID
import com.johngoodstadt.memorize.utils.Constants.ARG_PARAM_busDepotUID
import kotlinx.android.synthetic.main.activity_learn.back
import kotlinx.android.synthetic.main.activity_learn.btn_later
import kotlinx.android.synthetic.main.activity_learn.btn_learnt
import kotlinx.android.synthetic.main.activity_learn.btn_try
import kotlinx.android.synthetic.main.activity_learn.nav_settings
import kotlinx.android.synthetic.main.activity_learn.pageTitleTextView
import kotlinx.android.synthetic.main.words_only_activity_learn.*


class LearnNowWordsOnlyActivity : AppCompatActivity(), PieceActionListDialogFragment.Listener {

    //passed in
    private var recallItemUID: String? = null
    private var busDepotUID: String? = null
    private lateinit var recallItem: RecallItem

    //iVars
    private lateinit var localViewModel: CRUDLocalRecallItemViewModel

    private var try_now_image: Boolean = true
   // private var bottomlist: ArrayList<PieceDetailBottom> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        recallItemUID = intent.getStringExtra(ARG_PARAM_UID).guard { return }
        busDepotUID = intent.getStringExtra(ARG_PARAM_busDepotUID).guard { return }

        getItemFromRecallGroupUID(busDepotUID, recallItemUID)?.let{
            this.recallItem = it
        }

        setContentView(R.layout.words_only_activity_learn)
        if (savedInstanceState != null) {
            try_now_image = savedInstanceState.getBoolean("try_now_image", true)
        }

        localViewModel = ViewModelProviders.of(this).get(CRUDLocalRecallItemViewModel::class.java)

        setUpUI()
        setUpEvents()
        setUpBottomSheet()

    }

    private fun setUpUI() {

        pageTitleTextView.text = "${this.recallItem.title}"
        learnTextView.text = recallItem.words
        learnTextView.movementMethod = ScrollingMovementMethod()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState?.putBoolean("try_now_image", try_now_image)

    }





    private fun setUpEvents() {
        back.setOnClickListener { finish() }
        btn_later.setOnClickListener { finish() }
        btn_learnt.setOnClickListener {

            if (recallItem.canIGo()){

                vibrateForSuccess()

                recallItem.Go()
                localViewModel.updateProperties(recallItem)


//                NotificationHelper.addNewAlarm(this,recallItem) //actually just an alarm

                setResult(RESULT_OK, intent);
            }




            finish()
        }
        nav_settings.setOnClickListener { showBottomSheet() }
        btn_try.setOnClickListener {
            if (try_now_image) {
                btn_try.text = getString(R.string.view)
                learnTextView.visibility = INVISIBLE
                pageTitleTextView.visibility = VISIBLE
                pageTitleTextView.text = "Explain ${this.recallItem.title}"
                try_now_image = false
            } else {
                btn_try.text = getString(R.string.try_it_now)
                learnTextView.visibility = VISIBLE
//                textView15.visibility = INVISIBLE
                pageTitleTextView.text = "${this.recallItem.title}"
                try_now_image = true

            }
        }

    }


    private fun showBottomSheet() {
//        val actionDialog = PieceActionListDialogFragment.newInstance(
//            bottomlist,
//            getString(R.string.manage_phrases)
//        )
//        actionDialog.show(supportFragmentManager, "learn_bottom_sheet")

        var bottomlist:ArrayList<PieceDetailBottom> = ArrayList()


        bottomlist.add(PieceDetailBottom(Constants.MenuIDs.SET_UP, Constants.MenuTitles.SET_UP, false))


        val actionDialog= PieceActionListDialogFragment.newInstance(bottomlist,getString(R.string.manage_images))
        actionDialog.show(supportFragmentManager,"learn_bottom_sheet")


    }

    private fun setUpBottomSheet() {
//        val bottom_array = resources.getStringArray(R.array.learn_bottom_sheet)
//
//        for ((index, title) in bottom_array.withIndex()) {
//            bottomlist.add(PieceDetailBottom(index, title, false))
//        }




    }

    override fun onPieceActionClicked(bottomModel: PieceDetailBottom) {

        when (bottomModel.id) {
            Constants.MenuIDs.SET_UP -> {
                recallItem.moveStart()
                localViewModel.updateProperties(recallItem)
                setResult(RESULT_OK, intent);
                finish()
            }

        }

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
