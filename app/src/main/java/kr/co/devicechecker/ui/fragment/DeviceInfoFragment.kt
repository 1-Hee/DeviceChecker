package kr.co.devicechecker.ui.fragment

import kr.co.devicechecker.BR
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.ui.BaseFragment
import kr.co.devicechecker.data.dto.Info
import kr.co.devicechecker.databinding.LayoutInfoFragmentBinding
import java.io.BufferedReader
import java.io.InputStreamReader

class DeviceInfoFragment : BaseFragment<LayoutInfoFragmentBinding>() {

    private val infoList = mutableListOf<Info>()
    override fun initViewModel() {
    }

    override fun getDataBindingConfig(): DataBindingConfig {

        return DataBindingConfig(R.layout.layout_info_fragment)
            .addBindingParam(BR.infoList, listOf<String>())
    }

    override fun initView() {
        getDeviceInfo()
    }

    private fun getDeviceInfo(){

    }

    fun executeAdbCommand(command: String): String {
        val process = Runtime.getRuntime().exec(command)
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val output = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            output.append(line).append("\n")
        }
        val result = output.toString()
        // process.waitFor()
        return output.toString()
    }

    private val commandList = arrayOf<String>(
        "getprop ro.product.model", // 모델 명
        "getprop ro.product.manufacturer", // 제조사
        "getprop ro.baseband", // 모뎀 명
        "getprop gsm.version.baseband", //  모뎀 버전
        "getprop ro.build.display.id", // BUILD ID
        "getprop ro.bootimage.build.fingerprint", // 펌웨어 버전
        "getprop ro.bootloader", // 부트 로더
        "getprop ro.build.id", // BUILD KEY
        "getprop ro.build.version.min_supported_target_sdk", // SUPPORT_MIN_SDK
        "getprop ro.build.version.release", // Android Version
        "getprop ro.build.version.sdk", // SDK_INT
        "getprop ro.build.version.security_patch", // 보안 패치 수준 (security_patch)
        "getprop ro.hardware", // 하드웨어 플랫폼
        "getprop ro.telephony.default_network", // 기본 통신 네트워크 (3G, 4G ? , Code 값으로 주어짐 )
        "uname -a", // KERNEL VERSION
        "getprop ro.debuggable" // DEBUG_MODE 0 or 1
    )
    fun test(){
        // 모델명 // ro.product.model
        // 제조사 // ro.product.manufacturer

        "BaseBand" // ro.baseband , 모뎀 명
        "band version" // gsm.version.baseband  모뎀 버전
        /*
        // 베이스 밴드 버전
//        infoList.add(Info(
//            "Baseband Version",
//            Build.getRadioVersion()
//        ))
         */
        "Build ID" // ro.build.display.id
        "Build FingerFrint" // ro.bootimage.build.fingerprint, 펌웨어 버전
        "Bootloader" // ro.bootloader
        "BUILD KEY" // ro.build.id
        "MIN SDK VERSION" // ro.build.version.min_supported_target_sdk
        "VERSION CODE" // ro.build.version.release , 9, 10, 11 ~
        "SDK INT"// ro.build.version.sdk 28, 34 etc.
        "Security Patch" // ro.build.version.security_patch
        "BOARD"     // ranchu     // ro.hardware 하드웨어 플렛폼
        "DEFAULT NETWORK" // 기본 통신 네트워크, ro.telephony.default_network
        "KERNEL VERSION"  // 커널 버전은 uname -a 명령어로!
        // 디버깅 모드, ro.debuggable

        // display...
        // https://play.google.com/store/apps/details?id=com.inkwired.droidinfo&hl=en&pli=1
        // https://play.google.com/store/apps/details?id=com.ytheekshana.deviceinfo&hl=ko-KR
        // LCD 밀도 ro.sf.lcd_density, DPI (dots per inch) 값
        // 해상도 , 명령어 wm size

        /* 화면 주사율
  val displayManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val defaultDisplay: Display = displayManager.defaultDisplay

    // Android 5.0 이상에서만 지원
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val mode = defaultDisplay.mode
        mode.refreshRate
    } else {
        // Android 5.0 미만의 버전에서는 지원하지 않음
        0.0f
    }
         */

        /// about Java

        /*
Java 버전:
String javaVersion = System.getProperty("java.version");
System.out.println("Java Version: " + javaVersion);

Java VM 버전:
String vmVersion = System.getProperty("java.vm.version");
System.out.println("Java VM Version: " + vmVersion);

Java VM 공급자:
String vmVendor = System.getProperty("java.vm.vendor");
System.out.println("Java VM Vendor: " + vmVendor);

Java VM 이름:
String vmName = System.getProperty("java.vm.name");
System.out.println("Java VM Name: " + vmName);
         */

    }
}