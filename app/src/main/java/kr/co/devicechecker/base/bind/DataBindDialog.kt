package kr.co.devicechecker.base.bind

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.forEach
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment

abstract class DataBindDialog<D:ViewDataBinding> : DialogFragment() {
    protected lateinit var mActivity: AppCompatActivity
    protected lateinit var mBinding : D
    /**
     *  init view Model
     */
    protected abstract fun initViewModel()
    /**
     *  get data bind config!
     */
    protected abstract fun getDataBindingConfig():DataBindingConfig
    /**
     *  레이 아웃 생성 후!
     */
    protected abstract fun initView()
    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mActivity = context as AppCompatActivity
        initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // config load
        val dbConfig:DataBindingConfig = getDataBindingConfig()
        val binding : D = DataBindingUtil.inflate(inflater, dbConfig.layout, container, false)
        // set background transparent
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // set lifecycle owner
        binding.lifecycleOwner = viewLifecycleOwner
        // set data biding variables
        val params:SparseArray<Any?> = dbConfig.bindingParams
        params.forEach { key, value ->
            binding.setVariable(key, value)
        }
        mBinding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // init view!
        initView()
    }

    // release resource!
    override fun onDestroyView() {
        super.onDestroyView()
        mBinding.unbind()
    }
}