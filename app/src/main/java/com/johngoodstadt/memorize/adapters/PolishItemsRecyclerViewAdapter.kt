package com.johngoodstadt.memorize.adapters


//import kotlinx.android.synthetic.main.today_item.view.*

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.CheckedTextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.johngoodstadt.memorize.R
import com.johngoodstadt.memorize.databinding.FragmentPieceHeaderBinding
import com.johngoodstadt.memorize.databinding.PhrasesItemBinding
import com.johngoodstadt.memorize.databinding.PolishRowItemBinding
import com.johngoodstadt.memorize.fragments.PhrasesFragment
import com.johngoodstadt.memorize.fragments.RecallGroupsTabFragment.OnListFragmentInteractionListener
import com.johngoodstadt.memorize.models.CheckedTableViewCell
import com.johngoodstadt.memorize.models.SectionHeader
import com.johngoodstadt.memorize.utils.Constants
import kotlinx.android.synthetic.main.phrases_item.view.*
import kotlinx.android.synthetic.main.phrases_item.view.imageView3
import kotlinx.android.synthetic.main.phrases_item.view.main_container
import kotlinx.android.synthetic.main.phrases_item.view.titleTextView
import kotlinx.android.synthetic.main.polish_row_item.view.*
import androidx.appcompat.widget.AppCompatSpinner

//import androidx.databinding.InverseBindingAdapter
//import androidx.databinding.InverseBindingAdapter

//import com.sun.tools.doclets.internal.toolkit.util.DocPath.parent
import androidx.databinding.BindingAdapter

//import com.sun.tools.doclets.internal.toolkit.util.DocPath.parent







//import com.sun.tools.doclets.internal.toolkit.util.DocPath.parent





/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 *
 */
class PolishItemsRecyclerViewAdapter(
    private val mValues: List<Any>,
    private val mListener: PhrasesFragment.OnListFragmentInteractionListener?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->


            if (v.tag is CheckedTableViewCell) {
                val item = v.tag as CheckedTableViewCell

                mListener?.onListFragmentInteraction(item)
            }



        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {


        return if (viewType == Constants.ViewType.checkedline) {

            val binding:PolishRowItemBinding= DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.polish_row_item, parent, false)

            ViewHolderPolish(binding.root,binding)

        } else { //header

            val binding:FragmentPieceHeaderBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.fragment_piece_header, parent, false)

            HeaderViewHolder(binding.root,binding)

        }


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val row = mValues[position]

        if (holder is ViewHolderPolish && row is CheckedTableViewCell){
            with(holder.mView) {
                tag = row
                setOnClickListener(mOnClickListener)
            }
            holder.binding.vm = row
        }else if (holder is HeaderViewHolder && row is SectionHeader){
            holder.binding.header = row.title

        }
    }


    override fun getItemViewType(position: Int): Int {

        var returnValue = 0

        when (mValues[position]) {
            is CheckedTableViewCell -> {returnValue = Constants.ViewType.checkedline}

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
    inner class ViewHolderPolish(
        val mView: View,
        val binding: PolishRowItemBinding

    ) : RecyclerView.ViewHolder(mView) {

        val titletextview =  mView.titleTextView
        val checked_text_view = mView.checked_text_view
        val parent=  mView.main_container

    }

    inner class HeaderViewHolder(
        val mView: View,
        val binding:FragmentPieceHeaderBinding) : RecyclerView.ViewHolder(mView)
    {  }


//
//    @InverseBindingMethods({
//        @InverseBindingMethod(type = CheckedTableViewCell.class, attribute = "checked"),
//    })

//    @InverseBindingAdapter(attribute = "checked")
//    open fun getCheckedBool(b: Boolean): Boolean {
//        return ConvertColorEnumToInt(view.getColor())
//    }
//    @BindingAdapter("checked")
//    fun setChecked(view: CheckedTextView, checked: Boolean) {
//        if (checked != view.getChecked()) {
//            view.setChecked(checked)
//        }
//    }

}
