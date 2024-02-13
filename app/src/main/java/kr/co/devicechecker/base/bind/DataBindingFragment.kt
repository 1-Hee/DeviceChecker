package kr.co.devicechecker.base.bind

import android.content.Context
import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.forEach
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class DataBindingFragment<D:ViewDataBinding> : Fragment() {
    protected lateinit var mActivity: AppCompatActivity
    protected lateinit var mBinding:D
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
        // set binding fragment
        val binding : D = DataBindingUtil.inflate(inflater, dbConfig.layout, container, false)
        // set lifecycle owner
        binding.lifecycleOwner = viewLifecycleOwner
        // set data biding variables
        binding.setVariable(dbConfig.vmVariableId, dbConfig.stateViewModel)
        val params:SparseArray<Any?> = dbConfig.bindingParams
        params.forEach { key, value ->
            binding.setVariable(key, value)
        }
        mBinding = binding
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // view 초기화 시
        initView()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        mBinding.unbind()
    }
}