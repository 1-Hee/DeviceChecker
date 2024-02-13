package kr.co.devicechecker.ui.dialog

import android.widget.RadioButton
import kr.co.devicechecker.BR
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.ui.BaseDialog
import kr.co.devicechecker.data.dto.DialogCheck
import kr.co.devicechecker.databinding.DialogSelectTypeBinding

class SelectTypeDialog(
    private val checkDto: DialogCheck,
    private val dialogListener : OnCheckDialogListener
) : BaseDialog<DialogSelectTypeBinding>(){
    override fun initViewModel() {
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.dialog_select_type)
            .addBindingParam(BR.checkDto, checkDto)
    }

    override fun initView() {


        mBinding.btnSelect.setOnClickListener {
            val checkedId = mBinding.rgTypeGroup.checkedRadioButtonId;
            val view = mBinding.rgTypeGroup.findViewById<RadioButton>(checkedId)
            val index = mBinding.rgTypeGroup.indexOfChild(view)
            dialogListener.onSelect(index)
            dismiss()
        }
        mBinding.btnCancel.setOnClickListener {
            dialogListener.onCancel()
            dismiss()
        }
    }

    interface OnCheckDialogListener {
        fun onSelect(index:Int)
        fun onCancel() {}
    }

}