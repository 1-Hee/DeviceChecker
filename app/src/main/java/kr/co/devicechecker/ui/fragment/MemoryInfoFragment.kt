package kr.co.devicechecker.ui.fragment

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.devicechecker.BR
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.ui.BaseFragment
import kr.co.devicechecker.data.dto.Info
import kr.co.devicechecker.databinding.FragmentMemoryInfoBinding
import kr.co.devicechecker.util.AppUtil
import timber.log.Timber
import java.io.File


class MemoryInfoFragment : BaseFragment<FragmentMemoryInfoBinding>() {
    private var scope: CoroutineScope? = null; // 권한 요청 시 사용할 코루틴
    private val memoryInfoList = mutableListOf<Info>()
    private val internalStorageList = mutableListOf<Info>()
    private val externalStorageList = mutableListOf<Info>()
    private val emptyValue = "알 수 없음"

    override fun initViewModel() {
    }
    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_memory_info)
            .addBindingParam(BR.memoryInfoList, memoryInfoList)
            .addBindingParam(BR.internalStorageList, internalStorageList)
            .addBindingParam(BR.externalStorageList, externalStorageList)
    }
    override fun initView() {
        requestStoragePermission()
        getMemoryInfo()


        getExternalUsbStorageInfo()
    }
    private val memoryFieldMap = hashMapOf(
        Pair("MemTotal", "전체 메모리(Total Memory)"),
        Pair("MemFree", "가용 메모리(Available Memory)"),
        Pair("Buffers", "버퍼 크기(Buffer Size)"),
        Pair("Cached", "캐시 크기(Cached)"),
        Pair("Active", "활성 메모리(Active)"),
        Pair("Inactive", "비활성 메모리(InActive)"),
        Pair("Mapped", "메모리 맵(Memory Map)"),
        Pair("Slab", "Slab"),
        Pair("CmaTotal", "전체 CMA(Total Contiguous Memory Allocator)"),
        Pair("CmaFree", "여유 CMA(Available Contiguous Memory Allocator)"),
        Pair("Hugepagesize", "페이징 크기")
    )
    private val memoryKeyList = arrayOf(
        "MemTotal",
        "MemFree",
        "MemAvailable",
        "Buffers",
        "Cached",
        "Active",
        "Inactive",
        "Mapped",
        "Slab",
        "CmaTotal",
        "CmaFree",
        "Hugepagesize"
    )
    // data 1 . memeory info
    private val memoryMap = hashMapOf<String, String>()
    private fun getMemoryInfo(){
        val cmd = "cat /proc/meminfo"
        val mInfo = AppUtil.Command.executeAdbCommand(cmd)
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
                        Info(it, "알수 없음")
                    }else {
                        val parsedValue = AppUtil.Unit.parseUnit(value)
                        Info(it, parsedValue)
                    }
                    this.memoryInfoList.add(info)
                }
            }
        }
        mBinding.memoryInfoList = memoryInfoList
        mBinding.notifyChange()
    }

    // receiver
    private val usbReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (ACTION_USB_PERMISSION == action) {
                synchronized(this) {
                    val device =
                        intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            loggingDevice(device)
                            if(device.manufacturerName == "USB"){
                                loggingDevice(device)
//                                Timber.w("파일 쓰기 시작....")
                                //openUsbDevice(device)

                            }

                        }
                    } else {
                        // 권한이 거부된 경우 처리
                        Timber.e("usb 권한이 거부됨....")
                    }
                }
            }
        }
    }
    // 로깅용 함수
    private fun loggingDevice(device: UsbDevice){
        Timber.i("■■■■■ < usbDevice information > ■■■■■")
        Timber.i("vendorId : %s", device.vendorId)
        Timber.i("productId : %s", device.productId)
        Timber.i("deviceId : %s", device.deviceId)
        Timber.i("manufacturerName : %s", device.manufacturerName)
        Timber.i("version : %s", device.version)
        Timber.i("serialNumber : %s", device.serialNumber)
        Timber.i("deviceProtocol : %s", device.deviceProtocol)
        Timber.i("deviceName : %s", device.deviceName)
        Timber.i("deviceClass : %s", device.deviceClass)
        Timber.i("deviceSubclass : %s", device.deviceSubclass)
        Timber.i("configurationCount : %s", device.configurationCount)
        Timber.i("interfaceCount : %s", device.interfaceCount)
        Timber.i("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■")
    }


    private val ACTION_USB_PERMISSION = "com.example.USB_PERMISSION";
    private fun requestStoragePermission(){
        // USB manager
        val usbManager = requireActivity().getSystemService(Context.USB_SERVICE) as UsbManager;
        // USB 퍼미션을 얻기 위한 PendingIntent 생성
        val permissionIntent = PendingIntent.getBroadcast(requireContext(), 0, Intent(), PendingIntent.FLAG_MUTABLE);

        // USB 디바이스 연결 상태를 감지하는 BroadcastReceiver 등록
        val filter = IntentFilter(ACTION_USB_PERMISSION);
        requireActivity().registerReceiver(usbReceiver, filter)

        var displayText = "연결된 USB 장치 수 : ${usbManager.deviceList.size}\n";
        usbManager.deviceList.values.forEach { usbDevice ->
            displayText += "USB 장치 : [ ${usbDevice.deviceName}, ${usbDevice.manufacturerName}, ${usbDevice.vendorId}, ${usbDevice.productId}, ${usbDevice.version} ]\n";
        }
        // 저장소 명 init
        // storeList = ArrayList();
        /// usb 저장소
        val externalFilesDirs = requireActivity().getExternalFilesDirs(null)
        for (externalFilesDir in externalFilesDirs) {
            Timber.i("dir >>> $externalFilesDir")
//            if(!externalFilesDir.toString().contains("/storage/emulated")){
//                Log.d("StorageInfo", "External Files Dir: $externalFilesDir")
//                storeList.add(externalFilesDir.toString());
//
//                // writeTextFile(externalFilesDir.toString(), "hello.txt", "hello world!");
//            }
            // Do something with the externalFilesDir
        }


        val permissionList = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )

