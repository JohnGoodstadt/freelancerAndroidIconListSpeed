package com.johngoodstadt.memorize.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.johngoodstadt.memorize.R
import com.johngoodstadt.memorize.adapters.RecallGroupsRecyclerViewAdapter
import com.johngoodstadt.memorize.models.ReadyForReviewTableViewCell
import com.johngoodstadt.memorize.utils.RecallGroupInSections
import kotlinx.android.synthetic.main.recall_group_list_fragment.*


/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [RecallGroupsTabFragment.OnListFragmentInteractionListener] interface.
 */

class RecallGroupsTabFragment : Fragment() {


    private var listener: OnListFragmentInteractionListener? = null
    private var refresh: Int? = 0
    //var adapter: PieceRecyclerViewAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.recall_group_list_fragment, container, false)

        if (view is RecyclerView) {
            var ad = RecallGroupsRecyclerViewAdapter(
                RecallGroupInSections.ITEMS,
                listener
            )
            ad.setHasStableIds(true)
            with(view) {


                layoutManager = LinearLayoutManager(context)
               adapter=ad

            }
        }



        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onResume() {
        super.onResume()
//        if (this.adapter != null) {
//            this.adapter.notifyDataSetChanged();
//        }
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: ReadyForReviewTableViewCell)
    }
    fun refreshAdapter() {

        if (list != null ) {
//   if(refresh==RecallGroupInSections.ITEMS.size){
//       RecallGroupInSections.ITEMS.size
//   }
            list.adapter?.notifyDataSetChanged()
        }
    }
    fun smoothScrollToBottom() {

        if (list != null ) {
            list.adapter.let {
                var msgCount = RecallGroupInSections.ITEMS.count()
//                if ( ConstantsJava.whichApp.isRunning == ConstantsJava.whichApp.target.yoga ){
//                    msgCount =  SummerizeRecallGroupsYoga.ITEMS.count()
//                }
                list.scrollToPosition(msgCount - 1)
            }
        }
    }
    companion object {


        @JvmStatic
        fun newInstance() =
            RecallGroupsTabFragment()
    }
}
