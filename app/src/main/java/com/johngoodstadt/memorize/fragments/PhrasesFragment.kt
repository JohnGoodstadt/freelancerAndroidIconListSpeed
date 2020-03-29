package com.johngoodstadt.memorize.fragments


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator

import com.johngoodstadt.memorize.R
import com.johngoodstadt.memorize.adapters.RecallItemsRecyclerViewAdapter
import com.johngoodstadt.memorize.models.CheckedTableViewCell
import com.johngoodstadt.memorize.models.OneLineTableViewCell
import com.johngoodstadt.memorize.models.RecallItemRowItem
import com.johngoodstadt.memorize.models.WaitingForOrhersTableViewCell
import com.johngoodstadt.memorize.viewmodels.RecallGroupItemListViewModel
import kotlinx.android.synthetic.main.fragment_phrases.*

/**
 * A simple [Fragment] subclass.
 * Use the [PhrasesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PhrasesFragment : Fragment() {

    lateinit var viewModel: RecallGroupItemListViewModel
    private var listener: OnListFragmentInteractionListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = activity?.run {
            ViewModelProviders.of(this)[RecallGroupItemListViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
        viewModel.recallItemList.observe(this, Observer<List<Any>> { sectionOfItems ->
            // Update the UI
            with(list99) {
                //https://stackoverflow.com/questions/29331075/recyclerview-blinking-after-notifydatasetchanged
                list99.getItemAnimator()?.let {
                    if (it is SimpleItemAnimator) {
                        (it as SimpleItemAnimator).supportsChangeAnimations = false
                    }
                }

                layoutManager = LinearLayoutManager(context)
                adapter = RecallItemsRecyclerViewAdapter(
                    sectionOfItems,
                    listener
                )
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_today, container, false)

        if (view is RecyclerView) {
            //https://stackoverflow.com/questions/29331075/recyclerview-blinking-after-notifydatasetchanged
            view.getItemAnimator()?.let {
                if (it is SimpleItemAnimator) {
                    (it as SimpleItemAnimator).supportsChangeAnimations = false
                }
            }
        }

        return view
    }


    companion object {
        /**
         * Use this factory method to create a new instance
         * @return A new instance of fragment TodayFragment.
         */
        @JvmStatic
        fun newInstance() = PhrasesFragment()
    }
    interface OnListFragmentInteractionListener {

        fun onListFragmentInteraction(item: RecallItemRowItem)
        fun onListFragmentInteraction(item: OneLineTableViewCell)
        fun onListFragmentInteraction(item: CheckedTableViewCell)
        fun onListFragmentInteraction(item: WaitingForOrhersTableViewCell)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener99")
        }
    }

}
