package com.johngoodstadt.memorize.fragments.PromptAsImageWizard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.johngoodstadt.memorize.Libraries.LibraryFilesystem
import com.johngoodstadt.memorize.Libraries.getItemFromRecallGroupUID
import com.johngoodstadt.memorize.R
import com.johngoodstadt.memorize.activities.guard
import com.johngoodstadt.memorize.models.PieceDetailBottom
import com.johngoodstadt.memorize.fragments.PieceActionListDialogFragment
import com.johngoodstadt.memorize.fragments.MusicWizard.MusicPhraseWizard1Fragment
import com.johngoodstadt.memorize.fragments.MusicWizard.MusicPromptWizard2Fragment
import com.johngoodstadt.memorize.fragments.MusicWizard.MusicFinalWizard3Fragment
import com.johngoodstadt.memorize.fragments.MusicWizard.MusicWizardDBViewModel
import com.johngoodstadt.memorize.fragments.WordsOnlyWizard.WordsOnlyWizardViewModel
import com.johngoodstadt.memorize.models.RecallItem
import com.johngoodstadt.memorize.utils.Constants
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_wizard.*
import kotlinx.android.synthetic.main.activity_wizard.back
import kotlinx.android.synthetic.main.activity_wizard.nav_title
import kotlinx.android.synthetic.main.music_wizard1_fragment.*

