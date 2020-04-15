package com.johngoodstadt.memorize.fragments


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.johngoodstadt.memorize.R
import com.johngoodstadt.memorize.adapters.TodayRecyclerViewAdapter
import com.johngoodstadt.memorize.models.RecallItemRowItem
import com.johngoodstadt.memorize.utils.TodayItemsList
import com.johngoodstadt.memorize.viewmodels.TodayListViewModel
import kotlinx.android.synthetic.main.fragment_today.list

/**
 * A simple [Fragment] subclass.
 * Use the [TodayTabFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TodayTabFragment : Fragment() {

    lateinit var todayListVM: TodayListViewModel
    private var listener: OnListFragmentInteractionListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        todayListVM = activity?.run {
            ViewModelProviders.of(this)[TodayListViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

//        if(true){
//
//
//            if (view is RecyclerView) {
//                with(list) {
//
//                    layoutManager = LinearLayoutManager(context)
//                    adapter = TodayRecyclerViewAdapter(
//                        TodayItemsList.ITEMS,
//                        listener
//                    )
//                }
//            }
//
//        }else{
//            todayListVM.todayRecallItemList.observe(this, Observer<List<Any>> { sectionOfItems ->
//                // Update the UI
//                with(list) {
//
//                    layoutManager = LinearLayoutManager(context)
//                    adapter = TodayRecyclerViewAdapter(
//                        sectionOfItems,
//                        listener
//                    )
//                }
//            })
//            todayListVM.getStaticDataForMain()
//        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_today, container, false)


        if (view is RecyclerView) {
            val ad=TodayRecyclerViewAdapter(
                TodayItemsList.ITEMS,
                listener
            )
            ad.setHasStableIds(true)
            with(view) {

                layoutManager = LinearLayoutManager(context)

                adapter =ad


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
        fun newInstance() =
            TodayTabFragment()
    }
    interface OnListFragmentInteractionListener {

        fun onListFragmentInteraction(item: RecallItemRowItem)
    }
    fun refreshAdapter() {

        if (::todayListVM.isInitialized) {
            //todayListVM.getStaticDataForMain()
//list.adapter?.setHasStableIds(true)
            list.adapter?.notifyDataSetChanged()
        }



//
//        if (list != null ) {
//           // Toast.makeText(context,"refreshed list", Toast.LENGTH_SHORT).show()
//            list.adapter?.notifyDataSetChanged()
//        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

}
