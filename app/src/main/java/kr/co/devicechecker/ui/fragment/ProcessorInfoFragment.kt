package kr.co.devicechecker.ui.fragment

import kr.co.devicechecker.BR
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.ui.BaseFragment
import kr.co.devicechecker.data.dto.Info
import kr.co.devicechecker.databinding.FragmentProcessorInfoBinding


class ProcessorInfoFragment : BaseFragment<FragmentProcessorInfoBinding>() {

    private val infoList = mutableListOf<Info>()
    override fun initViewModel() {
    }

    override fun getDataBindingConfig(): DataBindingConfig {

        return DataBindingConfig(R.layout.fragment_processor_info)
            .addBindingParam(BR.infoList, listOf<Info>())
    }

    override fun initView() {
    }

    private fun getSystemInfo(){

        /*
            var chipSet:String, // 칩셋 정보(CPU), 프로세서 정보
         */


    }
}