package kr.co.devicechecker.ui.fragment

import kr.co.devicechecker.BR
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.ui.BaseFragment
import kr.co.devicechecker.data.dto.Info
import kr.co.devicechecker.databinding.FragmentSensorInfoBinding


class SensorInfoFragment : BaseFragment<FragmentSensorInfoBinding>() {
    override fun initViewModel() {
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_sensor_info)
            .addBindingParam(BR.infoList, listOf<Info>())
    }

    override fun initView() {

    }

}