package com.johngoodstadt.memorize.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import com.johngoodstadt.memorize.BaseApplication
import com.johngoodstadt.memorize.R
import com.johngoodstadt.memorize.databinding.ActivityAddPieceBinding
import com.johngoodstadt.memorize.models.Piece
import com.johngoodstadt.memorize.models.RecallGroup

import com.johngoodstadt.memorize.tools.removeErrorOnTyping
import com.johngoodstadt.memorize.utils.Constants
import com.johngoodstadt.memorize.utils.Constants.GROUP_NAME_KEY
import com.johngoodstadt.memorize.viewmodels.PieceViewModel
import kotlinx.android.synthetic.main.activity_add_piece.*
import javax.inject.Inject

class AddPieceActivity : AppCompatActivity() {
    lateinit var binding:ActivityAddPieceBinding
    @Inject
    lateinit var viewModel: PieceViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_piece)

        (applicationContext as BaseApplication).getAppComponent()?.inject(this)
        setUpViewModel()
        setUpUI()
        setUpEvents()
        setUpPropertyChanges()
    }

    private fun setUpPropertyChanges() {
        viewModel.composer.addOnPropertyChangedCallback(object: Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {

                Log.d("composer changed",viewModel.composer.get())

            }

        })
    }

    private fun setUpEvents() {
        btn_create.setOnClickListener{view->
            validateInput()?.let {

                Toast.makeText(this@AddPieceActivity, it.composer+" - "+it.piece+" - "+it.movement, Toast.LENGTH_LONG).show()

                val pieceTitle = it.composer + " - " + it.piece+" - " + it.movement

                val data = Intent()
                data.putExtra(GROUP_NAME_KEY,pieceTitle)


                setResult(Activity.RESULT_OK,data)

                finish()
            }
        }
        btn_cancel.setOnClickListener{
            finish()
        }

        back.setOnClickListener{
            finish()
        }
    }

    private fun validateInput():Piece? {
        val composer = viewModel.composer.get()
        val piece = viewModel.piece.get()
        val movement = viewModel.movement.get()
        if (composer == null || composer.isEmpty()) {
            editText1.error = "Please enter valid value"
            return null
        }

        if (piece == null || piece.isEmpty()) {
            editText2.error = "Please enter valid value"
            return null
        }

        return Piece(composer,piece,movement)
    }

    private fun setUpUI() {
        editText1.removeErrorOnTyping()
        editText2.removeErrorOnTyping()
    }

    private fun setUpViewModel() {
        binding.viewModel=viewModel
    }



}
