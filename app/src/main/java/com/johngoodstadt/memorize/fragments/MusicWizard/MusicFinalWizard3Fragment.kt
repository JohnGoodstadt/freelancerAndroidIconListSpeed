package com.johngoodstadt.memorize.fragments.MusicWizard

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.johngoodstadt.memorize.Libraries.LibraryFilesystem.*
import com.johngoodstadt.memorize.Libraries.getItemFromRecallGroupUID

import com.johngoodstadt.memorize.R
import com.johngoodstadt.memorize.models.RecallItem
import com.johngoodstadt.memorize.utils.Constants
//import com.johngoodstadt.memorize.utils.ConstantsJava.ARG_PARAM_TITLE
import com.johngoodstadt.memorize.utils.Constants.ARG_PARAM_UID
import com.johngoodstadt.memorize.utils.Constants.ARG_PARAM_busDepotUID
import kotlinx.android.synthetic.main.music_wizard3_fragment.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MusicFinalWizard3Fragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MusicFinalWizard3Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MusicFinalWizard3Fragment : Fragment() {
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

//        val recallItems = Constants.GlobalVariables.busesNoOrderFromDB.filter { it.UID == recallItemUID }
//        if (recallItems.count() > 0 ){
//            this.recallItem = recallItems.first()
//            Log.e("===> item UID",this.recallItem.title + " " + this.recallItem.UID)
//        }
        getItemFromRecallGroupUID(busDepotUID, recallItemUID)?.let{
            this.recallItem = it
            Log.e("===> item UID",this.recallItem.title + " " + this.recallItem.UID)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.music_wizard3_fragment, container, false)
    }

    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentWizard3Interaction(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setUpUI()


        setUpEvents()
    }

    private fun setUpUI() {
        val imagePrompt = getUriFromFilename(getFileNameThumbnailImage(recallItem.intUID))

        imagePrompt.let {
            promptImageView.setImageURI(it)
        }

        val imagePhrase = getUriFromFilename(getFileNameLargeFirstBusID(recallItem.intUID))

        imagePhrase.let {
            largeImageView.setImageURI(it)
        }
    }

    private fun setUpEvents() {
        btn_later.setOnClickListener{



            activity?.finish()
        }
        btn_learn_now.setOnClickListener{




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
        fun onFragmentWizard3Interaction(dirty: Boolean)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment MusicFinalWizard3Fragment.
         */
        @JvmStatic
        fun newInstance(busDepotUID: String, UID:String) =
            MusicFinalWizard3Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM_busDepotUID, busDepotUID)
                    putString(ARG_PARAM_UID, UID)
                }
            }
    }
}
