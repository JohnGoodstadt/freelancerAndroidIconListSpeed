package com.johngoodstadt.memorize.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Icon
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.johngoodstadt.memorize.Libraries.MyApplication
import com.johngoodstadt.memorize.Libraries.iconAsInitialsIfNecessary
import com.johngoodstadt.memorize.R
import com.johngoodstadt.memorize.databinding.FragmentPieceBinding
import com.johngoodstadt.memorize.databinding.FragmentPieceHeaderBinding
import com.johngoodstadt.memorize.models.SectionHeader
import com.johngoodstadt.memorize.models.ReadyForReviewTableViewCell


import com.johngoodstadt.memorize.fragments.RecallGroupsTabFragment.OnListFragmentInteractionListener
import com.johngoodstadt.memorize.utils.Constants
import com.johngoodstadt.memorize.utils.writeTextOnDrawable
import java.io.ByteArrayOutputStream

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 *
 */
class RecallGroupsRecyclerViewAdapter(
    private val mValues: List<Any>,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as ReadyForReviewTableViewCell
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == 0) {
            val binding: FragmentPieceBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.fragment_piece, parent, false
            )
            ViewHolder(binding.root, binding)

        } else {
            val binding: FragmentPieceHeaderBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.fragment_piece_header, parent, false
            )
            HeaderViewHolder(binding.root, binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val row = mValues[position]
        if (holder is ViewHolder && row is ReadyForReviewTableViewCell) {
            with(holder.mView) {
                tag = row
                setOnClickListener(mOnClickListener)
            }

            if (Constants.GlobalVariables.hasImages&& row.image_path!=null) {
//               // row.image_path = Uri.parse("R.drawable.green_circle")
//                row.image_path = Uri.parse("android.resource://com.johngoodstadt.memorize/" + R.drawable.green_circle);
//            }else{

                row.image_path = iconAsInitialsIfNecessary(row.image_path, row.initials)
            } else {
                var icon: BitmapDrawable? = null;
                if (row.initials.isNullOrEmpty()) {
                    icon = MyApplication.getAppContext()
                        .writeTextOnDrawable(R.drawable.green_circle, "")
                } else {
                    icon = MyApplication.getAppContext()
                        .writeTextOnDrawable(R.drawable.green_circle, row.initials)
                }
                holder.imageView2.setImageBitmap(icon.bitmap)
                // row.image_path = iconAsInitialsIfNecessary( row.image_path , row.initials)
            }



            holder.binding.pieceItem = row

        } else if (holder is HeaderViewHolder && row is SectionHeader) {


            holder.binding.header = row.title

        }
    }


    override fun getItemViewType(position: Int): Int {
        return if (mValues[position] is SectionHeader) 1 else 0
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View, val binding: FragmentPieceBinding) :
        RecyclerView.ViewHolder(mView) {
        var imageView2 = binding.imageView2
    }

    inner class HeaderViewHolder(val mView: View, val binding: FragmentPieceHeaderBinding) :
        RecyclerView.ViewHolder(mView) {

    }

    override fun getItemId(position: Int): Long {
        if(mValues.get(position)is ReadyForReviewTableViewCell)
            return (mValues.get(position) as ReadyForReviewTableViewCell).uid.toLong()
        else
            return position.toLong()
    }

}
