package kr.co.devicechecker.ui.fragment.main

import android.Manifest
import android.os.Build
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
import kr.co.devicechecker.util.PreferenceUtil


class MemoryInfoFragment : BaseFragment<FragmentMemoryInfoBinding>() {
    private var scope: CoroutineScope? = null; // 권한 요청 시 사용할 코루틴
    private val memoryInfoList = mutableListOf<Info>()
    private val internalStorageList = mutableListOf<Info>()
    private val externalStorageList = mutableListOf<Info>()
    // 값 저장을 위한 prefs 변수
    private lateinit var prefs: PreferenceUtil
    override fun initViewModel() {
    }
    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_memory_info)
            .addBindingParam(BR.memoryInfoList, memoryInfoList)
            .addBindingParam(BR.internalStorageList, internalStorageList)
            .addBindingParam(BR.externalStorageList, externalStorageList)
    }
    override fun initView() {
        prefs = PreferenceUtil(requireContext())
        requestStoragePermission()
        getMemoryInfo()
    }

    override fun onPause() {
        super.onPause()
        val tag = "Memory"
        val builder = StringBuilder()
        builder.append("[Memory Info]\n")
        this.memoryInfoList.forEach { info ->
            builder.append("$info\n")
        }
        this.internalStorageList.forEach { info ->
            builder.append("$info\n")
        }
        this.externalStorageList.forEach { info ->
            builder.append("$info\n")
        }
        prefs.setValue(tag, builder.toString())
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
        this.memoryInfoList.clear()
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
                        val parsedValue = AppUtil.Unit.parseKbUnit(value)
                        Info(it, parsedValue)
                    }
                    this.memoryInfoList.add(info)
                }
            }
        }
        mBinding.memoryInfoList = memoryInfoList
        mBinding.notifyChange()
    }

    private fun requestStoragePermission(){
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            // 읽고 쓰기 권한 요청
            val permissions = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
            )

            //        // CoroutineScope
            scope = CoroutineScope(Dispatchers.Main)
            scope?.launch {
                TedPermission.create()
                    .setPermissionListener(object :PermissionListener {
                        override fun onPermissionGranted() {
                        }
                        override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                        }
                    })
                    .setDeniedMessage("권한을 허용해주세요. [설정] > [앱 및 알림] > [고급] > [앱 권한]")
                    .setPermissions(*permissions)
                    .check()
            }
        }
        getStorageInfo()
    }


    // data 2. storage info (internel, externel)
    private fun getStorageInfo(){
        val internalStorageURI = "/storage/emulated/0/Android/data/kr.co.devicechecker/files"
        val externalFilesDirs = requireActivity().getExternalFilesDirs(null)
        val pathList = mutableListOf<Info>()
        for (externalFilesDir in externalFilesDirs) {
            val pathArr = externalFilesDir.path.split("/")
            val pathName = if(pathArr.size > 2) "외부 저장 장치(${pathArr[2]})" else "Unknown"
//            Timber.i("path arr : $pathArr")
//            Timber.i("저장소 명 : $pathName")
//            Timber.i("저장소 경로 : ${externalFilesDir.path}")
//            val statFs = StatFs(externalFilesDir.path)
//            val avail = statFs.freeBytes
//            val pAvail = AppUtil.Unit.parseByteUnit(avail)
//            // Timber.i("사용 가능한 용량 : $pAvail")
//            val total = statFs.totalBytes
//            val pTotal = AppUtil.Unit.parseByteUnit(total)
//            Timber.i("전체 용량 : $pTotal")
            if(externalFilesDir.path.equals(internalStorageURI)){
                setInternalStorageInfo(externalFilesDir.path)
            }else {
                val info = Info(pathName, externalFilesDir.path)
                pathList.add(info)
            }
        }
        setExternalStorageInfo(pathList.toTypedArray())
    }
    private fun setInternalStorageInfo(path:String){
        this.internalStorageList.clear()
        val statFs = StatFs(path)
        val iInternalAvailable = statFs.freeBytes
        val iInternalTotal = statFs.totalBytes
        val parsedAvailable = AppUtil.Unit.parseByteUnit(iInternalAvailable)
        val parseTotal = AppUtil.Unit.parseByteUnit(iInternalTotal)
        val iAvailable = Info("사용 가능한 용량(Available)", parsedAvailable)
        val iTotal = Info("전체 용량(Total)", parseTotal)
        this.internalStorageList.add(iAvailable)
        this.internalStorageList.add(iTotal)
        mBinding.internalStorageList = internalStorageList
        mBinding.notifyChange()
    }
    private fun setExternalStorageInfo(pathList:Array<Info>) {
        this.externalStorageList.clear()
        pathList.forEach { info ->
            val statFs = StatFs(info.value)
            val iExternalAvailable = statFs.freeBytes
            val iExternalTotal = statFs.totalBytes

            val parsedAvailable = AppUtil.Unit.parseByteUnit(iExternalAvailable)
            val parseTotal = AppUtil.Unit.parseByteUnit(iExternalTotal)

            val value = "사용 가능한 용량(Available) : $parsedAvailable\n전체 용량(Total) : $parseTotal"
            val iExternal = Info("${info.name}", value)
            this.externalStorageList.add(iExternal)
        }
        mBinding.externalStorageList = externalStorageList
        mBinding.notifyChange()
    }

}