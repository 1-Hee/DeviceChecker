package kr.co.devicechecker.ui.fragment.main

import android.content.ContentResolver
import android.content.Context
import kr.co.devicechecker.BR
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.ui.BaseFragment
import kr.co.devicechecker.data.dto.Info
import kr.co.devicechecker.databinding.FragmentDeviceInfoBinding
import kr.co.devicechecker.util.DeviceInfo
import timber.log.Timber


class DeviceInfoFragment : BaseFragment<FragmentDeviceInfoBinding>() {

    // 표시할 정보 목록
    private val snapShotList = mutableListOf<Info>()
    private val displayInfoList = mutableListOf<Info>()
    private val processorInfoList = mutableListOf<Info>()
    private val javaInfoList = mutableListOf<Info>()
    private val otherInfoList = mutableListOf<Info>()

    companion object {

        @Deprecated("This function may cause potential errors and is being deprecated.")
        fun newInstance(): DeviceInfoFragment {
            return DeviceInfoFragment()
        }
    }
    override fun initViewModel() {

    }
    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_device_info)
            .addBindingParam(BR.snapShotList, snapShotList)
            .addBindingParam(BR.displayInfoList, displayInfoList)
            .addBindingParam(BR.processorInfoList, processorInfoList)
            .addBindingParam(BR.javaInfoList, javaInfoList)
            .addBindingParam(BR.otherInfoList, otherInfoList)
    }
    override fun initView() {
        Timber.i("initView ${this.javaClass.simpleName}")
        val context = requireContext()
        val resolver = requireActivity().contentResolver

        initModelAndOsVersion(context) // task 1
        initDeviceSnapshot(context, resolver) // task 2
        initDisplayInfo(context) // task 3
        initProcessorInfo(context) // task 4
        initJavaInformation(context) // task 5
        initOtherInfo(context) // task 6
        mBinding.notifyChange()

        // saveDisplayInfo()
    }

    // task 1. Get Device Model Name & Android Version
    private fun initModelAndOsVersion(context:Context) {
        val strArr = DeviceInfo.getModelAndOsVersion(context)
        if(strArr.size < 2) return
        // ui init
        mBinding.tvDeviceModel.text = strArr[0]
        mBinding.tvAndroidVersion.text = strArr[1]
    }

    // task 2. Get Device Snapshot Info
    private fun initDeviceSnapshot(context: Context, resolver: ContentResolver){
        this.snapShotList.clear()
        val snapInfoList = DeviceInfo.getDeviceSnapshotList(context, resolver)
        this.snapShotList.addAll(snapInfoList)
        mBinding.setVariable(BR.snapShotList, snapShotList)
    }


    // task 3. Get Display Info
    private fun initDisplayInfo(context: Context){
        this.displayInfoList.clear()
        displayInfoList.addAll(DeviceInfo.getDisplayInfo(context))
        mBinding.displayInfoList = displayInfoList
    }

    // task 4. Get Processor Info ( Common )
    private fun initProcessorInfo(context: Context){
        this.processorInfoList.clear()
        this.processorInfoList.addAll(DeviceInfo.getProcessorInfo(context))
        mBinding.processorInfoList = processorInfoList
    }


    // task 5. Get Java & JVM Info
    private fun initJavaInformation(context: Context){
        this.javaInfoList.clear()
        this.javaInfoList.addAll(DeviceInfo.getJavaInformation(context))
        mBinding.javaInfoList = javaInfoList
        mBinding.notifyChange()
    }

    // task 6. Other Information
    private fun initOtherInfo(context: Context){
        this.otherInfoList.clear()
        otherInfoList.addAll(DeviceInfo.getOtherInfo(context))
        mBinding.otherInfoList = otherInfoList
    }

    // TODO 값 저장하는 기능 이전하기...
    private fun saveDisplayInfo(){
        /*
        AppUtil.Device.saveDeviceData(
            requireContext(), this.displayInfoList, this.deviceInfoList
        )
         */
    }
}