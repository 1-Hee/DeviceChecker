package kr.co.devicechecker.ui.fragment

import kr.co.devicechecker.BR
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.ui.BaseFragment
import kr.co.devicechecker.data.dto.Info
import kr.co.devicechecker.databinding.LayoutInfoFragmentBinding


class ProcessorInfoFragment : BaseFragment<LayoutInfoFragmentBinding>() {

    private val infoList = mutableListOf<Info>()
    override fun initViewModel() {
    }

    override fun getDataBindingConfig(): DataBindingConfig {

        return DataBindingConfig(R.layout.layout_info_fragment)
            .addBindingParam(BR.infoList, listOf<Info>())
    }

    override fun initView() {
    }

    private fun getSystemInfo(){

        /*
            var chipSet:String, // 칩셋 정보(CPU), 프로세서 정보
    var memoryInfo:String, // 메모리 (RAM) 정보
    var storageInfo:String,// 기억장치 (ROM) 정보
    var storage:String, // RAM / ROM

         */


    }
}