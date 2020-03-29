package com.johngoodstadt.memorize.fragments

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.johngoodstadt.memorize.R
import com.johngoodstadt.memorize.databinding.BottomListItemBinding
import com.johngoodstadt.memorize.models.PieceDetailBottom
import kotlinx.android.synthetic.main.fragment_pieceaction_list_dialog.*

const val ARG_TITLE = "title"
const val ARG_BOTTOM_LIST = "bottom_list"

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    PieceActionListDialogFragment.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 *
 * You activity (or fragment) needs to implement [PieceActionListDialogFragment.Listener].
 */
class PieceActionListDialogFragment : BottomSheetDialogFragment() {
    private var mListener: Listener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pieceaction_list_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list.layoutManager = LinearLayoutManager(context)
        setUpEvents()
        list.adapter = arguments?.getParcelableArrayList<PieceDetailBottom>(ARG_BOTTOM_LIST)?.let { PieceActionAdapter(it) }
        val title = arguments?.getString(ARG_TITLE)
        textView13.text=title
    }

    private fun setUpEvents() {
        cancel_btn.setOnClickListener{
            dismiss()
        }
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        if (parent != null) {
            mListener = parent as Listener
        } else {
            mListener = context as Listener
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    interface Listener {
        fun onPieceActionClicked(bottomModel: PieceDetailBottom)
    }

    private inner class ViewHolder internal constructor(
        val mView: View,val binding:BottomListItemBinding
    ) : RecyclerView.ViewHolder(
        mView
    )
    private val mOnClickListener: View.OnClickListener
     init {
            mOnClickListener = View.OnClickListener { v ->
                val item = v.tag as PieceDetailBottom
                mListener?.onPieceActionClicked(item)
                dismiss()
            }
        }


    private inner class PieceActionAdapter internal constructor(private val bottomlist:ArrayList<PieceDetailBottom>) :
        RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding:BottomListItemBinding= DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.bottom_list_item, parent, false)
            return ViewHolder(binding.root,binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val bottomModel= bottomlist.get(position)
            with(holder.mView) {
                tag = bottomModel
                setOnClickListener(mOnClickListener)
            }
            holder.binding.bottomModel=bottomModel

        }

        override fun getItemCount(): Int {
            return bottomlist.size
        }
    }

    companion object {

        fun newInstance(list: ArrayList<out Parcelable>,title:String?): PieceActionListDialogFragment =
            PieceActionListDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_BOTTOM_LIST , list)
                    putString(ARG_TITLE , title)
                }
            }

    }
}
