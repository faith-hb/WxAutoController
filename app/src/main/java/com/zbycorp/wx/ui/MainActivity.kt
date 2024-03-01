package com.zbycorp.wx.ui

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.lzf.easyfloat.utils.DisplayUtils
import com.zbycorp.wx.contants.DyResId
import com.zbycorp.wx.databinding.ActivityMainBinding
import com.zbycorp.wx.utils.AccessUtil
import com.zbycorp.wx.utils.DyAccessUtil
import com.zbycorp.wx.utils.KsAccessUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate...")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AccessUtil.bindActivity(this)
        binding.btnOpenKs.setOnClickListener {
            if (safeCheckWhenOpenApp()) {
                AccessUtil.updateTips("进入快手APP")
                KsAccessUtil.openKsApp(this)
            }
        }
        binding.btnOpenDy.setOnClickListener {
            if (safeCheckWhenOpenApp()) {
                AccessUtil.updateTips("进入抖音APP")
                DyAccessUtil.openDyApp(this)
            }
            // 测试辅助框效果
//            val rect = Rect(0, 973, 1076, 1336)
//            val rect = Rect(0, 973, 1083, 1336)
//            val statusH = AccessUtil.getStatusBarHeight(this)
//            Log.i(TAG, "statusH=$statusH statusH1=${DisplayUtils.getStatusBarHeight(this)}")
//            val rect = Rect(0, 200 - statusH, 1080, 676)
//            AccessUtil.showAssistBox(this, rect)
        }
        binding.btnSend.setOnClickListener {
            if (isAccessibilitySettingsOn(this@MainActivity)) {
                AccessUtil.updateTips("服务已开启，可以打开抖音｜快手进行体验")
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
        AccessUtil.updateTips("Demo演示操作准备中\n请先开启无障碍服务")
        if (isAccessibilitySettingsOn(this)) {
            AccessUtil.updateTips("Demo演示操作准备中\n无障碍服务已开启")
            // 自动触发打开抖音动作
            GlobalScope.launch(Dispatchers.Main) {
                delay(2000)
                AccessUtil.updateTips("进入抖音APP")
                DyAccessUtil.openDyApp(this@MainActivity)
            }
        }
    }

    private fun safeCheckWhenOpenApp(): Boolean {
        if (!isAccessibilitySettingsOn(this)) {
//            AccessUtil.showToast(this, "请先开启无障碍服务")
            return false
        }
        return true
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

    override fun onBackPressed() {
        super.onBackPressed()
        Log.i(TAG, "onBackPressed...")
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy...")
        AccessUtil.dismissWindowTips()
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        am.killBackgroundProcesses(packageName)
    }

}
