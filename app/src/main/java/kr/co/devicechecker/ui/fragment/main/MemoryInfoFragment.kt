package kr.co.devicechecker.ui.fragment.main

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import kr.co.devicechecker.BR
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.ui.BaseFragment
import kr.co.devicechecker.data.dto.Info
import kr.co.devicechecker.data.dto.StorageInfo
import kr.co.devicechecker.databinding.FragmentMemoryInfoBinding
import kr.co.devicechecker.util.MemoryInfo
import timber.log.Timber
import kotlin.math.round


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
            .addBindingParam(BR.availMem, "")
            .addBindingParam(BR.totalMem, "")
            .addBindingParam(BR.isLowMemory, false)
            .addBindingParam(BR.memProgress, 0)
    }
    override fun initView() {
        Timber.i("initView ${this.javaClass.simpleName}")
        val context = requireContext()
        val mActivity = requireActivity()
        getMemoryInfo(context)
        getStorageInfo(mActivity)
        initMemorySize(context)

        mBinding.notifyChange()
    }

    private fun getMemoryInfo(context: Context){
        this.memoryInfoList.clear()
        this.memoryInfoList.addAll(MemoryInfo.getMemoryInfoList(context))
        mBinding.memoryInfoList = memoryInfoList
        mBinding.notifyChange()
    }
    // data 2. storage info (internel, externel)
    private fun getStorageInfo(mActivity: Activity){
        val pathList = MemoryInfo.getStoragePathList(mActivity)
        setStorageInfo(pathList)
    }

    private fun setStorageInfo(pathList:List<Info>){
        this.storageList.clear()
        val storageInfo = MemoryInfo.getStorageInfo(pathList.toTypedArray())
        this.storageList.addAll(storageInfo)
        mBinding.storageList = storageList
        mBinding.notifyChange()
    }

    // Memory Size 세팅하는 함수
    private fun initMemorySize(context: Context) {
        val mMemInfo = MemoryInfo.getMemoryInfo(context)
        mBinding.setVariable(BR.availMem, mMemInfo.availMem.toString())
        mBinding.setVariable(BR.totalMem, mMemInfo.totalMem.toString())
        mBinding.setVariable(BR.isLowMemory, mMemInfo.isLowMemory)
        val memPercentValue:Int = mMemInfo.getPercentInt()
        mBinding.setVariable(BR.memProgress, memPercentValue)

    }



}