package kr.co.devicechecker.ui.fragment.main

import android.content.Context
import kr.co.devicechecker.BR
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.ui.BaseFragment
import kr.co.devicechecker.data.dto.BatteryInfo
import kr.co.devicechecker.data.dto.CpuCoreInfo
import kr.co.devicechecker.databinding.FragmentSystemPerformBinding
import kr.co.devicechecker.util.SystemPerform


class SystemPerformFragment : BaseFragment<FragmentSystemPerformBinding>() {

    private val cpuCoreInfoList = mutableListOf<CpuCoreInfo>()
    override fun initViewModel() {
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_system_perform)
            .addBindingParam(BR.cpuCoreInfoList, cpuCoreInfoList)
    }

    override fun initView() {
        val context = requireContext()
        initChipsetAndArchitecture(context) // task 1.
        initCPUCoreSpecs(context) // task 2.

        initBatteryStatus(requireContext()) // task 4.

        mBinding.notifyChange()
    }

    // task 1. get CPU Architecture & Chipset Brand
    private fun initChipsetAndArchitecture(context: Context){
        val mCpuTagInfo = SystemPerform.getChipsetAndArchitecture(context)
        if(mCpuTagInfo.size < 2) return
        mBinding.tvCpuManufacture.text = mCpuTagInfo[0]
        mBinding.tvCpuArchitecture.text = mCpuTagInfo[1]
    }

    // task 2. get CPU Core Specific information
    private fun initCPUCoreSpecs(context: Context){
        val mCpuCoreInfoList = SystemPerform.getCPUCoreSpecs(context)
        cpuCoreInfoList.addAll(mCpuCoreInfoList)
        mBinding.cpuCoreInfoList = cpuCoreInfoList
        mBinding.tvCoreCnt.text = cpuCoreInfoList.size.toString()
    }

    // task 3. Tracking Memory Usage...

    // task 4. Get Battery Info
    private fun initBatteryStatus(context: Context){
        val batteryInfo:BatteryInfo? = SystemPerform.getBatteryStatus(context)
        mBinding.setVariable(BR.batteryInfo, batteryInfo)

    }

}