package com.dhl.wanandroid.drawable.util

import java.util.*
import kotlin.math.abs

/**
 * @author dhl
 */
class ColorGenerator private constructor(private val mColors: List<Int>) {
    companion object {
        var DEFAULT: ColorGenerator? = null
        @JvmField
        var MATERIAL: ColorGenerator? = null
        fun create(colorList: List<Int>): ColorGenerator {
            return ColorGenerator(colorList)
        }

        init {
            DEFAULT = create(
                listOf(
                    -0xe9c9c,
                    -0xa7aa7,
                    -0x65bc2,
                    -0x1b39d2,
                    -0x98408c,
                    -0xa65d42,
                    -0xdf6c33,
                    -0x529d59,
                    -0x7fa87f
                )
            )
            MATERIAL = create(
                listOf(
                    -0x1a8c8d,
                    -0xf9d6e,
                    -0x459738,
                    -0x6a8a33,
                    -0x867935,
                    -0x9b4a0a,
                    -0xb03c09,
                    -0xb22f1f,
                    -0xb24954,
                    -0x7e387c,
                    -0x512a7f,
                    -0x759b,
                    -0x2b1ea9,
                    -0x2ab1,
                    -0x48b3
                )
            )
            //0xffa1887f,
            // 0xff90a4aeß
        }
    }

    private val mRandom: Random = Random(System.currentTimeMillis())
    val randomColor: Int
        get() = mColors[mRandom.nextInt(mColors.size)]

    fun getColor(key: Any): Int {
        return mColors[abs(key.hashCode()) % mColors.size]
    }

}