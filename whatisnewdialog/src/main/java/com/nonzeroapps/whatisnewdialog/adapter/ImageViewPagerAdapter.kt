package com.nonzeroapps.whatisnewdialog.adapter

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.nonzeroapps.whatisnewdialog.R
import com.nonzeroapps.whatisnewdialog.model.NewFeatureItem
import com.nonzeroapps.whatisnewdialog.util.Util
import java.util.ArrayList

class ImageViewPagerAdapter(
    private val mContext: Context,
    private val mNewFeatureItems: ArrayList<NewFeatureItem>,
    private val mUsePaletteForDescBackground: Boolean,
    private val mUsePaletteForImageBackground: Boolean,
) : ViewPagerAdapter() {
    private var finalHeight = 0
    private var finalWidth = 0

    override fun getItem(position: Int): View {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_view_pager_image, null)

        val linearLayout = view.findViewById<LinearLayout>(R.id.linearLayout)
        val imageView = view.findViewById<ImageView>(R.id.imageView)
        val progress = view.findViewById<View>(R.id.progress)
        val textViewDesc = view.findViewById<TextView>(R.id.textViewDesc)
        val textViewTitle = view.findViewById<TextView>(R.id.textViewTitle)

        val newFeatureItem = mNewFeatureItems[position]
        textViewDesc.text = newFeatureItem.featureDesc
        textViewTitle.text = newFeatureItem.featureTitle
        imageView.contentDescription = newFeatureItem.featureTitle

        progress.visibility = View.VISIBLE
        val vto = imageView.viewTreeObserver
        vto.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    imageView.viewTreeObserver.removeOnPreDrawListener(this)
                    finalHeight = imageView.measuredHeight
                    finalWidth = imageView.measuredWidth

                    val isGif = newFeatureItem.imageResource?.lowercase()?.endsWith(".gif") ?: false

                    val requestManager = Glide.with(mContext)
                    var requestOptions =
                        RequestOptions()
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)

                    if (finalHeight != 0 && finalWidth != 0) {
                        requestOptions = requestOptions.override(finalWidth, finalHeight)
                    }

                    if (isGif) {
                        val drawableTypeRequest =
                            requestManager
                                .setDefaultRequestOptions(requestOptions)
                                .asGif()
                                .load(newFeatureItem.imageResource)
                                .listener(
                                    object : RequestListener<GifDrawable> {
                                        override fun onLoadFailed(
                                            e: GlideException?,
                                            model: Any?,
                                            target: Target<GifDrawable>,
                                            isFirstResource: Boolean,
                                        ): Boolean {
                                            progress.visibility = View.GONE
                                            return false
                                        }

                                        override fun onResourceReady(
                                            resource: GifDrawable,
                                            model: Any,
                                            target: Target<GifDrawable>?,
                                            dataSource: DataSource,
                                            isFirstResource: Boolean,
                                        ): Boolean {
                                            progress.visibility = View.GONE
                                            val bitmap = resource.firstFrame
                                            putBackgroundColors(bitmap, imageView, linearLayout, textViewTitle, textViewDesc)
                                            return false
                                        }
                                    },
                                )

                        drawableTypeRequest.into(imageView)
                    } else {
                        var drawableTypeRequest =
                            if (newFeatureItem.imageResource == null) {
                                requestManager.setDefaultRequestOptions(requestOptions)
                                    .load(newFeatureItem.imageDrawableResource)
                            } else {
                                requestManager.setDefaultRequestOptions(requestOptions)
                                    .load(newFeatureItem.imageResource)
                            }
                        drawableTypeRequest =
                            drawableTypeRequest
                                .listener(
                                    object : RequestListener<Drawable> {
                                        override fun onLoadFailed(
                                            e: GlideException?,
                                            model: Any?,
                                            target: Target<Drawable>,
                                            isFirstResource: Boolean,
                                        ): Boolean {
                                            progress.visibility = View.GONE
                                            return false
                                        }

                                        override fun onResourceReady(
                                            resource: Drawable,
                                            model: Any,
                                            target: Target<Drawable>?,
                                            dataSource: DataSource,
                                            isFirstResource: Boolean,
                                        ): Boolean {
                                            progress.visibility = View.GONE
                                            val bitmap = (resource as BitmapDrawable).bitmap
                                            putBackgroundColors(bitmap, imageView, linearLayout, textViewTitle, textViewDesc)
                                            return false
                                        }
                                    },
                                )

                        drawableTypeRequest.into(imageView)
                    }
                    return true
                }
            },
        )

        return view
    }

    override fun getCount(): Int {
        return mNewFeatureItems.size
    }

    private fun putBackgroundColors(
        bitmap: Bitmap?,
        imageView: ImageView,
        linearLayout: LinearLayout,
        textViewTitle: TextView,
        textViewDesc: TextView,
    ) {
        val usePalette = mUsePaletteForDescBackground || mUsePaletteForImageBackground
        if (!usePalette) {
            return
        }

        if (bitmap != null && !bitmap.isRecycled) {
            Palette.from(bitmap).generate { palette ->
                if (palette == null) return@generate
                val whiteColor = ContextCompat.getColor(mContext, android.R.color.white)
                val blackColor = ContextCompat.getColor(mContext, android.R.color.black)
                val dominantColor = palette.getDominantColor(whiteColor)
                val contrastColor = Util.getContrastColor(dominantColor)

                if (mUsePaletteForImageBackground) {
                    val colorAnim = ObjectAnimator.ofInt(imageView, "backgroundColor", whiteColor, dominantColor)
                    colorAnim.setEvaluator(ArgbEvaluator())
                    colorAnim.duration = 500
                    colorAnim.start()
                }

                if (mUsePaletteForDescBackground) {
                    val colorAnim2 = ObjectAnimator.ofInt(linearLayout, "backgroundColor", whiteColor, dominantColor)
                    colorAnim2.setEvaluator(ArgbEvaluator())
                    colorAnim2.duration = 500
                    colorAnim2.start()

                    val colorAnim3 = ObjectAnimator.ofInt(textViewTitle, "textColor", blackColor, contrastColor)
                    colorAnim3.setEvaluator(ArgbEvaluator())
                    colorAnim3.duration = 500
                    colorAnim3.start()

                    val colorAnim4 = ObjectAnimator.ofInt(textViewDesc, "textColor", blackColor, contrastColor)
                    colorAnim4.setEvaluator(ArgbEvaluator())
                    colorAnim4.duration = 500
                    colorAnim4.start()
                }
            }
        }
    }
}
