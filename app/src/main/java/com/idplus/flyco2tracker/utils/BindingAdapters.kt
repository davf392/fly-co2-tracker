package com.idplus.flyco2tracker.ui.adapter

import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.idplus.flyco2tracker.R
import com.squareup.picasso.Picasso


@BindingAdapter("cityPicture")
fun bindCityPictureUrlToImage(imageView: ImageView, url: String?) {

    val context = imageView.context
    if (url != null && url.isNotBlank()) {
        val widthImage = if(imageView.width > 0) imageView.width else "600"
        val urlWithHttp = "https:" + """([0-9]*px\-)""".toRegex().replace(url, "${widthImage}px-")
        Log.d("BindingAdapters", "url picture : $urlWithHttp")
        Picasso.with(context)
            .load(urlWithHttp)
            .placeholder(R.drawable.ic_broken_image)
            .centerCrop()
            .fit()
            .into(imageView)
    }
    else {
        imageView.setImageResource(R.drawable.ic_broken_image)
        imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
    }
}