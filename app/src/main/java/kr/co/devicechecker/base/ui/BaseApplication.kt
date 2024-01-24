package kr.co.devicechecker.base.ui

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.multidex.BuildConfig
import androidx.multidex.MultiDexApplication
import timber.log.Timber
import timber.log.Timber.DebugTree

class BaseApplication : MultiDexApplication(), ViewModelStoreOwner {
    // Viewmodel Store 정의
    private var mAppViewModelStore: ViewModelStore? = null
    override fun onCreate() {
        super.onCreate()
        // create ViewModelStore
        mAppViewModelStore = ViewModelStore()
        // set Timber Logger for Debugging
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }
    override val viewModelStore: ViewModelStore
        get() = mAppViewModelStore!!

}
