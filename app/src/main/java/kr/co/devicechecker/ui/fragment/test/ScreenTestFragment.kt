package kr.co.devicechecker.ui.fragment.test

import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.view.View
import kr.co.devicechecker.R
import kr.co.devicechecker.BR
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.listener.ViewClickListener
import kr.co.devicechecker.base.ui.BaseFragment
import kr.co.devicechecker.data.dto.IconInfo
import kr.co.devicechecker.databinding.FragmentScreenTestBinding

class ScreenTestFragment : BaseFragment<FragmentScreenTestBinding>() {

    // variables
    private lateinit var testInfoList:MutableList<IconInfo>

    override fun initViewModel() {
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        testInfoList = mutableListOf()

        // get values...
        val context = requireContext()
        val nameArray:Array<String> = context.resources
            .getStringArray(R.array.test_display_name); // 테스트 명
        val guideArray:Array<String> = context.resources
            .getStringArray(R.array.test_display_guide); // 테스트 설명
        val iconArray: TypedArray = context.resources.obtainTypedArray(R.array.icon_test_display) // 아이콘

        for(i in nameArray.indices){
            val drawable: Drawable? = iconArray.getDrawable(i)
            val mIconInfo = IconInfo(
                icon = drawable,name = nameArray[i], value = guideArray[i]
            )
            testInfoList.add(mIconInfo)
        }
        iconArray.recycle()

        return DataBindingConfig(R.layout.fragment_screen_test)
            .addBindingParam(BR.testInfoList, testInfoList)
            .addBindingParam(BR.click, viewClickListener)
    }

    override fun initView() {
    }

    private val viewClickListener = object : ViewClickListener {
        override fun onViewClick(view: View) {
            val context = requireContext()
            val nameArray:Array<String> = context.resources
                .getStringArray(R.array.test_display_name); // 테스트 명

            when(view.tag) {
                nameArray[0] -> { // 디스플레이 테스트
                    nav().navigate(R.id.displayColorFragment)
                }
                else -> {

                }
            }
        }
    }
}