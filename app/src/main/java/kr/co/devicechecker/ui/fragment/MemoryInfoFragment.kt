package kr.co.devicechecker.ui.fragment

import kr.co.devicechecker.BR
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.ui.BaseFragment
import kr.co.devicechecker.data.dto.Info
import kr.co.devicechecker.databinding.FragmentMemoryInfoBinding
import kr.co.devicechecker.util.AppUtil


class MemoryInfoFragment : BaseFragment<FragmentMemoryInfoBinding>() {

    private val memoryInfoList = mutableListOf<Info>()
    private val javaInfoList = mutableListOf<Info>()
    override fun initViewModel() {
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_memory_info)
            .addBindingParam(BR.memoryInfoList, listOf<Info>())
            .addBindingParam(BR.javaInfoList, listOf<Info>())
    }

    override fun initView() {
        getMemoryInfo()
        getJavaInformation()
    }

    private val memoryFieldMap = hashMapOf(
        Pair("MemTotal", "전체 메모리(Total Memory)"),
        Pair("MemFree", "가용 메모리(Available Memory)"),
        Pair("Buffers", "버퍼 크기(Buffer Size)"),
        Pair("Cached", "캐시 크기(Cached)"),
        Pair("Active", "활성 메모리(Active)"),
        Pair("Inactive", "비활성 메모리(InActive)"),
        Pair("Mapped", "메모리 맵(Memory Map)"),
        Pair("Slab", "Slab"),
        Pair("CmaTotal", "전체 CMA(Total Contiguous Memory Allocator)"),
        Pair("CmaFree", "여유 CMA(Available Contiguous Memory Allocator)"),
        Pair("Hugepagesize", "페이징 크기")
    )
    private val memoryKeyList = arrayOf(
        "MemTotal",
        "MemFree",
        "MemAvailable",
        "Buffers",
        "Cached",
        "Active",
        "Inactive",
        "Mapped",
        "Slab",
        "CmaTotal",
        "CmaFree",
        "Hugepagesize"
    )

    // data 1 . memeory info
    private val memoryMap = hashMapOf<String, String>()
    // data 2. java memory info! heap 등등
    private val javaCommandMap = mapOf(
        Pair("JAVA MIN HEAP", "getprop dalvik.vm.dex2oat-Xms"), // 프로세스의 초기 힙 크기
        Pair("JAVA MAX HEAP", "getprop dalvik.vm.dex2oat-Xmx"), // 프로세스 최대 힙 크기
        Pair("JAVA APPLICATION HEAP", "getprop dalvik.vm.heapsize") // 애플리케이션의 Dalvik 힙 크기
    )
    private fun getMemoryInfo(){
        val cmd = "cat /proc/meminfo"
        val mInfo = AppUtil.Command.executeAdbCommand(cmd)
        val mInfoList = mInfo.split("\n")
        for(i in mInfoList.indices){
            val info = mInfoList[i]
            if(info.isBlank()) continue
            val dataList = info.split(":")
            val field = dataList[0].trim()
            val value = dataList[1].trim()
            memoryMap[field] = value
        }
        // 조회 작업 시작
        memoryKeyList.forEach { key ->
            val field = memoryFieldMap[key]
            val value = memoryMap[key]
            field.let {
                if(!it.isNullOrBlank()){
                    val info = if(value.isNullOrBlank()){
                        Info(it, "알수 없음")
                    }else {
                        val parsedValue = AppUtil.Unit.parseUnit(value)
                        Info(it, parsedValue)
                    }

                    this.memoryInfoList.add(info)
                }
            }
        }
        mBinding.memoryInfoList = memoryInfoList
        mBinding.notifyChange()
    }

    fun getJavaInformation(){
        // get java info
        val javaVersion = System.getProperty("java.version")
        val javaVmVersion = System.getProperty("java.vm.version")
        val javaVmVendor = System.getProperty("java.vm.vendor")
        val javaVmName = System.getProperty("java.vm.name")
        val javaMap = mapOf<String, String>(
            Pair(
                "JAVA VERSION",
                javaVersion.toString()
            ),
            Pair(
                "JAVA VM VERSION",
                javaVmVersion.toString()
            ),
            Pair(
                "JAVA VM VENDOR",
                javaVmVendor.toString()
            ),
            Pair(
                "JAVA VM NAME",
                javaVmName.toString()
            )
        )
        val emptyValue = "알 수 없음"

        // add java info
        javaMap.forEach { (infoName, data) ->
            val value = data.trim().ifBlank { emptyValue }
            val info = Info(
                infoName,
                if(value == "unknown") emptyValue else value
            )
            javaInfoList.add(info)
        }
        // get java memory info
        javaCommandMap.forEach { (key, value) ->
            val resultValue = AppUtil.Command.executeAdbCommand(value).trim()
            val info = Info(key, resultValue.uppercase())
            this.javaInfoList.add(info)
        }

        mBinding.javaInfoList = javaInfoList
        mBinding.notifyChange()
    }


    fun test(){
    }


    // data 3. storage info (internel, externel)
    private fun getStorageInfo(){

    }

}