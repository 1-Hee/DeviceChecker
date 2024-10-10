package kr.co.devicechecker.data.dto


data class BatteryInfo (
    val isCharging:Boolean = false, // 배터리 충전 상태
    val chargeType:String = "Unknown", // 배터리 충전 방식
    val level:Float = 0.0f,
    val capacity:Long = 0L,
)
