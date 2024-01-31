package kr.co.devicechecker.ui.dialog

import kr.co.devicechecker.BR
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.ui.BaseDialog
import kr.co.devicechecker.data.dto.DialogCheck
import kr.co.devicechecker.databinding.DialogCheckBinding

class CheckDialog (
    private val checkDto:DialogCheck,
    private val dialogListener : OnCheckDialogListener
): BaseDialog<DialogCheckBinding>() {
    override fun initViewModel() {
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.dialog_check)
            .addBindingParam(BR.checkDto, checkDto)
    }

    override fun initView() {
        mBinding.tvCheck.setOnClickListener {
            dialogListener.onCheck()
            dismiss()
        }
        mBinding.tvCancel.setOnClickListener {
            dialogListener.onCancel()
            dismiss()
        }
    }

    interface OnCheckDialogListener {
        fun onCheck()
        fun onCancel() {}
    }

}