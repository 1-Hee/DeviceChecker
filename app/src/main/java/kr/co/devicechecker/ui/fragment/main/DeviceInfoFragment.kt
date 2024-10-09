package kr.co.devicechecker.ui.fragment.main

import kr.co.devicechecker.BR
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.ui.BaseFragment
import kr.co.devicechecker.data.dto.Info
import kr.co.devicechecker.databinding.FragmentDeviceInfoBinding
import kr.co.devicechecker.util.AppUtil
import timber.log.Timber

class DeviceInfoFragment : BaseFragment<FragmentDeviceInfoBinding>() {
    private val deviceInfoList = mutableListOf<Info>()
    private val displayInfoList = mutableListOf<Info>()
    private val snapShotList = mutableListOf<Info>()

    companion object {
        fun newInstance(): DeviceInfoFragment {
            return DeviceInfoFragment()
        }
    }
    override fun initViewModel() {}
    override fun getDataBindingConfig(): DataBindingConfig {

        snapShotList.add(Info("고유번호", "1234-1234"))
        snapShotList.add(Info("고유번호", "1234-1234"))
        snapShotList.add(Info("고유번호", "1234-1234"))


        return DataBindingConfig(R.layout.fragment_device_info)
            .addBindingParam(BR.deviceInfoList, deviceInfoList)
            .addBindingParam(BR.displayInfoList, displayInfoList)
            .addBindingParam(BR.snapShotList, snapShotList)
    }
    override fun initView() {
        Timber.i("initView ${this.javaClass.simpleName}")
        getDeviceInfo()
        getDisplayInfo()
        saveDisplayInfo()
    }
    private fun getDeviceInfo(){
        this.deviceInfoList.clear()
        deviceInfoList.addAll(AppUtil.Device.getDeviceInfo(requireContext()))
        mBinding.deviceInfoList = deviceInfoList
        mBinding.notifyChange()
    }
    private fun saveDisplayInfo(){
        AppUtil.Device.saveDeviceData(
            requireContext(), this.displayInfoList, this.deviceInfoList
        )
    }
    private fun getDisplayInfo(){
        this.displayInfoList.clear()
        displayInfoList.addAll(AppUtil.Device.getDisplayInfo(requireContext()))
        mBinding.displayInfoList = displayInfoList
        mBinding.notifyChange()
    }
}