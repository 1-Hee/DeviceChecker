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
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import kr.co.devicechecker.base.bind.DataBindingFragment
import timber.log.Timber

abstract class BaseFragment<D:ViewDataBinding> : DataBindingFragment<D>() {
    // viewModel provider!
    private var mFragmentProvider:ViewModelProvider? = null
    private var mApplicationProvider:ViewModelProvider? = null

    // get viewModel without factory
    protected fun<T: ViewModel> getFragmentScopeViewModel(@NonNull modelClass: Class<T>):T {
        if(mFragmentProvider == null){
            this.mFragmentProvider = ViewModelProvider(this)
        }
        return mFragmentProvider!![modelClass]
    }

    protected fun<T: ViewModel> getApplicationScopeViewModel(@NonNull modelClass:Class<T>):T{
        if(mApplicationProvider  == null){
            this.mApplicationProvider = ViewModelProvider(requireActivity().application as BaseApplication)
        }
        return mApplicationProvider!![modelClass]
    }

    // get viewModel with factory
    protected fun<T: ViewModel> getFragmentScopeViewModel(@NonNull modelClass: Class<T>, factory:ViewModelProvider.NewInstanceFactory):T {
        if(mFragmentProvider == null){
            this.mFragmentProvider = ViewModelProvider(this, factory)
        }
        return mFragmentProvider!![modelClass]
    }
    protected fun <T: AndroidViewModel> getApplicationScopeViewModel(modelClass: Class<T>, factory:ViewModelProvider.NewInstanceFactory):T{
        if(mApplicationProvider == null){
            mApplicationProvider = ViewModelProvider(requireActivity(), factory)
        }
        return mApplicationProvider!![modelClass]
    }

    @Suppress("DEPRECATION")
    protected fun hideSystemUI(isHideAll:Boolean = false){
        Timber.w("at %s, Top bar Hidden...", this.javaClass.simpleName)
        val window: Window = requireActivity().window;

        // Android 11(R) 대응
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.setDecorFitsSystemWindows(false)
            val controller = window.insetsController

            if(controller != null){
                if(isHideAll){ // 전체 숨김
                    controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                    controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }else { // 상단 바만 숨김
                    controller.hide(WindowInsets.Type.statusBars())
                    controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            }
        }else {
            val decorView: View = window.decorView
            // version Lollipop 대응
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                window.statusBarColor = Color.TRANSPARENT
            }

            var uiOption = View.SYSTEM_UI_FLAG_IMMERSIVE or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_FULLSCREEN

            if(isHideAll) { // 전체 숨김
                uiOption = View.SYSTEM_UI_FLAG_IMMERSIVE or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_FULLSCREEN;
            }

            decorView.systemUiVisibility = uiOption
        }
    }

    /**
     * 화면 이동을 위한 NavController
     */
    protected fun nav(): NavController {
        return NavHostFragment.findNavController(this)
    }
}