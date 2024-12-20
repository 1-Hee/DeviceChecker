package kr.co.devicechecker.base.adapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.listener.ViewClickListener
import kr.co.devicechecker.data.dto.ColorInfo
import kr.co.devicechecker.data.dto.CpuCoreInfo
import kr.co.devicechecker.data.dto.IconInfo
import kr.co.devicechecker.data.dto.Info
import kr.co.devicechecker.data.dto.Permission
import kr.co.devicechecker.data.dto.SensorInfo
import kr.co.devicechecker.data.dto.StorageInfo
import kr.co.devicechecker.databinding.ItemCpuCoreBinding
import kr.co.devicechecker.databinding.ItemDeviceSnapshotBinding
import kr.co.devicechecker.databinding.ItemInfoBinding
import kr.co.devicechecker.databinding.ItemPermissionBinding
import kr.co.devicechecker.databinding.ItemSenorBinding
import kr.co.devicechecker.databinding.ItemSettingBinding
import kr.co.devicechecker.databinding.ItemSpecificationReportBinding
import kr.co.devicechecker.databinding.ItemStorageBinding
import kr.co.devicechecker.databinding.ItemTestBinding
import kr.co.devicechecker.databinding.ItemTestBtnBinding
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
        @BindingAdapter(value = ["coreInfoList"], requireAll = true)
        fun setCoreAdapter(recyclerView: RecyclerView, infoList:List<CpuCoreInfo>){
            val llm = LinearLayoutManager(recyclerView.context, RecyclerView.VERTICAL, false);
            recyclerView.layoutManager = llm
            val adapter = object : BaseDataBindingAdapter<CpuCoreInfo, ItemCpuCoreBinding>(recyclerView.context){
                override fun getDataBindingConfig(): DataBindingConfig {
                    return DataBindingConfig(R.layout.item_cpu_core)
                }
                override fun onBindItem(
                    binding: ItemCpuCoreBinding,
                    position: Int,
                    item: CpuCoreInfo,
                    holder: RecyclerView.ViewHolder
                ) {
                    binding.coreInfo = item
                    binding.notifyChange()
                }
            }
            adapter.setItemList(infoList)
            recyclerView.adapter = adapter
        }

        @JvmStatic
        @BindingAdapter(value = ["storageInfoList"], requireAll = true)
        fun setStorageAdapter(recyclerView: RecyclerView, infoList:List<StorageInfo>){
            val llm = LinearLayoutManager(recyclerView.context, RecyclerView.VERTICAL, false);
            recyclerView.layoutManager = llm
            val adapter = object : BaseDataBindingAdapter<StorageInfo, ItemStorageBinding>(recyclerView.context){
                override fun getDataBindingConfig(): DataBindingConfig {
                    return DataBindingConfig(R.layout.item_storage)
                }
                override fun onBindItem(
                    binding: ItemStorageBinding,
                    position: Int,
                    item: StorageInfo,
                    holder: RecyclerView.ViewHolder
                ) {
                    binding.storageInfo = item
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

        @JvmStatic
        @BindingAdapter(value = ["testInfoList", "vClick"], requireAll = true)
        fun setTestAdapter(recyclerView: RecyclerView, infoList:List<IconInfo>, viewClick:ViewClickListener){
            val llm = LinearLayoutManager(recyclerView.context, RecyclerView.VERTICAL, false);
            recyclerView.layoutManager = llm
            val adapter = object : BaseDataBindingAdapter<IconInfo, ItemTestBtnBinding>(recyclerView.context) {
                override fun getDataBindingConfig(): DataBindingConfig {
                    return DataBindingConfig(R.layout.item_test_btn)
                }

                override fun onBindItem(
                    binding: ItemTestBtnBinding,
                    position: Int,
                    item: IconInfo,
                    holder: RecyclerView.ViewHolder
                ) {
                    binding.iconInfo = item
                    binding.click = viewClick
                    binding.ivTest.setImageDrawable(item.icon)
                    binding.notifyChange()
                }

            }
            adapter.setItemList(infoList)
            recyclerView.adapter = adapter
        }


        @JvmStatic
        @BindingAdapter(value = ["reportList", "vClick"], requireAll = true)
        fun setReportAdapter(recyclerView: RecyclerView, infoList:List<ColorInfo>, viewClick:ViewClickListener){
            val llm = LinearLayoutManager(recyclerView.context, RecyclerView.VERTICAL, false);
            recyclerView.layoutManager = llm
            val adapter = object : BaseDataBindingAdapter<
                    ColorInfo, ItemSpecificationReportBinding>(recyclerView.context){
                override fun getDataBindingConfig(): DataBindingConfig {
                    return DataBindingConfig(R.layout.item_specification_report)
                }

                override fun onBindItem(
                    binding: ItemSpecificationReportBinding,
                    position: Int,
                    item: ColorInfo,
                    holder: RecyclerView.ViewHolder
                ) {
                    binding.click = viewClick
                    binding.colorInfo = item
                    binding.notifyChange()
                }
            }
            adapter.setItemList(infoList)
            recyclerView.adapter = adapter
        }

        @JvmStatic
        @BindingAdapter(value = ["snapShotList"], requireAll = true)
        fun setSnapshotAdapter(recyclerView: RecyclerView, infoList:List<Info>){
            val llm = LinearLayoutManager(recyclerView.context, RecyclerView.VERTICAL, false);
            recyclerView.layoutManager = llm

            val adapter = object : BaseDataBindingAdapter<
                    Info, ItemDeviceSnapshotBinding>(recyclerView.context){
                override fun getDataBindingConfig(): DataBindingConfig {
                    return DataBindingConfig(R.layout.item_device_snapshot)
                }

                override fun onBindItem(
                    binding: ItemDeviceSnapshotBinding,
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

        // User Permissions ....
        @JvmStatic
        @BindingAdapter(value = ["permissionInfoList", "vClick"], requireAll = true)
        fun setPermissionAdapter(recyclerView: RecyclerView, infoList:List<Permission>, viewClick:ViewClickListener){
            val llm = LinearLayoutManager(recyclerView.context, RecyclerView.VERTICAL, false);
            recyclerView.layoutManager = llm
            val adapter = object : BaseDataBindingAdapter<
                    Permission, ItemPermissionBinding>(recyclerView.context) {
                override fun getDataBindingConfig(): DataBindingConfig {
                    return DataBindingConfig(R.layout.item_permission)
                }

                override fun onBindItem(
                    binding: ItemPermissionBinding,
                    position: Int,
                    item: Permission,
                    holder: RecyclerView.ViewHolder
                ) {
                    binding.permissionInfo = item
                    binding.click = viewClick
                    binding.notifyChange()
                }
            }

            adapter.setItemList(infoList)
            recyclerView.adapter = adapter
        }

        // Setting Options...
        @JvmStatic
        @BindingAdapter(value = ["settingOptionList", "vClick"], requireAll = true)
        fun setSettingAdapter(recyclerView: RecyclerView, infoList:List<Info>, viewClick:ViewClickListener){
            val llm = LinearLayoutManager(recyclerView.context, RecyclerView.VERTICAL, false);
            recyclerView.layoutManager = llm

            val adapter = object : BaseDataBindingAdapter<
                    Info, ItemSettingBinding>(recyclerView.context) {
                override fun getDataBindingConfig(): DataBindingConfig {
                    return DataBindingConfig(R.layout.item_setting)
                }

                override fun onBindItem(
                    binding: ItemSettingBinding,
                    position: Int,
                    item: Info,
                    holder: RecyclerView.ViewHolder
                ) {
                    binding.settingInfo = item
                    binding.click = viewClick
                    binding.notifyChange()
                }

            }
            adapter.setItemList(infoList)
            recyclerView.adapter = adapter
        }

        // Display Test & Audio Test
        @JvmStatic
        @BindingAdapter(value = ["testList", "vClick"], requireAll = true)
        fun setTestingAdapter(recyclerView: RecyclerView, infoList:List<IconInfo>, viewClick:ViewClickListener){
            val llm = LinearLayoutManager(recyclerView.context, RecyclerView.VERTICAL, false);
            recyclerView.layoutManager = llm

            val adapter = object : BaseDataBindingAdapter<
                    IconInfo, ItemTestBinding>(recyclerView.context){
                override fun getDataBindingConfig(): DataBindingConfig {
                    return DataBindingConfig(R.layout.item_test)
                }

                override fun onBindItem(
                    binding: ItemTestBinding,
                    position: Int,
                    item: IconInfo,
                    holder: RecyclerView.ViewHolder
                ) {
                    binding.iconInfo = item
                    binding.click = viewClick
                    binding.notifyChange()
                }

            }
            adapter.setItemList(infoList)
            recyclerView.adapter = adapter
        }


    }
}