package com.dhl.wanandroid.fragment

import android.content.SharedPreferences
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceFragmentCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.color.colorChooser
import com.dhl.wanandroid.R
import com.dhl.wanandroid.app.MyApplication
import com.dhl.wanandroid.util.ColorUtil
import com.dhl.wanandroid.util.SettingUtil
import com.dhl.wanandroid.widgt.IconPreference

/**
 * 系统设置
 * @author dhl
 * @date 2023 0303
 */
class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private val iconPreferenceKey by lazy {
        getString(R.string.IconPreference)
    }
    private lateinit var colorPreview: IconPreference


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        colorPreview = findPreference(iconPreferenceKey)!!
        colorPreview?.setOnPreferenceClickListener {
            activity?.let { activity ->
                MaterialDialog(activity).show {
                    title(R.string.choose_theme_color)
                    colorChooser(
                        ColorUtil.ACCENT_COLORS,
                        initialSelection = SettingUtil.getColor(),
                        subColors = ColorUtil.PRIMARY_COLORS_SUB
                    ) { dialog, color ->
                        SettingUtil.setColor(color)
                    }

                    positiveButton(R.string.done)
                    negativeButton(R.string.cancel)
                }
            }
            false
        }
    }


    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {

        if (key == iconPreferenceKey) {
            colorPreview.setView()
        }
    }


}