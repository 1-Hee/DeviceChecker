package kr.co.devicechecker.data.dto

import android.graphics.drawable.Drawable

data class Permission(
    val name:String,
    val guideText:String,
    val iconDrawable:Drawable?,
    var isAllowed:Boolean = false,
)
// TODO Image 아이콘 어떻게 줄지?
