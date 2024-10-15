package kr.co.devicechecker.ui.fragment.check

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import kr.co.devicechecker.R
import kr.co.devicechecker.BR
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.listener.ViewClickListener
import kr.co.devicechecker.base.ui.BaseFragment
import kr.co.devicechecker.data.dto.IconInfo
import kr.co.devicechecker.databinding.FragmentAudioTestBinding

class AudioTestFragment : BaseFragment<FragmentAudioTestBinding>() {

    // variables
    private lateinit var testInfoList:MutableList<IconInfo>

    // RingtoneManager에서 기본 벨소리의 Uri 가져오기
    private val ringtoneUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
    // RingtoneManager를 사용하여 기본 벨소리 재생
    private lateinit var ringtone: Ringtone
    override fun initViewModel() {
    }

    override fun getDataBindingConfig(): DataBindingConfig {

        testInfoList = mutableListOf()

        // get values...
        val context = requireContext()
        val nameArray:Array<String> = context.resources
            .getStringArray(R.array.test_audio_name); // 테스트 명
        val guideArray:Array<String> = context.resources
            .getStringArray(R.array.test_audio_guide); // 테스트 설명
        val iconArray: TypedArray = context.resources.obtainTypedArray(R.array.icon_test_audio) // 아이콘

        for(i in nameArray.indices){
            val drawable: Drawable? = iconArray.getDrawable(i)
            val mIconInfo = IconInfo(
                icon = drawable,name = nameArray[i], value = guideArray[i]
            )
            testInfoList.add(mIconInfo)
        }
        iconArray.recycle()

        return DataBindingConfig(R.layout.fragment_audio_test)
            .addBindingParam(BR.testInfoList, testInfoList)
            .addBindingParam(BR.click, viewClickListener)

    }

    override fun initView() {
        ringtone = RingtoneManager.getRingtone(requireContext(), ringtoneUri)

    }


    private val viewClickListener = object : ViewClickListener {
        override fun onViewClick(view: View) {
            val context = requireContext()
            val nameArray:Array<String> = context.resources
                .getStringArray(R.array.test_audio_name); // 테스트 명

            when(view.tag) {
                nameArray[0] -> { // 소리 테스트
                   playRingTone(context) // Ring Tone 재생 ...
                }
                else -> {

                }
            }
        }
    }


    // 소리 재생 테스트
    private fun playRingTone(context: Context):Boolean {
        val msgArr:Array<String> = context.resources
            .getStringArray(R.array.arr_test_ringtone_msg); // 알림 메세지 배열

        return if (isDevicePlayable(context)) {
            if (!ringtone.isPlaying) {
                Toast.makeText(context, msgArr[0], Toast.LENGTH_SHORT).show()
                ringtone.play()
            } else {
                Toast.makeText(context, msgArr[1], Toast.LENGTH_SHORT).show()
                ringtone.stop()
            }
            (!ringtone.isPlaying)
        } else {
            Toast.makeText(context, msgArr[2], Toast.LENGTH_SHORT).show()
            false
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