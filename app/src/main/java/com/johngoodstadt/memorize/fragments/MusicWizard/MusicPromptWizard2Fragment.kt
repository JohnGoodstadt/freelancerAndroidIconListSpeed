package com.johngoodstadt.memorize.fragments.MusicWizard

//import com.johngoodstadt.memorize.utils.ConstantsJava.ARG_PARAM_TITLE

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.johngoodstadt.memorize.utils.ConstantsJava.THUMBNAIL_SIZE
import com.johngoodstadt.memorize.Libraries.LibraryFilesystem
import com.johngoodstadt.memorize.Libraries.LibraryFilesystem.*
import com.johngoodstadt.memorize.Libraries.LibraryJava
import com.johngoodstadt.memorize.Libraries.MyApplication
import com.johngoodstadt.memorize.Libraries.getItemFromRecallGroupUID
import com.johngoodstadt.memorize.R
import com.johngoodstadt.memorize.fragments.WizardBaseFragment
import com.johngoodstadt.memorize.models.RecallItem
import com.johngoodstadt.memorize.utils.Constants
import com.johngoodstadt.memorize.utils.Constants.ARG_PARAM_UID
import com.johngoodstadt.memorize.utils.Constants.ARG_PARAM_busDepotUID
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.music_wizard2_fragment.*
import kotlinx.android.synthetic.main.music_wizard2_fragment.btn_camera
import kotlinx.android.synthetic.main.music_wizard2_fragment.btn_continue
import kotlinx.android.synthetic.main.music_wizard2_fragment.btn_folder
import kotlinx.android.synthetic.main.music_wizard2_fragment.musicWizard1imageView
import kotlinx.android.synthetic.main.music_wizard2_fragment.textView14

//import sun.jvm.hotspot.utilities.IntArray


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

