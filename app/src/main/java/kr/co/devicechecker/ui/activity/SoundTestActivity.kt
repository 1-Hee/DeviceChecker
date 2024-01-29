package kr.co.devicechecker.ui.activity

import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kr.co.devicechecker.BR
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.listener.ViewClickListener
import kr.co.devicechecker.base.ui.BaseActivity
import kr.co.devicechecker.databinding.ActivitySoundTestBinding

class SoundTestActivity : BaseActivity<ActivitySoundTestBinding>() {

    // RingtoneManager에서 기본 벨소리의 Uri 가져오기
    private val ringtoneUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
    // RingtoneManager를 사용하여 기본 벨소리 재생
    private lateinit var ringtone: Ringtone
    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.activity_sound_test)
            .addBindingParam(BR.click, viewClickListener)
            .addBindingParam(BR.isPlay, false)
    }

    override fun init(savedInstanceState: Bundle?) {
        ringtone = RingtoneManager.getRingtone(this, ringtoneUri)
    }

    private val viewClickListener = object : ViewClickListener {
        override fun onViewClick(view: View) {
            when(view.id){
                R.id.btn_sound_test -> {
                    val isPlay = !(mBinding.isPlay?:false)
                    mBinding.isPlay = isPlay
                    if(isPlay){
                        Toast.makeText(this@SoundTestActivity, "소리를 재생합니다...", Toast.LENGTH_SHORT).show()
                        // ringtone.play()
                    }else {
                        Toast.makeText(this@SoundTestActivity, "소리 재생을 종료합니다...", Toast.LENGTH_SHORT).show()
                        // ringtone.stop()
                    }
                }
                R.id.btn_go_back -> {
                    setResult(RESULT_CANCELED)
                    finish()
                }
            }
        }
    }

    // Full Screen Setting
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if(hasFocus){
            super.hideSystemUI()
        }
    }

}