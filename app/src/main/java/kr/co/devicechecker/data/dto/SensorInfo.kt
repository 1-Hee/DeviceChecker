package kr.co.devicechecker.data.dto

data class SensorInfo(
    // 센서 변수
    var hasProximity:Boolean, // 근접 센서 여부
    var hasPressure:Boolean, // 기압 센서 여부
    var hasDistance:Boolean, // 거리 센서 여부
    var hasGyroscope:Boolean, // 자이로 스코프 센서 여부
    var hasAcceleration:Boolean, // 가속도 센서 여부
    var hasMagnetic:Boolean, // 자기장 센서 여부
    var hasTemperature:Boolean // 온도 센서 여부
)
