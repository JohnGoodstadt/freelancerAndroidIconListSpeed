package com.johngoodstadt.memorize.fragments.WordsOnlyWizard

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.johngoodstadt.memorize.Libraries.LibraryFilesystem
import com.johngoodstadt.memorize.Libraries.getItemFromRecallGroupUID
import com.johngoodstadt.memorize.R
import com.johngoodstadt.memorize.activities.guard
import com.johngoodstadt.memorize.fragments.MusicWizard.MusicFinalWizard3Fragment
import com.johngoodstadt.memorize.fragments.MusicWizard.MusicPromptWizard2Fragment
import com.johngoodstadt.memorize.fragments.MusicWizard.MusicWizardDBViewModel
import com.johngoodstadt.memorize.models.PieceDetailBottom
import com.johngoodstadt.memorize.fragments.PieceActionListDialogFragment

import com.johngoodstadt.memorize.models.RecallItem
import com.johngoodstadt.memorize.utils.Constants
import kotlinx.android.synthetic.main.activity_wizard.*
import kotlinx.android.synthetic.main.activity_wizard.back
import kotlinx.android.synthetic.main.activity_wizard.nav_title

class WordsOnlyWizardActivity : AppCompatActivity(), WordsOnlyWizard1Fragment.OnFragmentInteractionListener,
    PieceActionListDialogFragment.Listener,
    WordsOnlyWizard3Fragment.OnFragmentInteractionListener,
    WordsOnlyWizard2Fragment.OnFragmentInteractionListener {


    //passed in
    private lateinit var recallItem: RecallItem
    private lateinit var wordsOnlyWizardViewModel: WordsOnlyWizardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val recallItemUID = intent.getStringExtra("UID").guard { return }
        val busDepotUID = intent.getStringExtra("busDepotUID").guard { return }


        getItemFromRecallGroupUID(busDepotUID, recallItemUID)?.let{
            this.recallItem = it

            setContentView(R.layout.words_only_activity_wizard)
            wordsOnlyWizardViewModel = ViewModelProviders.of(this).get(WordsOnlyWizardViewModel::class.java)
            setUpUI()
            setUpEvents()
            setUpBottomSheet()

        }



    }
    override fun onFragmentWizard1Interaction(dirty: Boolean) {

        //1. update DB
        if (dirty){
            println("Words Changed  WordsOnlyWizardActivity.onFragmentWizard1Interaction()")
            //Save to DB
            wordsOnlyWizardViewModel.updateWordsProperty(recallItem.words,recallItem.UID)

        }else{
            println("NO CHANGE WordsOnlyWizardActivity.onFragmentWizard1Interaction()")
        }


        //2. move on
        onFragment=1

        title = this.recallItem.title

        nav_title.text=title
        val wizard2Fragment = WordsOnlyWizard2Fragment.newInstance(recallItem.busDepotUID,this.recallItem.UID)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.wizard_container, wizard2Fragment, "wizard2").addToBackStack(null)
            .commitAllowingStateLoss()
        addBottomDots(onFragment)



    }

    override fun onFragmentWizard2Interaction(dirty: Boolean) {

        //1. update DB
        if (dirty){
            println("title Changed  .onFragmentWizard1Interaction()")
            //Save to DB
            wordsOnlyWizardViewModel.updateTitleProperty(recallItem.title,recallItem.UID)

        }else{
            println("NO title CHANGE .onFragmentWizard1Interaction()")
        }



        onFragment=2
        title = this.recallItem.title

        //var UID = intent.getStringExtra("UID")

        val wizard3Fragment = WordsOnlyWizard3Fragment.newInstance(recallItem.busDepotUID,recallItem.UID)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.wizard_container, wizard3Fragment, "wizard3").addToBackStack(null)
            .commitAllowingStateLoss()
        addBottomDots(onFragment)
        timings.visibility= View.VISIBLE
    }

    override fun onFragmentWizard3Interaction(dirty: Boolean) {
        if (dirty){
            println("SOMETHING CHANGED WordsOnlyWizardActivity.onFragmentWizard3Interaction()")

            //Learn Now button Pressed
            if (recallItem.canILearn()) {
                recallItem.LEARN()
            }


        }else{
            println("NO CHANGE WordsOnlyWizardActivity.onFragmentWizard3Interaction()")
        }

    }



    private var bottomlist:ArrayList<PieceDetailBottom> = ArrayList()
    private var onFragment:Int=0





    private fun setUpBottomSheet() {
        //readfy for timings
        val bottom_array=resources.getStringArray(R.array.wizard_bottom_sheet)
        for (title in bottom_array){
            if(title.equals("Remove Selected Photo"))
            {
                bottomlist.add(PieceDetailBottom(1,title,true))
                continue
            }
            bottomlist.add(PieceDetailBottom(1,title,false))
        }

    }

    private fun setUpEvents() {
        back.setOnClickListener {
            when(onFragment){
                0->finish()
                1->{supportFragmentManager.popBackStack()
                onFragment=0
                    addBottomDots(onFragment)
                }
                2->{
                    supportFragmentManager.popBackStack()
                    onFragment=1
                    addBottomDots(onFragment)
                }
            }
        }

        timings.setOnClickListener {
            openBottomsheet()
        }

    }



    private fun setUpUI() {
        //title= intent.getStringExtra("title")
        title = this.recallItem.title

       nav_title.text= "${title}"

       this.recallItem.title?.also {title ->

            val wizard1Fragment = WordsOnlyWizard1Fragment.newInstance(recallItem.busDepotUID,recallItem.UID)

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.wizard_container, wizard1Fragment, "wizard1")
                .commit()
            addBottomDots(onFragment)
        }





    }

    private fun addBottomDots(currentPage: Int) {
        val dots = arrayOfNulls<TextView>(3)

        val colorsActive = resources.getIntArray(R.array.array_dot_active)
        val colorsInactive = resources.getIntArray(R.array.array_dot_inactive)

        layoutDots.removeAllViews()
        for (i in dots.indices) {
            dots[i] = TextView(this)
            dots[i]?.setText(Html.fromHtml("&#8226;"))
            dots[i]?.setTextSize(35f)
            dots[i]?.setTextColor(colorsInactive[currentPage])
            layoutDots.addView(dots[i])
        }

        if (dots.size > 0)
            dots[currentPage]?.setTextColor(colorsActive[currentPage])

        if(currentPage==0 || currentPage==1){
            timings.visibility= View.GONE
        }else{
            timings.visibility= View.VISIBLE
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val frag=supportFragmentManager.findFragmentById(R.id.wizard_container)
        when(frag) {
            is WordsOnlyWizard1Fragment -> addBottomDots(0)
            is WordsOnlyWizard2Fragment -> addBottomDots(1)
            is WordsOnlyWizard3Fragment -> addBottomDots(2)
        }

    }
    private fun openBottomsheet() {

        var bottomlist:ArrayList<PieceDetailBottom> = ArrayList()

        bottomlist.add(PieceDetailBottom(Constants.MenuIDs.DELETE_ITEM, Constants.MenuTitles.DELETE_ITEM))


        val actionDialog= PieceActionListDialogFragment.newInstance(bottomlist,getString(R.string.manage_items))
        actionDialog.show(supportFragmentManager,"words_only_fragment_wizard3")

    }

    override fun onPieceActionClicked(bottomModel: PieceDetailBottom) {

        when (bottomModel.id) {


            Constants.MenuIDs.DELETE_ITEM -> {

                recallItem.recallGroup.let {rg ->

                    Constants.GlobalVariables.busesNoOrderFromDB.remove(recallItem) //global memory
                    rg.removeItem(recallItem) //memory
                    wordsOnlyWizardViewModel.delete(recallItem) //sql
                    setResult(RESULT_OK, intent);
                    finish()


                }
            }
            else -> { // Note the block
                print("x is neither 1 nor 2")
            }

        }

    }
}
