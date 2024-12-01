package kr.co.devicechecker.data.dto

import kotlin.math.round

data class MemInfo(
    var availMem:Long,
    var totalMem:Long,
    var inUseMem:Long,
    var isLowMemory:Boolean
) {
    fun getPercent():Float{
        return (((totalMem-availMem).toFloat())/(totalMem.toFloat()))*100
    }

    fun getPercentInt():Int {
        val memPercent = this.getPercent()
        return (round(memPercent)).toInt()
    }
}
