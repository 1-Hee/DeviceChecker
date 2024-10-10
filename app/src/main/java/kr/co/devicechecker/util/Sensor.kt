package kr.co.devicechecker.util

import android.app.Activity
import android.content.Context
import android.hardware.SensorManager
import kr.co.devicechecker.data.dto.SensorInfo

/**
 * This object is a separately designed class
 * To extract and calculate the information needed by the fragment class.
 *
 * Ultimately, it is used to implement "DI(Dependency Injection)."
 *
 * For detailed usage examples, please refer to the SensorInfoFragment class.
 */
object Sensor {

    // Get Device's Sensor Info List....
    fun getSensorInfo(activity: Activity) : List<SensorInfo> {
        val sensorInfoList = mutableListOf<SensorInfo>()
        // 센서 매니저 가져오기
        val sensorManager = activity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // 사용 가능한 모든 센서 가져오기
        val sensorList: List<android.hardware.Sensor> = sensorManager.getSensorList(android.hardware.Sensor.TYPE_ALL)
        // 센서 정보 출력
        for (sensor in sensorList) {
            val sensorInfo = SensorInfo(sensor.name, sensor.type, sensor.vendor)
            sensorInfoList.add(sensorInfo)
        }
        return sensorInfoList
    }
}