package kr.co.devicechecker.ui.fragment.display

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.View
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kr.co.devicechecker.BR
import kr.co.devicechecker.BuildConfig
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
    // 전면 광고 런처
    private var mInterstitialAd: InterstitialAd? = null
    override fun initViewModel() {
    }
    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_display_color)
            .addBindingParam(BR.isHide, false)
            .addBindingParam(BR.click, viewClickListener)
    }
    override fun initView() {
        autoHideButton()

        // 광고 콜백 함수 세팅
        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Timber.i("Ad was clicked...!!!!!!!!!!!")
                nav().popBackStack()
            }
            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                Timber.i("Ad dismissed fullscreen content...!!!!!!!!!")
                mInterstitialAd = null
            }
            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                // Called when ad fails to show.
                Timber.e("Ad failed to show fullscreen content...!!!!!!!!")
                mInterstitialAd = null
            }
            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Timber.e("Ad recorded an impression...!!!!!!!!!!!")
            }
            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Timber.e("Ad showed fullscreen content...!!!!!!!!!!!!!")
            }
        }
    }
    override fun onResume() {
        super.onResume()
        readyInterstitialAd() // 광고 준비
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
                    if(mInterstitialAd != null){
                        Timber.i("mInterstitialAd null 아님!!!")
                        mInterstitialAd?.show(requireActivity())
                        nav().popBackStack()
                    }else {
                        Timber.i("mInterstitialAd null....!!!!!!!!!!")
                        nav().popBackStack()
                    }
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

    private fun readyInterstitialAd(){
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(requireActivity(), BuildConfig.ADMOB_SCREEN_SDK_KEY, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Timber.e("%s", adError.toString())
                mInterstitialAd = null
            }
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Timber.d("Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })
    }

}