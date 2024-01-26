package kr.co.devicechecker.ui.fragment

import kr.co.devicechecker.BR
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.ui.BaseFragment
import kr.co.devicechecker.data.dto.Info
import kr.co.devicechecker.databinding.FragmentSensorInfoBinding


class SensorInfoFragment : BaseFragment<FragmentSensorInfoBinding>() {

    private val sensorInfoList = mutableListOf<Info>()
    override fun initViewModel() {
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_sensor_info)
            .addBindingParam(BR.sensorInfoList, sensorInfoList)
    }

    override fun initView() {

    }

}