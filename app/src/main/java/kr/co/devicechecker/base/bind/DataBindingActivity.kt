package kr.co.devicechecker.base.bind

import android.content.res.Configuration
import android.os.Bundle
import android.util.SparseArray
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.forEach
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class DataBindingActivity<D:ViewDataBinding> : AppCompatActivity() {

    protected lateinit var mBinding:D
    protected open fun initViewModel() {

    }

    /**
     * get DataBind Config
     */

    protected abstract fun getDataBindingConfig():DataBindingConfig
    /**
     * Activity 초기화
     */
    abstract fun init(savedInstanceState: Bundle?)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        // config load
        val dbConfig:DataBindingConfig = getDataBindingConfig()
        // create binding property
        val binding:D = DataBindingUtil.setContentView(this, dbConfig.layout)
        // set lifcycler owner
        binding.lifecycleOwner = this
        // set data binding variables
        binding.setVariable(dbConfig.vmVariableId, dbConfig.stateViewModel)
        val params:SparseArray<Any?> = dbConfig.bindingParams
        params.forEach { key, value ->
            binding.setVariable(key, value)
        }
        mBinding = binding
        // init activity
        init(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.unbind()
    }

}