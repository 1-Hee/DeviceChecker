package kr.co.devicechecker.ui.fragment.main

import android.app.Activity
import android.content.Context
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.co.devicechecker.BR
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.ui.BaseFragment
import kr.co.devicechecker.data.dto.Info
import kr.co.devicechecker.data.dto.StorageInfo
import kr.co.devicechecker.databinding.FragmentMemoryInfoBinding
import kr.co.devicechecker.util.MemoryFetcher
import timber.log.Timber


class MemoryInfoFragment : BaseFragment<FragmentMemoryInfoBinding>() {

    private val memoryInfoList = mutableListOf<Info>()
    private val internalStorageList = mutableListOf<StorageInfo>()
    private val externalStorageList = mutableListOf<StorageInfo>()

    private val storageList = mutableListOf<StorageInfo>()

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
            .addBindingParam(BR.onRefreshListener, onRefreshListener)
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
        this.memoryInfoList.addAll(MemoryFetcher.getMemoryInfoList(context))
        mBinding.memoryInfoList = memoryInfoList
        mBinding.notifyChange()
    }
    // data 2. storage info (internel, externel)
    private fun getStorageInfo(mActivity: Activity){
        val pathList = MemoryFetcher.getStoragePathList(mActivity)
        setStorageInfo(pathList)
    }

    private fun setStorageInfo(pathList:List<Info>){
        this.storageList.clear()
        val storageInfo = MemoryFetcher.getStorageInfo(pathList.toTypedArray())
        this.storageList.addAll(storageInfo)
        mBinding.storageList = storageList
        mBinding.notifyChange()
    }

    // Memory Size 세팅하는 함수
    private fun initMemorySize(context: Context) {
        val mMemInfo = MemoryFetcher.getMemoryInfo(context)
        mBinding.setVariable(BR.availMem, mMemInfo.availMem.toString())
        mBinding.setVariable(BR.totalMem, mMemInfo.totalMem.toString())
        mBinding.setVariable(BR.isLowMemory, mMemInfo.isLowMemory)
        val memPercentValue:Int = mMemInfo.getPercentInt()
        mBinding.setVariable(BR.memProgress, memPercentValue)

    }

    // 리프레쉬 리스너
    private val onRefreshListener = SwipeRefreshLayout.OnRefreshListener {
        lifecycleScope.launch {
            initView()
            delay(500)
            mBinding.swiperMemoryInfo.isRefreshing = false
        }
    }


}