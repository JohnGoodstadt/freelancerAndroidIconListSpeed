package com.johngoodstadt.memorize.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.johngoodstadt.memorize.Libraries.LibraryFilesystem
import com.johngoodstadt.memorize.Libraries.LibraryJava
import com.johngoodstadt.memorize.Libraries.MyApplication
import com.johngoodstadt.memorize.R
import com.johngoodstadt.memorize.adapters.RecallItemsRecyclerViewAdapter
import com.johngoodstadt.memorize.database.MusicPieceViewModel
import com.johngoodstadt.memorize.databinding.ActivityPiecesDetailBinding
import com.johngoodstadt.memorize.fragments.MusicWizard.MusicWizardActivity
import com.johngoodstadt.memorize.fragments.PhrasesFragment
import com.johngoodstadt.memorize.fragments.PieceActionListDialogFragment
import com.johngoodstadt.memorize.fragments.PieceActionListDialogFragment.Listener
import com.johngoodstadt.memorize.models.*
import com.johngoodstadt.memorize.utils.Constants
import com.johngoodstadt.memorize.utils.Constants.MenuIDs.ADD_NEXT_PHRASE
import com.johngoodstadt.memorize.utils.Constants.MenuIDs.CHANGE_TITLE
import com.johngoodstadt.memorize.utils.Constants.MenuIDs.DELETE_PHRASE
import com.johngoodstadt.memorize.utils.Constants.MenuIDs.DELETE_PIECE
import com.johngoodstadt.memorize.utils.Constants.MenuIDs.GET_IMAGE
import com.johngoodstadt.memorize.utils.Constants.MenuIDs.HIDE_PIECE
import com.johngoodstadt.memorize.utils.Constants.MenuIDs.LEARN_PHRASES
import com.johngoodstadt.memorize.utils.Constants.MenuIDs.MANAGE_PHOTOS
import com.johngoodstadt.memorize.utils.Constants.MenuIDs.PIECE_PERFORMED
import com.johngoodstadt.memorize.utils.Constants.MenuIDs.VIEW_HELP
import com.johngoodstadt.memorize.utils.Constants.MenuIDs.VIEW_HELP_STEP1
import com.johngoodstadt.memorize.utils.Constants.MenuIDs.VIEW_SCORE_PAGE
import com.johngoodstadt.memorize.utils.ConstantsJava.RANDOM_UPPER_LIMIT_INT
import com.johngoodstadt.memorize.utils.ConstantsJava.WhichPartOfPiece
import com.johngoodstadt.memorize.viewmodels.RecallGroupItemListViewModel
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_pieces_detail.*
import kotlinx.android.synthetic.main.activity_pieces_detail.back
import kotlinx.android.synthetic.main.activity_pieces_detail.nav_title
import kotlin.random.Random

// Declare an extension function that calls a lambda called block if the value is null
inline fun <T> T.guard(block: T.() -> Unit): T {
    if (this == null) block(); return this
}

