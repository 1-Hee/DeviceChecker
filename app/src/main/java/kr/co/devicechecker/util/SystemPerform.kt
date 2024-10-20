package kr.co.devicechecker.util

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import kr.co.devicechecker.R
import kr.co.devicechecker.data.dto.BatteryInfo
import kr.co.devicechecker.data.dto.CpuCoreInfo
import java.io.BufferedReader
import java.io.FileReader


/**
 * This object is a separately designed class
 * To extract and calculate the information needed by the fragment class.
 *
 * Ultimately, it is used to implement "DI(Dependency Injection)."
 *
 * For detailed usage examples, please refer to the SystemPerformFragment class.
 */
object SystemPerform {

    // Get Device's Chipset Brands And CPU Architecture (eg. qcom, arm64-v8a )
    fun getChipsetAndArchitecture(context: Context) : Array<String> {
        val mArchCmd = "getprop ro.product.cpu.abi"
        val mChipCmd = "getprop ro.hardware"

        val emptyValue = context.getString(R.string.txt_unknown)

        val mChipValue = Command.execute(mChipCmd).trim().ifBlank { emptyValue }
        val mArchValue = Command.execute(mArchCmd).trim().ifBlank { emptyValue }

        return arrayOf(mChipValue, mArchValue)
    }


    // Get Device's Core Information (eg. Minimum Core Hertz, Maximum Core Hertz )
    fun getCPUCoreSpecs(context: Context) : List<CpuCoreInfo> {
        // 코어수
        val coreInfo = Command
            .execute("cat sys/devices/system/cpu/present").split("-")
        val corNumber = coreInfo[0].trim().toInt() + coreInfo[1].trim().toInt() + 1 // number

        val emptyValue = context.getString(R.string.txt_unknown)
        val mCpuCoreInfoList = mutableListOf<CpuCoreInfo>()

        // corNumber
        for (idx in 0 until corNumber) {
            val cpuMinFreq = Command
                .execute("cat sys/devices/system/cpu/cpu$idx/cpufreq/cpuinfo_min_freq")
            val cpuMaxFreq = Command
                .execute("cat sys/devices/system/cpu/cpu$idx/cpufreq/cpuinfo_max_freq")
            val transitionLatency = Command
                .execute("cat sys/devices/system/cpu/cpu$idx/cpufreq/transition_latency")
            val minFreq = if (cpuMinFreq.isBlank()) {
                emptyValue
            } else {
                "${cpuMinFreq.trim().toLong() / 1000} MHz"
            }
            val maxFreq = if (cpuMaxFreq.isBlank()) {
                emptyValue
            } else {
                "${cpuMaxFreq.trim().toLong() / 1000} MHz"
            }
            val transLatency = if (transitionLatency.isBlank()) {
                emptyValue
            } else {
                "${transitionLatency.trim().toLong()} µs"
            }
            val cpuCoreInfo = CpuCoreInfo(
                "CPU $idx", minFreq, maxFreq, transLatency
            )
            mCpuCoreInfoList.add(cpuCoreInfo)
        }
        return mCpuCoreInfoList
    }


    // Get Device Battery Information..
    fun getBatteryStatus(context: Context): BatteryInfo? {
        // 배터리 상태를 확인하는 인텐트 필터 설정
        val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus: Intent? = context.registerReceiver(null, ifilter)

       return if(batteryStatus != null) {
           // 1. 배터리 충전 상태 가져오기
           val status: Int = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
           val isCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                   status == BatteryManager.BATTERY_STATUS_FULL

           // 2. 배터리 충전 방식 가져오기 (USB, AC)
           val chargePlug: Int = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
           val usbCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
           val acCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_AC

           // 3. 배터리 잔량 퍼센트 가져오기
           val level: Int = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
           val scale: Int = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
           val batteryPct: Float = level / scale.toFloat() * 100

           // 4. 배터리 최대 용량 가져오기
           val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
           val chargeCounter = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
           val batteryCapacityPercentage: Long = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

           // chargeCounter는 µAh 단위이므로 1000으로 나눠서 mAh로 변환
           val chargeCounterInmAh = chargeCounter / 1000
           // 잔여 배터리 용량(mAh) 계산 (현재 남은 용량을 비율로 나눔)
           val remainBatteryCapacity = chargeCounterInmAh * 100 / batteryCapacityPercentage

           val mChargeTypeStr = if (usbCharge) "USB" else if (acCharge) "AC" else "None"
           BatteryInfo(isCharging, mChargeTypeStr, batteryPct, remainBatteryCapacity)
       } else null
    }
}