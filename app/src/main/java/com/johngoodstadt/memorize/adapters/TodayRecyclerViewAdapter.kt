package com.johngoodstadt.memorize.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.johngoodstadt.memorize.Libraries.iconAsInitialsIfNecessary
import com.johngoodstadt.memorize.R
import com.johngoodstadt.memorize.databinding.FragmentPieceHeaderBinding
import com.johngoodstadt.memorize.databinding.TodayItemBinding
import com.johngoodstadt.memorize.models.SectionHeader


import com.johngoodstadt.memorize.fragments.RecallGroupsTabFragment.OnListFragmentInteractionListener
import com.johngoodstadt.memorize.fragments.TodayTabFragment
import com.johngoodstadt.memorize.models.RecallItem
import com.johngoodstadt.memorize.models.RecallItem.ITEM_JOURNEY_STATE_ENUM
import com.johngoodstadt.memorize.models.RecallItemRowItem
import kotlinx.android.synthetic.main.today_item.view.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 *
 */
class TodayRecyclerViewAdapter(
    private val mValues: List<Any>,
    private val mListener: TodayTabFragment.OnListFragmentInteractionListener?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as RecallItemRowItem
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val binding:TodayItemBinding=DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.today_item, parent, false)
            ViewHolder(binding.root,binding)

        } else {
            val binding:FragmentPieceHeaderBinding=DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.fragment_piece_header, parent, false)
            HeaderViewHolder(binding.root,binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val row = mValues[position]
        if (holder is ViewHolder && row is RecallItemRowItem )
        {
            with(holder.mView) {
                tag = row
                setOnClickListener(mOnClickListener)
            }
            row.image_uri = iconAsInitialsIfNecessary(row.image_uri, row.initials)
            holder.binding.recallItemRowItem = row
//            if (row.heading == null){
            if (row.journeyState == RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateInDepotSetup || row.journeyState == ITEM_JOURNEY_STATE_ENUM.JourneyStateInDepotLearning){
                val params = holder.titletextview.layoutParams as ConstraintLayout.LayoutParams
                params.bottomToBottom = holder.parent.id
                params.topMargin=0
                holder.titletextview.requestLayout()

            }

        }else if (holder is HeaderViewHolder && row is SectionHeader){
            holder.binding.header = row.title

        }
    }


    override fun getItemViewType(position: Int): Int {
        return if (mValues[position] is SectionHeader) 1 else 0
    }
    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(
        val mView: View,
        val binding: TodayItemBinding
    ) : RecyclerView.ViewHolder(mView) {
            val titletextview=  mView.titleTextView
            val parent=  mView.main_container
    }

    inner class HeaderViewHolder(
        val mView: View,
        val binding:FragmentPieceHeaderBinding) : RecyclerView.ViewHolder(mView)
    {

    }
}
