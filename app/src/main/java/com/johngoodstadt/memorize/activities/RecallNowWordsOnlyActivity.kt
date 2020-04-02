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
import com.johngoodstadt.memorize.Libraries.getItemFromRecallGroupUID
import com.johngoodstadt.memorize.R
import com.johngoodstadt.memorize.fragments.CRUDLocalRecallItemViewModel
import com.johngoodstadt.memorize.models.RecallItem
//import com.johngoodstadt.memorize.notifications.NotificationHelper
import com.johngoodstadt.memorize.utils.Constants.ARG_PARAM_UID
import com.johngoodstadt.memorize.utils.Constants.ARG_PARAM_busDepotUID
import kotlinx.android.synthetic.main.activity_recall.back
import kotlinx.android.synthetic.main.activity_recall.btn_later
import kotlinx.android.synthetic.main.activity_recall.btn_no
import kotlinx.android.synthetic.main.activity_recall.btn_yes
import kotlinx.android.synthetic.main.activity_recall.nav_title
import kotlinx.android.synthetic.main.activity_recall.switch1
import kotlinx.android.synthetic.main.activity_wordsonly_recall.*


class RecallNowWordsOnlyActivity : AppCompatActivity() {

    //passed in
    private var recallItemUID: String? = null
    private var busDepotUID: String? = null
    //ivar
    private lateinit var recallItem: RecallItem
    private lateinit var localViewModel: CRUDLocalRecallItemViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recallItemUID = intent.getStringExtra(ARG_PARAM_UID).guard { return }
        busDepotUID = intent.getStringExtra(ARG_PARAM_busDepotUID).guard { return }


        getItemFromRecallGroupUID(busDepotUID, recallItemUID)?.let{
            this.recallItem = it
        }

        setContentView(R.layout.activity_wordsonly_recall)

        localViewModel = ViewModelProviders.of(this).get(CRUDLocalRecallItemViewModel::class.java)

        setUpUI()
        setUpEvents()
    }

    private fun setUpUI() {

        promptTextView.text = "${this.recallItem.title}"
        nav_title.text = this.recallItem.title
    }

    private fun setUpEvents() {
        back.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        btn_later.setOnClickListener {
            Toast.makeText(this@RecallNowWordsOnlyActivity, btn_later.text, Toast.LENGTH_SHORT).show()


            setResult(Activity.RESULT_CANCELED)

            finish()
        }
        btn_yes.setOnClickListener {
            Toast.makeText(this@RecallNowWordsOnlyActivity, btn_yes.text, Toast.LENGTH_SHORT).show()


            vibrateForSuccess()

            recallItem.arriveAtNextStop()
            recallItem.leaveStop()
            localViewModel.updateProperties(recallItem)

//            NotificationHelper.addNewAlarm(this,recallItem) //actually just an alarm

            setResult(Activity.RESULT_OK)

            finish()
        }
        btn_no.setOnClickListener {
            showOptionsDialog()
        }
        switch1.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if (isChecked) {
                    Toast.makeText(this@RecallNowWordsOnlyActivity, "ON", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@RecallNowWordsOnlyActivity, "OFF", Toast.LENGTH_SHORT).show()

                }

            }

        })
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

                    setResult(Activity.RESULT_OK)

                    finish()
                }
                1 -> {
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }

            }
        }

// create and show the alert dialog
        val dialog = builder.create()
        dialog.show()
    }
}
