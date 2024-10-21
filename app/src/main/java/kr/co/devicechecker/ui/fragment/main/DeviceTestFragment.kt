package kr.co.devicechecker.ui.fragment.main

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdRequest
import kr.co.devicechecker.BR
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.listener.ViewClickListener
import kr.co.devicechecker.base.ui.BaseFragment
import kr.co.devicechecker.data.dto.ColorInfo
import kr.co.devicechecker.data.dto.IconInfo
import kr.co.devicechecker.databinding.FragmentDeviceTestBinding
import kr.co.devicechecker.ui.activity.DisplayTestActivity
import kr.co.devicechecker.util.FileManager
import timber.log.Timber


class DeviceTestFragment : BaseFragment<FragmentDeviceTestBinding>() {

    // 다른 액티비티 이동후 결과 값을 받아 핸들링할 런쳐
    private lateinit var launcher: ActivityResultLauncher<Intent>;
    override fun initViewModel() {}

    override fun getDataBindingConfig(): DataBindingConfig {
        // 디스플레이 테스트
        val testInfoList = mutableListOf<IconInfo>()
        val context = requireContext()

        // Display Check
        testInfoList.add(IconInfo(
            icon = ContextCompat.getDrawable(context, R.drawable.ic_display_check),
            name = context.getString(R.string.device_display_test),
            value = context.getString(R.string.device_display_test_desc)
        ))

        testInfoList.add(
            IconInfo(
                icon = ContextCompat.getDrawable(context, R.drawable.ic_headset_mic),
                name = context.getString(R.string.sound_device_test),
                value = context.getString(R.string.sound_device_test_desc)
        ))

        // 기기 사양 보고서
        val reportList = mutableListOf<ColorInfo>()
        val reportArray:Array<String> = context.resources.getStringArray(R.array.device_report_list); // 레포트 리스트
        for(i in reportArray.indices){
            val reportName = reportArray[i]
            val color = when(i){
                0 -> ContextCompat.getColor(context,R.color.red400)
                1 -> ContextCompat.getColor(context, R.color.orange400)
                else -> ContextCompat.getColor(context, R.color.mono400)
            }
            val colorInfo = ColorInfo(colorId = color, name = reportName)
            reportList.add(colorInfo)
        }

        return DataBindingConfig(R.layout.fragment_device_test)
            .addBindingParam(BR.click, clickListener)
            .addBindingParam(BR.testInfoList, testInfoList)
            .addBindingParam(BR.reportList, reportList)

    }

    override fun initView() {
        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { // 액티비티 종료시 결과릴 리턴받기 위한 콜백 함수
                result -> Timber.d("onActivityResult.......")
            if (result.resultCode == Activity.RESULT_OK) {
            }
        }
        // 배너 광고 로드
        val adRequest = AdRequest.Builder().build()
        mBinding.avDeviceTest.loadAd(adRequest)
        mBinding.notifyChange()
    }

    private val clickListener = object : ViewClickListener {
        override fun onViewClick(view: View) {
            val mContext = view.context
            when(view.id){
                R.id.cl_test_btn -> { // 테스트 버튼 클릭
                    val mTag = view.tag?:""
                    when(mTag){
                        mContext.getString(R.string.device_display_test) -> {
                            val intent = Intent(mActivity, DisplayTestActivity::class.java)
                            launcher.launch(intent)
                            // nav().navigate(R.id.move_display_test)
                        }
                        mContext.getString(R.string.sound_device_test) -> {
                            // val intent = Intent(mActivity, SoundTestActivity::class.java)
                            // launcher.launch(intent)
                            nav().navigate(R.id.move_audio_test)
                        }
                    }

                }
                R.id.mcv_spec_report -> { // 레포트 버튼 클릭
                    val reportArray:Array<String> = mContext.resources.getStringArray(R.array.device_report_list); // 레포트 리스트
                    val mTag = view.tag?:""

                    // TODO 문서 다운로드 함수 추가
                    when(mTag){
                        reportArray[0] -> { // HTML
                            // Toast.makeText(mContext, reportArray[0], Toast.LENGTH_SHORT).show()
                            FileManager.saveHTMLFile(requireActivity()) // HTML 저장
                        }
                        reportArray[1] -> { // JSON
                            // Toast.makeText(mContext, reportArray[1], Toast.LENGTH_SHORT).show()
                            FileManager.saveJsonFile(requireActivity()) // JSON 저장
                        }
                        reportArray[2] -> { // TXT
                            FileManager.saveTextFile(requireActivity()) // 텍스트 저장
                        }
                    }
                }

            }
        }
    }
}