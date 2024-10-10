package kr.co.devicechecker.util

import java.io.BufferedReader
import java.io.InputStreamReader

object Command {
    fun execute(command: String): String {
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