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
import com.johngoodstadt.memorize.viewmodels.RecallGroupItemListViewModel
import kotlinx.android.synthetic.main.activity_pieces_detail.*
import kotlinx.android.synthetic.main.activity_pieces_detail.add
import kotlinx.android.synthetic.main.alert_dialog_get_group_name.view.*
import kotlin.collections.ArrayList

// Declare an extension function that calls a lambda called block if the value is null

class RecallGroupActivity : AppCompatActivity(), PhrasesFragment.OnListFragmentInteractionListener,
    Listener {


    private lateinit var recallGroup: RecallGroup
    private lateinit var recallGroupViewModel: RecallGroupViewModel
    lateinit var activityDataViewModel: RecallGroupItemListViewModel
    var list:List<Any> = ArrayList()


    private var bottomlist: ArrayList<PieceDetailBottom> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val groupUID = intent.getStringExtra(ARG_PARAM_busDepotUID).guard { return }

        val recallGroups = Constants.GlobalVariables.groups.filter { it.UID == groupUID }
        if (recallGroups.count() > 0) {
            this.recallGroup = recallGroups.first()
        }

        this.recallGroupViewModel =
            ViewModelProviders.of(this).get(RecallGroupViewModel::class.java)

        setContentView(R.layout.activity_recallgroup_detail)
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_recallgroup_detail)

        setUpUI()
        setUpViewModel()
        setUpEvents()
        setUpBottomSheet()

        //activityViewModel.getStaticDataforDetail(this.recallGroup.UID)
        activityDataViewModel.getPhraseListforPiece(this.recallGroup)


    }

    override fun onPieceActionClicked(bottomModel: PieceDetailBottom) {
        //TODO specific calls to specific options e.g. function addPhraseH

        Toast.makeText(this, bottomModel.title, Toast.LENGTH_SHORT).show()
    }

    override fun onListFragmentInteraction(item: OneLineTableViewCell) {
        println("onListFragmentInteraction CheckedTableViewCell")
        Toast.makeText(this, item.title, Toast.LENGTH_SHORT).show()
        when (item.journeyState) {
            RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateInDepotSetup -> {

                if (Constants.whichApp.isRunning == Constants.whichApp.target.yoga) {
                    val intent =
                        Intent(this@RecallGroupActivity, PromptAsImageWizardActivity::class.java)
                    intent.putExtra("UID", item.UID)
                    intent.putExtra(ARG_PARAM_busDepotUID, item.busDepotUID)
                    startActivityForResult(intent, Constants.RequestCodes.REQUEST_ITEM_SETUP)
                } else {
                    val intent =
                        Intent(this@RecallGroupActivity, WordsOnlyWizardActivity::class.java)
                    intent.putExtra("UID", item.UID)
                    intent.putExtra(ARG_PARAM_busDepotUID, item.busDepotUID)
                    startActivityForResult(intent, Constants.RequestCodes.REQUEST_ITEM_SETUP)
                }


            }
            RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateInDepotLearning -> {

                if (Constants.whichApp.isRunning == Constants.whichApp.target.music) {
                    val intent = Intent(this@RecallGroupActivity, LearnNowActivity::class.java)
                    intent.putExtra(Constants.ARG_PARAM_UID, item.UID)
                    intent.putExtra(Constants.ARG_PARAM_busDepotUID, item.busDepotUID)

                    startActivityForResult(intent, Constants.RequestCodes.REQUEST_ITEM_LEARN)
                } else {
                    val intent =
                        Intent(this@RecallGroupActivity, LearnNowWordsOnlyActivity::class.java)
                    intent.putExtra(Constants.ARG_PARAM_UID, item.UID)
                    intent.putExtra(Constants.ARG_PARAM_busDepotUID, item.busDepotUID)

                    startActivityForResult(intent, Constants.RequestCodes.REQUEST_ITEM_LEARN)
                }


            }
        }
    }

    override fun onListFragmentInteraction(ri: CheckedTableViewCell) {

        println("onListFragmentInteraction CheckedTableViewCell")
        Toast.makeText(this, ri.title, Toast.LENGTH_SHORT).show()

    }

    override fun onListFragmentInteraction(ri: WaitingForOrhersTableViewCell) {

        println("onListFragmentInteraction WaitingForOrhersTableViewCell")
        Toast.makeText(this, ri.title, Toast.LENGTH_SHORT).show()


    }

    override fun onListFragmentInteraction(item: RecallItemRowItem) {
        Toast.makeText(this, item.title, Toast.LENGTH_SHORT).show()

        when (item.journeyState) {
            RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateInDepotSetup -> {

                if (Constants.whichApp.isRunning == Constants.whichApp.target.yoga) {
                    val intent =
                        Intent(this@RecallGroupActivity, PromptAsImageWizardActivity::class.java)
                    intent.putExtra("UID", item.UID)
                    intent.putExtra(ARG_PARAM_busDepotUID, item.busDepotUID)
                    startActivityForResult(intent, Constants.RequestCodes.REQUEST_ITEM_SETUP)
                } else {
                    val intent =
                        Intent(this@RecallGroupActivity, WordsOnlyWizardActivity::class.java)
                    intent.putExtra("UID", item.UID)
                    intent.putExtra(ARG_PARAM_busDepotUID, item.busDepotUID)
                    startActivityForResult(intent, Constants.RequestCodes.REQUEST_ITEM_SETUP)
                }


            }
            RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateInDepotLearning -> {

                if (Constants.whichApp.isRunning == Constants.whichApp.target.legaldemo) {
                    val intent =
                        Intent(this@RecallGroupActivity, LearnNowWordsOnlyActivity::class.java)
                    intent.putExtra(ARG_PARAM_UID, item.UID)
                    intent.putExtra(ARG_PARAM_busDepotUID, item.busDepotUID)
                    startActivityForResult(intent, Constants.RequestCodes.REQUEST_ITEM_LEARN)
                } else {
                    val intent = Intent(this@RecallGroupActivity, LearnNowActivity::class.java)
                    intent.putExtra(ARG_PARAM_UID, item.UID)
                    intent.putExtra(ARG_PARAM_busDepotUID, item.busDepotUID)
                    startActivityForResult(intent, Constants.RequestCodes.REQUEST_ITEM_LEARN)
                }

            }
            RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravelling, RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravellingOverdue -> {

                if (Constants.whichApp.isRunning == Constants.whichApp.target.music) {
                    val intent =
                        Intent(this@RecallGroupActivity, RecallNowMusicActivity::class.java)
                    intent.putExtra(ARG_PARAM_UID, item.UID)
                    intent.putExtra(ARG_PARAM_busDepotUID, item.busDepotUID)

                    startActivityForResult(intent, Constants.RequestCodes.REQUEST_ITEM_RECALL)


                } else {
                    val intent =
                        Intent(this@RecallGroupActivity, RecallNowWordsOnlyActivity::class.java)
                    intent.putExtra(ARG_PARAM_UID, item.UID)
                    intent.putExtra(ARG_PARAM_busDepotUID, item.busDepotUID)

                    startActivityForResult(intent, Constants.RequestCodes.REQUEST_ITEM_RECALL)
                }

            }

        }

    }


    private val mMessageReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            val msg = intent?.getStringExtra("message")
            //TODO: resets page to top!
            Toast.makeText(
                this@RecallGroupActivity,
                "Refresh on Recall Group Page",
                Toast.LENGTH_SHORT
            ).show()

            Log.d("broadcast", "Recall Group page ${msg}")
            Log.e("broadcast", "RecallGroupActivity.onReceive()()")

            activityDataViewModel.getPhraseListforPiece(recallGroup)
        }

    }


    private fun setUpBottomSheet() {

        val bottom_array = resources.getStringArray(R.array.piece_detail_bottom_sheet)
        for (title in bottom_array) {
            if (title.equals("delete piece", ignoreCase = true)) {
                bottomlist.add(PieceDetailBottom(1, title, true))
                continue
            }
            bottomlist.add(PieceDetailBottom(1, title, false))
        }

    }

    @SuppressLint("DefaultLocale")
    private fun setUpUI() {

        recallGroup.let {
            nav_title.text = it.title
        }


    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            mMessageReceiver,
            IntentFilter(Constants.BROADCAST_RECEIVER_INTENT)
        );
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

    }

    private fun setUpEvents() {
        back.setOnClickListener {
            finish()
        }
        add.setOnClickListener {
            addItem()
        }


    }

    private fun addItem() {
        Toast.makeText(this, "Add new item to remember", Toast.LENGTH_SHORT).show()


        var dialog_title = "Add an Asana"
        var message = "Add a new group name that you can add asanas to"
        if (Constants.whichApp.isRunning == Constants.whichApp.target.yoga) {
            dialog_title = "Add an Asana"   //ready for next target
            message = "Add a name of the pose you want to remember"
        } else if (Constants.whichApp.isRunning == Constants.whichApp.target.legaldemo) {
            dialog_title = "Add an law case"   //ready for next target
            message = "Add a name of the case you want to remember"
        } else if (Constants.whichApp.isRunning == Constants.whichApp.target.general) {
            dialog_title = "Add a short title"   //ready for next target
            message = "Add the name of something you want to remember"
        }

        val mDialogView =
            LayoutInflater.from(this).inflate(R.layout.alert_dialog_get_group_name, null)

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
            val itemName = mDialogView.dialogNameEt.text.toString()//.toUpperCase()

            if (itemName.isEmpty() == false) {
                val ri = RecallItem(
                    itemName,
                    this.recallGroup.UID
                )
                //TODO: Remove this debug line
                //ri.schemeTitles = DEFAULT_SCHEME_DEBUG


                Constants.GlobalVariables.busesNoOrderFromDB.add(ri)
                recallGroup.addItem(ri)
                recallGroupViewModel.insert(ri)

                activityDataViewModel.getPhraseListforPiece(this.recallGroup)
            }


        }

        mDialogView.dialogCancelBtn.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }

    private fun setUpViewModel() {

        activityDataViewModel =
            ViewModelProviders.of(this)[RecallGroupItemListViewModel::class.java]


        activityDataViewModel.recallItemList.observe(this, Observer<List<Any>> { allTableviewRows ->
            // Update the UI
            list=allTableviewRows;

            with(detail_list) {
                if (adapter == null) {
                    layoutManager = LinearLayoutManager(context)

                    adapter = RecallItemsRecyclerViewAdapter(
                        list,

                        this@RecallGroupActivity
                    )

                } else {
                    (adapter as RecallItemsRecyclerViewAdapter).setData(list)
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // if(requestCode==Constants.RequestCodes.REQUEST_PIECE_DETAIL_TO_RECALL && resultCode== Activity.RESULT_OK){
        // don't worry about result code - just refresh page
        //dont worry about any codes - just refresh

        if (requestCode == Constants.RequestCodes.REQUEST_ITEM_SETUP) {
            refreshDataAndView()
        } else if (resultCode == Activity.RESULT_OK) { //somethings changed so update
            refreshDataAndView()
        }


    }

    private fun refreshDataAndView() {

        //data
        activityDataViewModel.getPhraseListforPiece(this.recallGroup)

        //view - above will trigger LIVE Data update
//        val vpa = detail_list.adapter
//
//        if (vpa !is RecallItemsRecyclerViewAdapter) {
//            return
//        }
//
//        vpa.notifyDataSetChanged()

    }

}
