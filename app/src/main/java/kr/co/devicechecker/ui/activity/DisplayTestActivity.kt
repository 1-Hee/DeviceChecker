package kr.co.devicechecker.ui.activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.ads.MobileAds
import kr.co.devicechecker.R
import kr.co.devicechecker.BR
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.listener.ViewClickListener
import kr.co.devicechecker.base.ui.BaseActivity
import kr.co.devicechecker.databinding.ActivityDisplayTestBinding

class DisplayTestActivity : BaseActivity<ActivityDisplayTestBinding>() {

    override fun getDataBindingConfig(): DataBindingConfig {

        return DataBindingConfig(R.layout.activity_display_test)
            .addBindingParam(BR.click, clickListener)
    }

    override fun init(savedInstanceState: Bundle?) {
        MobileAds.initialize(this) {}
    }

    fun toggleAppBarWithAnimation(isAppBarVisible:Boolean = true) {
        if (isAppBarVisible) {
            mBinding.sAppbar.animate()
                .translationY(-mBinding.sAppbar.height.toFloat())
                .setDuration(300)
                .withEndAction {
                    mBinding.sAppbar.visibility = View.GONE
                }.start()
        } else {
            mBinding.sAppbar.visibility = View.VISIBLE
            mBinding.sAppbar.animate()
                .translationY(0f)
                .setDuration(300)
                .start()
        }
    }

    // Full Screen Setting
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if(hasFocus){
            super.hideAllSystemUI()
            Toast.makeText(this, getString(R.string.ts_guide_full_screen), Toast.LENGTH_SHORT).show()
        }
    }

    private val clickListener = object : ViewClickListener {
        override fun onViewClick(view: View) {
            when(view.id){
                R.id.s_appbar -> {
                   setResult(Activity.RESULT_CANCELED)
                    finish()
                }
                else -> {

                }
            }
        }

    }
}