//        // CoroutineScope
        scope = CoroutineScope(Dispatchers.Main)
        scope?.launch {
            TedPermission.create()
                .setPermissionListener(object :PermissionListener {
                    override fun onPermissionGranted() {
                        getInternalStorageInfo()
                        getExternalStorageInfo()
                    }
                    override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    }
                })
                .setDeniedMessage("권한을 허용해주세요. [설정] > [앱 및 알림] > [고급] > [앱 권한]")
                .setPermissions(*permissionList)
                .check()

        //            val permissionResult =
//                TedPermission.create()
//                    .setDeniedMessage("권한을 허용해주세요. [설정] > [앱 및 알림] > [고급] > [앱 권한]")
//                    .setPermissions(*permissionList)
//                    .check()
//            if(permissionResult.isGranted){
//                // viewmodel 등을 초기화해주고, 프래그먼트의 이동을 구현
//                // https://velog.io/@kimbsu00/Android-4
//                // 기본 handler가 deprecate 되었으므로 아래와 같이 사용
//                val iHandler: Handler = Handler(Looper.getMainLooper());
//                val delayMillis = 1500L // 1.5초
//                iHandler.postDelayed({
//                    val nextId = R.id.lockFragment
//                    nav().popBackStack()
//                    nav().navigate(nextId)
//                }, delayMillis)
//            } else {
//                // 토스트 메세지 호출
//                AppUtil.toast(requireContext(), "권한을 허용해주세요")
//            }
        }

    }
    // data 2. storage info (internel, externel)
    private fun getInternalStorageInfo(){
//        val iAvailable = Info("사용 가능한 용량(Available)", getAvailableInternalMemorySize()?:emptyValue)
//        this.internalStorageList.add(iAvailable)
//        val iTotal = Info("전체 용량(Total)", getTotalInternalMemorySize()?:emptyValue)
//        this.internalStorageList.add(iTotal)

//        val dir = requireActivity().cacheDir // internal
//        // val path = Environment.getDataDirectory().path
//        val statFs = StatFs(dir.path)
//        val iInternalAvailable = statFs.freeBytes
//        val iAvailable = Info("사용 가능한 용량(Available)", iInternalAvailable.toString())
//        this.internalStorageList.add(iAvailable)
//        val iInternalTotal = statFs.totalBytes
//        val iTotal = Info("전체 용량(Total)", iInternalTotal.toString())
//        this.internalStorageList.add(iTotal)
        mBinding.internalStorageList = internalStorageList
        mBinding.notifyChange()
    }
    private fun getExternalStorageInfo() {
//        val iAvailable = Info("사용 가능한 용량(Available)", getAvailableExternalMemorySize()?:emptyValue)
//        this.externalStorageList.add(iAvailable)
//        val iTotal = Info("전체 용량(Total)", getTotalExternalMemorySize()?:emptyValue)
//        this.externalStorageList.add(iTotal)

//        val dir = requireActivity().externalCacheDir
////        val path = Environment.getExternalStorageDirectory().path
//        val statFs = StatFs(dir?.path)
//        val iExternalAvailable = statFs.freeBytes
//        val iAvailable = Info("사용 가능한 용량(Available)", iExternalAvailable.toString())
//        this.externalStorageList.add(iAvailable)
//        val iExternalTotal = statFs.totalBytes
//        val iTotal = Info("전체 용량(Total)", iExternalTotal.toString())
//        this.externalStorageList.add(iTotal)
        mBinding.externalStorageList = externalStorageList
        mBinding.notifyChange()
    }


