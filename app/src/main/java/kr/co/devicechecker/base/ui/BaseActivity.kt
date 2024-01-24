package kr.co.devicechecker.base.ui

import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.annotation.NonNull
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kr.co.devicechecker.base.bind.DataBindingActivity
import timber.log.Timber

abstract class BaseActivity<D:ViewDataBinding> : DataBindingActivity<D>() {
    // application scope vm provider
    private var mApplicationProvider: ViewModelProvider? = null

    // get viewModel without factory
    protected fun <T:ViewModel> getApplicationScopeViewModel(@NonNull modelClass:Class<T>):T{
        if(mApplicationProvider  == null){
            this.mApplicationProvider = ViewModelProvider(this.application as BaseApplication)
        }
        return mApplicationProvider!![modelClass]
    }
    // get viewModel with factory
    protected fun <T: AndroidViewModel> getApplicationScopeViewModel(modelClass: Class<T>, factory:ViewModelProvider.NewInstanceFactory):T{
        if(mApplicationProvider == null){
            mApplicationProvider = ViewModelProvider(this, factory)
        }
        return mApplicationProvider!![modelClass]
    }

    // 시스템 UI를 숨기는 함수
    @Suppress("DEPRECATION")
    protected fun hideSystemUI(){
        Timber.w("at %s, System UI is hidden....", this.javaClass.simpleName)
        val window: Window = window;

        // Android 11(R) 대응
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.setDecorFitsSystemWindows(false)
            val controller = window.insetsController

            if(controller != null){
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }else {
            val decorView: View = window.decorView
            // version Lollipop 대응
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                window.statusBarColor = Color.TRANSPARENT
            }
            val uiOption = View.SYSTEM_UI_FLAG_IMMERSIVE or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_FULLSCREEN;

            decorView.systemUiVisibility = uiOption
        }
    }

}