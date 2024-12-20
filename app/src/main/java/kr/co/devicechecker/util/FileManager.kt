package kr.co.devicechecker.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.os.Build
import android.os.Environment
import android.widget.Toast
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kr.co.devicechecker.R
import kr.co.devicechecker.data.dto.BatteryInfo
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
import java.text.SimpleDateFormat
import java.util.Date


/**
 * This object is a separately created class.
 *
 * It is responsible for saving data loaded
 * within the app as data in various formats.
 */
object FileManager {

    @SuppressLint("SimpleDateFormat")
    fun saveTextFile(mActivity: Activity) {
        val context: Context = mActivity.baseContext
        val resolver: ContentResolver = mActivity.contentResolver

        val builder = StringBuilder()
        // Save device data via Object 'DeviceInfo'
        val mDeviceTitle = "[Device Info]\n"
        builder.append(mDeviceTitle)
        val mDeviceInfoList = mutableListOf<Info>()

        val arrModelAndOs = DeviceFetcher.getModelAndOsVersion(context)
        val listModelAndOs = listOf(
            // first list!
            Info("Model", arrModelAndOs[0]), // Model
            Info("Android", arrModelAndOs[1]), // Android
        )

        val mDeviceSnapshots = DeviceFetcher
            .getDeviceSnapshotList(context, resolver)
        val mDisplayInfoList = DeviceFetcher.getDisplayInfo(context)
        val mProcessInfoList = DeviceFetcher.getProcessorInfo(context)
        val mJavaInfoList = DeviceFetcher.getJavaInformation(context)
        val mOtherList = DeviceFetcher.getOtherInfo(context)
        // Add all Device Info!
        mDeviceInfoList.addAll(listModelAndOs)
        mDeviceInfoList.addAll(mDeviceSnapshots)
        mDeviceInfoList.addAll(mDisplayInfoList)
        mDeviceInfoList.addAll(mProcessInfoList)
        mDeviceInfoList.addAll(mJavaInfoList)
        mDeviceInfoList.addAll(mOtherList)

        mDeviceInfoList.forEach { it ->
            builder.append("${it.name} :\t${it.value}\n")
        }

        // Save device data via Object 'SystemPerform'
        val mSystemTitle = "[System Info]\n"
        builder.append("\n").append(mSystemTitle)

        val arrChipAndArch = SystemFetcher.getChipsetAndArchitecture(context)
        val listChipAndArch = listOf<Info>(
            Info("Chip Set", arrChipAndArch[0]), // chip set
            Info("Hardware", arrChipAndArch[1]), // Hardware(=board)
        )

        val mCpuCoreSpecs:List<CpuCoreInfo> = SystemFetcher.getCPUCoreSpecs(context)
        val mBatteryInfo:BatteryInfo? = SystemFetcher.getBatteryStatus(context)

        // Add all System Perform Information!
        listChipAndArch.forEach { it ->
            builder.append("${it.name} :\t${it.value}\n")
        }

        builder.append("\n<CPU Cores>\n")
        mCpuCoreSpecs.forEach {  it ->
            builder.append("${it.name}\n")
                .append("\tminHz : ${it.minHz}\n")
                .append("\tmaxHz : ${it.maxHz}\n")
        }

        if(mBatteryInfo != null){
            builder.append("\n\n<Battery Status>\n")
                .append("- charging status :\t${mBatteryInfo.isCharging}\n")
                .append("- chargeType :\t${mBatteryInfo.chargeType}\n")
                .append("- level :\t${mBatteryInfo.level}\n")
                .append("- capacity :\t${mBatteryInfo.capacity} mAh\n")
        }

        // Save device data via Object 'MemoryInfo'
        val mMemoryTitle = "[Memory Info]\n"
        builder.append("\n").append(mMemoryTitle)
        val mMemoryInfo = MemoryFetcher.getMemoryInfoList(context)

        val pathList = MemoryFetcher.getStoragePathList(mActivity)
        val mStorageList = MemoryFetcher.getStorageInfo(pathList.toTypedArray())

        // Add all Memory Information!
        mMemoryInfo.forEach { it ->
            builder.append("${it.name} :\t${it.value}\n")
        }

        builder.append("\n")
        mStorageList.forEach { it ->
            builder.append("<Storage ${it.name}>\n")
                .append("- available :\t${it.available}\n")
                .append("- total :\t${it.total}\n")
        }

        // Save device data via Object 'Sensor'
        val mSensorTitle = "[Sensor Info]\n"
        builder.append("\n").append(mSensorTitle)

        // Add All Sensor List
        val mSensorList = SensorFetcher.getSensorInfo(mActivity)
        mSensorList.forEach { it ->
            builder.append("#${it.sensorName}\n")
                .append("- type :\t${it.sensorType}\n")
                .append("- vendor :\t${it.sensorVendor}\n\n")
        }

        // 현재 날짜 형식 설정
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss") // 원하는 형식으로 변경 가능
        val currentDate = dateFormat.format(Date())

        // StringBuilder 마지막에 날짜 추가
        builder.append("\n\n\n")
            .append("Created at : $currentDate\n")
        saveFile(context, builder.toString()) // 저장 시작!
    }

