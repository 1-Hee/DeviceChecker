package kr.co.devicechecker.ui.activity

import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.ads.MobileAds
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.ui.BaseActivity
import kr.co.devicechecker.databinding.ActivityDisplayTestBinding

class DisplayTestActivity : BaseActivity<ActivityDisplayTestBinding>() {
    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.activity_display_test)
    }

    override fun init(savedInstanceState: Bundle?) {
        MobileAds.initialize(this) {}
    }

    // Full Screen Setting
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if(hasFocus){
            super.hideAllSystemUI()
            Toast.makeText(this, getString(R.string.ts_guide_full_screen), Toast.LENGTH_SHORT).show()
        }
    }
}