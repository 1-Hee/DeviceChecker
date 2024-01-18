package kr.co.devicechecker

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import kr.co.devicechecker.databinding.ActivityMainBinding
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.lang.Math.ceil
import java.nio.charset.StandardCharsets


class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var deviceInfo:DeviceInfo
    private var requestCode1 = 1001
    private var requestCode2 = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val modelName = "모델 명 : ${Build.MODEL}"
        val androidVersion = "안드로이드 버전 : Android ${Build.VERSION.RELEASE} ( ${Build.VERSION.SDK_INT} )"
        val firmwareInfo = "펌웨어 버전 : "
        val buildNumber = "빌드 번호 : ${Build.FINGERPRINT}"
        val processorInfo = "칩셋 / 프로세서 정보 : ${Build.HARDWARE}"

        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        val totalRAM = memoryInfo.totalMem
        val totalRAMGB = totalRAM / (1024 * 1024).toFloat()
        val displayRAMGB = ceil(totalRAMGB.toDouble()).toInt()
        val memoryInfoString = "RAM : $displayRAMGB MB"
        val storageInfo = "저장 장치 (여유/전체) : "


        deviceInfo = DeviceInfo(
            modelName,
            androidVersion,
            firmwareInfo,
            buildNumber,
            processorInfo,
            memoryInfoString,
            storageInfo
        )

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mBinding.lifecycleOwner = this
        mBinding.setVariable(BR.deviceInfo, deviceInfo)
        mBinding.setVariable(BR.path, "저장 경로 : ")
        mBinding.notifyChange()

        mBinding.btnSaveDeviceInfo.setOnClickListener { saveData() }

        Timber.plant(Timber.DebugTree())
    }

    override fun onResume() {
        super.onResume()
        checkStorageRead()
        checkStorageWrite()
    }

    private fun checkStorageRead() {
        // 액티비티나 프래그먼트 내에서 권한 체크 및 요청
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            // 권한이 이미 허용된 경우
            setStorageInfo(deviceInfo, true)
        } else {
            // 사용자에게 권한 요청
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode1)
        }
    }

    private fun checkStorageWrite() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한이 없으면 권한 요청
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                requestCode2
            )
        }
    }

    private fun setStorageInfo(deviceInfo: DeviceInfo, isAllow:Boolean) {
        val storageInfo:String = if (isAllow) {
            val path = Environment.getExternalStorageDirectory().absolutePath
            val stat = StatFs(path)
            // 블록 크기를 가져와서 MB로 변환
            val blockSize = stat.blockSizeLong / (1024 * 1024).toFloat()

            // 전체 블록 수 및 사용 가능한 블록 수
            val totalBlocks = stat.blockCountLong
            val availableBlocks = stat.availableBlocksLong

            // 전체 용량 및 사용 가능한 용량을 MB로 변환
            val totalStorageMB = totalBlocks * blockSize
            val availableStorageMB = availableBlocks * blockSize

            val dpTotalStorage = ceil(totalStorageMB.toDouble()).toInt()
            val dpAvailStorage = ceil(availableStorageMB.toDouble()).toInt()

            "저장 장치 (여유/전체) :  : $dpAvailStorage MB/ $dpTotalStorage MB "
        }else {
            "저장 장치 (여유/전체) : (표시할 수 없음)"
        }
        deviceInfo.storageInfo = storageInfo
        mBinding.deviceInfo = deviceInfo
        mBinding.notifyChange()
    }

    private fun saveData() {
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
        mBinding.setVariable(BR.path, "저장 경로 : $path")
        mBinding.notifyChange()

        val fileName = "device_info.txt"
        deviceInfo.toString()
        val content = "${deviceInfo.modelName}\n" +
                "${deviceInfo.androidVersion}\n" +
                "${deviceInfo.firmWareVersion}\n" +
                "${deviceInfo.buildNumber}\n" +
                "${deviceInfo.processor}\n" +
                "${deviceInfo.memoryInfo}\n" +
                "${deviceInfo.storageInfo}\n"

        val downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDirectory, fileName)

        try {
            // 파일 쓰기
            FileOutputStream(file).use{ fileOutputStream ->
                OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8).use { outputStreamWriter ->
                    outputStreamWriter.write(content)
                }
            }
            Toast.makeText(this, "파일 저장 완료 ( ${file.absolutePath} )", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "파일 저장 실패", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            requestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한이 허용된 경우
                    when(requestCode){
                        1001 -> {
                            setStorageInfo(deviceInfo, true)
                        }
                        1002 -> { // 쓰기 허용
                        }
                    }
                } else {
                    when(requestCode){
                        1001 -> { // 쓰기 허용
                            setStorageInfo(deviceInfo, false)
                        }
                        1002 -> { // 쓰기 거절

                        }
                    }
                }
            }
        }
    }
}