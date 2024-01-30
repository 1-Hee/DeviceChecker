package kr.co.devicechecker.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import kr.co.devicechecker.BR
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.listener.ViewClickListener
import kr.co.devicechecker.base.ui.BaseActivity
import kr.co.devicechecker.databinding.ActivityMainBinding
import kr.co.devicechecker.ui.adapter.ViewPager2Adapter
import kr.co.devicechecker.ui.fragment.main.DeviceInfoFragment
import kr.co.devicechecker.ui.fragment.main.DeviceTestFragment
import kr.co.devicechecker.ui.fragment.main.MemoryInfoFragment
import kr.co.devicechecker.ui.fragment.main.ProcessorInfoFragment
import kr.co.devicechecker.ui.fragment.main.SensorInfoFragment
import kr.co.devicechecker.util.AppUtil
import kr.co.devicechecker.util.PreferenceUtil
import timber.log.Timber


class MainActivity : BaseActivity<ActivityMainBinding>() {

    // tabLayout을 위한 리스너
    private lateinit var tabListener: TabLayout.OnTabSelectedListener
    private lateinit var vpAdapter : ViewPager2Adapter
    private lateinit var pagerCallback: ViewPager2.OnPageChangeCallback
    private lateinit var prefs:PreferenceUtil

    override fun getDataBindingConfig(): DataBindingConfig {

        tabListener = object : TabLayout.OnTabSelectedListener {
            // 탭이 선택되었을 때 호출
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // 선택된 탭에 대한 처리
                Timber.d("selected tab is... %s",tab?.position)
                val position = tab?.position?:0
                 mBinding.vpCheckMenu.currentItem = position
            }
            // 탭이 선택 해제되었을 때 호출
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // 선택 해제된 탭에 대한 처리
            }
            // 이미 선택된 탭이 다시 선택되었을 때 호출
            override fun onTabReselected(tab: TabLayout.Tab?) {
                // 이미 선택된 탭이 다시 선택되었을 때의 처리
            }
        }
        // 뷰페이저2 어댑터
        // create child fragments
        val childFragments = listOf(
            DeviceInfoFragment(),
            ProcessorInfoFragment(),
            MemoryInfoFragment(),
            SensorInfoFragment(),
            DeviceTestFragment()
        )
        vpAdapter = ViewPager2Adapter(this)
        vpAdapter.setFragmentList(childFragments)

        pagerCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Timber.d("current position ... %s",position)
                mBinding.tlCheckMenu.getTabAt(position)?.select()
            }
        }

        return DataBindingConfig(R.layout.activity_main)
            .addBindingParam(BR.tabListener, tabListener)
            .addBindingParam(BR.vpAdapter, vpAdapter)
            .addBindingParam(BR.vpCallback, pagerCallback)
            .addBindingParam(BR.click, viewClickListener)
    }

    override fun init(savedInstanceState: Bundle?) {
        prefs = PreferenceUtil(this)
        Timber.plant(Timber.DebugTree())
    }

    private val viewClickListener = object : ViewClickListener {
        override fun onViewClick(view: View) {
            when(view.id){
                R.id.iv_save_all_info -> {
                    Toast.makeText(this@MainActivity, "하드웨어 정보를 저장합니다..", Toast.LENGTH_SHORT).show()
                    AppUtil.File.saveAllHardwareInfo(this@MainActivity, this@MainActivity)
                    val deviceContent = prefs.getValue("Device")
                    val processorContent = prefs.getValue("Processor")
                    val memoryContent = prefs.getValue("Memory")
                    val sensorContent = prefs.getValue("Sensor")
                    val builder = StringBuilder()
                    builder.append(deviceContent).append("\n")
                        .append(processorContent).append("\n")
                        .append(memoryContent).append("\n")
                        .append(sensorContent).append("\n")
                    val totalData = builder.toString()
                    AppUtil.File.saveData(this@MainActivity, totalData)
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if(hasFocus){
            super.hideSystemUI()
        }
    }
}
