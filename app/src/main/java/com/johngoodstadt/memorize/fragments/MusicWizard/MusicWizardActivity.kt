package com.johngoodstadt.memorize.fragments.MusicWizard

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.media.ThumbnailUtils
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.johngoodstadt.memorize.Libraries.LibraryFilesystem
import com.johngoodstadt.memorize.Libraries.MyApplication
import com.johngoodstadt.memorize.Libraries.getItemFromRecallGroupUID
import com.johngoodstadt.memorize.R
import com.johngoodstadt.memorize.activities.guard
import com.johngoodstadt.memorize.fragments.PieceActionListDialogFragment
import com.johngoodstadt.memorize.models.PieceDetailBottom
import com.johngoodstadt.memorize.models.RecallItem
import com.johngoodstadt.memorize.utils.Constants
import com.johngoodstadt.memorize.utils.Constants.MenuIDs.CROP
import com.johngoodstadt.memorize.utils.Constants.MenuIDs.DELETE_PHRASE
import com.johngoodstadt.memorize.utils.Constants.MenuIDs.GET_IMAGE
import com.johngoodstadt.memorize.utils.Constants.MenuIDs.REMOVE
import com.johngoodstadt.memorize.utils.Constants.MenuIDs.REPLACE_IMAGE
import com.johngoodstadt.memorize.utils.Constants.MenuIDs.SCORE_PAGE
import com.johngoodstadt.memorize.utils.ConstantsJava
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_wizard.*
import kotlinx.android.synthetic.main.music_wizard1_fragment.musicWizard1imageView

//import sun.jvm.hotspot.utilities.IntArray


