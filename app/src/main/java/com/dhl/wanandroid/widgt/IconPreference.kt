package com.dhl.wanandroid.widgt

import android.content.Context
import android.util.AttributeSet
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.dhl.wanandroid.R
import com.dhl.wanandroid.util.SettingUtil



/**
 * @author dhl
 * @date 2023 0303
 */
class IconPreference(context: Context, attrs: AttributeSet) : Preference(context, attrs) {

    private var colorCircleView: ColorCircleView? = null

    init {

        widgetLayoutResource = R.layout.item_icon_preference_preview
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)
        colorCircleView = holder?.itemView?.findViewById(R.id.iv_preview)
        val color = SettingUtil.getColor()
        colorCircleView?.color = color
        colorCircleView?.border = color

    }

    fun setView() {
        val color = SettingUtil.getColor()
        colorCircleView?.color = color
        colorCircleView?.border = color
    }

}