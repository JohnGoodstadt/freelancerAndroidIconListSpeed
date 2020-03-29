package com.johngoodstadt.memorize.tools

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.johngoodstadt.memorize.R

@BindingAdapter(value = arrayOf("circleimg"), requireAll = false)
fun setImageUrl(imageView: ImageView, url: String?) {
    val context = imageView.context
    if (url != null && !url.isEmpty()) {
        Glide.with(context).load(url).circleCrop()
            .into(imageView)
    }

}

@BindingAdapter(value = arrayOf("circledrawable"), requireAll = false)
fun setImageUrlDrawable(imageView: ImageView, drawable: Int?) {
    val context = imageView.context
    if (drawable != null ) {
        Glide.with(context).load(drawable).circleCrop()
            .into(imageView)
    }

}
@BindingAdapter(value = arrayOf("circledrawable"), requireAll = false)
fun setImageUrlDrawable(imageView: ImageView, drawable: Uri?) {
    val context = imageView.context
    if (drawable != null ) {
        Glide.with(context).load(drawable).circleCrop()
            .into(imageView)
    }

}

@BindingAdapter(value = arrayOf("img"), requireAll = false)
fun setImageDrawable(imageView: ImageView, drawable: Int?) {
    val context = imageView.context
    if (drawable != null ) {
        Glide.with(context).load(drawable).circleCrop().placeholder(R.drawable.ic_action_settings)
            .into(imageView)
    }

}