class MusicWizardActivity : AppCompatActivity(), MusicPhraseWizard1Fragment.OnFragmentInteractionListener,
    PieceActionListDialogFragment.Listener,
    MusicFinalWizard3Fragment.OnFragmentInteractionListener,
    MusicPromptWizard2Fragment.OnFragmentInteractionListener {



    //passed in
    private lateinit var recallItem: RecallItem
    private lateinit var musicWizardDBViewModel: MusicWizardDBViewModel


//iVars

    private var onFragment:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val recallItemUID = intent.getStringExtra("UID").guard { return }
        val busDepotUID = intent.getStringExtra("busDepotUID").guard { return }

        getItemFromRecallGroupUID(busDepotUID, recallItemUID)?.let{
            this.recallItem = it

            setContentView(R.layout.activity_wizard)
            musicWizardDBViewModel = ViewModelProviders.of(this).get(MusicWizardDBViewModel::class.java)
            setUpUI()
            setUpEvents()
            //setUpBottomSheet()
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

    private fun setUpUI() {
        //title= intent.getStringExtra("title")
        title = this.recallItem.title

        val parts= title.split(" - ");

        nav_title.text= "Phrase ${parts[2]}"


        val wizard1Fragment=
            MusicPhraseWizard1Fragment.newInstance(recallItem.busDepotUID,recallItem.UID)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.wizard_container, wizard1Fragment, "wizard1")
            .commit()
        addBottomDots(onFragment)    }

    //region Fragment Interactions

    override fun onFragmentWizard1Interaction(dirty: Boolean) {

        if (dirty){
            println("SOMETHING CHANGED .onFragmentWizard1Interaction()")
        }else{
            println("NO CHANGE .onFragmentWizard1Interaction()")
        }

        onFragment=1
        // title= intent.getStringExtra("title")
        title = this.recallItem.title

        nav_title.text=title
        val wizard2Fragment = MusicPromptWizard2Fragment.newInstance(recallItem.busDepotUID,this.recallItem.UID)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.wizard_container, wizard2Fragment, "wizard2").addToBackStack(null)
            .commitAllowingStateLoss()
        addBottomDots(onFragment)



    }
    override fun onFragmentWizard2Interaction(dirty: Boolean) {




        if (dirty){
            println("SOMETHING CHANGED .onFragmentWizard2Interaction()")
        }else{
            println("NO CHANGE .onFragmentWizard2Interaction()")
        }

        onFragment=2

        //activity.title
        //title = intent.getStringExtra("title")
        title = this.recallItem.title

        var UID = intent.getStringExtra("UID")

        val wizard3Fragment = MusicFinalWizard3Fragment.newInstance(recallItem.busDepotUID,UID)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.wizard_container, wizard3Fragment, "wizard3").addToBackStack(null)
            .commitAllowingStateLoss()
        addBottomDots(onFragment)
        timings.visibility= View.VISIBLE
        camera.visibility= View.GONE


        //TODO: temp code - save thumbnail if it does not exist
        val thumbnail = LibraryFilesystem.getFileNameThumbnailImage(recallItem.intUID)
        if (LibraryFilesystem.exists(thumbnail) == false ) {
            val promptfilename = LibraryFilesystem.getFileNameLargeMusicCueByBusID(recallItem.intUID)
            if (LibraryFilesystem.exists(promptfilename)) {

                val imageUri = LibraryFilesystem.getUriFromFilename(promptfilename)
                val bitmap = MediaStore.Images.Media.getBitmap(MyApplication.getAppContext().getContentResolver(), imageUri)

                val thumbImage = ThumbnailUtils.extractThumbnail(
                    bitmap,
                    ConstantsJava.THUMBNAIL_SIZE,
                    ConstantsJava.THUMBNAIL_SIZE
                )

                LibraryFilesystem.createImageThumbnail(this.recallItem.intUID,thumbImage)
            }
        }



    }

    override fun onFragmentWizard3Interaction(dirty: Boolean) {

        onFragment=3
        if (dirty){
            println("SOMETHING CHANGED .onFragmentWizard3Interaction()")

            if (recallItem.canILearn()) {
                recallItem.LEARN()
                //Learn Now button Pressed
                
                musicWizardDBViewModel.update(recallItem) //update Local DB
            }


        }else{
            println("NO CHANGE .onFragmentWizard3Interaction()")
        }


    }

    //endregion

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
    //region Menu



    private fun openBottomsheet() {

        var bottomlist:ArrayList<PieceDetailBottom> = ArrayList()

        var wizardPageNumber = 1

        when(supportFragmentManager.findFragmentById(R.id.wizard_container)) {
            is MusicPromptWizard2Fragment -> {
                wizardPageNumber = 2
            } is MusicFinalWizard3Fragment -> {
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
            bottomlist.add(PieceDetailBottom(CROP, Constants.MenuTitles.CROP))
            bottomlist.add(PieceDetailBottom(REPLACE_IMAGE, Constants.MenuTitles.REPLACE_IMAGE))
        }else{
            bottomlist.add(PieceDetailBottom(GET_IMAGE, Constants.MenuTitles.GET_IMAGE))
        }


        bottomlist.add(PieceDetailBottom(SCORE_PAGE, Constants.MenuTitles.SCORE_PAGE))


        bottomlist.add(PieceDetailBottom(DELETE_PHRASE, Constants.MenuTitles.DELETE_PHRASE, true))

        if (fileExists){
         bottomlist.add(PieceDetailBottom(REMOVE, Constants.MenuTitles.REMOVE, true))
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
            CROP -> {

                if (wizardPageNumber == 1){
                    val uri = LibraryFilesystem.readMainImage(recallItem.intUID)
                    CropImage.activity(uri).start(this);
                }else if (wizardPageNumber == 2){
                    val uri = LibraryFilesystem.readPromptImage(recallItem.intUID)
                    CropImage.activity(uri).start(this);
                } //no edit on page 3 - final

            }
            GET_IMAGE, REPLACE_IMAGE -> {
                CropImage.activity().start(this);
            }
            SCORE_PAGE -> {
                Log.w("menu","Select Score Page") }
            DELETE_PHRASE -> {
                //if last phrase in piece - can do it
                recallItem.recallGroup.let {
                    //TODO same ITEM different mem address
                    if (it.itemList.last().UID == recallItem.UID){

                        Constants.GlobalVariables.busesNoOrderFromDB.remove(recallItem) //global memory
                        it.removeItem(recallItem) //memory
                        musicWizardDBViewModel.delete(recallItem)
                        //recallItem = null
                        setResult(RESULT_OK, intent);
                        finish()
                    }else{

                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("This phrase cannot be deleted")
                        builder.setMessage("Only the last Phrase of a piece can be removed")
                        builder.setPositiveButton("OK", null)


                        builder.show()
                    }
                }

            }
            Constants.MenuIDs.REMOVE -> {

//                val frag=supportFragmentManager.findFragmentById(R.id.wizard_container)
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
                musicWizard1imageView.setImageURI(null)


            }
        }


    }








    //endregion


    override fun onBackPressed() {
        super.onBackPressed()
        val frag=supportFragmentManager.findFragmentById(R.id.wizard_container)
        when(frag) {
            is MusicPhraseWizard1Fragment -> addBottomDots(0)
            is MusicPromptWizard2Fragment -> addBottomDots(1)
            is MusicFinalWizard3Fragment -> addBottomDots(2)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

//called from toolbar crop icon
        if (requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {


            if (resultCode == Activity.RESULT_OK) {


                data?.let {
                    val result = CropImage.getActivityResult(it)
                    val bitmap = MediaStore.Images.Media.getBitmap(MyApplication.getAppContext().getContentResolver(), result.uri)

                    val frag=supportFragmentManager.findFragmentById(R.id.wizard_container)
                    when(frag) {
                        is MusicPhraseWizard1Fragment -> {
                            LibraryFilesystem.createImageMainMusicImage(this.recallItem.intUID,bitmap)
                            musicWizard1imageView.setImageURI(result.uri)
                        }
                        is MusicPromptWizard2Fragment -> {
                            LibraryFilesystem.createImagePromptImage(this.recallItem.intUID,bitmap)

                            val thumbImage = ThumbnailUtils.extractThumbnail(
                                bitmap,
                                ConstantsJava.THUMBNAIL_SIZE,
                                ConstantsJava.THUMBNAIL_SIZE
                            )

                            LibraryFilesystem.createImageThumbnail(this.recallItem.intUID,thumbImage)
                            musicWizard1imageView.setImageURI(result.uri)
                        }
                    }


                }

            }else{
                // error - cancelled
            }

        }


    }
}
