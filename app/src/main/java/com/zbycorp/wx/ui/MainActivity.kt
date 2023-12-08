package com.zbycorp.wx.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.zbycorp.wx.R
import com.zbycorp.wx.databinding.ActivityMainBinding
import com.zbycorp.wx.utils.AccessUtil
import com.zbycorp.wx.utils.DyAccessUtil
import com.zbycorp.wx.utils.KsAccessUtil

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        EasyFloat.with(this).setShowPattern(ShowPattern.ALL_TIME).setLayout(R.layout.pop_window)
//            .show()
        binding.btnOpenKs.setOnClickListener {
            AccessUtil.updateTips("进入快手APP")
            KsAccessUtil.openKsApp(this)
        }
        binding.btnOpenDy.setOnClickListener {
            DyAccessUtil.openDyApp(this)
        }
        binding.btnSend.setOnClickListener {
            if (isAccessibilitySettingsOn(this@MainActivity)) {

            } else {
                AlertDialog.Builder(this@MainActivity)
                    .setMessage("请在无障碍服务中给该应用授权，否则无法使用该软件")
                    .setPositiveButton("设置") { _, _ ->
                        val accessibleIntent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        startActivity(accessibleIntent)
                    }
                    .setNegativeButton("取消") { _, _ -> onBackPressed() }
                    .show()
            }
        }

        AccessUtil.showWindowTips(this)
    }

    override fun onResume() {
        super.onResume()
        if (isAccessibilitySettingsOn(this)) {
            AccessUtil.updateTips("Demo演示操作准备中\n无障碍服务已开启")
        }
    }

    private fun isAccessibilitySettingsOn(context: Context): Boolean {
        var accessibilityEnabled = 0
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                context.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            )
        } catch (e: SettingNotFoundException) {
            Log.i("URL", "错误信息为：" + e.message)
        }

        if (accessibilityEnabled == 1) {
            val services =
                Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
            if (services != null) {
                return services.toLowerCase().contains(context.packageName.toLowerCase())
            }
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        KsAccessUtil.userProfileIsExecuteFinish = false
        KsAccessUtil.imChatIsExecuteFinish = false
    }

}
