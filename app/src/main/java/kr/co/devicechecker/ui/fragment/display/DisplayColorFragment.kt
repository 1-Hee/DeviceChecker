package kr.co.devicechecker.ui.fragment.display

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kr.co.devicechecker.BR
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.listener.ViewClickListener
import kr.co.devicechecker.base.ui.BaseFragment
import kr.co.devicechecker.databinding.FragmentDisplayColorBinding
import timber.log.Timber
import java.util.Timer
import java.util.TimerTask

class DisplayColorFragment : BaseFragment<FragmentDisplayColorBinding>(){
    private var timer: Timer? = null
    private val TIME_PERIOD = 3000L
    override fun initViewModel() {
    }
    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_display_color)
            .addBindingParam(BR.isHide, false)
            .addBindingParam(BR.click, viewClickListener)
    }
    override fun initView() {
        autoHideButton()
    }
    override fun onResume() {
        super.onResume()


        var idx = 0
        val colorArray = arrayOf(
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.BLACK,
            Color.WHITE
        )
        timer?.cancel()
        timer = Timer()

        val handler = Handler(Looper.getMainLooper())
        val timerTask = object : TimerTask(){
            override fun run() {
                handler.post {
                    mBinding.ivColor.setBackgroundColor(colorArray[idx])
                    idx = (idx + 1) % 5
                }
            }
        }
        timer?.schedule(timerTask, 0, 1500)// 1.5초
    }
    private val viewClickListener = object : ViewClickListener {
        override fun onViewClick(view: View) {
            when(view.id){
                R.id.iv_color -> {
                    autoHideButton()
                }
                R.id.btn_exit_color -> {
                    nav().popBackStack()
                }
            }
        }
    }
    private fun autoHideButton(){
        val flag = mBinding.isHide?:false
        mBinding.isHide = !flag
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            // 3초 후에 실행할 코드 작성
            if(mBinding.isHide != true){
                mBinding.isHide = true
                mBinding.notifyChange()
            }
        }, TIME_PERIOD) // 3000 밀리초 = 3초
        mBinding.notifyChange()
    }
    override fun onPause() {
        super.onPause()
        Timber.d("onPause...")
        // timer 정리
        timer?.cancel()
        timer = null
    }
}