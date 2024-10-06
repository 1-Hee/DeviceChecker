package kr.co.devicechecker.ui.activity

import android.os.Bundle
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.ui.BaseActivity
import kr.co.devicechecker.databinding.ActivtyStartBinding

class StartActivity : BaseActivity<ActivtyStartBinding>() {
    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.activty_start)
    }

    override fun init(savedInstanceState: Bundle?) {

    }
}