package kr.co.devicechecker.ui.fragment.main

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import kr.co.devicechecker.BR
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.listener.ViewClickListener
import kr.co.devicechecker.base.ui.BaseFragment
import kr.co.devicechecker.databinding.FragmentDeviceTestBinding
import kr.co.devicechecker.ui.activity.DisplayTestActivity
import kr.co.devicechecker.ui.activity.SoundTestActivity
import timber.log.Timber


class DeviceTestFragment : BaseFragment<FragmentDeviceTestBinding>() {

    override fun initViewModel() {
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_device_test)
            .addBindingParam(BR.click, clickListener)
    }

    override fun initView() {

    }

    private val clickListener = object : ViewClickListener {
        override fun onViewClick(view: View) {
            when(view.id){
                R.id.btn_display_test -> {
                    val intent = Intent(requireContext(), DisplayTestActivity::class.java)
                    launcher.launch(intent)

                }
                R.id.btn_sound_test -> {
                    val intent = Intent(requireContext(), SoundTestActivity::class.java)
                    launcher.launch(intent)
                }
            }

        }
    }

    // 다른 액티비티 이동후 결과 값을 받아 핸들링할 런쳐
    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { // 액티비티 종료시 결과릴 리턴받기 위한 콜백 함수
            result -> Timber.d("onActivityResult.......")
        if (result.resultCode == Activity.RESULT_OK) {
        }
    }

}