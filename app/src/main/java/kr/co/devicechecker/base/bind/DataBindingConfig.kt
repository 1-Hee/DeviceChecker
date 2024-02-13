package kr.co.devicechecker.base.bind

import android.util.SparseArray
import androidx.annotation.NonNull
import androidx.lifecycle.ViewModel

class DataBindingConfig {
    // layout Id
    private val _layout:Int
    val layout:Int get() = _layout
    // viewModel Id in data binding
    private val _vmVariableId:Int
    val vmVariableId:Int get() = _vmVariableId
    // viewModel
    private val _stateViewModel:ViewModel?
    val stateViewModel:ViewModel? get() = _stateViewModel
    // params
    private val _bindingParams:SparseArray<Any?> = SparseArray()
    val bindingParams:SparseArray<Any?> get() = _bindingParams

    // constructor
    constructor(@NonNull layout:Int){
        this._layout = layout
        _vmVariableId = -1
        _stateViewModel = null
    }
    constructor(
        layout:Int,
        vmVariableId: Int,
        viewModel: ViewModel
    ){
        this._layout = layout
        this._vmVariableId = vmVariableId
        this._stateViewModel = viewModel
    }

    fun addBindingParam(
        variableId: Int,
        obj:Any?
    ): DataBindingConfig {
        if(_bindingParams[variableId] == null && obj != null){
            _bindingParams.put(variableId, obj)
        }
        return this
    }
}
