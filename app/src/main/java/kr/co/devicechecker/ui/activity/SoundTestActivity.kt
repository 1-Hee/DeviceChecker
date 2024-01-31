package kr.co.devicechecker.ui.activity

import android.content.Context
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kr.co.devicechecker.BR
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.listener.ViewClickListener
import kr.co.devicechecker.base.ui.BaseActivity
import kr.co.devicechecker.databinding.ActivitySoundTestBinding
import timber.log.Timber

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
        // requestPermission()
        ringtone = RingtoneManager.getRingtone(this, ringtoneUri)
        Timber.i("Ringtone URI: $ringtoneUri")
    }

    private val viewClickListener = object : ViewClickListener {
        override fun onViewClick(view: View) {
            when(view.id){
                R.id.btn_sound_test -> {
                    if(isDevicePlayable(this@SoundTestActivity)){
                        val isPlay = !(mBinding.isPlay?:false)
                        mBinding.isPlay = isPlay
                        if(isPlay){
                            Toast.makeText(this@SoundTestActivity, "벨소리를 재생을 시작합니다...", Toast.LENGTH_SHORT).show()
                            ringtone.play()
                        }else {
                            Toast.makeText(this@SoundTestActivity, "벨소리 재생을 종료합니다...", Toast.LENGTH_SHORT).show()
                            ringtone.stop()
                        }
                    }else {
                        Toast.makeText(this@SoundTestActivity, "현재 벨소리를 재생할 수 없습니다.\n[소리 모드]로 변경해주세요. ", Toast.LENGTH_SHORT).show() }
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
            super.hideTopBar()
        }
    }

    private fun isDevicePlayable(context: Context): Boolean {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                // Android 6.0 (API level 23) 이상에서는 AudioManager의 getRingerMode() 메서드를 사용
                val ringerMode = audioManager.ringerMode
                return ringerMode == AudioManager.RINGER_MODE_NORMAL
            }
            else -> {
                // Android 6.0 미만에서는 deprecated된 getMode() 메서드를 사용
                @Suppress("DEPRECATION")
                return audioManager.mode == AudioManager.RINGER_MODE_NORMAL
            }
        }
    }
}