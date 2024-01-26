package kr.co.devicechecker.ui.fragment

import kr.co.devicechecker.BR
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.ui.BaseFragment
import kr.co.devicechecker.data.dto.Info
import kr.co.devicechecker.databinding.FragmentProcessorInfoBinding
import kr.co.devicechecker.util.AppUtil


class ProcessorInfoFragment : BaseFragment<FragmentProcessorInfoBinding>() {

    private val processorInfoList = mutableListOf<Info>()
    private val cpuCoreInfoList = mutableListOf<Info>()
    private val javaInfoList = mutableListOf<Info>()
    private var corNumber = 0
    override fun initViewModel() {
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_processor_info)
            .addBindingParam(BR.processorInfoList, processorInfoList)
            .addBindingParam(BR.cpuCoreInfoList, cpuCoreInfoList)
            .addBindingParam(BR.javaInfoList, listOf<Info>())
    }

    override fun initView() {
        getProcessorInfo()
        getJavaInformation()
    }

    private val cpuCmdMap = mapOf(
        Pair("기본 CPU 아키텍처(Default CPU Architecture)", "getprop ro.product.cpu.abi"), // 기본 CPU 아키텍처
        Pair("호환 가능한 CPU 아키텍처(Compatible CPU Architecture)", "getprop ro.product.cpu.abilist"), // 호환 CPU 아키텍처
        Pair("보드(Board)", "getprop ro.product.board"), // 보드명
        Pair("칩셋(Chipset)", "getprop ro.hardware"), // 칩셋명
    )
    private fun getProcessorInfo(){
        // 기본 cpu info setting
        val emptyValue = "알 수 없음"
        cpuCmdMap.forEach { (field, cmd) ->
            val data = AppUtil.Command.executeAdbCommand(cmd)
            val info = Info(field, data.trim().ifBlank { emptyValue })
            processorInfoList.add(info)
        }
        // 코어수
        val coreInfo = AppUtil.Command
            .executeAdbCommand("cat sys/devices/system/cpu/present").split("-")
        this.corNumber = coreInfo[0].trim().toInt() + coreInfo[1].trim().toInt() + 1 // number
        val corNumInfo = Info("코어 수(Number of Cores)", this.corNumber.toString())
        processorInfoList.add(corNumInfo)

        val cpuMinCMD = "cat sys/devices/system/cpu/cpufreq/policy0/cpuinfo_min_freq"
        val cpuMaxCMD = "cat sys/devices/system/cpu/cpufreq/policy0/cpuinfo_max_freq"
        val clockMin = AppUtil.Command
            .executeAdbCommand(cpuMinCMD) // KHz
        val clockMax = AppUtil.Command
            .executeAdbCommand(cpuMaxCMD) // KHz

        val clockSpeed = if(clockMax.isBlank() || clockMin.isBlank()){
            emptyValue
        }else{
            val clockMinMHz = (clockMin.trim().toInt())/1000
            val clockMaxMHz = (clockMax.trim().toInt())/1000
            "$clockMinMHz MHz ~ $clockMaxMHz MHz"
        }
        processorInfoList.add(Info("클럭 속도(Clock Speed)", clockSpeed))

        // 커널
        val kernelInfoAll = AppUtil.Command
            .executeAdbCommand("uname -a")
        processorInfoList.add(Info("커널(KERNEL)", kernelInfoAll.trim()))
        mBinding.processorInfoList = processorInfoList
        mBinding.notifyChange()
        getCpuCoreInfo()
    }

    private fun getCpuCoreInfo(){
        // corNumber
        for(idx in 0..<corNumber){
            val cpuMinFreq = AppUtil.Command
                .executeAdbCommand("cat sys/devices/system/cpu/cpu$idx/cpufreq/cpuinfo_min_freq")
            val cpuMaxFreq = AppUtil.Command
                .executeAdbCommand("cat sys/devices/system/cpu/cpu$idx/cpufreq/cpuinfo_max_freq")
            val transitionLatency = AppUtil.Command
                .executeAdbCommand("cat sys/devices/system/cpu/cpu$idx/cpufreq/transition_latency")
            val minFreq = if(cpuMinFreq.isBlank()){
                "알 수 없음"
            } else {
               "${cpuMinFreq.trim().toLong()/1000} MHz"
            }
            val maxFreq = if(cpuMaxFreq.isBlank()){
                "알 수 없음"
            } else {
                "${cpuMaxFreq.trim().toLong()/1000} MHz"
            }
            val transLatency = if (transitionLatency.isBlank()) {
                "알 수 없음"
            }else {
                "${transitionLatency.trim().toLong()} µs"
            }
            val coreData = "최소 주파수(Min Hz) : $minFreq\n" +
                    "최대 주파수(Max Hz) : $maxFreq\n" +
                    "전환 지연 시간(Transition Latency) : $transLatency"
            val info = Info(
                "CPU ${(idx+1)}",
                coreData
            )
            this.cpuCoreInfoList.add(info)
        }
        mBinding.cpuCoreInfoList = cpuCoreInfoList
        mBinding.notifyChange()
    }

    // data 2. java memory info! heap 등등
    private val javaCommandMap = mapOf(
        Pair("JAVA MIN HEAP", "getprop dalvik.vm.dex2oat-Xms"), // 프로세스의 초기 힙 크기
        Pair("JAVA MAX HEAP", "getprop dalvik.vm.dex2oat-Xmx"), // 프로세스 최대 힙 크기
        Pair("JAVA APPLICATION HEAP", "getprop dalvik.vm.heapsize") // 애플리케이션의 Dalvik 힙 크기
    )
    fun getJavaInformation(){
        // get java info
        val javaVersion = System.getProperty("java.version")
        val javaVmVersion = System.getProperty("java.vm.version")
        val javaVmVendor = System.getProperty("java.vm.vendor")
        val javaVmName = System.getProperty("java.vm.name")
        val javaMap = mapOf(
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
}