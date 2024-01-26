package kr.co.devicechecker.ui.fragment

import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.ui.BaseFragment
import kr.co.devicechecker.databinding.FragmentDeviceTestBinding


class DeviceTestFragment : BaseFragment<FragmentDeviceTestBinding>() {

    override fun initViewModel() {
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_device_test)
    }

    override fun initView() {
        // TODO 1. 색깔 테스트트, 액티비티 하나 만들어서!
        // TODO 2. 사운드 테스트, 음원 하나 재생!

    }
}