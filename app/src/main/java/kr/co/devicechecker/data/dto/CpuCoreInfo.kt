package kr.co.devicechecker.data.dto

data class CpuCoreInfo(
    val name:String,
    val minHz:String,
    val maxHz:String,
    val transitionLatency:String
)