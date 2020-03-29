package com.johngoodstadt.memorize.fragments.PromptAsImageWizard

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.johngoodstadt.memorize.Libraries.LibraryFilesystem
import com.johngoodstadt.memorize.Libraries.getItemFromRecallGroupUID

import com.johngoodstadt.memorize.R
//import com.johngoodstadt.memorize.fragments.WordsOnlyWizard.WordsOnlyWizard3Fragment
import com.johngoodstadt.memorize.models.RecallItem
import com.johngoodstadt.memorize.utils.Constants
//import com.johngoodstadt.memorize.utils.ConstantsJava.ARG_PARAM_TITLE
import com.johngoodstadt.memorize.utils.Constants.ARG_PARAM_UID
import com.johngoodstadt.memorize.utils.Constants.ARG_PARAM_busDepotUID
import kotlinx.android.synthetic.main.music_wizard3_fragment.*


class PromptAsImageWizard3Fragment : Fragment() {
    //private var param1: String? = null
    private var recallItemUID: String? = null
    private var busDepotUID: String? = null
    private lateinit var recallItem: RecallItem


    private var listener: OnFragmentInteractionListener? = null
    private lateinit var promptAsImageWizard3ViewModel: PromptAsImageWizard3ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            recallItemUID = it.getString(ARG_PARAM_UID)
            busDepotUID = it.getString(Constants.ARG_PARAM_busDepotUID)

        }

        getItemFromRecallGroupUID(busDepotUID, recallItemUID)?.let{
            this.recallItem = it
            Log.e("===> item UID",this.recallItem.title + " " + this.recallItem.UID)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        promptAsImageWizard3ViewModel = ViewModelProviders.of(this).get(
            PromptAsImageWizard3ViewModel::class.java)

        return inflater.inflate(R.layout.prompt_as_image_fragment_wizard3, container, false)
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

        val imagePrompt = LibraryFilesystem.getUriFromFilename(
            LibraryFilesystem.getFileNameSmallPromptImage(recallItem.intUID)
        )

        promptImageView.let {
            it.setImageURI(imagePrompt)
        }

        val imagePhrase = LibraryFilesystem.getUriFromFilename(
            LibraryFilesystem.getFileNameLargeFirstBusID(recallItem.intUID)
        )

        largeImageView.let {
            it.setImageURI(imagePhrase)
        }
    }
    private fun setUpEvents() {
        btn_later.setOnClickListener{

            listener?.onFragmentWizard3Interaction(false)

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
            PromptAsImageWizard3Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM_busDepotUID, busDepotUID)
                    putString(ARG_PARAM_UID, UID)
                }
            }
    }
}
