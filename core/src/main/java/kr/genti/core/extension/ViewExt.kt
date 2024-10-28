package kr.genti.core.extension

import android.graphics.LinearGradient
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.view.View
import android.widget.TextView

inline fun View.setOnSingleClickListener(
    delay: Long = 1000L,
    crossinline block: (View) -> Unit,
) {
    var previousClickedTime = 0L
    setOnClickListener { view ->
        val clickedTime = System.currentTimeMillis()
        if (clickedTime - previousClickedTime >= delay) {
            block(view)
            previousClickedTime = clickedTime
        }
    }
}

fun View.setGusianBlur(radius: Float?) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (radius != null) {
            val blurEffect =
                RenderEffect.createBlurEffect(radius, radius, Shader.TileMode.REPEAT)
            this.setRenderEffect(blurEffect)
        } else {
            this.setRenderEffect(null)
        }
    }
}

fun TextView.setGradientText(startColor: Int, endColor: Int) {
    paint.shader = LinearGradient(
        0f,
        0f,
        paint.measureText(text.toString()),
        textSize,
        intArrayOf(startColor, endColor),
        null,
        Shader.TileMode.CLAMP
    )
}

fun TextView.setTextWithImage(text: String, imageResId: Int) {
    val spannableString = SpannableString("  $text")
    val imageSpan = ImageSpan(context, imageResId, ImageSpan.ALIGN_BOTTOM)
    spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    this.text = spannableString
}