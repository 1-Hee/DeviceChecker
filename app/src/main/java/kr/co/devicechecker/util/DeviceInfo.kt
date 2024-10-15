package kr.co.devicechecker.util

import android.content.ContentResolver
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.view.Display
import android.view.WindowManager
import kr.co.devicechecker.R
import kr.co.devicechecker.data.dto.Info
import kotlin.math.floor

/**
 * This object is a separately designed class
 * To extract and calculate the information needed by the fragment class.
 *
 * Ultimately, it is used to implement "DI(Dependency Injection)."
 *
 * For detailed usage examples, please refer to the DeviceInfoFragment class.
 */
object DeviceInfo {

    // Get Model Name & Android Version (eg. Android 14)
    fun getModelAndOsVersion(context: Context):Array<String>{
        // 모델명 추출
        val emptyValue = context.getString(R.string.txt_unknown)
        val modelNameCmd = "getprop ro.product.model"
        val mModelName =  Command.execute(modelNameCmd).trim().ifBlank { emptyValue }

        // Android OS Version 추출
        val mAndroidHeader = context.getString(R.string.h_android_version)
        val mOsVersion = Build.VERSION.RELEASE
        val mAndroidVersion = mAndroidHeader + mOsVersion

        return arrayOf(mModelName, mAndroidVersion)
    }

    // Get Device Snapshot Information (eg. Manufacture, Model, Secure Patch...)
    fun getDeviceSnapshotList(context: Context, resolver: ContentResolver) : List<Info> {
        // 기기 시리얼 넘버 => https://helloit.tistory.com/293
        val snapInfoList = mutableListOf<Info>()

        // Serial ID
        val hSerial = context.getString(R.string.h_serial_no)
        // feedback : https://developer.android.com/identity/user-data-ids
        val serialId = Settings.Secure.getString(resolver, Settings.Secure.ANDROID_ID)
        snapInfoList.add(Info(hSerial, serialId))

        // by commands...
        val emptyValue = context.getString(R.string.txt_unknown)
        val snapHeaderList = context.resources.getStringArray(R.array.snapshot_header_list)
        val snapCmdList = context.resources.getStringArray(R.array.snapshot_cmd_list)
        snapHeaderList.forEachIndexed { index, name ->
            val cmd = snapCmdList[index]
            val value = Command.execute(cmd).trim().ifBlank { emptyValue }
            val info = Info(name, if (value == "unknown") emptyValue else value.trim())
            snapInfoList.add(info)
        }
        return snapInfoList
    }

    // Get Device's Display Information (eg. DPI, Screen Resolution... )
    fun getDisplayInfo(context:Context):List<Info> {
        val emptyValue = context.getString(R.string.txt_unknown)
        val displayInfoList = mutableListOf<Info>()
        val displayHeaderList = context.resources.getStringArray(R.array.display_headers)
        val displayCmdList = context.resources.getStringArray(R.array.display_command)
        displayCmdList.forEachIndexed { index, cmd ->
            val name = displayHeaderList[index]
            val value = Command.execute(cmd).trim().ifBlank { emptyValue }
            val info = Info(name, if(value == "unknown") emptyValue else value.trim())
            displayInfoList.add(info)
        }
        // 화면 주사율 정보
        val displayManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
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
        displayInfoList.add(Info(displayHeaderList[1], resolution))
        displayInfoList.add(Info(displayHeaderList[2], "$refreshRate Hz"))
        return displayInfoList
    }

