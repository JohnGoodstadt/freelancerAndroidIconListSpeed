package com.johngoodstadt.memorize.utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import org.jetbrains.anko.dip


fun Context.writeTextOnDrawable(drawableId: Int, text: String) =
    DrawableUtil.writeTextOnDrawableInternal(this, drawableId, text, 72, -2, 0)



object DrawableUtil {


    fun writeTextOnDrawableInternal(context: Context, drawableId: Int, text: String,
                                    textSizeDp: Int, horizontalOffset: Int, verticalOffset: Int): BitmapDrawable {


        var xOffset = 60.0F
        if (text.length == 1){
            xOffset = 80.0F
        }

        val bm = BitmapFactory.decodeResource(context.resources, drawableId)
            .copy(Bitmap.Config.ARGB_8888, true)

        val tf = Typeface.create("Roboto", Typeface.NORMAL)

        val paint = Paint()
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        paint.typeface = tf
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = context.dip(textSizeDp).toFloat()
        paint.setTextAlign(Paint.Align.CENTER);

        val textRect = Rect()
        paint.getTextBounds(text, 0, text.length, textRect)

        val canvas = Canvas(bm)

        //https://blog.danlew.net/2013/10/03/centering_single_line_text_in_a_canvas/
        val bounds = RectF(0.0F, 0.0F, canvas.getWidth().toFloat(), canvas.getHeight().toFloat())
        val textHeight: Float = paint.descent() - paint.ascent()
        val textOffset: Float = textHeight / 2 - paint.descent()

        //If the text is bigger than the canvas , reduce the font size
        if (textRect.width() >= canvas.getWidth() - 4)
        //the padding on either sides is considered as 4, so as to appropriately fit in the text
            paint.textSize = context.dip(12).toFloat()

        //Calculate the positions

//        var xPos = canvas.width.toFloat()/2 + horizontalOffset

        val xPos = xOffset//60.0F
        //"- ((paint.descent() + paint.ascent()) / 2)" is the distance from the baseline to the center.
        val yPos = (canvas.height / 2 - (paint.descent() + paint.ascent()) / 2) + verticalOffset

        //https://blog.danlew.net/2013/10/03/centering_single_line_text_in_a_canvas/
        canvas.drawText(text, bounds.centerX(), bounds.centerY() + textOffset, paint);
        //canvas.drawText(text, xPos, yPos, paint)

        return BitmapDrawable(context.resources, bm)
    }
}