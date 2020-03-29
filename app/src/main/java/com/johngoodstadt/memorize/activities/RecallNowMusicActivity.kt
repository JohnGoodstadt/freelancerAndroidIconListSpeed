package com.johngoodstadt.memorize.activities

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import com.johngoodstadt.memorize.Libraries.LibraryFilesystem
import com.johngoodstadt.memorize.Libraries.getItemFromRecallGroupUID
import com.johngoodstadt.memorize.R
import com.johngoodstadt.memorize.fragments.CRUDLocalRecallItemViewModel
import com.johngoodstadt.memorize.fragments.PieceActionListDialogFragment
import com.johngoodstadt.memorize.models.PieceDetailBottom
import com.johngoodstadt.memorize.models.RecallGroup
import com.johngoodstadt.memorize.models.RecallItem
import com.johngoodstadt.memorize.utils.Constants
import kotlinx.android.synthetic.main.activity_recall.*
import kotlinx.android.synthetic.main.activity_recall.back
import kotlinx.android.synthetic.main.activity_recall.btn_later
import kotlinx.android.synthetic.main.activity_recall.learnImageView
import kotlinx.android.synthetic.main.activity_recall.nav_title
import kotlinx.android.synthetic.main.activity_recall.pageTitleTextView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class RecallNowMusicActivity : AppCompatActivity() {

    //passed in
    private var recallItemUID: String? = null
    private var busDepotUID: String? = null
    //ivar
    private lateinit var recallItem: RecallItem
    private lateinit var localViewModel: CRUDLocalRecallItemViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recallItemUID = intent.getStringExtra(Constants.ARG_PARAM_UID).guard { return }
        busDepotUID = intent.getStringExtra(Constants.ARG_PARAM_busDepotUID).guard { return }

        getItemFromRecallGroupUID(busDepotUID, recallItemUID)?.let{
            this.recallItem = it
        }

        setContentView(R.layout.activity_recall)
        localViewModel = ViewModelProviders.of(this).get(CRUDLocalRecallItemViewModel::class.java)
        setUpUI()
        setUpEvents()
    }

    private fun setUpUI() {

        nav_title.text = this.recallItem.title

        recallItem.recallGroup.let {

            if (it.stepNumber == RecallGroup.PIECE_STEP_NUMBER_ENUM.step_number2) {
                pageTitleTextView.text = this.getString(R.string.recall_now_piece_page_title)
            }

        }


        val prompt_image_uri = LibraryFilesystem.readPromptImage(recallItem.intUID)

        learnImageView.setImageURI(prompt_image_uri)
    }

    private fun setUpEvents() {
        back.setOnClickListener {
            finish()
        }
        btn_later.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
//            Toast.makeText(this@RecallNowMusicActivity, btn_later.text, Toast.LENGTH_SHORT).show()
            finish()
        }
        btn_yes.setOnClickListener {
            vibrateForSuccess()

            recallItem.arriveAtNextStop()
            recallItem.leaveStop()
            localViewModel.updateProperties(recallItem)


            setResult(Activity.RESULT_OK)
            finish()
        }
        btn_no.setOnClickListener {
            showOptionsDialog()
        }
        switch1.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if (isChecked) {
                    Toast.makeText(this@RecallNowMusicActivity, "ON", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@RecallNowMusicActivity, "OFF", Toast.LENGTH_SHORT).show()

                }

            }

        })
        settings.setOnClickListener {


            val datetimeToAlarm = Calendar.getInstance(Locale.getDefault())
            datetimeToAlarm.timeInMillis = System.currentTimeMillis()


            datetimeToAlarm.add(Calendar.SECOND, 3)


            recallItem.UID?.let {UID ->
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                println("Set Alarm for ${UID.take(8)}  ${sdf.format(datetimeToAlarm.time)}")
                //AlarmScheduler.scheduleAlarmsForReminder(applicationContext, ri.UID,datetimeToAlarm)
            }


//            showBottomSheet()
        }
    }
    private fun showBottomSheet() {

        var bottomlist: ArrayList<PieceDetailBottom> = ArrayList()




        bottomlist.add( PieceDetailBottom( Constants.MenuIDs.NOTIF_PHRASE,Constants.MenuTitles.NOTIF_PHRASE,true))
//

        val actionDialog = PieceActionListDialogFragment.newInstance( bottomlist,"Options")
        actionDialog.show(supportFragmentManager, "piece_detail_bottom")




    }
     fun onPieceActionClicked(bottomModel: PieceDetailBottom) {


        when (bottomModel.id) {


            Constants.MenuIDs.NOTIF_PHRASE -> {
            println("NOTIF_PHRASE")

            }
            else -> { // Note the block
                print("x is neither 1 nor 2")
            }

        }

    }


    private fun showOptionsDialog() {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)

        builder.setTitle("What Do You Want to Do?")

        val options = arrayOf("Learn it again now", "Don't do anything just now")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    recallItem.moveLearn()
                    println(recallItem.print())
                    localViewModel.updateProperties(recallItem)
                    setResult(RESULT_OK, intent);

                    finish()
                }
                1 -> {
                    finish()
                }

            }
        }

// create and show the alert dialog
        val dialog = builder.create()
        dialog.show()
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
