package kr.co.devicechecker.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kr.co.devicechecker.R
import kr.co.devicechecker.data.dto.BatteryInfo
import kr.co.devicechecker.data.dto.CpuCoreInfo
import kr.co.devicechecker.data.dto.Info
import kr.co.devicechecker.util.DeviceInfo
import kr.co.devicechecker.util.SystemPerform
import kr.co.devicechecker.util.MemoryInfo
import kr.co.devicechecker.util.Sensor
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.lang.StringBuilder
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
    fun saveTextFiles(mActivity: Activity) {
        val context: Context = mActivity.baseContext
        val resolver: ContentResolver = mActivity.contentResolver

        val builder = StringBuilder()
        // Save device data via Object 'DeviceInfo'
        val mDeviceTitle = "[Device Info]\n"
        builder.append(mDeviceTitle)
        val mDeviceInfoList = mutableListOf<Info>()

        val arrModelAndOs = DeviceInfo.getModelAndOsVersion(context)
        val listModelAndOs = listOf( // first list!
            Info("Model", arrModelAndOs[0]), // Model
            Info("Android", arrModelAndOs[1]), // Android
        )

        val mDeviceSnapshots = DeviceInfo
            .getDeviceSnapshotList(context, resolver)
        val mDisplayInfoList = DeviceInfo.getDisplayInfo(context)
        val mProcessInfoList = DeviceInfo.getProcessorInfo(context)
        val mJavaInfoList = DeviceInfo.getJavaInformation(context)
        val mOtherList = DeviceInfo.getOtherInfo(context)
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

        val arrChipAndArch = SystemPerform.getChipsetAndArchitecture(context)
        val listChipAndArch = listOf<Info>(
            Info("Chip Set", arrChipAndArch[0]), // chip set
            Info("Hardware", arrChipAndArch[1]), // Hardware(=board)
        )

        val mCpuCoreSpecs:List<CpuCoreInfo> = SystemPerform.getCPUCoreSpecs(context)
        val mBatteryInfo:BatteryInfo? = SystemPerform.getBatteryStatus(context)

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
                .append("\tstatus :\t${mBatteryInfo.isCharging}\n")
                .append("\tchargeType :\t${mBatteryInfo.chargeType}\n")
                .append("\tlevel :\t${mBatteryInfo.level}\n")
                .append("\tcapacity :\t${mBatteryInfo.capacity} mAh\n")
        }

        // Save device data via Object 'MemoryInfo'
        val mMemoryTitle = "[Memory Info]\n"
        builder.append("\n").append(mMemoryTitle)
        val mMemoryInfo = MemoryInfo.getMemoryInfo(context)

        val pathList = MemoryInfo.getStoragePathList(mActivity)
        val mStorageList = MemoryInfo.getStorageInfo(pathList.toTypedArray())

        // Add all Memory Information!
        mMemoryInfo.forEach { it ->
            builder.append("${it.name} :\t${it.value}\n")
        }

        mStorageList.forEach { it ->
            builder.append("<Storage ${it.name}>\n")
                .append("\tavailable :\t${it.available}\n")
                .append("\ttotal :\t${it.total}\n")
        }

        // Save device data via Object 'Sensor'
        val mSensorTitle = "[Sensor Info]\n"
        builder.append("\n").append(mSensorTitle)

        // Add All Sensor List
        val mSensorList = Sensor.getSensorInfo(mActivity)
        mSensorList.forEach { it ->
            builder.append("<${it.sensorName}>\n")
                .append("\ttype :\t${it.sensorType}\n")
                .append("\tvendor :\t${it.sensorVendor}\n")
        }

        // 현재 날짜 형식 설정
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss") // 원하는 형식으로 변경 가능
        val currentDate = dateFormat.format(Date())

        // StringBuilder 마지막에 날짜 추가
        builder.append("\n\n\n")
            .append("Created at : $currentDate\n")
        saveFile(context, builder.toString()) // 저장 시작!
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