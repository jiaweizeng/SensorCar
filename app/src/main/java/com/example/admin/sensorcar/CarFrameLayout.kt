package com.example.admin.sensorcar

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * Created by zjw on 2018/9/28.
 */
class CarFrameLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    init {
        setWillNotDraw(false)

    }
}