    @SuppressLint("SimpleDateFormat")
    fun saveJsonFile(mActivity: Activity) {
        val context: Context = mActivity.baseContext
        val resolver: ContentResolver = mActivity.contentResolver

        val mJsonParent = JsonObject()
        // Save device data via Object 'DeviceInfo'
        val mDeviceTitle = "DeviceInfo"
        val mDeviceInfoList = mutableListOf<Info>()

        val arrModelAndOs = DeviceFetcher.getModelAndOsVersion(context)
        val listModelAndOs = listOf(
            // first list!
            Info("Model", arrModelAndOs[0]), // Model
            Info("Android", arrModelAndOs[1]), // Android
        )

        val mDeviceSnapshots = DeviceFetcher
            .getDeviceSnapshotList(context, resolver)
        val mDisplayInfoList = DeviceFetcher.getDisplayInfo(context)
        val mProcessInfoList = DeviceFetcher.getProcessorInfo(context)
        val mJavaInfoList = DeviceFetcher.getJavaInformation(context)
        val mOtherList = DeviceFetcher.getOtherInfo(context)
        // Add all Device Info!
        mDeviceInfoList.addAll(listModelAndOs)
        mDeviceInfoList.addAll(mDeviceSnapshots)
        mDeviceInfoList.addAll(mDisplayInfoList)
        mDeviceInfoList.addAll(mProcessInfoList)
        mDeviceInfoList.addAll(mJavaInfoList)
        mDeviceInfoList.addAll(mOtherList)

        // Device Info -> Json Data!
        try {
            val deviceObjList = mutableListOf<JsonObject>()
            mDeviceInfoList.forEach { info ->
                val jsonObj = info.toJsonObject()
                deviceObjList.add(jsonObj)
            }
            val deviceJsonArray:JsonArray = JsonParser.toJsonArray(deviceObjList)
            mJsonParent.add(mDeviceTitle, deviceJsonArray)
        }catch (e:Exception){
            Timber.e("[msg] %s", e.message)
        }

        // Save device data via Object 'SystemPerform'
        val mSystemTitle = "SystemInfo"
        val mSystemParent = JsonObject()

        val arrChipAndArch = SystemFetcher.getChipsetAndArchitecture(context)
        val listChipAndArch = listOf<Info>(
            Info("Chip Set", arrChipAndArch[0]), // chip set
            Info("Hardware", arrChipAndArch[1]), // Hardware(=board)
        )

        // Parse Chip & Architecture
        try {
            val chipsObjList = mutableListOf<JsonObject>()
            listChipAndArch.forEach { info ->
                val jsonObj = info.toJsonObject()
                chipsObjList.add(jsonObj)
            }
            val chipsJsonArray:JsonArray = JsonParser.toJsonArray(chipsObjList)
            mSystemParent.add("ChipArch", chipsJsonArray)
        }catch (e:Exception){
            Timber.e("[msg] %s", e.message)
        }

        // Add Core Info
        val mCpuCoreSpecs:List<CpuCoreInfo> = SystemFetcher.getCPUCoreSpecs(context)
        try {
            val mCpuCoreObjList = mutableListOf<JsonObject>()
            mCpuCoreSpecs.forEach { info ->
                val mCoreSpecObj = info.toJsonObject()
                mCpuCoreObjList.add(mCoreSpecObj)
            }
            val mCpuCoreObjArray:JsonArray = JsonParser.toJsonArray(mCpuCoreObjList)
            mSystemParent.add("CpuCoreInfo", mCpuCoreObjArray)
        }catch (e:Exception){
            Timber.e("[msg] %s", e.message)
        }

        // Add Battery info
        val mBatteryInfo:BatteryInfo? = SystemFetcher.getBatteryStatus(context)
        if(mBatteryInfo != null){
            val mBatteryJsonObj = mBatteryInfo.toJsonObject()
            mSystemParent.add("BatteryInfo", mBatteryJsonObj)
        }

        // Add System Info to Parent Json Object
        mJsonParent.add(mSystemTitle, mSystemParent)

        // Save device data via Object 'MemoryInfo'
        val mMemoryTitle = "MemoryInfo"
        val mMemoryParent = JsonObject()

        val mMemoryInfo = MemoryFetcher.getMemoryInfoList(context)
        val pathList = MemoryFetcher.getStoragePathList(mActivity)
        val mStorageList = MemoryFetcher.getStorageInfo(pathList.toTypedArray())

        // Add all Memory Information!
        // Add Memory Json Info to Memory Parent Json
        try {
            val mMemoryObjList = mutableListOf<JsonObject>()
            mMemoryInfo.forEach { info->
                val mMemoryObj = info.toJsonObject()
                mMemoryObjList.add(mMemoryObj)
            }

            val mMemoryObjArray:JsonArray = JsonParser.toJsonArray(mMemoryObjList)
            mMemoryParent.add("MemoryInfo", mMemoryObjArray)
        }catch (e:Exception){
            Timber.e("[msg] %s", e.message)
        }

        // Add Storage Json Info to Memory Parent Json
        try {
            val mStorageObjList = mutableListOf<JsonObject>()
            mStorageList.forEach { info ->
                val mStorageObj = info.toJsonObject()
                mStorageObjList.add(mStorageObj)
            }
            val mStorageObjArray:JsonArray = JsonParser.toJsonArray(mStorageObjList)
            mMemoryParent.add("StorageInfo", mStorageObjArray)
        }catch (e:Exception){
            Timber.e("[msg] %s", e.message)
        }

        // Add Memory Info to Parent Json Object
        mJsonParent.add(mMemoryTitle, mMemoryParent)

        // Save device data via Object 'Sensor'
        val mSensorTitle = "SensorInfo"

        // Add All Sensor List
        val mSensorList = SensorFetcher.getSensorInfo(mActivity)
        mSensorList.forEach { it ->
        }

        // Add Sensor Json Info to Parent
        try {
            val mSensorObjList = mutableListOf<JsonObject>()
            mSensorList.forEach { info ->
                val mSensorObj = info.toJsonObject()
                mSensorObjList.add(mSensorObj)
            }
            val mSensorObjArray:JsonArray = JsonParser.toJsonArray(mSensorObjList)
            mJsonParent.add(mSensorTitle, mSensorObjArray)
        }catch (e:Exception){
            Timber.e("[msg] %s", e.message)
        }

        // 현재 날짜 형식 설정
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss") // 원하는 형식으로 변경 가능
        val currentDate = dateFormat.format(Date())

        // 날짜 데이터 추가
        mJsonParent.addProperty("Date", currentDate)

        // Json 파일로 저장
        saveFile(context, mJsonParent.toString(), ".json")
    }