class PromptAsImageWizardActivity : AppCompatActivity(), PromptAsImageWizard1Fragment.OnFragmentInteractionListener,
    PieceActionListDialogFragment.Listener,
    PromptAsImageWizard3Fragment.OnFragmentInteractionListener,
    PromptAsImageWizard2Fragment.OnFragmentInteractionListener {



    //passed in
    private lateinit var recallItem: RecallItem
    private lateinit var promptAsImageWizardViewModel:  PromptAsImageWizardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val recallItemUID = intent.getStringExtra("UID").guard { return }
        val busDepotUID = intent.getStringExtra("busDepotUID").guard { return }

        getItemFromRecallGroupUID(busDepotUID, recallItemUID)?.let{
            this.recallItem = it

            setContentView(R.layout.prompt_as_image_activity_wizard)
            promptAsImageWizardViewModel = ViewModelProviders.of(this).get(PromptAsImageWizardViewModel::class.java)
            setUpUI()
            setUpEvents()
            setUpBottomSheet()
        }


    }


    override fun onFragmentWizard1Interaction(dirty: Boolean) {
        onFragment=1
        // title= intent.getStringExtra("title")
        title = this.recallItem.title

        nav_title.text=title
        val wizard2Fragment = PromptAsImageWizard2Fragment.newInstance(recallItem.busDepotUID,this.recallItem.UID)
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
            promptAsImageWizardViewModel.updateTitleProperty(recallItem.title,recallItem.UID)

        }else{
            println("NO title CHANGE .onFragmentWizard1Interaction()")
        }


        onFragment=2

        //activity.title
        //title = intent.getStringExtra("title")
        title = this.recallItem.title

        var UID = intent.getStringExtra("UID")

        val wizard3Fragment = PromptAsImageWizard3Fragment.newInstance(recallItem.busDepotUID,UID)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.wizard_container, wizard3Fragment, "wizard3").addToBackStack(null)
            .commitAllowingStateLoss()
        addBottomDots(onFragment)
        timings.visibility= View.VISIBLE
        camera.visibility= View.GONE
    }

    override fun onFragmentWizard3Interaction(dirty: Boolean) {

        if (recallItem.canILearn()) {
            recallItem.LEARN()


            promptAsImageWizardViewModel.update(recallItem) //update Local DB
        }


    }



    private var bottomlist:ArrayList<PieceDetailBottom> = ArrayList()
    private var onFragment:Int=0




    private fun setUpBottomSheet() {

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
        camera.setOnClickListener { openBottomsheet() }
    }

    private fun openBottomsheet() {

        var bottomlist:ArrayList<PieceDetailBottom> = ArrayList()

        var wizardPageNumber = 1

        when(supportFragmentManager.findFragmentById(R.id.wizard_container)) {
            is PromptAsImageWizard2Fragment -> {
                wizardPageNumber = 2
            } is PromptAsImageWizard3Fragment -> {
                wizardPageNumber = 3
            }
        }

        var fileExists = false
        if (wizardPageNumber == 1){
            fileExists = LibraryFilesystem.doesLargeImageExist(recallItem.intUID)
        }else if (wizardPageNumber == 2){
            fileExists = LibraryFilesystem.doesPromptImageExist(recallItem.intUID)
        }//no edit on final wizard

        if (fileExists){
            bottomlist.add(PieceDetailBottom(Constants.MenuIDs.CROP, Constants.MenuTitles.CROP, false))
        }

        bottomlist.add(PieceDetailBottom(Constants.MenuIDs.GET_IMAGE, Constants.MenuTitles.GET_IMAGE, false))
        bottomlist.add(PieceDetailBottom(Constants.MenuIDs.SCORE_PAGE, Constants.MenuTitles.SCORE_PAGE, false))

        if (fileExists){
            bottomlist.add(PieceDetailBottom(Constants.MenuIDs.REMOVE, Constants.MenuTitles.REMOVE, true))
        }


        val actionDialog= PieceActionListDialogFragment.newInstance(bottomlist,getString(R.string.manage_images))
        actionDialog.show(supportFragmentManager,"wizard1_bottom")
    }
    override fun onPieceActionClicked(bottomModel: PieceDetailBottom) {


        var wizardPageNumber = 1

        when(supportFragmentManager.findFragmentById(R.id.wizard_container)) {
            is MusicPromptWizard2Fragment -> {
                wizardPageNumber = 2
            } is MusicFinalWizard3Fragment -> {
            wizardPageNumber = 3
        }
        }

        when (bottomModel.id) {
            Constants.MenuIDs.CROP -> {

                if (wizardPageNumber == 1){
                    val uri = LibraryFilesystem.readMainImage(recallItem.intUID)
                    CropImage.activity(uri).start(this);
                }else if (wizardPageNumber == 2){
                    val uri = LibraryFilesystem.readPromptImage(recallItem.intUID)
                    CropImage.activity(uri).start(this);
                } //no edit on page 3 - final

            }
            Constants.MenuIDs.GET_IMAGE -> {
                CropImage.activity().start(this);
            }
            Constants.MenuIDs.SCORE_PAGE -> {
                Log.w("menu","Select Score Page") }
            Constants.MenuIDs.REMOVE -> {

                when(wizardPageNumber) {
                    1 -> {
                        val filename = LibraryFilesystem.getFileNameMainImageByBusID(recallItem.intUID)
                        LibraryFilesystem.removeFile(filename)
                    }
                    2 -> {
                        var filename = LibraryFilesystem.getFileNamePromptImage(recallItem.intUID)
                        LibraryFilesystem.removeFile(filename)
                        filename = LibraryFilesystem.getFileNameSmallPromptImage(recallItem.intUID)
                        LibraryFilesystem.removeFile(filename)
                    }
                }
                //TODO: is this correct?
                musicWizard1imageView.setImageURI(null)

            }
        }


    }


    private fun setUpUI() {
        //title= intent.getStringExtra("title")
        title = this.recallItem.title

        nav_title.text= "Asana ${title}"

       this.recallItem.title?.also {title ->

            val wizard1Fragment = PromptAsImageWizard1Fragment.newInstance(recallItem.busDepotUID,recallItem.UID)

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
            camera.visibility= View.VISIBLE
        }else{
            timings.visibility= View.VISIBLE
            camera.visibility= View.GONE
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val frag=supportFragmentManager.findFragmentById(R.id.wizard_container)
        when(frag) {
            is MusicPhraseWizard1Fragment -> addBottomDots(0)
            is MusicPromptWizard2Fragment -> addBottomDots(1)
            is MusicFinalWizard3Fragment -> addBottomDots(2)
        }

    }
}
