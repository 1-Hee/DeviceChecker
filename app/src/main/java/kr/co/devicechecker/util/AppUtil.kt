package kr.co.devicechecker.util

import android.app.Activity
import android.content.Context
import android.hardware.SensorManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.view.Display
import android.view.WindowManager
import android.widget.Toast
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kr.co.devicechecker.R
import kr.co.devicechecker.data.dto.CpuCoreInfo
import kr.co.devicechecker.data.dto.Info
import kr.co.devicechecker.data.dto.SensorInfo
import kr.co.devicechecker.data.dto.StorageInfo
import timber.log.Timber
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round
import kotlin.math.roundToInt

class AppUtil {
    object Device { // about Device Info
        // 모든 기기 정보 불러오는 메서드
        fun getAllDeviceInfo(context:Context):List<Info>{
            val allDeviceInfoList = mutableListOf<Info>()
            val deviceList = getDeviceInfo(context)
            val displayList = getDisplayInfo(context)
            allDeviceInfoList.addAll(deviceList)
            allDeviceInfoList.addAll(displayList)
            return allDeviceInfoList
        }

        fun getDeviceInfo(context: Context):List<Info>{
            val emptyValue = context.getString(R.string.txt_unknown)
            val deviceInfoList = mutableListOf<Info>()
            val deviceHeaderList = context.resources.getStringArray(R.array.device_headers)
            val deviceCmdList = context.resources.getStringArray(R.array.device_command)
            deviceHeaderList.forEachIndexed { index, name ->
                val cmd = deviceCmdList[index]
                val value = Command.executeAdbCommand(cmd).trim().ifBlank { emptyValue }
                val info = Info(name, if(value == "unknown") emptyValue else value.trim())
                deviceInfoList.add(info)
            }
            return deviceInfoList
        }
        fun getDisplayInfo(context:Context):List<Info>{
            val emptyValue = context.getString(R.string.txt_unknown)
            val displayInfoList = mutableListOf<Info>()
            val displayHeaderList = context.resources.getStringArray(R.array.display_headers)
            val displayCmdList = context.resources.getStringArray(R.array.display_command)
            displayCmdList.forEachIndexed { index, cmd ->
                val name = displayHeaderList[index]
                val value = Command.executeAdbCommand(cmd).trim().ifBlank { emptyValue }
                val info = Info(name, if(value == "unknown") emptyValue else value.trim())
                displayInfoList.add(info)
            }
            // 화면 주사율 정보
            val displayManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val defaultDisplay: Display = displayManager.defaultDisplay
            // 화면 해상도
            val resolution:String
            // 화면 주사율
            val refreshRate:Int
            // 화면 주사율
            // Android 5.0 이상에서만 지원
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val mode = defaultDisplay.mode
                refreshRate = floor(mode.refreshRate.toDouble()).toInt()
                resolution = "${mode.physicalWidth} x ${mode.physicalHeight}"
            } else {
                // Android 5.0 미만의 버전에서는 지원하지 않음
                refreshRate = 0
                resolution = "알 수 없음"
            }
            displayInfoList.add(Info(displayHeaderList[1], resolution))
            displayInfoList.add(Info(displayHeaderList[2], "$refreshRate Hz"))
            return displayInfoList
        }
        fun saveDeviceData(context: Context, displayInfoList:List<Info>, deviceInfoList:List<Info>){
            val prefs = PreferenceUtil(context)
            val tag = "Device"
            val builder = StringBuilder()
            builder.append(context.getString(R.string.txt_h_save_device)).append("\n")
            displayInfoList.forEach { info ->
                builder.append("${info.name} : ${info.value}\n")
            }
            deviceInfoList.forEach { info ->
                builder.append("${info.name} : ${info.value}\n")
            }
            prefs.setValue(tag, builder.toString())
        }
    }
    object Processor {
        fun getProcessorInfo(context: Context):List<Info>{
            val emptyValue = context.getString(R.string.txt_unknown)
            val processorInfoList = mutableListOf<Info>()
            val processorHeaderList = context.resources.getStringArray(R.array.processor_headers)
            val processorCmdList = context.resources.getStringArray(R.array.processor_command)
            processorCmdList.forEachIndexed { index, cmd ->
                val name = processorHeaderList[index]
                val value = Command.executeAdbCommand(cmd).trim().ifBlank { emptyValue }
                val info = Info(name, value.trim())
                processorInfoList.add(info)
            }
            // 코어수
            val coreInfo = Command
                .executeAdbCommand("cat sys/devices/system/cpu/present").split("-")
            val corNumber = coreInfo[0].trim().toInt() + coreInfo[1].trim().toInt() + 1 // number
            val corNumInfo = Info(processorHeaderList[4], corNumber.toString())
            processorInfoList.add(corNumInfo)
            val cpuMinCMD = "cat sys/devices/system/cpu/cpufreq/policy0/cpuinfo_min_freq"
            val cpuMaxCMD = "cat sys/devices/system/cpu/cpufreq/policy0/cpuinfo_max_freq"
            val clockMin = Command
                .executeAdbCommand(cpuMinCMD) // KHz
            val clockMax = Command
                .executeAdbCommand(cpuMaxCMD) // KHz
            val clockSpeed = if(clockMax.isBlank() || clockMin.isBlank()){
                emptyValue
            }else{
                val clockMinMHz = (clockMin.trim().toInt())/1000
                val clockMaxMHz = (clockMax.trim().toInt())/1000
                "$clockMinMHz MHz ~ $clockMaxMHz MHz"
            }
            processorInfoList.add(Info(processorHeaderList[5], clockSpeed))
            // 커널
            val kernelInfoAll = Command
                .executeAdbCommand("uname -a")
            processorInfoList.add(Info(processorHeaderList[6], kernelInfoAll.trim()))
            return processorInfoList
        }
        fun getCpuCoreInfo(corNumber:Int, context: Context):List<CpuCoreInfo>{
            val emptyValue = context.getString(R.string.txt_unknown)
            val cpuCoreInfoList = mutableListOf<CpuCoreInfo>()
            // corNumber
            for(idx in 0..<corNumber){
                val cpuMinFreq = AppUtil.Command
                    .executeAdbCommand("cat sys/devices/system/cpu/cpu$idx/cpufreq/cpuinfo_min_freq")
                val cpuMaxFreq = AppUtil.Command
                    .executeAdbCommand("cat sys/devices/system/cpu/cpu$idx/cpufreq/cpuinfo_max_freq")
                val transitionLatency = AppUtil.Command
                    .executeAdbCommand("cat sys/devices/system/cpu/cpu$idx/cpufreq/transition_latency")
                val minFreq = if(cpuMinFreq.isBlank()){
                    emptyValue
                } else {
                    "${cpuMinFreq.trim().toLong()/1000} MHz"
                }
                val maxFreq = if(cpuMaxFreq.isBlank()){
                    emptyValue
                } else {
                    "${cpuMaxFreq.trim().toLong()/1000} MHz"
                }
                val transLatency = if (transitionLatency.isBlank()) {
                    emptyValue
                }else {
                    "${transitionLatency.trim().toLong()} µs"
                }
                val cpuCoreInfo = CpuCoreInfo(
                    "CPU $idx", minFreq, maxFreq, transitionLatency
                )
                cpuCoreInfoList.add(cpuCoreInfo)
            }
            return cpuCoreInfoList
        }
        fun getJavaInformation(context: Context):List<Info>{
            val emptyValue = context.getString(R.string.txt_unknown)
            val javaInfoList = mutableListOf<Info>()
            val javaHeaderList = context.resources.getStringArray(R.array.java_headers)
            val javaCmdList = context.resources.getStringArray(R.array.java_command)
            // get java info
            val javaVersion = System.getProperty("java.version")
            val javaVmVersion = System.getProperty("java.vm.version")
            val javaVmVendor = System.getProperty("java.vm.vendor")
            val javaVmName = System.getProperty("java.vm.name")
            val javaValueList = arrayOf(
                javaVersion.toString().trim().ifBlank { emptyValue },
                javaVmVersion.toString().trim().ifBlank { emptyValue },
                javaVmVendor.toString().trim().ifBlank { emptyValue },
                javaVmName.toString().trim().ifBlank { emptyValue }
            )
            javaValueList.forEachIndexed { index, value ->
                val name = javaHeaderList[index]
                val info = Info(name, value)
                javaInfoList.add(info)
            }
            val gap = javaValueList.size
            javaCmdList.forEachIndexed { index, cmd ->
                val name = javaHeaderList[index+gap]
                val value = Command.executeAdbCommand(cmd).trim()
                val info = Info(name, value.uppercase())
                javaInfoList.add(info)
            }
            return javaInfoList
        }
        fun saveProcessorInfo(
            context: Context,
            processorInfoList:List<Info>, cpuCoreList:List<CpuCoreInfo>, javaInfoList:List<Info>
        ){
            val prefs = PreferenceUtil(context)
            val tag = "Processor"
            val builder = StringBuilder()
            builder.append(context.getString(R.string.txt_h_save_processor)).append("\n")
            processorInfoList.forEach { info ->
                builder.append("${info.name} : ${info.value}\n")
            }
            builder.append("\n")
            val minHzHeader = context.getString(R.string.txt_h_core_min)
            val maxHzHeader = context.getString(R.string.txt_h_core_max)
            val latencyHeader = context.getString(R.string.txt_h_core_latency)
            cpuCoreList.forEach { sInfo ->
                builder.append("${sInfo.name}\n$minHzHeader${sInfo.minHz}\n$maxHzHeader${sInfo.maxHz}\n$latencyHeader${sInfo.transitionLatency}\n")
            }
            builder.append("\n")
            javaInfoList.forEach { info ->
                builder.append("${info.name} : ${info.value}\n")
            }
            prefs.setValue(tag, builder.toString())
        }
    }

    object Memory {
        fun getMemoryInfo(context: Context):List<Info>{
            val emptyValue = context.resources.getString(R.string.txt_unknown)
            val memoryInfoList = mutableListOf<Info>()
            val memoryMap = hashMapOf<String, String>()
            val memoryFieldMap = hashMapOf<String, String>()
            val memoryHeaderList = context.resources.getStringArray(R.array.memory_headers)
            val memoryKeyList = context.resources.getStringArray(R.array.memory_key_list)
            memoryKeyList.forEachIndexed { index, key ->
                memoryFieldMap[key] = memoryHeaderList[index]
            }
            val cmd = "cat /proc/meminfo"
            val mInfo = Command.executeAdbCommand(cmd)
            val mInfoList = mInfo.split("\n")
            for(i in mInfoList.indices){
                val info = mInfoList[i]
                if(info.isBlank()) continue
                val dataList = info.split(":")
                val field = dataList[0].trim()
                val value = dataList[1].trim()
                memoryMap[field] = value
            }
            // 조회 작업 시작
            memoryKeyList.forEach { key ->
                val field = memoryFieldMap[key]
                val value = memoryMap[key]
                field.let {
                    if(!it.isNullOrBlank()){
                        val info = if(value.isNullOrBlank()){
                            Info(it, emptyValue)
                        }else {
                            val parsedValue = Unit.parseKbUnit(value)
                            Info(it, parsedValue)
                        }
                        memoryInfoList.add(info)
                    }
                }
            }
            return memoryInfoList
        }

        // data 2. storage info (internel, externel)
        fun getStoragePathList(activity: Activity):List<Info>{
            val internalStorageURI = "/storage/emulated/0/Android/data/kr.co.devicechecker/files"
            val externalFilesDirs = activity.getExternalFilesDirs(null)
            val internalPath = mutableListOf<Info>()
            val externalPath = mutableListOf<Info>()
            val pathList = mutableListOf<Info>()
            for (externalFilesDir in externalFilesDirs) {
                Timber.i("path :${externalFilesDir.path}")
                if(externalFilesDir.path.equals(internalStorageURI)){
                    val info = Info(
                        activity.getString(R.string.txt_internal_name),
                        externalFilesDir.path
                    )
                    internalPath.add(info)
                }else {
                    val pathArr = externalFilesDir.path.split("/")
                    val pathName = try {
                        "${pathArr[2]}"
                    }catch (e:Exception){
                        activity.getString(R.string.txt_unknown)
                    }
                    val info = Info(pathName, externalFilesDir.path)
                    externalPath.add(info)
                }
            }
            pathList.addAll(internalPath)
            pathList.addAll(externalPath)
            return pathList
        }
        fun getInternalStorageInfo(pathList:Array<Info>):List<StorageInfo>{
            val internalStorageList = mutableListOf<StorageInfo>()
            pathList.forEach { info ->
                val statFs = StatFs(info.value)
                val iInternalAvailable = statFs.freeBytes
                val iInternalTotal = statFs.totalBytes
                val parsedAvailable = Unit.parseByteUnit(iInternalAvailable)
                val parseTotal = Unit.parseByteUnit(iInternalTotal)
                val storageInfo = StorageInfo(info.name, parsedAvailable, parseTotal)
                internalStorageList.add(storageInfo)
            }
            return internalStorageList
        }
        fun getExternalStorageInfo(pathList:Array<Info>):List<StorageInfo>{
            val externalStorageList = mutableListOf<StorageInfo>()
            pathList.forEach { info ->
                val statFs = StatFs(info.value)
                val iExternalAvailable = statFs.freeBytes
                val iExternalTotal = statFs.totalBytes
                val parsedAvailable = Unit.parseByteUnit(iExternalAvailable)
                val parseTotal = Unit.parseByteUnit(iExternalTotal)
                val storageInfo = StorageInfo(info.name, parsedAvailable, parseTotal)
                externalStorageList.add(storageInfo)
            }
            return externalStorageList
        }
        fun saveMemoryInfo(
            context: Context,
            memoryInfoList:List<Info>,
            internalStorageList:List<StorageInfo>,
            externalStorageList:List<StorageInfo>
        ){
            val prefs = PreferenceUtil(context)
            val tag = "Memory"
            val builder = StringBuilder()
            builder.append(context.getString(R.string.txt_h_save_memory)).append("\n")
            memoryInfoList.forEach { info ->
                builder.append("${info.name} : ${info.value}\n")
            }
            val nameHeader = context.getString(R.string.txt_h_storage_name)
            val availHeader = context.getString(R.string.txt_h_storage_avail)
            val totalHeader = context.getString(R.string.txt_h_storage_total)
            builder.append(context.getString(R.string.txt_h_save_storage)).append("\n")
            internalStorageList.forEach { info ->
                builder.append("$nameHeader\n${info.name}\n$availHeader\t${info.available}\n$totalHeader\t${info.total}\n")
            }
            externalStorageList.forEach { info ->
                builder.append("$nameHeader\n${info.name}\n$availHeader\t${info.available}\n$totalHeader\t${info.total}\n")
            }
            prefs.setValue(tag, builder.toString())
        }
    }
    object Sensor {
        fun getSensorInfo(activity: Activity):List<SensorInfo>{
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
        fun saveSensorInfo(
            context: Context,
            sensorInfoList:List<SensorInfo>
        ){
            val prefs = PreferenceUtil(context)
            val tag = "Sensor"
            val builder = StringBuilder()
            builder.append(context.getString(R.string.txt_h_save_sensor)).append("\n")
            val typeHeader = context.getString(R.string.txt_h_sensor_type)
            val vendorHeader = context.getString(R.string.txt_h_sensor_vendor)
            sensorInfoList.forEach { info ->
                builder.append("${info.sensorName} $typeHeader (${info.sensorType}}\n$vendorHeader : ${info.sensorVendor}\n")
            }
            prefs.setValue(tag, builder.toString())
        }
    }

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
    object File {
        fun saveAllHardwareInfoForText(activity:Activity){
            // display info
            val displayInfo = Device.getDisplayInfo(activity)
            // device info
            val deviceInfo = Device.getDeviceInfo(activity)
            /**
             *  디바이스 정보 저장
             */
            Device.saveDeviceData(activity, displayInfo, deviceInfo)
            // processor info
            val processorInfo = Processor.getProcessorInfo(activity)
            // cpu core Info
            val corNumber = try {
                processorInfo[4].value.toInt()
            }catch (e:Exception){
                0
            }
            val cpuCoreInfo = Processor.getCpuCoreInfo(corNumber, activity)
            // java info
            val javaInfo = Processor.getJavaInformation(activity)
            /**
             *  프로세서 정보 저장
             */
            Processor.saveProcessorInfo(
                activity, processorInfo, cpuCoreInfo, javaInfo
            )
            // memory Info
            val memoryInfo = Memory.getMemoryInfo(activity)
            // all path
            val pathList = Memory.getStoragePathList(activity)
            val internalPathList = mutableListOf<Info>()
            val externalPathList = mutableListOf<Info>()
            pathList.forEach { info ->
                if(info.name.contains("internal")){
                    internalPathList.add(info)
                }else {
                    externalPathList.add(info)
                }
            }
            // internal info
            val internalInfo = Memory.getInternalStorageInfo(internalPathList.toTypedArray())
            // external info
            val externalInfo = Memory.getExternalStorageInfo(externalPathList.toTypedArray())
            /**
             * 메모리 정보 저장
             */
            Memory.saveMemoryInfo(
                activity,
                memoryInfo, internalInfo, externalInfo
            )
            // sensor
            val sensorInfo = Sensor.getSensorInfo(activity)
            /**
             * 센서 정보 저장
             */
            Sensor.saveSensorInfo(activity, sensorInfo)
        }
        fun saveData(context: Context, content:String, type:String=".txt") {
            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
            val fileName = "device_info_${Build.MODEL.replace(" ", "").lowercase()}$type"
            val file = File(path, fileName)
            try {
                // 파일 쓰기
                FileOutputStream(file).use{ fileOutputStream ->
                    OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8).use { outputStreamWriter ->
                        outputStreamWriter.write(content)
                    }
                }
                val saveHeader = context.getString(R.string.txt_h_save_file)
                Toast.makeText(context, "$saveHeader $fileName", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                // e.printStackTrace()
                // 오류가 발생했을 때 오류 로그를 test_err_log.txt에 저장
                val errorLogFileName = "test_it_error_log.txt"
                val errorLogFile = File(path, errorLogFileName)
                try {
                    FileOutputStream(errorLogFile).use { fileOutputStream ->
                        OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8).use { outputStreamWriter ->
                            outputStreamWriter.write("Error Log:\n${e.message}\n\n")
                            // Stack Trace:${Log.getStackTraceString(e)}
                        }
                    }
                    val failHeader = context.getString(R.string.txt_h_fail_save)
                    Toast.makeText(context,
                        context.getString(R.string.txt_error_log_result, failHeader, errorLogFileName), Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    // 오류 로그를 저장하는 도중에 또 다른 오류가 발생한 경우
                    // e.printStackTrace()
                    val failHeader = context.getString(R.string.txt_h_fail_save)
                    Toast.makeText(context,
                        context.getString(R.string.txt_error_log_create, failHeader), Toast.LENGTH_SHORT).show()
                }
                val failHeader = context.getString(R.string.txt_h_fail_save)
                Toast.makeText(context, failHeader, Toast.LENGTH_SHORT).show()
            }
        }
    }

    object Json {
        private fun gsonWithoutHtmlEscaping(): Gson {
            return GsonBuilder()
                .disableHtmlEscaping()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .create()
        }

        fun toInfoJsonArray(infoList:List<Info>):JsonArray{
            val jsonArray = JsonArray()
            infoList.forEach { info ->
                val jsonObj = JsonObject()
                jsonObj.addProperty("name", info.name)
                jsonObj.addProperty("value", info.value)
                jsonArray.add(jsonObj)
            }
            return jsonArray
        }

        fun toJsonArray(jsonObjList:List<JsonObject>):JsonArray{
            val jsonArray = JsonArray()
            jsonObjList.forEach { jsonObj ->
                jsonArray.add(jsonObj)
            }
            return jsonArray
        }

        fun toJsonObject(key:String, value:String): JsonObject {
            val backupJson = JsonObject()
            backupJson.add(
                key,
                gsonWithoutHtmlEscaping().fromJson(
                    value,
                    JsonObject::class.java
                )
            )
            return backupJson
        }
    }
}