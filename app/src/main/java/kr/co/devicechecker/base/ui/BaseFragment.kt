package kr.co.devicechecker.base.ui

import androidx.annotation.NonNull
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import kr.co.devicechecker.base.bind.DataBindingFragment

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

    /**
     * 화면 이동을 위한 NavController
     */
    protected fun nav(): NavController {
        return NavHostFragment.findNavController(this)
    }
}