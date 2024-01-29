package kr.co.devicechecker.ui.fragment.main

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import kr.co.devicechecker.BR
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.ui.BaseFragment
import kr.co.devicechecker.data.dto.SensorInfo
import kr.co.devicechecker.databinding.FragmentSensorInfoBinding
import kr.co.devicechecker.util.PreferenceUtil


class SensorInfoFragment : BaseFragment<FragmentSensorInfoBinding>() {

    private val sensorInfoList = mutableListOf<SensorInfo>()
    // 값 저장을 위한 prefs 변수
    private lateinit var prefs: PreferenceUtil
    override fun initViewModel() {
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_sensor_info)
            .addBindingParam(BR.sensorInfoList, sensorInfoList)
    }

    override fun initView() {
        prefs = PreferenceUtil(requireContext())
        getSensorInfo()
    }

    override fun onPause() {
        super.onPause()
        val tag = "Sensor"
        val builder = StringBuilder()
        builder.append("[Sensor Info]\n")
        this.sensorInfoList.forEach { info ->
            builder.append("$info\n")
        }
        prefs.setValue(tag, builder.toString())
    }

    private fun getSensorInfo(){
        this.sensorInfoList.clear()
        // 센서 매니저 가져오기
        val sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // 사용 가능한 모든 센서 가져오기
        val sensorList: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)
        // 센서 정보 출력
        for (sensor in sensorList) {
            val sensorInfo = SensorInfo(sensor.name, sensor.type, sensor.vendor)
            this.sensorInfoList.add(sensorInfo)
        }
        mBinding.sensorInfoList = sensorInfoList
        mBinding.notifyChange()
    }

}