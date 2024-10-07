package kr.co.devicechecker.ui.fragment.main

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
    private val memoryInfoList = mutableListOf<Info>()
    private val internalStorageList = mutableListOf<StorageInfo>()
    private val externalStorageList = mutableListOf<StorageInfo>()

    private val storageList = mutableListOf<StorageInfo>()
    // 값 저장을 위한 prefs 변수
    private lateinit var prefs: PreferenceUtil

    companion object {
        fun newInstance(): MemoryInfoFragment {
            return MemoryInfoFragment()
        }
    }
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
        getMemoryInfo()
        saveMemoryInfo()
        getStorageInfo()

        // temp
        mBinding.pbRamStatus.progress = 70
        mBinding.notifyChange()

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
    // data 2. storage info (internel, externel)
    private fun getStorageInfo(){
        /*
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
         */

        val pathList = AppUtil.Memory.getStoragePathList(requireActivity())
        setStorageInfo(pathList)
    }


    private fun setStorageInfo(pathList:List<Info>){
        this.storageList.clear()
        val storageInfo = AppUtil.Memory.getInternalStorageInfo(pathList.toTypedArray())
        this.storageList.addAll(storageInfo)
        mBinding.storageList = storageList
        mBinding.notifyChange()
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