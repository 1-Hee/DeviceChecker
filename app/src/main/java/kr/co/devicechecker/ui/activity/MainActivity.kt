package kr.co.devicechecker.ui.activity

import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import kr.co.devicechecker.BR
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.ui.BaseActivity
import kr.co.devicechecker.databinding.ActivityMainBinding
import kr.co.devicechecker.ui.adapter.ViewPager2Adapter
import kr.co.devicechecker.ui.fragment.DeviceInfoFragment
import kr.co.devicechecker.ui.fragment.MemoryInfoFragment
import kr.co.devicechecker.ui.fragment.ProcessorInfoFragment
import kr.co.devicechecker.ui.fragment.SensorInfoFragment
import timber.log.Timber


class MainActivity : BaseActivity<ActivityMainBinding>() {

    // tabLayout을 위한 리스너
    private lateinit var tabListener: TabLayout.OnTabSelectedListener
    private lateinit var vpAdapter : ViewPager2Adapter
    private lateinit var pagerCallback: ViewPager2.OnPageChangeCallback

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
            SensorInfoFragment()
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
            .addBindingParam(BR.deviceInfo, null)
            .addBindingParam(BR.path, "")
            .addBindingParam(BR.tabListener, tabListener)
            .addBindingParam(BR.vpAdapter, vpAdapter)
            .addBindingParam(BR.vpCallback, pagerCallback)
    }

    override fun init(savedInstanceState: Bundle?) {
        Timber.plant(Timber.DebugTree())
    }
}
