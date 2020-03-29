package com.johngoodstadt.memorize.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.johngoodstadt.memorize.R
import com.johngoodstadt.memorize.databinding.FragmentPieceHeaderBinding
import com.johngoodstadt.memorize.databinding.SettingsItemBinding
import com.johngoodstadt.memorize.models.Settings
import com.johngoodstadt.memorize.models.SettingsSections
import com.johngoodstadt.memorize.utils.interfaces.ItemClicked


/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 *
 */
class SettingsRecyclerViewAdapter(
    private val mValues: List<Any>,private val mListener: ItemClicked
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Settings
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.showMessage(item.header)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == 0) {
            val binding:SettingsItemBinding=DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.settings_item, parent, false)
            ViewHolder(binding.root,binding)

        } else {
            val binding:FragmentPieceHeaderBinding=DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.fragment_piece_header, parent, false)
            HeaderViewHolder(binding.root,binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mValues[position]
        if (holder is ViewHolder && item is Settings)
        {
            holder.binding.settting=item
            with(holder.mView) {
                tag = item
                setOnClickListener(mOnClickListener)
            }
        }else if (holder is HeaderViewHolder && item is SettingsSections){
            holder.binding.header=item.section

        }
    }


    override fun getItemViewType(position: Int): Int {
        return if (mValues[position] is SettingsSections) 1 else 0
    }
    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View,val binding:SettingsItemBinding) : RecyclerView.ViewHolder(mView) {

    }

    inner class HeaderViewHolder(val mView: View,val binding:FragmentPieceHeaderBinding) : RecyclerView.ViewHolder(mView) {

    }
}