    // save HTML File
    fun saveHTMLFile(mActivity: Activity) {
        val context: Context = mActivity.baseContext
        val resolver: ContentResolver = mActivity.contentResolver

        val htmlBuilder = StringBuilder()
        // header init
        val mHtmlHeader = readRawTextFile(context, R.raw.html_head)
        htmlBuilder.append(mHtmlHeader)

        // Device Overview = Device Snapshot!

        // Load Snapshot Data...
        val mSnapInfoList = mutableListOf<Info>()
        val arrModelAndOs = DeviceFetcher.getModelAndOsVersion(context)
        val listModelAndOs = listOf(
            // first list!
            Info("Model", arrModelAndOs[0]), // Model
            Info("Android", arrModelAndOs[1]), // Android
        )
        val mDeviceSnapshots = DeviceFetcher
            .getDeviceSnapshotList(context, resolver)
        mSnapInfoList.addAll(listModelAndOs)
        mSnapInfoList.addAll(mDeviceSnapshots)

        val mSnapShotHtml =  getSnapShotHtml(mSnapInfoList)
        htmlBuilder.append(mSnapShotHtml)

        // Save device data via Object 'DeviceInfo'
        val mDeviceTitle = "Device Info"
        val mDeviceInfoList = mutableListOf<Info>()
        val mDisplayInfoList = DeviceFetcher.getDisplayInfo(context)
        val mProcessInfoList = DeviceFetcher.getProcessorInfo(context)
        val mJavaInfoList = DeviceFetcher.getJavaInformation(context)
        val mOtherList = DeviceFetcher.getOtherInfo(context)

        // Add all Device Info!
        mDeviceInfoList.addAll(listModelAndOs)
        mDeviceInfoList.addAll(mDeviceSnapshots)
        mDeviceInfoList.addAll(mDisplayInfoList)
        mDeviceInfoList.addAll(mProcessInfoList)
        mDeviceInfoList.addAll(mJavaInfoList)
        mDeviceInfoList.addAll(mOtherList)

        val mDeviceInfoHtml = getInfoListHtml(mDeviceTitle, mDeviceInfoList)
        htmlBuilder.append(mDeviceInfoHtml)

        // Save device data via Object 'SystemPerform'
        val mSystemTitle = "Hardware Info"

        val arrChipAndArch = SystemFetcher.getChipsetAndArchitecture(context)
        val listChipAndArch = listOf<Info>(
            Info("Chip Set", arrChipAndArch[0]), // chip set
            Info("Hardware", arrChipAndArch[1]), // Hardware(=board)
        )

        val mHardwareInfo = getInfoListHtml(mSystemTitle, listChipAndArch)
        htmlBuilder.append(mHardwareInfo)

        val mCoreTitle = "CPU Cores"
        val mCpuCoreSpecs:List<CpuCoreInfo> = SystemFetcher.getCPUCoreSpecs(context)
        val mCoresHtml = getCpuCoresHtml(mCoreTitle, mCpuCoreSpecs)
        htmlBuilder.append(mCoresHtml)

        val mBatteryInfo:BatteryInfo? = SystemFetcher.getBatteryStatus(context)

        if(mBatteryInfo != null){
            val mBatteryTitle = "Battery Status"
            val mBatterInfoList = listOf<Info>(
                Info("charging status", mBatteryInfo.isCharging.toString()),
                Info("chargeType", mBatteryInfo.chargeType),
                Info("level", mBatteryInfo.level.toString()),
                Info("capacity", mBatteryInfo.capacity.toString()),
            )
            val mBatteryHtml = getInfoListHtml(mBatteryTitle, mBatterInfoList)
            htmlBuilder.append(mBatteryHtml)
        }

        // Save device data via Object 'MemoryInfo'
        val mMemoryTitle = "Memory Info"
        val mMemoryInfo = MemoryFetcher.getMemoryInfoList(context)
        val mMemoryHtml = getInfoListHtml(mMemoryTitle, mMemoryInfo)
        htmlBuilder.append(mMemoryHtml)

        val mStorageTitle = "Storage Info"
        val pathList = MemoryFetcher.getStoragePathList(mActivity)
        val mStorageList = MemoryFetcher.getStorageInfo(pathList.toTypedArray())
        val mStorageHtml = getStorageHtml(mStorageTitle, mStorageList)
        htmlBuilder.append(mStorageHtml)

        // Save device data via Object 'Sensor'
        val mSensorTitle = "Sensor Info"
        val mSensorList = SensorFetcher.getSensorInfo(mActivity)
        val mSensorHtml = getSensorHtml(mSensorTitle, mSensorList)
        htmlBuilder.append(mSensorHtml)

        // 현재 날짜 형식 설정
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss") // 원하는 형식으로 변경 가능
        val currentDate = dateFormat.format(Date())

        val timeStampHtml =  """
                <div class="signature">
                    <p>발행자 : Test it Developer</p>
                    <p>발행 일자 : ${currentDate}</p>
                </div>
        """.trimIndent()

        val mHtmlMiddle = readRawTextFile(context, R.raw.html_middle)
        val mHtmlTail = readRawTextFile(context, R.raw.html_tail)
        htmlBuilder.append(mHtmlMiddle)
        htmlBuilder.append(timeStampHtml)
        htmlBuilder.append(mHtmlTail)
        saveFile(context,   htmlBuilder.toString(), ".html") // 저장 시작!
    }


