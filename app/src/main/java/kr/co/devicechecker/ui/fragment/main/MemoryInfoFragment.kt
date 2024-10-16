package kr.co.devicechecker.ui.fragment.main

import kr.co.devicechecker.BR
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.ui.BaseFragment
import kr.co.devicechecker.data.dto.Info
import kr.co.devicechecker.data.dto.StorageInfo
import kr.co.devicechecker.databinding.FragmentMemoryInfoBinding
import kr.co.devicechecker.util.AppUtil
import kr.co.devicechecker.util.MemoryInfo
import kr.co.devicechecker.util.PreferenceUtil
import timber.log.Timber


class MemoryInfoFragment : BaseFragment<FragmentMemoryInfoBinding>() {
    private val memoryInfoList = mutableListOf<Info>()
    private val internalStorageList = mutableListOf<StorageInfo>()
    private val externalStorageList = mutableListOf<StorageInfo>()

    private val storageList = mutableListOf<StorageInfo>()

    companion object {
        @Deprecated("This function may cause potential errors and is being deprecated.")
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
        getMemoryInfo()
        getStorageInfo()

        // temp
        mBinding.pbRamStatus.progress = 70
        mBinding.notifyChange()

    }

    private fun getMemoryInfo(){
        this.memoryInfoList.clear()
        this.memoryInfoList.addAll(MemoryInfo.getMemoryInfo(requireContext()))
        mBinding.memoryInfoList = memoryInfoList
        mBinding.notifyChange()
    }
    // data 2. storage info (internel, externel)
    private fun getStorageInfo(){
        val pathList = MemoryInfo.getStoragePathList(requireActivity())
        setStorageInfo(pathList)
    }

    private fun setStorageInfo(pathList:List<Info>){
        this.storageList.clear()
        val storageInfo = MemoryInfo.getStorageInfo(pathList.toTypedArray())
        this.storageList.addAll(storageInfo)
        mBinding.storageList = storageList
        mBinding.notifyChange()
    }

}