package kr.co.devicechecker.ui.fragment.main

import kr.co.devicechecker.BR
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.ui.BaseFragment
import kr.co.devicechecker.data.dto.CpuCoreInfo
import kr.co.devicechecker.data.dto.Info
import kr.co.devicechecker.databinding.FragmentProcessorInfoBinding
import kr.co.devicechecker.util.AppUtil
import kr.co.devicechecker.util.PreferenceUtil
import timber.log.Timber


class ProcessorInfoFragment : BaseFragment<FragmentProcessorInfoBinding>() {

    private val processorInfoList = mutableListOf<Info>()
    private val cpuCoreInfoList = mutableListOf<CpuCoreInfo>()
    private val javaInfoList = mutableListOf<Info>()
    private var corNumber = 0
    // 값 저장을 위한 prefs 변수
    private lateinit var prefs: PreferenceUtil

    companion object {
        fun newInstance(): ProcessorInfoFragment {
            return ProcessorInfoFragment()
        }
    }
    override fun initViewModel() {}
    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_processor_info)
            .addBindingParam(BR.processorInfoList, processorInfoList)
            .addBindingParam(BR.cpuCoreInfoList, cpuCoreInfoList)
            .addBindingParam(BR.javaInfoList, javaInfoList)
    }
    override fun initView() {
        Timber.i("initView ${this.javaClass.simpleName}")
        prefs = PreferenceUtil(requireContext())
        getProcessorInfo()
        getCpuCoreInfo()
        getJavaInformation()
        saveProcessorInfo()
    }
    private fun saveProcessorInfo(){
        AppUtil.Processor.saveProcessorInfo(
            requireContext(),
            processorInfoList, cpuCoreInfoList, javaInfoList
        )
    }
    private fun getProcessorInfo(){
        this.processorInfoList.clear()
        this.processorInfoList.addAll(AppUtil.Processor.getProcessorInfo(requireContext()))
        mBinding.processorInfoList = processorInfoList
        mBinding.notifyChange()
    }
    private fun getCpuCoreInfo(){
        this.cpuCoreInfoList.clear()
        this.corNumber = try {
            processorInfoList[4].value.toInt()
        } catch (e:Exception){
            0
        }
        val cpuCoreInfo = AppUtil.Processor.getCpuCoreInfo(this.corNumber, requireContext())
        cpuCoreInfoList.addAll(cpuCoreInfo)

        mBinding.cpuCoreInfoList = cpuCoreInfoList
        mBinding.notifyChange()
    }
    private fun getJavaInformation(){
        this.javaInfoList.clear()
        this.javaInfoList.addAll(AppUtil.Processor.getJavaInformation(requireContext()))
        mBinding.javaInfoList = javaInfoList
        mBinding.notifyChange()
    }
}