//    fun externalMemoryAvailable(): Boolean {
//        return Environment.getExternalStorageState() ==
//                Environment.MEDIA_MOUNTED
//    }
//
//    fun getAvailableInternalMemorySize(): String? {
//        val path = Environment.getDataDirectory()
//        val stat = StatFs(path.path)
//        val blockSize = stat.blockSizeLong
//        val availableBlocks = stat.availableBlocksLong
//        return formatSize(availableBlocks * blockSize)
//    }
//
//    fun getTotalInternalMemorySize(): String? {
//        val path = Environment.getDataDirectory()
//        val stat = StatFs(path.path)
//        val blockSize = stat.blockSizeLong
//        val totalBlocks = stat.blockCountLong
//        return formatSize(totalBlocks * blockSize)
//    }
//
//    fun getAvailableExternalMemorySize(): String? {
//        return if (externalMemoryAvailable()) {
//            val path = Environment.getExternalStorageDirectory()
//            val stat = StatFs(path.path)
//            val blockSize = stat.blockSizeLong
//            val availableBlocks = stat.availableBlocksLong
//            formatSize(availableBlocks * blockSize)
//        } else {
//            null
//        }
//    }
//
//    fun getTotalExternalMemorySize(): String? {
//        return if (externalMemoryAvailable()) {
//            val path = Environment.getExternalStorageDirectory()
//            val stat = StatFs(path.path)
//            val blockSize = stat.blockSizeLong
//            val totalBlocks = stat.blockCountLong
//            formatSize(totalBlocks * blockSize)
//        } else {
//            null
//        }
//    }
//
//    fun formatSize(size: Long): String? {
//        var size = size
//        var suffix: String? = null
//        if (size >= 1024) {
//            suffix = "KB"
//            size /= 1024
//            if (size >= 1024) {
//                suffix = "MB"
//                size /= 1024
//            }
//        }
//        val resultBuffer = StringBuilder(java.lang.Long.toString(size))
//        var commaOffset = resultBuffer.length - 3
//        while (commaOffset > 0) {
//            resultBuffer.insert(commaOffset, ',')
//            commaOffset -= 3
//        }
//        if (suffix != null) resultBuffer.append(suffix)
//        return resultBuffer.toString()
//    }


    fun getExternalUsbStorageInfo() {
        val externalStorageList = getExternalStorageList()

        if (externalStorageList.isNotEmpty()) {
            val firstExternalStorage = externalStorageList.first()
            val statFs = StatFs(firstExternalStorage.path)

            val blockSize = statFs.blockSizeLong
            val totalBlocks = statFs.blockCountLong
            val availableBlocks = statFs.availableBlocksLong

            val totalBytes = totalBlocks * blockSize
            val availableBytes = availableBlocks * blockSize
            Timber.i("USB Total Space: ${formatBytes(totalBytes)}")
            Timber.i("USB Available Space: ${formatBytes(availableBytes)}")
        } else {
            Timber.i("No external USB storage found")
        }
    }

    private fun getExternalStorageList(): List<File> {
        val externalStorageList = mutableListOf<File>()
        val externalDirs = getExternalDirs()
        for (externalDir in externalDirs) {
            if (isUsbStorage(externalDir)) {
                externalStorageList.add(externalDir)
            }
        }
        return externalStorageList
    }

    private fun getExternalDirs(): Array<File> {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // For API level 19 and above
                val context = /* your context here */
                    requireContext().getExternalFilesDirs(null)
            } else {
                // For API level below 19
                val state = Environment.getExternalStorageState()
                return if (state == Environment.MEDIA_MOUNTED || state == Environment.MEDIA_MOUNTED_READ_ONLY) {
                    arrayOf(Environment.getExternalStorageDirectory())
                } else {
                    emptyArray()
                }
            }
        } catch (e: Exception) {
            return emptyArray()
        }
        return emptyArray()
    }

    private fun isUsbStorage(file: File): Boolean {
        // Add any condition to identify USB storage, e.g., file path, name, etc.
        return file.path.contains("usb")
    }

    private fun formatBytes(bytes: Long): String {
        val kiloBytes = bytes / 1024
        val megaBytes = kiloBytes / 1024
        val gigaBytes = megaBytes / 1024
        return when {
            gigaBytes > 0 -> "$gigaBytes GB"
            megaBytes > 0 -> "$megaBytes MB"
            kiloBytes > 0 -> "$kiloBytes KB"
            else -> "$bytes Bytes"
        }
    }

}