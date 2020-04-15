package com.johngoodstadt.memorize.adapters

import android.graphics.drawable.BitmapDrawable
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.johngoodstadt.memorize.Libraries.MyApplication
import com.johngoodstadt.memorize.Libraries.iconAsInitialsIfNecessary
import com.johngoodstadt.memorize.R
import com.johngoodstadt.memorize.databinding.FragmentPieceHeaderBinding
import com.johngoodstadt.memorize.databinding.PhrasesItemBinding
import com.johngoodstadt.memorize.databinding.PhrasesOnelineRowItemBinding
//import com.johngoodstadt.memorize.databinding.PhrasesSetupItemBinding
import com.johngoodstadt.memorize.databinding.WaitingItemBinding
import com.johngoodstadt.memorize.fragments.PhrasesFragment


import com.johngoodstadt.memorize.fragments.RecallGroupsTabFragment.OnListFragmentInteractionListener
import com.johngoodstadt.memorize.models.*
import com.johngoodstadt.memorize.utils.Constants
import com.johngoodstadt.memorize.utils.ItemsInGroupList
import com.johngoodstadt.memorize.utils.writeTextOnDrawable
//import kotlinx.android.synthetic.main.today_item.view.*
import kotlinx.android.synthetic.main.phrases_item.view.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 *
 */
class RecallItemsRecyclerViewAdapter(
    private var mValues: List<Any>,
    private val mListener: PhrasesFragment.OnListFragmentInteractionListener?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val mOnClickListener: View.OnClickListener

    init {
        setHasStableIds(true)
        mOnClickListener = View.OnClickListener { v ->


            if (v.tag is RecallItemRowItem) {
                val item = v.tag as RecallItemRowItem
                mListener?.onListFragmentInteraction(item)
            }else  if (v.tag is OneLineTableViewCell) {
                val item = v.tag as OneLineTableViewCell
                mListener?.onListFragmentInteraction(item)
            }else  if (v.tag is WaitingForOrhersTableViewCell) {
                val item = v.tag as WaitingForOrhersTableViewCell
                mListener?.onListFragmentInteraction(item)
            }


            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {


        return if (viewType == Constants.ViewType.phrases) {

            val binding:PhrasesItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.phrases_item, parent, false)



            ViewHolder(binding.root,binding)

        } else if (viewType == Constants.ViewType.header) {

            val binding:FragmentPieceHeaderBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.fragment_piece_header, parent, false)

            HeaderViewHolder(binding.root,binding)
        } else if (viewType == Constants.ViewType.oneline) {

            val binding:PhrasesOnelineRowItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.phrases_oneline_row_item, parent, false)

            ViewHolderSetup(binding.root,binding)
        }else { //3 - waiting

            val binding:WaitingItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.waiting_item, parent, false)



            ViewHolderWaiting(binding.root,binding)
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
            if(row.image_uri==null)
            {
                var icon: BitmapDrawable? = null;
                if (row.initials.isNullOrEmpty()) {
                    icon = MyApplication.getAppContext()
                        .writeTextOnDrawable(R.drawable.green_circle, "")
                } else {
                    icon = MyApplication.getAppContext()
                        .writeTextOnDrawable(R.drawable.green_circle, row.initials)
                }
                holder.binding.imageView3.setImageBitmap(icon.bitmap)

            }
            else{
                row.image_uri = iconAsInitialsIfNecessary(row.image_uri, row.initials)
            }

            //row.image_uri = iconAsInitialsIfNecessary(row.image_uri, row.initials)
            holder.binding.recallItemRowItemViewModel = row
            val titleViewLayoutParams = holder.titletextview.layoutParams as ConstraintLayout.LayoutParams

        }
        else if (holder is ViewHolderSetup && row is OneLineTableViewCell){
            with(holder.mView) {
                tag = row
                setOnClickListener(mOnClickListener)
            }
            if(row.image_uri==null)
            {
                var icon: BitmapDrawable? = null;
                if (row.initials.isNullOrEmpty()) {
                    icon = MyApplication.getAppContext()
                        .writeTextOnDrawable(R.drawable.green_circle, "")
                } else {
                    icon = MyApplication.getAppContext()
                        .writeTextOnDrawable(R.drawable.green_circle, row.initials)
                }
                holder.binding.imageView3.setImageBitmap(icon.bitmap)

            }
            else{
                row.image_uri = iconAsInitialsIfNecessary(row.image_uri, row.initials)
            }

            holder.binding.vm = row
        }
        else if (holder is ViewHolderWaiting && row is WaitingForOrhersTableViewCell){
            with(holder.mView) {
                tag = row
                setOnClickListener(mOnClickListener)
            }

            if(row.image_uri==null)
            {
                var icon: BitmapDrawable? = null;
                if (row.initials.isNullOrEmpty()) {
                    icon = MyApplication.getAppContext()
                        .writeTextOnDrawable(R.drawable.green_circle, "")
                } else {
                    icon = MyApplication.getAppContext()
                        .writeTextOnDrawable(R.drawable.green_circle, row.initials)
                }
                holder.binding.imageView3.setImageBitmap(icon.bitmap)

            }
            else{
                row.image_uri = iconAsInitialsIfNecessary(row.image_uri, row.initials)
            }
            //row.image_uri = iconAsInitialsIfNecessary(row.image_uri, row.initials)


            holder.binding.vm = row
        }else
            if (holder is HeaderViewHolder && row is SectionHeader){
            holder.binding.header = row.title

        }
    }




    override fun getItemViewType(position: Int): Int {

        var returnValue = 0

        when (mValues[position]) {
            is SectionHeader -> {returnValue = Constants.ViewType.header}
            is RecallItemRowItem -> {returnValue = Constants.ViewType.phrases}
            is OneLineTableViewCell -> {returnValue = Constants.ViewType.oneline}
            is WaitingForOrhersTableViewCell -> {returnValue = Constants.ViewType.waiting}

        }

        return returnValue //if (mValues[position] is SectionHeader) 1 else 0
    }
    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(
        val mView: View,
        val binding: PhrasesItemBinding

    ) : RecyclerView.ViewHolder(mView) {

            val titletextview=  mView.titleTextView
            val parent=  mView.main_container




    }
    inner class ViewHolderSetup(
        val mView: View,
        val binding: PhrasesOnelineRowItemBinding

    ) : RecyclerView.ViewHolder(mView) {

        val titletextview=  mView.titleTextView
        val parent=  mView.main_container

    }
    inner class ViewHolderWaiting(
        val mView: View,
        val binding: WaitingItemBinding

    ) : RecyclerView.ViewHolder(mView) {

        val titletextview=  mView.titleTextView
        val parent=  mView.main_container

    }
    inner class HeaderViewHolder(
        val mView: View,
        val binding:FragmentPieceHeaderBinding) : RecyclerView.ViewHolder(mView)
    {

    }


    fun update(rg:RecallGroup){
        mValues = ItemsInGroupList.calcItems(rg)
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        var row=mValues.get(position)
        if(row is RecallItemRowItem)
           return position.toLong()
        else if(row is OneLineTableViewCell)
            return position.toLong()
        else if(row is WaitingForOrhersTableViewCell)
            return position.toLong()
        else
            return position.toLong()

    }

    fun setData(list: List<Any>) {
        mValues=list
        notifyDataSetChanged()
    }
}
