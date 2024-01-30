package kr.co.devicechecker.ui.fragment.main

import android.Manifest
import android.os.Build
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
import kr.co.devicechecker.data.dto.StorageInfo
import kr.co.devicechecker.databinding.FragmentMemoryInfoBinding
import kr.co.devicechecker.util.AppUtil
import kr.co.devicechecker.util.PreferenceUtil
import timber.log.Timber


class MemoryInfoFragment : BaseFragment<FragmentMemoryInfoBinding>() {
    private var scope: CoroutineScope? = null; // 권한 요청 시 사용할 코루틴
    private val memoryInfoList = mutableListOf<Info>()
    private val internalStorageList = mutableListOf<StorageInfo>()
    private val externalStorageList = mutableListOf<StorageInfo>()
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
        Timber.i("initView ${this.javaClass.simpleName}")
        prefs = PreferenceUtil(requireContext())
        requestStoragePermission()
        getMemoryInfo()
        saveMemoryInfo()
    }
    private fun saveMemoryInfo(){
       AppUtil.Memory.saveMemoryInfo(
           requireContext(),
           memoryInfoList, internalStorageList, externalStorageList
       )
    }
    private fun getMemoryInfo(){
        this.memoryInfoList.clear()
        this.memoryInfoList.addAll(AppUtil.Memory.getMemoryInfo(requireContext()))
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
        val pathList = AppUtil.Memory.getStoragePathList(requireActivity())
        val internalPathList = mutableListOf<Info>()
        val externalPathList = mutableListOf<Info>()
        val internalName = requireContext().getString(R.string.txt_internal_name)
        pathList.forEach { info ->
            if(info.name.contains(internalName)){
                internalPathList.add(info)
            }else {
                externalPathList.add(info)
            }
        }
        setInternalStorageInfo(internalPathList)
        setExternalStorageInfo(externalPathList)
    }
    private fun setInternalStorageInfo(pathList:List<Info>){
        this.internalStorageList.clear()
        val storageInfo = AppUtil.Memory.getInternalStorageInfo(pathList.toTypedArray())
        this.internalStorageList.addAll(storageInfo)
        mBinding.internalStorageList = internalStorageList
        mBinding.notifyChange()
    }
    private fun setExternalStorageInfo(pathList:List<Info>) {
        this.externalStorageList.clear()
        val storageInfo = AppUtil.Memory.getExternalStorageInfo(pathList.toTypedArray())
        this.externalStorageList.addAll(storageInfo)
        mBinding.externalStorageList = externalStorageList
        mBinding.notifyChange()
    }
}