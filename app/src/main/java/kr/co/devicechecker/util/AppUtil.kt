package kr.co.devicechecker.util

import java.io.BufferedReader
import java.io.InputStreamReader
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.ceil

class AppUtil {
    object Command {
        fun executeAdbCommand(command: String): String {
            val process = Runtime.getRuntime().exec(command)
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }
            return output.toString()
        }
    }

    object Unit {
        fun parseUnit(data:String):String{
            val volumeUnit = data.split(" ")
            val volume = volumeUnit[0].toLong()
            // Timber.i("volume : %s", volume)
            return if(volume < 1024) data
            else if(volume/(1024*1024) >= 1){ // GB
                val resultValue = BigDecimal(volume.toDouble() / (1024 * 1024)).setScale(2, RoundingMode.HALF_UP)
                "$resultValue GB"
            }else if (volume/(1024) >= 1){ // MB
                val resultValue = ceil(volume.toDouble() / 1024).toInt()
                "$resultValue MB"
            }else {
                "UnKnown"
            }
        }
    }
}