package kr.co.devicechecker.ui.fragment.main

import android.content.Context
import android.os.Build
import android.view.Display
import android.view.WindowManager
import kr.co.devicechecker.BR
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.ui.BaseFragment
import kr.co.devicechecker.data.dto.Info
import kr.co.devicechecker.databinding.FragmentDeviceInfoBinding
import kr.co.devicechecker.util.AppUtil
import kr.co.devicechecker.util.PreferenceUtil
import kotlin.math.floor

class DeviceInfoFragment : BaseFragment<FragmentDeviceInfoBinding>() {

    private val deviceInfoList = mutableListOf<Info>()
    private val displayInfoList = mutableListOf<Info>()
    // 값 저장을 위한 prefs 변수
    private lateinit var prefs:PreferenceUtil

    override fun initViewModel() {
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_device_info)
            .addBindingParam(BR.deviceInfoList, deviceInfoList)
            .addBindingParam(BR.displayInfoList, displayInfoList)
    }

    override fun initView() {
        prefs = PreferenceUtil(requireContext())
        getDeviceInfo()
        getDisplayInfo()
    }

    override fun onPause() {
        super.onPause()
        val tag = "Device"
        val builder = StringBuilder()
        builder.append("[Device Info]\n")
        this.displayInfoList.forEach { info ->
            builder.append("$info\n")
        }
        this.deviceInfoList.forEach { info ->
            builder.append("$info\n")
        }
        prefs.setValue(tag, builder.toString())
    }

    private fun getDeviceInfo(){
        val emptyValue = "알 수 없음"
        this.deviceInfoList.clear()
        deviceInfoCommand.forEach { (infoName, data) ->
            val value = AppUtil.Command.executeAdbCommand(data).trim().ifBlank { emptyValue }
            val info = Info(
                infoName,
                if(value == "unknown") emptyValue else value.trim()
            )
            deviceInfoList.add(info)
        }
        mBinding.deviceInfoList = deviceInfoList
        mBinding.notifyChange()
    }
    private fun getDisplayInfo(){
        val emptyValue = "알 수 없음"
        this.displayInfoList.clear()
        displayInfoCommand.forEach { (infoName, data) ->
            // println("$infoName : "+ executeAdbCommand(data))
            val value = AppUtil.Command.executeAdbCommand(data).trim().ifBlank { emptyValue }
            val info = Info(
                infoName,
                if(value == "unknown") emptyValue else value
            )
            displayInfoList.add(info)
        }
        // 화면 주사율 정보
        val displayManager = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val defaultDisplay: Display = displayManager.defaultDisplay

        // 화면 해상도
        val resolution:String
        // 화면 주사율
        val refreshRate:Int

        // 화면 주사율
        // Android 5.0 이상에서만 지원
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val mode = defaultDisplay.mode
            refreshRate = floor(mode.refreshRate.toDouble()).toInt()
            resolution = "${mode.physicalWidth} x ${mode.physicalHeight}"
        } else {
            // Android 5.0 미만의 버전에서는 지원하지 않음
            refreshRate = 0
            resolution = "알 수 없음"
        }
        displayInfoList.add(
            Info(
                "해상도(Resolution)",
                resolution
            )
        )
        displayInfoList.add(
            Info(
            "화면 주사율(Refresh Rate)",
           "$refreshRate Hz"
        ))
        mBinding.displayInfoList = displayInfoList
        mBinding.notifyChange()
    }

    private val deviceInfoCommand = mapOf(
        Pair("모델(Model)", "getprop ro.product.model"), // 모델명
        Pair("제조사(Manufacturer)", "getprop ro.product.manufacturer"), // 제조사
        Pair("모뎀(Baseband)", "getprop ro.baseband"), // 모뎀 명
        Pair("모뎀 버전(Baseband Version)", "getprop gsm.version.baseband"), //  모뎀 버전
        Pair("Build ID", "getprop ro.build.display.id"), // BUILD ID
        Pair("펌웨어 버전(Boot Fingerprint)", "getprop ro.bootimage.build.fingerprint"), // 펌웨어 버전
        Pair("부트 로더(Boot Loader)", "getprop ro.bootloader"), // 부트 로더
        Pair("Build KEY", "getprop ro.build.id"), // BUILD KEY
        Pair("Android Version", "getprop ro.build.version.release"), // Android Version
        Pair("SDK INT", "getprop ro.build.version.sdk"), // SDK_INT
        Pair("SUPPORT_MIN_SDK", "getprop ro.build.version.min_supported_target_sdk"), // SUPPORT_MIN_SDK
        Pair("보안 패치 수준(Security Patch)", "getprop ro.build.version.security_patch"), // 보안 패치 수준 (security_patch)
        Pair("하드웨어 플랫폼(Hardware Platform)", "getprop ro.hardware"), // 하드웨어 플랫폼
        Pair("기본 통신 네트워크(CODE)", "getprop ro.telephony.default_network"), // 기본 통신 네트워크 (3G, 4G ? , Code 값으로 주어짐 )
        Pair("디버깅 모드(Debuggable)", "getprop ro.debuggable") // DEBUG_MODE 0 or 1
    )

    private val displayInfoCommand = mapOf(
        Pair("DPI(Dot Per Inch)", "getprop ro.sf.lcd_density"),
    )
}