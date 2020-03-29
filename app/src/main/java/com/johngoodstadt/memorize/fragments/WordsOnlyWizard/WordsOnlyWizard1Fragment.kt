package com.johngoodstadt.memorize.fragments.WordsOnlyWizard

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent

import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.johngoodstadt.memorize.Libraries.LibraryFilesystem
import com.johngoodstadt.memorize.Libraries.MyApplication
import com.johngoodstadt.memorize.Libraries.getItemFromRecallGroupUID
import com.johngoodstadt.memorize.fragments.WizardBaseFragment
import com.johngoodstadt.memorize.models.RecallItem
import com.johngoodstadt.memorize.utils.Constants
import com.johngoodstadt.memorize.utils.Constants.ARG_PARAM_UID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.johngoodstadt.memorize.databinding.WordsOnlyFragmentWizard1Binding
import com.johngoodstadt.memorize.utils.Constants.ARG_PARAM_busDepotUID
import kotlinx.android.synthetic.main.music_wizard1_fragment.btn_camera
import kotlinx.android.synthetic.main.music_wizard1_fragment.btn_continue
import kotlinx.android.synthetic.main.music_wizard1_fragment.btn_folder
import kotlinx.android.synthetic.main.music_wizard1_fragment.musicWizard1imageView


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [WordsOnlyWizard1Fragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [WordsOnlyWizard1Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WordsOnlyWizard1Fragment : WizardBaseFragment() {
    //private var param1: String? = null
    private var recallItemUID: String? = null
    private var busDepotUID: String? = null
    private lateinit var recallItem: RecallItem

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private var listener: OnFragmentInteractionListener? = null
    private lateinit var  binding: WordsOnlyFragmentWizard1Binding
    private var originalWords = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            recallItemUID = it.getString(ARG_PARAM_UID)
            busDepotUID = it.getString(Constants.ARG_PARAM_busDepotUID)
        }

//        val recallItems = Constants.GlobalVariables.busesNoOrderFromDB.filter { it.UID == recallItemUID }
//        if (recallItems.count() > 0 ){
//            this.recallItem = recallItems.first()
//            Log.e("===> item UID",this.recallItem.title + " " + this.recallItem.UID)
//
//            originalWords = recallItem.words
//            coroutineScope.launch(Dispatchers.IO) {
//                updateYOGAToDBsuspend()
//                Log.e("ASNC","loaded")
//
//
//            }
//        }


        getItemFromRecallGroupUID(busDepotUID, recallItemUID)?.let{
            this.recallItem = it
            Log.e("===> item UID",this.recallItem.title + " " + this.recallItem.UID)

            originalWords = recallItem.words
            coroutineScope.launch(Dispatchers.IO) {
                updateYOGAToDBsuspend()
                Log.e("ASNC","loaded")


            }

        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.words_only_fragment_wizard1, container, false)
        this.binding = WordsOnlyFragmentWizard1Binding.inflate(inflater, container, false)

        binding.recallItem = recallItem


        return binding.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setUpEvents()
    }

    private fun setUpEvents() {
        btn_continue.setOnClickListener {
            var isDirty = false

            if (originalWords != recallItem.words){
                isDirty = true
            }

            listener?.onFragmentWizard1Interaction(isDirty)


            if (!checkWritePermission()) { //else the initials images will be empty

                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    Constants.RequestCodes.REQUEST_WRITE_STORAGE_PERMISSION
                )
            }

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
            WordsOnlyWizard1Fragment().apply {
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

                LibraryFilesystem.createImageMainImage(this.recallItem.intUID,bitmap)
            }


        }
    }

    private suspend fun updateYOGAToDBsuspend(){




//            MyApplication.getAppContext().let {
//                val db = Room.databaseBuilder(MyApplication.getAppContext(), AppDatabase::class.java,"memorize.db").build()
//
//                Log.e("THREAD","RECALL ITEM - SAVING")
//
//
//
//
//                db.recallGroupDAO().getAllRecallGroups().forEach()
//                {
//                    Log.e("Fetch Records", "UID:${it.UID}, title: ${it.title} ")
//
//                }
//
//                db.recallItemDAO().getAllRecallItems().forEach()
//                {
//                    Log.e("Fetch Records", "${it.UID} : ${it.title}, groupUID: ${it.recallGroupUUID}")
//
//
//                }
//
//                this.recallItem.title = this.recallItem.title + " 1"
//
//                db.recallItemDAO().saveRecallItem(this.recallItem)
//
//
////                activity.runOnUiThread(java.lang.Runnable {
////                    //sendNotification()
////                })
//
//            }







    }
}