    // Get Device's Basic CPU Information (eg. Num of CPU core, CPU Speed )
    fun getProcessorInfo(context: Context):List<Info>{
        val emptyValue = context.getString(R.string.txt_unknown)
        val processorInfoList = mutableListOf<Info>()
        val processorHeaderList = context.resources.getStringArray(R.array.processor_headers)
        val processorCmdList = context.resources.getStringArray(R.array.processor_command)
        processorCmdList.forEachIndexed { index, cmd ->
            val name = processorHeaderList[index]
            val value = Command.execute(cmd).trim().ifBlank { emptyValue }
            val info = Info(name, value.trim())
            processorInfoList.add(info)
        }
        // 코어수
        val coreInfo = Command.execute("cat sys/devices/system/cpu/present").split("-")
        val corNumber = coreInfo[0].trim().toInt() + coreInfo[1].trim().toInt() + 1 // number
        val corNumInfo = Info(processorHeaderList[4], corNumber.toString())
        processorInfoList.add(corNumInfo)
        val cpuMinCMD = "cat sys/devices/system/cpu/cpufreq/policy0/cpuinfo_min_freq"
        val cpuMaxCMD = "cat sys/devices/system/cpu/cpufreq/policy0/cpuinfo_max_freq"
        val clockMin = Command.execute(cpuMinCMD) // KHz
        val clockMax = Command.execute(cpuMaxCMD) // KHz
        val clockSpeed = if(clockMax.isBlank() || clockMin.isBlank()){
            emptyValue
        }else{
            val clockMinMHz = (clockMin.trim().toInt())/1000
            val clockMaxMHz = (clockMax.trim().toInt())/1000
            "$clockMinMHz MHz ~ $clockMaxMHz MHz"
        }
        processorInfoList.add(Info(processorHeaderList[5], clockSpeed))
        return processorInfoList
    }

    // Get Device's Java Information (eg. kind of JVM, Java Version.. )
    fun getJavaInformation(context: Context):List<Info>{
        val emptyValue = context.getString(R.string.txt_unknown)
        val javaInfoList = mutableListOf<Info>()
        val javaHeaderList = context.resources.getStringArray(R.array.java_headers)
        val javaCmdList = context.resources.getStringArray(R.array.java_command)
        // get java info
        val javaVersion = System.getProperty("java.version")
        val javaVmVersion = System.getProperty("java.vm.version")
        val javaVmVendor = System.getProperty("java.vm.vendor")
        val javaVmName = System.getProperty("java.vm.name")
        val javaValueList = arrayOf(
            javaVersion.toString().trim().ifBlank { emptyValue },
            javaVmVersion.toString().trim().ifBlank { emptyValue },
            javaVmVendor.toString().trim().ifBlank { emptyValue },
            javaVmName.toString().trim().ifBlank { emptyValue }
        )
        javaValueList.forEachIndexed { index, value ->
            val name = javaHeaderList[index]
            val info = Info(name, value)
            javaInfoList.add(info)
        }
        val gap = javaValueList.size
        javaCmdList.forEachIndexed { index, cmd ->
            val name = javaHeaderList[index+gap]
            val value = Command.execute(cmd).trim()
            val info = Info(name, value.uppercase())
            javaInfoList.add(info)
        }
        return javaInfoList
    }


    // Get Other Device's Information (eg. Build ID, Kernel Information... )
    fun getOtherInfo(context: Context):List<Info>{
        val emptyValue = context.getString(R.string.txt_unknown)
        val deviceInfoList = mutableListOf<Info>()
        val deviceHeaderList = context.resources.getStringArray(R.array.other_info_headers)
        val deviceCmdList = context.resources.getStringArray(R.array.other_info_command)
        deviceHeaderList.forEachIndexed { index, name ->
            val cmd = deviceCmdList[index]
            val value = Command.execute(cmd).trim().ifBlank { emptyValue }
            val info = Info(name, if(value == "unknown") emptyValue else value.trim())
            deviceInfoList.add(info)
        }

        // 커널
        val hKernel = context.getString(R.string.h_kernel_info)
        val kernelInfoAll = Command.execute("uname -a")
        deviceInfoList.add(Info(hKernel, kernelInfoAll.trim()))
        return deviceInfoList
    }


    // 저장용 함수...
    fun saveDeviceData(context: Context, displayInfoList:List<Info>, deviceInfoList:List<Info>){
        val prefs = PreferenceUtil(context)
        val tag = "Device"
        val builder = StringBuilder()
        builder.append(context.getString(R.string.txt_h_save_device)).append("\n")
        displayInfoList.forEach { info ->
            builder.append("${info.name} : ${info.value}\n")
        }
        deviceInfoList.forEach { info ->
            builder.append("${info.name} : ${info.value}\n")
        }
        prefs.setValue(tag, builder.toString())
    }
}