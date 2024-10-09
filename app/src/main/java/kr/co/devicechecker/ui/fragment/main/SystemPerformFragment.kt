package kr.co.devicechecker.ui.fragment.main

import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.ui.BaseFragment
import kr.co.devicechecker.databinding.FragmentSystemPerformBinding

class SystemPerformFragment : BaseFragment<FragmentSystemPerformBinding>() {
    override fun initViewModel() {
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_system_perform)
    }

    override fun initView() {

    }
}