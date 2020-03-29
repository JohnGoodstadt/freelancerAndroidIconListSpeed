package com.johngoodstadt.memorize.fragments.PromptAsImageWizard

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.johngoodstadt.memorize.Libraries.LibraryFilesystem
import com.johngoodstadt.memorize.Libraries.MyApplication
import com.johngoodstadt.memorize.Libraries.getItemFromRecallGroupUID
import com.johngoodstadt.memorize.databinding.PromptAsImageFragmentWizard2Binding
import com.johngoodstadt.memorize.fragments.WizardBaseFragment
import com.johngoodstadt.memorize.models.RecallItem
import com.johngoodstadt.memorize.utils.Constants
import com.johngoodstadt.memorize.utils.Constants.ARG_PARAM_UID
import com.johngoodstadt.memorize.utils.Constants.ARG_PARAM_busDepotUID
import kotlinx.android.synthetic.main.music_wizard2_fragment.*


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
class PromptAsImageWizard2Fragment : WizardBaseFragment() {
    //private var param1: String? = null
    private var recallItemUID: String? = null
    private var busDepotUID: String? = null
    private lateinit var recallItem: RecallItem

    private lateinit var  binding: PromptAsImageFragmentWizard2Binding

    private var listener: OnFragmentInteractionListener? = null
    private var originaltitle = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            recallItemUID = it.getString(ARG_PARAM_UID)
            busDepotUID = it.getString(Constants.ARG_PARAM_busDepotUID)

        }



        getItemFromRecallGroupUID(busDepotUID, recallItemUID)?.let{
            this.recallItem = it
            Log.e("===> item UID",this.recallItem.title + " " + this.recallItem.UID)
            originaltitle = recallItem.title
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        this.binding = PromptAsImageFragmentWizard2Binding.inflate(inflater, container, false)

        binding.recallItem = recallItem

        return binding.getRoot()

        //return inflater.inflate(com.johngoodstadt.memorize.R.layout.fragment_wizard2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val f = LibraryFilesystem.getFileNameLargeFirstBusID(recallItem.intUID)

        if (LibraryFilesystem.doesPromptImageExist(recallItem.intUID)){


            val path = LibraryFilesystem.getDocumentsDirectoryString()
            Log.e("file",path)
            //TODO: can this just pass ID to file and not read in whole IMAGE?
            val image = LibraryFilesystem.readPromptImage(recallItem.intUID)

            musicWizard1imageView.let {
                musicWizard1imageView.setImageURI(image)
            }



        }
        setUpEvents()
    }

    override fun onStop() {

        super.onStop()
    }



    private fun setUpEvents() {
        btn_continue.setOnClickListener {



            var isDirty = false

            if (originaltitle != recallItem.title){
                isDirty = true
            }

            listener?.onFragmentWizard2Interaction(isDirty)
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
            PromptAsImageWizard2Fragment().apply {
                arguments = Bundle().apply {
                   // putString(ARG_PARAM_TITLE, param1)
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

                LibraryFilesystem.createImagePromptImage(recallItem.intUID,bitmap)
            }

        }
    }

}