    private fun getSensorHtml(tag: String, sensorList:List<SensorInfo>) : String {
        val sensorBuilder = StringBuilder()

        val mSensorHeader = """
               <div class="section-title">
                    <h2>${tag}</h2>
               </div>
               
               <div class="tripple-info">
                    <table>
                        <tr>
                            <th>Sensor Name</th>
                            <th>Type</th>
                            <th>Vendor</th>
                        </tr>            
        """.trimIndent()

        val mSensorFormat = """            
                <tr>
                    <td data-label="이름">%s</td>
                    <td data-label="값 1">%s</td>
                    <td data-label="값 2">%s</td>
                </tr>            
        """.trimIndent()

        val mSensorTail = """
                </table>
            </div>
        """.trimIndent()

        sensorBuilder.append(mSensorHeader)

        sensorList.forEach { it ->
            val mSensorHtml = String
                .format(mSensorFormat, it.sensorName, it.sensorType, it.sensorVendor)
            sensorBuilder.append(mSensorHtml)
        }
        sensorBuilder.append(mSensorTail)

        return sensorBuilder.toString()
    }

    private fun getStorageHtml(tag: String, storageList:List<StorageInfo>) : String {
        val storageBuilder = StringBuilder()

        val mStorageHeader = """
               <div class="section-title">
                    <h2>${tag}</h2>
               </div>
                              
               <div class="tripple-info">
                    <table>
                        <tr>
                            <th>Storage Name</th>
                            <th>Available</th>
                            <th>Total</th>
                        </tr>            
        """.trimIndent()

        val mStorageFormat = """            
                <tr>
                    <td data-label="이름">%s</td>
                    <td data-label="값 1">%s</td>
                    <td data-label="값 2">%s</td>
                </tr>            
        """.trimIndent()

        val mStorageTail = """
                </table>
            </div>
        """.trimIndent()

        storageBuilder.append(mStorageHeader)

        storageList.forEach { it ->
            val mStorageHtml = String
                .format(mStorageFormat, it.name, it.available, it.total)
            storageBuilder.append(mStorageHtml)
        }

        storageBuilder.append(mStorageTail)

        return storageBuilder.toString()
    }

