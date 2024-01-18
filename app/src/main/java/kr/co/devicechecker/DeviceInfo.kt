package kr.co.devicechecker

data class DeviceInfo(
    val modelName:String,
    val androidVersion:String,
    val firmWareVersion:String,
    val buildNumber:String,
    val processor:String,
    var memoryInfo:String,
    var storageInfo:String
)
