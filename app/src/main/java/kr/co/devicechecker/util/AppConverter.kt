package kr.co.devicechecker.util

import kotlin.math.ceil
import kotlin.math.round
import kotlin.math.roundToInt


/**
 *
 * This object is a separately created class.
 *
 * Provides functions necessary
 * for unit conversion within the app.
 *
 * If transformation is required for given data within the app,
 * it can be added as a submethod of this object.
 *
 */
object AppConverter {

    fun parseKbUnit(data:String):String{
        val volumeUnit = data.split(" ")
        val volume = volumeUnit[0].toLong()
        // Timber.i("volume : %s", volume)
        return if(volume < 1024) data.uppercase()
        else if (volume/(1024) >= 1){ // MB
            val resultValue = ceil(volume.toDouble() / 1024).toInt()
            "$resultValue MB"
        }else {
            "UnKnown"
        }
    }

    fun parseByteUnit(data:Long):String{
        val mbUnit = (1024.0 * 1024.0)
        val gbUnit = (1024.0 * 1024.0 * 1024.0)
        return if(data  < mbUnit) { // KB
            "$data KB"
        }else if (data < gbUnit){ // MB
            val parsedValue = round(data / mbUnit).toInt()
            "$parsedValue MB"
        }else { // GB
            val parsedValue = ((data / gbUnit) * 100).roundToInt() / 100.0
            "$parsedValue GB"
        }
    }
}