    private fun getCpuCoresHtml(tag: String, cpuCoreList:List<CpuCoreInfo>) : String {
        val coreBuilder = StringBuilder()

        val mCoreHeader = """
               <div class="section-title">
                    <h2>${tag}</h2>
               </div>
               
               <div class="tripple-info">
                    <table>
                        <tr>
                            <th>Core No.</th>
                            <th>min Hz</th>
                            <th>max Hz</th>
                        </tr>            
        """.trimIndent()

        val mCoreFormat = """            
                <tr>
                    <td data-label="이름">%s</td>
                    <td data-label="값 1">%s</td>
                    <td data-label="값 2">%s</td>
                </tr>            
        """.trimIndent()

        val mCoreTail = """
                </table>
            </div>
        """.trimIndent()

        coreBuilder.append(mCoreHeader)

        cpuCoreList.forEach { it ->
            val mCoreHtml = String.format(mCoreFormat, it.name, it.minHz, it.maxHz)
            coreBuilder.append(mCoreHtml)
        }
        coreBuilder.append(mCoreTail)

        return coreBuilder.toString()
    }

    private fun getInfoListHtml(tag:String, infoList:List<Info>) : String {
        val mBuilder = StringBuilder()
        val dpTagName = if(tag.isBlank()) "None" else tag
        val mInfoTitle = """
            <div class="section-title">
                <h2>$dpTagName</h2>
            </div>      
                  
            <div class="extra-info">
                <table>            
                    <tr>
                        <th>항목</th>
                        <th>내용</th>
                    </tr>
        """.trimIndent()
        val mInfoFormat = """
            <tr>
                <td data-label="항목">%s</td>
                <td data-label="내용">%s</td>
            </tr>
        """.trimIndent()

        val mInfoTail = """
                </table>
            </div>
        """.trimIndent()

        mBuilder.append(mInfoTitle)

        infoList.forEach { it ->
            val mInfoHtml = String.format(mInfoFormat, it.name, it.value)
            mBuilder.append(mInfoHtml)
        }
        mBuilder.append(mInfoTail)

        return mBuilder.toString()
    }

    private fun getSnapShotHtml(snapInfoList:List<Info>):String {
        val snapBuilder = StringBuilder()
        // Device Overview = Device Snapshot!
        val mSnapHead = """
            <div class="details">
            <table>            
        """.trimIndent()

        val mSnapShotFormat = """
            <tr>
                <th>%s</th>            
                <td data-label="">%s</td>            
            </tr>
        """.trimIndent()

        val mSnapTail = """
            </table>
        </div>
        """.trimIndent()

        snapBuilder.append(mSnapHead)

        snapInfoList.forEach { it ->
            val mSnapInfo = String.format(mSnapShotFormat, it.name, it.value)
            snapBuilder.append(mSnapInfo)
        }
        snapBuilder.append(mSnapTail)
        return snapBuilder.toString()
    }

    private fun readRawTextFile(context: Context, resId: Int): String {
        val inputStream = context.resources.openRawResource(resId)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        var line: String?
        try {
            while (reader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
                stringBuilder.append("\n")
            }
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return stringBuilder.toString()
    }



    private fun saveFile(context: Context, content:String, format:String = ".txt"){
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
        val fileName = "device_info_${Build.MODEL.replace(" ", "").lowercase()}$format"
        val file = File(path, fileName)
        // Timber.i("[저장 경로] : %s", file.path)
        val preClearCmd = "rm -r ${file.path}"
        Command.execute(preClearCmd)
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