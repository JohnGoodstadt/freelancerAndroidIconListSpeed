package com.johngoodstadt.memorize.activities

import android.app.PendingIntent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.johngoodstadt.memorize.Libraries.getNotficationItems
import com.johngoodstadt.memorize.Libraries.launchTomorrowNotificationNow
import com.johngoodstadt.memorize.Libraries.listAlarms
import com.johngoodstadt.memorize.R


import com.johngoodstadt.memorize.utils.Constants


import kotlinx.android.synthetic.main.activity_alarm.*
import java.text.SimpleDateFormat
import java.util.*

class AlarmActivity : AppCompatActivity() {

    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)
        setSupportActionBar(toolbar)

        setUpEvents()


      //  listAlarms(this)

    }
    fun setUpEvents(){

        buttonView.setOnClickListener{

           listAlarms(applicationContext)
            //LibraryJava.listAlarms(applicationContext)

            displayAlarmInfo("1")
            displayAlarmInfo("2")
            displayAlarmInfo("3")


        }
        alarm1Button.setOnClickListener{
            val datetimeToAlarm = Calendar.getInstance(Locale.getDefault())
            datetimeToAlarm.timeInMillis = System.currentTimeMillis()



            datetimeToAlarm.add(Calendar.SECOND, 20)
            println("Set Alarm 1 date trigger  ${sdf.format(datetimeToAlarm.time)}")


        }
        alarm2Button.setOnClickListener{

            val datetimeToAlarm = Calendar.getInstance(Locale.getDefault())
            datetimeToAlarm.timeInMillis = System.currentTimeMillis()

            datetimeToAlarm.add(Calendar.MINUTE, 2)
            println("Set Alarm 2 date trigger  ${sdf.format(datetimeToAlarm.time)}")



        }
        alarm3Button.setOnClickListener{

            val datetimeToAlarm = Calendar.getInstance(Locale.getDefault())
            datetimeToAlarm.timeInMillis = System.currentTimeMillis()





            datetimeToAlarm.add(Calendar.MINUTE, 3)
            println("Set Alarm 3 date trigger  ${sdf.format(datetimeToAlarm.time)}")



        }
        button1viewmemorize.setOnClickListener{

            println("=======================================================================")
            val list = getNotficationItems()
            for (ri in list){
                println("${ri.title} prev:${sdf.format(ri.prevEventTime)} next:${ri.nextEventTime} ${ri.UID.take(8)} ${ri.currentStopTitle()} ")
            }
            println("=======================================================================")

        }
        button1setalarms.setOnClickListener{

            val list = getNotficationItems()
            for (ri in list){

                ri.UID?.let {
                    println("${ri.title} prev:${sdf.format(ri.prevEventTime)} next:${ri.nextEventTime} ${ri.UID.take(8)} ${ri.currentStopTitle()} ")

                }
            }


        }

        buttonSetdailyalarm.setOnClickListener{

        }

        buttonExecDaily.setOnClickListener{
            launchTomorrowNotificationNow(this)
        }



    }

    private fun displayAlarmInfo(UID:String) {




    }
}
