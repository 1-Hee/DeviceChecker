package kr.co.devicechecker.base.adapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.data.dto.Info
import kr.co.devicechecker.data.dto.SensorInfo
import kr.co.devicechecker.databinding.ItemInfoBinding
import kr.co.devicechecker.databinding.ItemSenorBinding
import kr.co.devicechecker.ui.adapter.ViewPager2Adapter

class StaticAdapter {

    companion object {
        //스케줄 호스트 프래그먼트의 뷰페이저 adapter 등 세팅
        @JvmStatic
        @BindingAdapter(value = ["vpAdapter", "vpCallback"], requireAll = false)
        fun setScheduleHostPager(
            viewPager: ViewPager2,
            listPagerAdapter: ViewPager2Adapter,
            callBack: ViewPager2.OnPageChangeCallback
        ) {
            viewPager.adapter = listPagerAdapter
            viewPager.isUserInputEnabled = true
            viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            // PageChangeCallback Listener unregister하고 다시 register 함. 중복 호출 방지
            viewPager.unregisterOnPageChangeCallback(callBack)
            viewPager.registerOnPageChangeCallback(callBack)
        }

        @JvmStatic
        @BindingAdapter(value = ["infoList"], requireAll = true)
        fun setInfoAdapter(recyclerView: RecyclerView, infoList:List<Info>){
            val llm = LinearLayoutManager(recyclerView.context, RecyclerView.VERTICAL, false);
            recyclerView.layoutManager = llm
            val adapter = object : BaseDataBindingAdapter<Info, ItemInfoBinding>(recyclerView.context){
                override fun getDataBindingConfig(): DataBindingConfig {
                    return DataBindingConfig(R.layout.item_info)
                }
                override fun onBindItem(
                    binding: ItemInfoBinding,
                    position: Int,
                    item: Info,
                    holder: RecyclerView.ViewHolder
                ) {
                    binding.info = item
                    binding.notifyChange()
                }
            }
            adapter.setItemList(infoList)
            recyclerView.adapter = adapter
        }

        @JvmStatic
        @BindingAdapter(value = ["sensorInfoList"], requireAll = true)
        fun setSensorAdapter(recyclerView: RecyclerView, infoList:List<SensorInfo>){
            val llm = LinearLayoutManager(recyclerView.context, RecyclerView.VERTICAL, false);
            recyclerView.layoutManager = llm
            val adapter = object : BaseDataBindingAdapter<SensorInfo, ItemSenorBinding>(recyclerView.context){
                override fun getDataBindingConfig(): DataBindingConfig {
                    return DataBindingConfig(R.layout.item_senor)
                }

                override fun onBindItem(
                    binding: ItemSenorBinding,
                    position: Int,
                    item: SensorInfo,
                    holder: RecyclerView.ViewHolder
                ) {
                    binding.info = item
                    binding.notifyChange()
                }
            }
            adapter.setItemList(infoList)
            recyclerView.adapter = adapter
        }
    }
}