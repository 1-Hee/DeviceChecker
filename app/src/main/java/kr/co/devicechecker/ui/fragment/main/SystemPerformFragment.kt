package kr.co.devicechecker.ui.fragment.main

import android.content.Context
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kr.co.devicechecker.BR
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.ui.BaseFragment
import kr.co.devicechecker.data.dto.BatteryInfo
import kr.co.devicechecker.data.dto.CpuCoreInfo
import kr.co.devicechecker.databinding.FragmentSystemPerformBinding
import kr.co.devicechecker.util.MemoryInfo
import kr.co.devicechecker.util.SystemPerform
import timber.log.Timber
import java.util.ArrayDeque
import java.util.LinkedList
import java.util.Queue


class SystemPerformFragment : BaseFragment<FragmentSystemPerformBinding>() {

    private val cpuCoreInfoList = mutableListOf<CpuCoreInfo>()
    // 주기적으로 메모리 트래킹을 위한 Job
    private var job: Job? = null
    private lateinit var mUsageQueue:Queue<Entry>

    override fun initViewModel() {
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        mUsageQueue= ArrayDeque()

        return DataBindingConfig(R.layout.fragment_system_perform)
            .addBindingParam(BR.cpuCoreInfoList, cpuCoreInfoList)
    }

    override fun initView() {
        val context = requireContext()
        initChipsetAndArchitecture(context) // task 1.
        initCPUCoreSpecs(context) // task 2.
        setMemInfo(context) // task 3.
        initBatteryStatus(requireContext()) // task 4.
        mBinding.notifyChange()

    }

    override fun onResume() {
        super.onResume()
        // 트래킹 시작
        val context = requireContext()
        // Coroutine을 사용한 반복 작업 시작
        job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                val mMemInfo = MemoryInfo.getMemoryInfo(context)
                // Timber.i("[MemInfo] => %s", mMemInfo.toString())
                setMemInfo(context)
                // 1초 대기
                delay(1000)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        job?.cancel()
    }


    // task 1. get CPU Architecture & Chipset Brand
    private fun initChipsetAndArchitecture(context: Context){
        val mCpuTagInfo = SystemPerform.getChipsetAndArchitecture(context)
        if(mCpuTagInfo.size < 2) return
        mBinding.tvCpuManufacture.text = mCpuTagInfo[0]
        mBinding.tvCpuArchitecture.text = mCpuTagInfo[1]
    }

    // task 2. get CPU Core Specific information
    private fun initCPUCoreSpecs(context: Context){
        val mCpuCoreInfoList = SystemPerform.getCPUCoreSpecs(context)
        cpuCoreInfoList.addAll(mCpuCoreInfoList)
        mBinding.cpuCoreInfoList = cpuCoreInfoList
        mBinding.tvCoreCnt.text = cpuCoreInfoList.size.toString()
    }

    // task 3. Tracking Memory Usage...
    private fun setMemInfo(context: Context){
        val mMemInfo = MemoryInfo.getMemoryInfo(context)
        mBinding.setVariable(BR.memInfo, mMemInfo)

        // 최대 데이터 포인트 개수 설정
        val MAX_ENTRIES = 20

        // 새로운 데이터를 리스트에 추가
        var qIdx = mUsageQueue.size

        // 1. 오래된 데이터를 제거하고 새 데이터 추가
        if (mUsageQueue.size >= MAX_ENTRIES) {
            val mFirst:Entry? = mUsageQueue.poll()
            if(mFirst != null){
                qIdx += mFirst.x.toInt()
            }
        }

        mUsageQueue.offer(Entry(qIdx.toFloat(), mMemInfo.inUseMem.toFloat()))
        // 2. LineDataSet 생성
        val dataSet = LineDataSet(mUsageQueue.toList(), "Memory Usage")  // Label은 데이터 세트의 이름

        dataSet.color = ContextCompat.getColor(context, R.color.navy700)
        dataSet.circleColors = listOf(ContextCompat.getColor(context, R.color.navy500))

        // 3. LineData 생성
        val lineData = LineData(dataSet)

        // 4. LineChart에 LineData 설정
        val mLineChart = mBinding.lMemoryStatus.graphMemMonitor
        mLineChart.data = lineData
        mLineChart.setVisibleXRangeMaximum(20f)

        // 5. 차트 업데이트
        mLineChart.invalidate()  // 차트 다시 그리기

    }

    // task 4. Get Battery Info
    private fun initBatteryStatus(context: Context){
        val batteryInfo:BatteryInfo? = SystemPerform.getBatteryStatus(context)
        mBinding.setVariable(BR.batteryInfo, batteryInfo)

    }


}