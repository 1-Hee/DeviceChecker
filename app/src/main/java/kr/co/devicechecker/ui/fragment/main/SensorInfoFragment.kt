package kr.co.devicechecker.ui.fragment.main

import kr.co.devicechecker.BR
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.ui.BaseFragment
import kr.co.devicechecker.data.dto.SensorInfo
import kr.co.devicechecker.databinding.FragmentSensorInfoBinding
import kr.co.devicechecker.util.AppUtil
import kr.co.devicechecker.util.PreferenceUtil
import timber.log.Timber

class SensorInfoFragment : BaseFragment<FragmentSensorInfoBinding>() {
    private val sensorInfoList = mutableListOf<SensorInfo>()
    // 값 저장을 위한 prefs 변수
    private lateinit var prefs: PreferenceUtil
    override fun initViewModel() {}
    companion object {
        fun newInstance(): SensorInfoFragment {
            return SensorInfoFragment()
        }
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_sensor_info)
            .addBindingParam(BR.sensorInfoList, sensorInfoList)
    }
    override fun initView() {
        Timber.i("initView ${this.javaClass.simpleName}")
        prefs = PreferenceUtil(requireContext())
        getSensorInfo()
        saveSensorInfo()
    }
    private fun saveSensorInfo(){
        AppUtil.Sensor.saveSensorInfo(
            requireContext(), this.sensorInfoList
        )
    }
    private fun getSensorInfo(){
        this.sensorInfoList.clear()
        this.sensorInfoList.addAll(
            AppUtil.Sensor.getSensorInfo(requireActivity())
        )
        mBinding.sensorInfoList = sensorInfoList
        mBinding.tvSensorCnt.text = "${sensorInfoList.size}"
        mBinding.notifyChange()
    }

}