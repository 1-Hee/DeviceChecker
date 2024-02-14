package kr.co.devicechecker.ui.fragment.display

import android.app.Activity
import android.view.View
import com.google.android.gms.ads.interstitial.InterstitialAd
import kr.co.devicechecker.BR
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.listener.ViewClickListener
import kr.co.devicechecker.base.ui.BaseFragment
import kr.co.devicechecker.databinding.FragmentDisplayStartBinding

class DisplayStartFragment : BaseFragment<FragmentDisplayStartBinding>() {
    override fun initViewModel() {
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_display_start)
            .addBindingParam(BR.click, viewClickListener)
    }

    override fun initView() {
    }

    val viewClickListener = object : ViewClickListener {
        override fun onViewClick(view: View) {
            when(view.id){
                R.id.btn_color_test -> {
                    nav().navigate(R.id.displayColorFragment)
                }
                R.id.btn_go_back -> {
                    requireActivity().setResult(Activity.RESULT_CANCELED)
                    requireActivity().finish()
                }
            }
        }

    }
}