package com.johngoodstadt.memorize.fragments.MusicWizard

//import com.johngoodstadt.memorize.utils.ConstantsJava.ARG_PARAM_TITLE

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.johngoodstadt.memorize.Libraries.LibraryFilesystem
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
import kotlinx.android.synthetic.main.music_wizard1_fragment.btn_camera
import kotlinx.android.synthetic.main.music_wizard1_fragment.btn_continue
import kotlinx.android.synthetic.main.music_wizard1_fragment.btn_folder
import kotlinx.android.synthetic.main.music_wizard1_fragment.musicWizard1imageView
import kotlinx.android.synthetic.main.music_wizard1_fragment.textView14

//import sun.jvm.hotspot.utilities.IntArray


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER



/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MusicPhraseWizard1Fragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MusicPhraseWizard1Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MusicPhraseWizard1Fragment : WizardBaseFragment() {
    //private var param1: String? = null
    private var recallItemUID: String? = null
    private var busDepotUID: String? = null
    private lateinit var recallItem: RecallItem

    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            recallItemUID = it.getString(ARG_PARAM_UID)
            busDepotUID = it.getString(ARG_PARAM_busDepotUID)

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
        return inflater.inflate(R.layout.music_wizard1_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*
        1. if we have a phrase use it - else
        2. if we have a score use - else
        3. message user to add from libraryt/camera
         */

        val phrasefilename = LibraryFilesystem.getFileNameMainMusicImageByBusID(recallItem.intUID)
        //1. if we have a phrase use it
        if (LibraryFilesystem.exists(phrasefilename)) {
            LibraryFilesystem.getUriFromFilename(phrasefilename).let {
                musicWizard1imageView.setImageURI(it)
                textView14.text = "This is the phrase ${recallItem.title}"
            }
        }else{
            //3. no phrase so if we have a photo of the full page - use it
            val count = LibraryFilesystem.getCountOfPhotoScorePages(busDepotUID)
            //2. if we have a score use
            if (count > 0) {
                val scorefilename = LibraryFilesystem.getFileNameFirstScorePhotoByDepotUID(busDepotUID)
                LibraryFilesystem.getUriFromFilename(scorefilename).let {
                    musicWizard1imageView.setImageURI(it)
                    val suffix = LibraryJava.getSuffixLetter(recallItem.title)
                    textView14.text = "Crop the score to the phrase '${suffix}'"
                }
            } else {
                //3. message user to add from libraryt/camera
                //have not got phrase or score
                textView14.text = "Add an image for phrase '${recallItem.title}'"
            }
        }


        setUpEvents()
    }

    private fun setUpEvents() {
        btn_continue.setOnClickListener {
            listener?.onFragmentWizard1Interaction(false)
        }
        btn_folder.setOnClickListener {

            if (checkReadPermission()) {
//                CropImage.activity().start(getContext()!!, this);
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
//                CropImage.activity().start(getContext()!!, this);
                dispatchTakePictureIntent()
            } else {

                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    Constants.RequestCodes.REQUEST_WRITE_STORAGE_PERMISSION
                )
            }
        }
        musicWizard1imageView.setOnClickListener{

            val phrasefilename = LibraryFilesystem.getFileNameMainMusicImageByBusID(recallItem.intUID)
            //1. if we have a phrase use it
            if (LibraryFilesystem.exists(phrasefilename)) {
                val uri = LibraryFilesystem.readMainImage(recallItem.intUID)
                CropImage.activity(uri).start(getContext()!!,this)
            }else{
                //2. no phrase so if we have a photo of the full page - use it
                val count = LibraryFilesystem.getCountOfPhotoScorePages(busDepotUID)
                //2. if we have a score use
                if (LibraryFilesystem.getCountOfPhotoScorePages(busDepotUID) > 0) {
                    val scorefilename = LibraryFilesystem.getFileNameFirstScorePhotoByDepotUID(busDepotUID)
                    LibraryFilesystem.getUriFromFilename(scorefilename).let { uri ->
                        CropImage.activity(uri).start(getContext()!!, this)
                    }
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
        fun onFragmentWizard1Interaction(dirty: Boolean)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment MusicPhraseWizard1Fragment.
         */
        @JvmStatic
        fun newInstance(busDepotUID: String,UID:String) =
            MusicPhraseWizard1Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM_busDepotUID, busDepotUID)
                    putString(ARG_PARAM_UID, UID)

                }
            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == Constants.RequestCodes.REQUEST_CAMERA && resultCode == RESULT_OK) {

            val selectedImage = data?.data

            musicWizard1imageView.setImageURI(selectedImage)

            data?.let {
                val imageUri = it.getData()
                val bitmap = MediaStore.Images.Media.getBitmap(MyApplication.getAppContext().getContentResolver(), imageUri)

                LibraryFilesystem.createImageMainMusicImage(this.recallItem.intUID,bitmap)
            }
//            imageView6.setImageURI(Uri.parse(currentPhotoPath))


        } else if (requestCode == Constants.RequestCodes.REQUEST_GALLERY && resultCode == RESULT_OK) {
            val selectedImage = data?.data

            musicWizard1imageView.setImageURI(selectedImage)

            data?.let {
                val imageUri = it.getData()
                val bitmap = MediaStore.Images.Media.getBitmap(MyApplication.getAppContext().getContentResolver(), imageUri)

                LibraryFilesystem.createImageMainMusicImage(this.recallItem.intUID,bitmap)
            }
        }else if (requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {


            if (resultCode == RESULT_OK) {

                data?.let {
                    val result = CropImage.getActivityResult(it)

                    val bitmap = MediaStore.Images.Media.getBitmap(MyApplication.getAppContext().getContentResolver(), result.uri)
                    LibraryFilesystem.createImageMainMusicImage(this.recallItem.intUID,bitmap)

                    musicWizard1imageView.setImageURI(result.uri)
                }

            }else{
                // error - cancelled
            }

        }


    }


}