class MusicPieceActivity : AppCompatActivity(), PhrasesFragment.OnListFragmentInteractionListener,
    Listener {


    private lateinit var recallGroup: RecallGroup

    lateinit var activityDataViewModel: RecallGroupItemListViewModel
    var list:List<Any> = ArrayList()
    lateinit var binding: ActivityPiecesDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        val groupUID = intent.getStringExtra(Constants.ARG_PARAM_busDepotUID).guard { return }

        val recallGroups = Constants.GlobalVariables.groups.filter { it.UID == groupUID }
        if (recallGroups.count() > 0) {
            this.recallGroup = recallGroups.first()
            Log.i("===> Piece UUID", this.recallGroup.title + " " + this.recallGroup.UID)
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_pieces_detail)

        setUpUI()
        setUpViewModel()
        setUpEvents()

        fullRefreshDataAndView()

        //full refresh
//        with(detail_list) {
//            layoutManager = LinearLayoutManager(context)
//            adapter = RecallItemsRecyclerViewAdapter(
//                activityDataViewModel.getItemListforPiece(recallGroup),
//                this@MusicPieceActivity
//            )
//        }

        (detail_list.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        detail_list.getItemAnimator()!!.setChangeDuration(0)
       // detail_list.setItemAnimator(null)

//        val anim = detail_list.itemAnimator
//        anim.animateChange(false)

        activityDataViewModel.recallItemList.observe(this, Observer<List<Any>> { allTableviewRows ->
            // Update the UI
            list=allTableviewRows;
            with(detail_list) {
                if (adapter == null) {
                    layoutManager = LinearLayoutManager(context)
                    adapter = RecallItemsRecyclerViewAdapter(
                        allTableviewRows,
                        this@MusicPieceActivity
                    )
                }
                else
                {
                    (adapter as RecallItemsRecyclerViewAdapter).setData(list)
                   // adapter?.notifyDataSetChanged()
                }
            }

        })





    }

    //region setup
    @SuppressLint("DefaultLocale")
    private fun setUpUI() {


        if (recallGroup == null){
            return
        }

        var gotTitle = this.recallGroup.title
        if (true) {
            gotTitle =
                LibraryJava.getPartFromPiece(this.recallGroup.title, WhichPartOfPiece.composer)
        }

        nav_title.text = gotTitle.capitalize()


    }

    private fun setUpEvents() {
        back.setOnClickListener {
            finish()
        }
        add.setOnClickListener {
            addNextPhrase()
        }

        ConfigureButton.setOnClickListener {
            showBottomSheet()

        }
         buttonStep1.setOnClickListener {
             when (recallGroup.stepNumber) {
                 RecallGroup.PIECE_STEP_NUMBER_ENUM.step_number1 ->{
                     val builder = AlertDialog.Builder(this)
                     builder.setTitle("Step 1 Learn Phrases")
                     builder.setMessage("You are currently doing Step 1 - Learn Phrases. Finish all phrases before going on to step 2.")
                     builder.setPositiveButton(android.R.string.ok) { dialog, which ->}
                     builder.show()
                 }
                 RecallGroup.PIECE_STEP_NUMBER_ENUM.step_number2 ->{
                     recallGroup.goToStep1()
                     activityDataViewModel.DB.let {
                         for (ri in recallGroup.itemList){
                             activityDataViewModel.DB.update(ri)
                         }
                         activityDataViewModel.DB.update(recallGroup)
                         fullRefreshDataAndView()
                         configureButtonsDependingOnCurrentStep()
                     }


                 }
                 RecallGroup.PIECE_STEP_NUMBER_ENUM.step_number3, RecallGroup.PIECE_STEP_NUMBER_ENUM.step_number4 ->{
                     if (recallGroup.stepNumber == RecallGroup.PIECE_STEP_NUMBER_ENUM.step_number4){
                         //LibraryCombinePhrases.resetTitles(recallGroup) //reverse naming for all titles
                     }else  if (recallGroup.stepNumber == RecallGroup.PIECE_STEP_NUMBER_ENUM.step_number3){
                        setRelearnAll()
                         recallGroup.stepNumber = RecallGroup.PIECE_STEP_NUMBER_ENUM.step_number1

                         activityDataViewModel.DB.let {
                             activityDataViewModel.DB.update(recallGroup)
                             fullRefreshDataAndView()
                             configureButtonsDependingOnCurrentStep()
                         }




                     }



                 }
             }
         }


        buttonStep2.setOnClickListener {


            when (recallGroup.stepNumber) {
                RecallGroup.PIECE_STEP_NUMBER_ENUM.step_number1 ->{

                    if(recallGroup.canIGotoStep2()){

//                        doMergeAction()
//                        configureButtonsDependingOnCurrentStep()
                        recallGroup.goToStep2()
                        activityDataViewModel.DB.let {
                            activityDataViewModel.DB.update(recallGroup.itemList.first())
                            activityDataViewModel.DB.update(recallGroup)
                            fullRefreshDataAndView()
                            configureButtonsDependingOnCurrentStep()
                        }

                    }else{
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Unable to go to Step 2")
                        builder.setMessage("All phrases must have been learnt before leaving step 1.")
                        builder.setPositiveButton(android.R.string.ok) { dialog, which ->}
                        builder.show()
                    }
                }
                RecallGroup.PIECE_STEP_NUMBER_ENUM.step_number2 ->{
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Step 2 Learn Piece")
                    builder.setMessage("You are already on Step 2 - learn piece")
                    builder.setPositiveButton(android.R.string.ok) { dialog, which ->}
                    builder.show()
                }
                RecallGroup.PIECE_STEP_NUMBER_ENUM.step_number3, RecallGroup.PIECE_STEP_NUMBER_ENUM.step_number4 ->{


                    if (recallGroup.stepNumber == RecallGroup.PIECE_STEP_NUMBER_ENUM.step_number3){
                        //LibraryCombinePhrases.resetTitles(recallGroup) //reverse naming for all titles
                    }

                    recallGroup.stepNumber = RecallGroup.PIECE_STEP_NUMBER_ENUM.step_number2
                    configureButtonsDependingOnCurrentStep()
//                    doMergeAction()
//                    configureButtonsDependingOnCurrentStep()
                }
            }



        }
        buttonStep3.setOnClickListener {

            when (recallGroup.stepNumber) {
                RecallGroup.PIECE_STEP_NUMBER_ENUM.step_number1 ->{
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Step 3 Polish Weak Phrases")
                    builder.setMessage("You cannot go from 'Step 1 Learn Phrases' straight to 'Step 3 Polishing' - do Step 2 next.")
                    builder.setPositiveButton(android.R.string.yes) { dialog, which ->}
                    builder.show()
                }
                RecallGroup.PIECE_STEP_NUMBER_ENUM.step_number2 ->{
                    val intent = Intent(this@MusicPieceActivity, PolishPhrasesActivity::class.java)

                    intent.putExtra(Constants.ARG_PARAM_busDepotUID, recallGroup.UID)
                    startActivityForResult(intent, Constants.RequestCodes.REQUEST_PIECE_POLISH)

                }
                RecallGroup.PIECE_STEP_NUMBER_ENUM.step_number3 ->{
                    val intent = Intent(this@MusicPieceActivity, PolishPhrasesActivity::class.java)

                    intent.putExtra(Constants.ARG_PARAM_busDepotUID, recallGroup.UID)
                    startActivityForResult(intent, Constants.RequestCodes.REQUEST_PIECE_POLISH)

                }
                else ->{


                    if (recallGroup.otheritemList.count() >= 3) {




                        if (false){
                            //TODO: First item is NOT on otherItemList
                            val checked =
                                arrayListOf(recallGroup.itemList.first(), recallGroup.otheritemList.get(0))
                            val unchecked =
                                arrayListOf(recallGroup.otheritemList.get(1), recallGroup.otheritemList.last())


                            if (recallGroup.goToStep3(checked, unchecked)){
                                //OK
                                recallGroup.itemList.addAll(recallGroup.otheritemList)
                                recallGroup.otheritemList.clear()

                                activityDataViewModel.DB.let {
                                    for (ri in recallGroup.itemList){
                                        activityDataViewModel.DB.update(ri)
                                    }
                                    activityDataViewModel.DB.update(recallGroup)
                                    fullRefreshDataAndView()
                                    configureButtonsDependingOnCurrentStep()
                                }
                            }
                        }


                    }
                }
            }



        }

    }
    private fun setUpViewModel() {

//        musicPieceDBViewModel = ViewModelProviders.of(this).get(MusicPieceViewModel::class.java)

        activityDataViewModel = ViewModelProviders.of(this)[RecallGroupItemListViewModel::class.java]
        activityDataViewModel.recallGroup = recallGroup
        activityDataViewModel.DB = ViewModelProviders.of(this).get(MusicPieceViewModel::class.java)

        binding.viewmodel = activityDataViewModel

        configureButtonsDependingOnCurrentStep()


    }



    //endregion

    private fun addNextPhrase() {

        //TODO: expensive
        var new_title = ""
        val recallItemsInGroup = recallGroup.itemList

        if (recallItemsInGroup.isEmpty()){
            new_title =  LibraryJava.transformGroupNameToTitle(recallGroup.title).second as String
        }else{

            val sortedItems = recallItemsInGroup.sortedByDescending { it.title }
            val lastTitle = sortedItems.first()

            new_title = LibraryJava.transformTitleToNewTitle(lastTitle.title).second as String

            print(new_title)


        }
//Log.e("BUG","group UID:" + recallGroup.UID)

        val ri = RecallItem(
            new_title,
            this.recallGroup.UID
        )

//        Log.e("BUG","ri UID:" + ri.UID)
//        Log.e("BUG","ri busDepotUID:" + ri.busDepotUID)

        //INIT() seems to copy last random number??

        //https://medium.com/@TheSomeshKumar/randoms-in-kotlin-3e8c4b46803
        //Use Kotlin Randon
        //var ran = Random.nextInt(1, RANDOM_UPPER_LIMIT_INT)
        val someInt = (1..RANDOM_UPPER_LIMIT_INT).shuffled().first()




//        ri.intUID = ThreadLocalRandom.current().nextInt(1, RANDOM_UPPER_LIMIT_INT)
        ri.intUID = Random.nextInt(1, RANDOM_UPPER_LIMIT_INT)

        Constants.GlobalVariables.busesNoOrderFromDB.add(ri)
        recallGroup.addItem(ri)

        //val suffix = LibraryJava.getSuffixLetter(new_title)

        //com.johngoodstadt.memorize.Libraries.write_text_to_thumbnailUri(ri.intUID,suffix)


        activityDataViewModel.DB.insert(ri) //DB

        fullRefreshDataAndView()
        configureButtonsDependingOnCurrentStep()

    }



    override fun onListFragmentInteraction (ri: OneLineTableViewCell) {


        when (ri.journeyState) {
            RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateInDepotSetup -> {
                val intent = Intent(this@MusicPieceActivity, MusicWizardActivity::class.java)
                intent.putExtra(Constants.ARG_PARAM_UID, ri.UID)
                intent.putExtra(Constants.ARG_PARAM_busDepotUID, ri.busDepotUID)

                startActivityForResult(intent, Constants.RequestCodes.REQUEST_ITEM_SETUP)
            }
            RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateInDepotLearning -> {
                val intent = Intent(this@MusicPieceActivity, LearnNowActivity::class.java)
                intent.putExtra(Constants.ARG_PARAM_UID, ri.UID)
                intent.putExtra(Constants.ARG_PARAM_busDepotUID, ri.busDepotUID)

                startActivityForResult(intent, Constants.RequestCodes.REQUEST_ITEM_LEARN)
            }
        }


    }

    override fun onListFragmentInteraction (ri: WaitingForOrhersTableViewCell) {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Waiting for Other Phrases")
        builder.setMessage("Learn outstanding phrases and then do Step 2 - Whole Piece.")
        builder.setPositiveButton(android.R.string.yes) { dialog, which ->}
        builder.show()

    }
    override fun onListFragmentInteraction (ri: CheckedTableViewCell) {



    }
    override fun onListFragmentInteraction (ri: RecallItemRowItem) {

        when (ri.journeyState) {

            RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravelling,RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravellingOverdue -> {
                val intent = Intent(this@MusicPieceActivity, RecallNowMusicActivity::class.java)

                intent.putExtra(Constants.ARG_PARAM_UID, ri.UID)
                intent.putExtra(Constants.ARG_PARAM_busDepotUID, ri.busDepotUID)

                startActivityForResult(intent, Constants.RequestCodes.REQUEST_ITEM_RECALL)
            }

        }

    }




    private fun showBottomSheet() {

        var bottomlist: ArrayList<PieceDetailBottom> = ArrayList()


        bottomlist.add(PieceDetailBottom(CHANGE_TITLE, Constants.MenuTitles.CHANGE_TITLE, false))
        if (recallGroup.stepNumber == RecallGroup.PIECE_STEP_NUMBER_ENUM.step_number1){

            var title = Constants.MenuTitles.ADD_NEXT_PHRASE
            if (recallGroup.itemList.isEmpty()){
                title = resources.getString(R.string.menu_add_first_phrase)
            }
            bottomlist.add(PieceDetailBottom( ADD_NEXT_PHRASE, title,false ) )
        }

        //does score page exists
        val fileName = LibraryFilesystem.getFileNameFirstScorePhotoByDepotUID(recallGroup.UID)
        if (LibraryFilesystem.exists(fileName)) {
            bottomlist.add(PieceDetailBottom(VIEW_SCORE_PAGE, Constants.MenuTitles.VIEW_SCORE_PAGE))
        }else{
            bottomlist.add(PieceDetailBottom(GET_IMAGE, Constants.MenuTitles.GET_IMAGE))
        }

//        bottomlist.add(PieceDetailBottom(VIEW_HELP, Constants.MenuTitles.VIEW_HELP, false))
//        bottomlist.add(PieceDetailBottom(VIEW_HELP_STEP1, Constants.MenuTitles.VIEW_HELP_STEP1, false))
        if (recallGroup.stepNumber == RecallGroup.PIECE_STEP_NUMBER_ENUM.step_number1){
            bottomlist.add(PieceDetailBottom(LEARN_PHRASES, Constants.MenuTitles.LEARN_PHRASES, false))
        }else{
            bottomlist.add(PieceDetailBottom(Constants.MenuIDs.LEARN_PIECE, Constants.MenuTitles.LEARN_PIECE, false))
        }
        bottomlist.add(PieceDetailBottom(PIECE_PERFORMED, Constants.MenuTitles.PIECE_PERFORMED, false))
        bottomlist.add(PieceDetailBottom(HIDE_PIECE, Constants.MenuTitles.HIDE_PIECE, false))

        if (recallGroup.stepNumber == RecallGroup.PIECE_STEP_NUMBER_ENUM.step_number1) {
            if (recallGroup.itemList.isEmpty() == false){
                bottomlist.add( PieceDetailBottom( Constants.MenuIDs.DELETE_PHRASE,Constants.MenuTitles.DELETE_PHRASE,true))

            }
        }

         bottomlist.add( PieceDetailBottom( DELETE_PIECE,Constants.MenuTitles.DELETE_PIECE,true))
//
//        val actionDialog= PieceActionListDialogFragment.newInstance(bottomlist,getString(R.string.manage_images))
//        actionDialog.show(supportFragmentManager,"learn_bottom_sheet")

        val actionDialog = PieceActionListDialogFragment.newInstance( bottomlist,this.recallGroup.title)
        actionDialog.show(supportFragmentManager, "piece_detail_bottom")




    }



    override fun onPieceActionClicked(bottomModel: PieceDetailBottom) {
        //TODO specific calls to specific options e.g. function addPhraseH

//        Log.w("fred", bottomModel.title + " " + bottomModel.id.toString())
//        Toast.makeText(this, bottomModel.title, Toast.LENGTH_SHORT).show()

        when (bottomModel.id) {


            CHANGE_TITLE -> {
//                Log.w("fred", "found Change title")


                this.recallGroup.title = this.recallGroup.title


                activityDataViewModel.DB.update(this.recallGroup)

                fullRefreshDataAndView()
            }
            ADD_NEXT_PHRASE -> {
//                Log.w("fred", "found Next Phrase title")

                addNextPhrase()



            }
            MANAGE_PHOTOS -> {
//                Log.w("fred", "refresh for now")

                fullRefreshDataAndView()



            }
            GET_IMAGE-> {
                val fileName = LibraryFilesystem.getFileNameFirstScorePhotoByDepotUID(recallGroup.UID)
                val uri = LibraryFilesystem.getUriFromFilename(fileName)
                CropImage.activity(uri).start(this);
            }
            VIEW_SCORE_PAGE -> {
                val intent = Intent(this@MusicPieceActivity, SimpleSampleActivity::class.java)



                val filename = LibraryFilesystem.getFileNameFirstScorePhotoByDepotUID(recallGroup.UID)
                intent.putExtra("filename", filename)
                startActivityForResult(intent, Constants.RequestCodes.REQUEST_VIEW_IMAGE)

            }
            VIEW_HELP -> {
                Log.w("fred", "VIEW_HELP")
            }
            VIEW_HELP_STEP1 -> {
                Log.w("fred", "VIEW_HELP")
            }
            LEARN_PHRASES -> {
                Log.w("fred", "VIEW_HELP")
                if (recallGroup.stepNumber == RecallGroup.PIECE_STEP_NUMBER_ENUM.step_number1){

                    setRelearnAll()
                    for (ri in recallGroup.itemList){
                        if (ri.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateInDepotLearning){
                            activityDataViewModel.DB.update(ri)
                        }
                    }
                    fullRefreshDataAndView()


                }
            }
            Constants.MenuIDs.LEARN_PIECE -> {
                Log.w("fred", "VIEW_HELP")
                if (recallGroup.stepNumber == RecallGroup.PIECE_STEP_NUMBER_ENUM.step_number2){
                    val ri = recallGroup.itemList.first()
                    ri.LEARN()
                    activityDataViewModel.DB.update(ri)
                }
            }
            PIECE_PERFORMED -> {
                Log.w("fred", "VIEW_HELP")
            }
            HIDE_PIECE -> {
                Log.w("fred", "HIDE_PIECE")
            }

            DELETE_PHRASE -> {
                Log.w("fred", "found delete")

//                val recallItemsInGroup =    Constants.GlobalVariables.busesNoOrderFromDB.filter { it.busDepotUID == this.recallGroup.UID }
                val recallItemsInGroup = recallGroup.itemList
                val sortedItems = recallItemsInGroup.sortedByDescending { it.title }
                val first = sortedItems.first()

                Constants.GlobalVariables.busesNoOrderFromDB.remove(first) //global memory
                recallGroup.removeItem(first) //memory
                activityDataViewModel.DB.delete(first) //Local

                fullRefreshDataAndView()
            }
            DELETE_PIECE -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Delete Piece")
                builder.setMessage("Are you sure? You cannot undo this removal.")
                builder.setPositiveButton("DELETE") { dialog, which ->

                    recallGroup.let {
                        for (ri in it.itemList){
                            Constants.GlobalVariables.busesNoOrderFromDB.remove(ri) //global memory
                            activityDataViewModel.DB.delete(ri) //Local
                        }
                        it.itemList.clear()
                        for (ri in it.otheritemList){
                            Constants.GlobalVariables.busesNoOrderFromDB.remove(ri) //global memory
                            activityDataViewModel.DB.delete(ri) //Local
                        }
                        it.otheritemList.clear()
                        Constants.GlobalVariables.groups.remove(it) //global memory

                        activityDataViewModel.DB.delete(it) //Local
                        setResult(RESULT_OK, intent);
                        finish()
                    }

                }
                builder.setNegativeButton(android.R.string.cancel) { dialog, which ->}
                builder.show()
            }
            else -> { // Note the block
                print("x is neither 1 nor 2")
            }

        }

    }


    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val msg = intent?.getStringExtra("message")
            Toast.makeText(this@MusicPieceActivity, "refresh on Music Piece Page", Toast.LENGTH_SHORT).show()
            Log.d("broadcast", "Music Piece page ${msg}")

            refreshDataAndView()
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



    fun processPolishingPhrases(checked:java.util.ArrayList<String>?,unchecked:java.util.ArrayList<String>?){

        var nowChecked = ArrayList<RecallItem>()// = emptyList()
        var nowUnchecked = ArrayList<RecallItem>()// = emptyList(2)

//
//        val nowChecked = checked!!.toHashSet()
//        val nowUnchecked = unchecked!!.toHashSet()

        val allItems = recallGroup.itemList + recallGroup.otheritemList

        checked.let {
            for (UID in it!!){
                val r = allItems.filter { it.UID == UID }
                if (r.count() > 0){
                    nowChecked.addAll(r)
                }
            }
        }

        unchecked.let {
            for (UID in it!!){
                val r = allItems.filter { it.UID == UID }
                if (r.count() > 0){
                    nowUnchecked.addAll(r)
                }
            }
        }

        println(nowChecked)
        println(nowUnchecked.toString())

        recallGroup.stepNumber = RecallGroup.PIECE_STEP_NUMBER_ENUM.step_number3
        for (ri in nowUnchecked){
            println(ri.UID)
            ri.journeyState = RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateWaitingForOthers
            ri.moveEnd()
            ri.linkedPhrases = ""
        }

        for (ri in nowChecked){
            println(ri.UID)
            ri.LEARN()
            ri.linkedPhrases = ""
        }







    }

    fun setRelearnAll(): Boolean{
        var returnValue = true

        for (ri in recallGroup.itemList){
            when(ri.journeyState){
                RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateAtStop,
                RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravellingOverdue,
                RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateTravelling,
                RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateInDepotSetup,
                RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateEnded,
                RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateWaitingForOthers -> {
                    val promptExists = LibraryFilesystem.doesPromptImageExist(ri.intUID)

                    if (promptExists == false || (ri.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateInDepotSetup && ri.canILearn() == false)){
                        returnValue = false
                    }else{
                        ri.moveLearn()
                    }
                }
            }
        }

        return returnValue
    }
    fun refreshDataAndView(){
        activityDataViewModel.getPhraseListforPiece(this.recallGroup)
//            with(detail_list) {
//               adapter!!.notifyDataSetChanged()
//            }

    }
    fun fullRefreshDataAndView(){
        activityDataViewModel.getPhraseListforPiece(this.recallGroup)

//        with(detail_list) {
//            layoutManager = LinearLayoutManager(context)
//            adapter = RecallItemsRecyclerViewAdapter(
//                activityDataViewModel.getItemListforPiece(recallGroup),
//                this@MusicPieceActivity
//            )
//        }
    }
    private fun configureButtonsDependingOnCurrentStep() {

        buttonStep1.setBackgroundTintList(getResources().getColorStateList(android.R.color.white))
        buttonStep1.setTextColor(getResources().getColor(R.color.colorAccent))

        buttonStep2.setBackgroundTintList(getResources().getColorStateList(android.R.color.white))
        buttonStep2.setTextColor(getResources().getColor(R.color.colorAccent))

        buttonStep3.setBackgroundTintList(getResources().getColorStateList(android.R.color.white))
        buttonStep3.setTextColor(getResources().getColor(R.color.colorAccent))

        when(recallGroup.stepNumber){
            RecallGroup.PIECE_STEP_NUMBER_ENUM.step_number2 -> {
                //activityDataViewModel.buttonSelected = ObservableInt(2)

                buttonStep2.setBackgroundTintList(getResources().getColorStateList(R.color.btnSelected));

//                buttonStep2.setBackgroundTolor(getResources().getColor(R.color.btnSelected))
                buttonStep2.setTextColor(getResources().getColor(android.R.color.white))



            }
            RecallGroup.PIECE_STEP_NUMBER_ENUM.step_number3 -> {
               // activityDataViewModel.buttonSelected = ObservableInt(3)

                buttonStep3.setBackgroundTintList(getResources().getColorStateList(R.color.btnSelected));
                buttonStep3.setBackgroundColor(getResources().getColor(R.color.btnSelected))
                buttonStep3.setTextColor(getResources().getColor(android.R.color.white))

            }
            else -> {
//                activityDataViewModel.buttonSelected = ObservableInt(1)
                buttonStep1.setBackgroundTintList(getResources().getColorStateList(R.color.btnSelected));
                buttonStep1.setBackgroundColor(getResources().getColor(R.color.btnSelected))
                buttonStep1.setTextColor(getResources().getColor(android.R.color.white))


            }
        }

//        if (recallGroup.stepNumber == RecallGroup.PIECE_STEP_NUMBER_ENUM.step_number2) {
//            activityDataViewModel.buttonSelected = ObservableInt(2)
//
//        } else if (recallGroup.stepNumber == RecallGroup.PIECE_STEP_NUMBER_ENUM.step_number3) {
//            activityDataViewModel.buttonSelected = ObservableInt(3)
//
//        } else {
//            //default to step 1
//            activityDataViewModel.buttonSelected = ObservableInt(1)
//        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // don't worry about result code - just refresh page

        if (requestCode == Constants.RequestCodes.REQUEST_ITEM_RECALL ) {
            fullRefreshDataAndView()
            return
        }

        if (resultCode == Activity.RESULT_OK && requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE ) {


            data?.let {
                val result = CropImage.getActivityResult(it)
                val bitmap = MediaStore.Images.Media.getBitmap(MyApplication.getAppContext().getContentResolver(), result.uri)


                LibraryFilesystem.createImageLocalFirstScorePhotoByDepotUID(this.recallGroup.UID,bitmap)
            }
        }

        if (resultCode == Activity.RESULT_OK && requestCode == Constants.RequestCodes.REQUEST_PIECE_POLISH) {
            data.let {
                val checked = it?.getStringArrayListExtra(Constants.POLISH_CHECKED_KEY)
                val unchecked = it?.getStringArrayListExtra(Constants.POLISH_UNCHECKED_KEY)



                processPolishingPhrases(checked,unchecked)

                //OK
                recallGroup.itemList.addAll(recallGroup.otheritemList)
                recallGroup.otheritemList.clear()

                activityDataViewModel.DB.let {
                    for (ri in recallGroup.itemList){
                        activityDataViewModel.DB.update(ri)
                    }
                    activityDataViewModel.DB.update(recallGroup)
                    fullRefreshDataAndView()
                    configureButtonsDependingOnCurrentStep()
                }
            }

            fullRefreshDataAndView()
            return
        }

        if (requestCode == Constants.RequestCodes.REQUEST_ITEM_LEARN || requestCode == Constants.RequestCodes.REQUEST_ITEM_SETUP) { //any back key hit on Learn Now
            fullRefreshDataAndView()
            return
        }

        if (resultCode == Activity.RESULT_OK) {
            fullRefreshDataAndView()
            return
        }


    }
}
