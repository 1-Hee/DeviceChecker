package kr.co.devicechecker.util

import android.content.Context
import android.os.Environment
import android.widget.Toast
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import kotlin.math.ceil
import kotlin.math.round

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
        fun parseKbUnit(data:String):String{
            val volumeUnit = data.split(" ")
            val volume = volumeUnit[0].toLong()
            // Timber.i("volume : %s", volume)
            return if(volume < 1024) data.uppercase()
//            else if(volume/(1024*1024) >= 1){ // GB
//                val resultValue = BigDecimal(volume.toDouble() / (1024 * 1024)).setScale(2, RoundingMode.HALF_UP)
//                "$resultValue GB"
//            }
            else if (volume/(1024) >= 1){ // MB
                val resultValue = ceil(volume.toDouble() / 1024).toInt()
                "$resultValue MB"
            }else {
                "UnKnown"
            }
        }
        fun parseByteUnit(data:Long):String{
            val parsedValue = round(data / (1024.0 * 1024.0)).toInt()
            return "$parsedValue MB"
        }
    }

    object File {
        fun saveData(context: Context, content:String) {
            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
            val fileName = "device_info.txt"
            val downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDirectory, fileName)
            try {
                // 파일 쓰기
                FileOutputStream(file).use{ fileOutputStream ->
                    OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8).use { outputStreamWriter ->
                        outputStreamWriter.write(content)
                    }
                }
                Toast.makeText(context, "파일 저장 완료 ( ${file.absolutePath} )", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(context, "파일 저장 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }
}