//private const val ARG_PARAM_UID = "param_uid"
/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MusicPhraseWizard1Fragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MusicPhraseWizard1Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MusicPromptWizard2Fragment : WizardBaseFragment() {
    //private var param1: String? = null
    private var recallItemUID: String? = null
    private var busDepotUID: String? = null
    private lateinit var recallItem: RecallItem

    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            recallItemUID = it.getString(ARG_PARAM_UID)
            busDepotUID = it.getString(Constants.ARG_PARAM_busDepotUID)

        }

        getItemFromRecallGroupUID(busDepotUID, recallItemUID)?.let{
            this.recallItem = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.music_wizard2_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        SetupUI()
        setUpEvents()
    }

    private fun SetupUI() {

        val suffix = LibraryJava.getSuffixLetter(recallItem.title)
        textView14.text = recallItem.title

        //1. if already have a thumbnail use it
        val promptfilename = getFileNameLargeMusicCueByBusID(recallItem.intUID)
        if (exists(promptfilename)) {
            getUriFromFilename(promptfilename).let {
                musicWizard1imageView.setImageURI(it)

                textView18.text = "Add a photo of the first bar of '${suffix}' as a prompt"
            }
        } else {

            //2. No? if we have the phrase use it
            val phrasefilename = getFileNameMainMusicImageByBusID(recallItem.intUID)
            if (exists(phrasefilename)) {
                getUriFromFilename(phrasefilename).let {
                    musicWizard1imageView.setImageURI(it)
                    textView18.text = "Crop the phrase to the first bar of '${suffix}' as a prompt"

                }
            } else {

                //3. no cue or phrase so if we have a photo of the full page - use it
                val count = getCountOfPhotoScorePages(busDepotUID)
                if (count == 0) {
                    //nothing to do
                } else if (count == 1) {
                    val scorefilename = getFileNameFirstScorePhotoByDepotUID(busDepotUID)
                    getUriFromFilename(scorefilename).let {
                        musicWizard1imageView.setImageURI(it)
                        textView18.text =
                            "Crop the score to the first bar of '${suffix}' as a prompt"
                    }
                } else {
                    //TODO: Still to do
                }
            }
        }
    }

    private fun setUpEvents() {
        btn_continue.setOnClickListener {

            //Temp check: main prompt but no thumbnail
            if (LibraryFilesystem.doesPromptImageExist(recallItem.intUID)){
                if (LibraryFilesystem.doesThumbnailImageExist(recallItem.intUID) == false){

                    val bitmap = (musicWizard1imageView.getDrawable() as BitmapDrawable).bitmap
                    val thumbImage = ThumbnailUtils.extractThumbnail(
                        bitmap,
                        THUMBNAIL_SIZE,
                        THUMBNAIL_SIZE
                    )

                    LibraryFilesystem.createImageThumbnail(this.recallItem.intUID,thumbImage)

                }
            }

            listener?.onFragmentWizard2Interaction(false)
        }
        btn_folder.setOnClickListener {
            if (checkReadPermission()) {
                dispatchGalleryPictureIntent()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.RequestCodes.REQUEST_STORAGE_PERMISSION
                )
            }
        }
        btn_camera.setOnClickListener {
            if (checkWritePermission()) {
                dispatchTakePictureIntent()
            } else {

                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    Constants.RequestCodes.REQUEST_WRITE_STORAGE_PERMISSION
                )
            }
        }
        musicWizard1imageView.setOnClickListener{

            val promptfilename = getFileNameLargeMusicCueByBusID(recallItem.intUID)
            if (exists(promptfilename)) { //edit it
                val uri = LibraryFilesystem.getUriFromFilename(promptfilename)
                CropImage.activity(uri).start(getContext()!!,this);
            }else{
                val phrasefilename = getFileNameMainMusicImageByBusID(recallItem.intUID)
                if (exists(phrasefilename)) {
                    val uri = LibraryFilesystem.getUriFromFilename(phrasefilename)
                    CropImage.activity(uri).start(getContext()!!,this);
                }
            }




        }

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        fun onFragmentWizard2Interaction(dirty: Boolean)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MusicPromptWizard2Fragment.
         */
        @JvmStatic
        fun newInstance(busDepotUID: String, UID: String) =
            MusicPromptWizard2Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM_busDepotUID, busDepotUID)
                    putString(ARG_PARAM_UID, UID)
                }
            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        if (requestCode == Constants.RequestCodes.REQUEST_CAMERA && resultCode == RESULT_OK) {

            musicWizard1imageView.setImageURI(Uri.parse(currentPhotoPath))

        } else if (requestCode == Constants.RequestCodes.REQUEST_GALLERY && resultCode == RESULT_OK) {

            val selectedImage = data?.data
            musicWizard1imageView.setImageURI(selectedImage)

            data?.let {
                val imageUri = it.getData()
                val bitmap = MediaStore.Images.Media.getBitmap(MyApplication.getAppContext().getContentResolver(), imageUri)

                LibraryFilesystem.createImagePromptImage(this.recallItem.intUID,bitmap)

                val thumbImage = ThumbnailUtils.extractThumbnail(
                    bitmap,
                    THUMBNAIL_SIZE,
                    THUMBNAIL_SIZE
                )

                LibraryFilesystem.createImageThumbnail(this.recallItem.intUID,thumbImage)

            }

        }else if (requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

//TODO: this might not get called from toolbar
            if (resultCode == RESULT_OK) {

                data?.let {
                    val result = CropImage.getActivityResult(it)

                    val bitmap = MediaStore.Images.Media.getBitmap(MyApplication.getAppContext().getContentResolver(), result.uri)

                    LibraryFilesystem.createImagePromptImage(this.recallItem.intUID,bitmap)

                    val thumbImage = ThumbnailUtils.extractThumbnail(
                        bitmap,
                        THUMBNAIL_SIZE,
                        THUMBNAIL_SIZE
                    )

                    LibraryFilesystem.createImageThumbnail(this.recallItem.intUID,thumbImage)


                    musicWizard1imageView.setImageURI(result.uri)
                }


            }else{
                // error - cancelled
            }

        }
    }

}
