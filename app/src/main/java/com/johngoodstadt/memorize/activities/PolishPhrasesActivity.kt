package com.johngoodstadt.memorize.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.johngoodstadt.memorize.R
import com.johngoodstadt.memorize.adapters.PolishItemsRecyclerViewAdapter
import com.johngoodstadt.memorize.adapters.RecallItemsRecyclerViewAdapter
import com.johngoodstadt.memorize.database.RecallGroupViewModel
import com.johngoodstadt.memorize.fragments.PhrasesFragment
import com.johngoodstadt.memorize.fragments.PieceActionListDialogFragment.Listener
import com.johngoodstadt.memorize.fragments.PromptAsImageWizard.PromptAsImageWizardActivity
import com.johngoodstadt.memorize.fragments.WordsOnlyWizard.WordsOnlyWizardActivity
import com.johngoodstadt.memorize.models.*
import com.johngoodstadt.memorize.utils.Constants
import com.johngoodstadt.memorize.utils.Constants.ARG_PARAM_UID
import com.johngoodstadt.memorize.utils.Constants.ARG_PARAM_busDepotUID
import com.johngoodstadt.memorize.utils.Constants.POLISH_CHECKED_KEY
import com.johngoodstadt.memorize.utils.Constants.POLISH_UNCHECKED_KEY
import com.johngoodstadt.memorize.viewmodels.PolishingItemListViewModel
import com.johngoodstadt.memorize.viewmodels.RecallGroupItemListViewModel
import kotlinx.android.synthetic.main.activity_pieces_detail.*
import kotlinx.android.synthetic.main.activity_pieces_detail.add
import kotlinx.android.synthetic.main.activity_pieces_detail.back
import kotlinx.android.synthetic.main.activity_pieces_detail.detail_list
import kotlinx.android.synthetic.main.activity_pieces_detail.nav_title
import kotlinx.android.synthetic.main.activity_polish_phrases_detail.*
import kotlinx.android.synthetic.main.alert_dialog_get_group_name.view.*
//import kotlin.collections.ArrayList

// Declare an extension function that calls a lambda called block if the value is null

class PolishPhrasesActivity : AppCompatActivity(), PhrasesFragment.OnListFragmentInteractionListener,
    Listener {


    private lateinit var recallGroup:RecallGroup
    private lateinit var recallGroupViewModel: RecallGroupViewModel
    lateinit var activityDataViewModel: PolishingItemListViewModel

    var copyOfList = ArrayList<CheckedRecallItems>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val groupUID = intent.getStringExtra(ARG_PARAM_busDepotUID).guard { return }

        val recallGroups = Constants.GlobalVariables.groups.filter { it.UID == groupUID }
        if (recallGroups.count() > 0 ){
            this.recallGroup = recallGroups.first()

            val combinedList = recallGroup.itemList + recallGroup.otheritemList
            val listModel = ArrayList<CheckedRecallItems>()

            for (ri in combinedList){

                var checked = true
                if (ri.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateWaitingForOthers){
                    checked = false
                }
                listModel.add(CheckedRecallItems(ri.intUID,ri.UID,ri.journeyState, ri.title,checked))
            }


            copyOfList = listModel


        }

        this.recallGroupViewModel = ViewModelProviders.of(this).get(RecallGroupViewModel::class.java)

        setContentView(R.layout.activity_polish_phrases_detail)

        setUpUI()
        setUpViewModel()
        setUpEvents()


        activityDataViewModel.getItemListforPolishedItems(this.copyOfList)



    }

    override fun onPieceActionClicked(bottomModel: PieceDetailBottom) {
        //TODO specific calls to specific options e.g. function addPhraseH

        Toast.makeText(this, bottomModel.title, Toast.LENGTH_SHORT).show()
        println("onPieceActionClicked")
    }

    override fun onListFragmentInteraction(item: OneLineTableViewCell) {

        println("OneLineTableViewCell")

    }
    override fun onListFragmentInteraction (item: WaitingForOrhersTableViewCell) {

        println("WaitingForOrhersTableViewCell")

    }
    override fun onListFragmentInteraction(item: RecallItemRowItem) {
        print("RecallItemRowItem")


    }

    override fun onListFragmentInteraction (item: CheckedTableViewCell) {

        println("CheckedTableViewCell")

        if (item != null) {
            item.checked = !item.checked

            for (it in copyOfList){
                println("${it.checked}, ${it.title}")
            }

            for (it in copyOfList){
                if (it.UID == item.UID){
                    println("CheckedTableViewCell FOUND")
                    it.checked = !it.checked
                    break
                }
            }
            activityDataViewModel.getItemListforPolishedItems(this.copyOfList)
        }

    }




    @SuppressLint("DefaultLocale")
    private fun setUpUI() {

        recallGroup.let {
            nav_title.text = it.title
        }

    }



    private fun setUpEvents() {
        back.setOnClickListener {
            finish()
        }
        save.setOnClickListener {
            saveSelection()
        }



    }
    fun saveSelection(){

        var checked  = ArrayList<String> ()
        var unchecked  = ArrayList<String> ()

        for (it in copyOfList){

            println("${it.checked}, ${it.title}")


            if (it.checked){
                checked.add(it.UID!!)
            }else{
                unchecked.add(it.UID!!)
            }
        }




//
//
//
//        val joined = recallGroup.itemList + recallGroup.otheritemList
//        if (joined.count() >= 4){
//
//
//            checked.add(joined.first().UID)
//            checked.add(joined.get(1).UID)
//
//            unchecked.add(joined.get(2).UID)
//            unchecked.add(joined.last().UID)
//
//        }



        val data = Intent()
        data.putExtra(POLISH_CHECKED_KEY,checked)
        data.putExtra(POLISH_UNCHECKED_KEY,unchecked)
        setResult(Activity.RESULT_OK,data)
        finish()
    }

    private fun setUpViewModel() {
        activityDataViewModel = ViewModelProviders.of(this)[PolishingItemListViewModel::class.java]

        activityDataViewModel.recallItemList.observe(this, Observer<List<Any>> { allTableviewRows ->
            // Update the UI
            with(detail_list) {
                layoutManager = LinearLayoutManager(context)
                adapter = PolishItemsRecyclerViewAdapter(
                    allTableviewRows,
                    this@PolishPhrasesActivity
                )
            }
        })
    }


//    private fun refreshDataAndView() {
//
//
//        activityDataViewModel.getItemListforPieceLiveData(this.recallGroup)
//
//
//    }

}
