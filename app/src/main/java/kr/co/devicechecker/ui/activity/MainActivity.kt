package kr.co.devicechecker.ui.activity

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.devicechecker.BR
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.listener.ViewClickListener
import kr.co.devicechecker.base.ui.BaseActivity
import kr.co.devicechecker.data.dto.CpuCoreInfo
import kr.co.devicechecker.data.dto.DialogCheck
import kr.co.devicechecker.data.dto.Info
import kr.co.devicechecker.data.dto.StorageInfo
import kr.co.devicechecker.databinding.ActivityMainBinding
import kr.co.devicechecker.ui.adapter.ViewPager2Adapter
import kr.co.devicechecker.ui.dialog.SelectTypeDialog
import kr.co.devicechecker.ui.fragment.main.DeviceInfoFragment
import kr.co.devicechecker.ui.fragment.main.DeviceTestFragment
import kr.co.devicechecker.ui.fragment.main.MemoryInfoFragment
import kr.co.devicechecker.ui.fragment.main.ProcessorInfoFragment
import kr.co.devicechecker.ui.fragment.main.SensorInfoFragment
import kr.co.devicechecker.util.AppUtil
import kr.co.devicechecker.util.PreferenceUtil
import timber.log.Timber


class MainActivity : BaseActivity<ActivityMainBinding>() {
    // tabLayout을 위한 리스너
    private lateinit var tabListener: TabLayout.OnTabSelectedListener
    private lateinit var vpAdapter : ViewPager2Adapter
    private lateinit var pagerCallback: ViewPager2.OnPageChangeCallback
    private lateinit var prefs:PreferenceUtil
    override fun getDataBindingConfig(): DataBindingConfig {
        tabListener = object : TabLayout.OnTabSelectedListener {
            // 탭이 선택되었을 때 호출
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // 선택된 탭에 대한 처리
                Timber.d("selected tab is... %s",tab?.position)
                val position = tab?.position?:0
                 mBinding.vpCheckMenu.currentItem = position
            }
            // 탭이 선택 해제되었을 때 호출
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // 선택 해제된 탭에 대한 처리
            }
            // 이미 선택된 탭이 다시 선택되었을 때 호출
            override fun onTabReselected(tab: TabLayout.Tab?) {
                // 이미 선택된 탭이 다시 선택되었을 때의 처리
            }
        }
        // 뷰페이저2 어댑터
        // create child fragments
        val childFragments = listOf(
            DeviceInfoFragment(),
            ProcessorInfoFragment(),
            MemoryInfoFragment(),
            SensorInfoFragment(),
            DeviceTestFragment()
        )
        vpAdapter = ViewPager2Adapter(this)
        vpAdapter.setFragmentList(childFragments)
        pagerCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Timber.d("current position ... %s",position)
                mBinding.tlCheckMenu.getTabAt(position)?.select()
            }
        }
        return DataBindingConfig(R.layout.activity_main)
            .addBindingParam(BR.tabListener, tabListener)
            .addBindingParam(BR.vpAdapter, vpAdapter)
            .addBindingParam(BR.vpCallback, pagerCallback)
            .addBindingParam(BR.click, viewClickListener)
    }
    override fun init(savedInstanceState: Bundle?) {
        prefs = PreferenceUtil(this)
        Timber.plant(Timber.DebugTree())
        requestStoragePermission()
    }
    private val viewClickListener = object : ViewClickListener {
        override fun onViewClick(view: View) {
            when(view.id){
                R.id.fb_save_all_info -> {
                    val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
                    val fileName = "device_info_${Build.MODEL.replace(" ", "").lowercase()}.txt"
                    val totalPath = "$path/$fileName"
                    val dialogCheck = DialogCheck(
                            title = getString(R.string.txt_d_save_title),
                        content = getString(R.string.txt_d_save_content, totalPath),
                        cancel = getString(R.string.txt_cancel), check = getString(R.string.txt_save))
                    val dialog = SelectTypeDialog(
                        checkDto = dialogCheck,
                        dialogListener = dialogListener
                    )
                    dialog.show(supportFragmentManager, null)
                }
            }
        }
    }
    // 다이얼로그 리스너
    private val dialogListener = object : SelectTypeDialog.OnCheckDialogListener {
        override fun onSelect(index: Int) {
            Timber.i("선택된 index... %s", index)
            when(index){
                2 -> { // to json

                    // 각 데이터별 JsonObject를 담을 List
                    val allInfoObj = JsonObject()
                    val context = this@MainActivity;
                    // Device
                    val allDeviceInfoList:List<Info> = AppUtil.Device.getAllDeviceInfo(context)
                    try {
                        val deviceObjList = mutableListOf<JsonObject>()
                        allDeviceInfoList.forEach { info ->
                            val jsonObj = info.toJsonObject()
                            deviceObjList.add(jsonObj)
                        }
                        val deviceJsonArray:JsonElement = AppUtil.Json.toJsonArray(deviceObjList)
                        allInfoObj.add("device", deviceJsonArray)
                    }catch (e:Exception){
                        Timber.e("[msg] %s", e.message)
                    }

                    // Processor
                    /**
                     * 프로세서는 CpuCore를 위한 별도의 dto가 존재하므로 아래와 같이 별도로 저장
                     */
                    // info 1 프로세서 정보
                    val processorInfoList:List<Info> = AppUtil.Processor.getProcessorInfo(context)
                    try {
                        val processObjList = mutableListOf<JsonObject>()
                        processorInfoList.forEach { info ->
                            val jsonObj = info.toJsonObject()
                            processObjList.add(jsonObj)
                        }
                        val processJsonArr = AppUtil.Json.toJsonArray(processObjList)
                        allInfoObj.add("processor", processJsonArr)
                    }catch (e:Exception){
                        Timber.e("[msg] %s", e.message)
                    }
                    // info 2 코어별 정보
                    val coreInfo = AppUtil.Command
                        .executeAdbCommand("cat sys/devices/system/cpu/present").split("-")
                    val corNumber = coreInfo[0].trim().toInt() + coreInfo[1].trim().toInt() + 1 // number
                    val coreInfoList:List<CpuCoreInfo> = AppUtil.Processor.getCpuCoreInfo(corNumber, context)
                    try {
                        val coreObjList = mutableListOf<JsonObject>()
                        coreInfoList.forEach { cInfo ->
                            val jsonObj = cInfo.toJsonObject()
                            coreObjList.add(jsonObj)
                        }
                        val coreJsonArr = AppUtil.Json.toJsonArray(coreObjList)
                        allInfoObj.add("cpuCore", coreJsonArr)
                    }catch (e:Exception){
                        Timber.e("[msg] %s", e.message)
                    }
                    // info 3 자바 정보
                    val javaInfoList:List<Info> = AppUtil.Processor.getJavaInformation(context)
                    try {
                        val javaObjList = mutableListOf<JsonObject>()
                        javaInfoList.forEach { info ->
                            val jsonObj = info.toJsonObject()
                            javaObjList.add(jsonObj)
                        }
                        val javaJsonArr = AppUtil.Json.toJsonArray(javaObjList)
                        allInfoObj.add("javaObj", javaJsonArr)
                    }catch (e:Exception){
                        Timber.e("[msg] %s", e.message)
                    }

                    // Memory
                    // info 1 메모리 정보
                    val memoryInfoList:List<Info> = AppUtil.Memory.getMemoryInfo(context)
                    try {
                        val memoryObjList = mutableListOf<JsonObject>()
                        memoryInfoList.forEach { info ->
                            val jsonObj = info.toJsonObject()
                            memoryObjList.add(jsonObj)
                        }
                        val memoryJsonArr = AppUtil.Json.toJsonArray(memoryObjList)
                        allInfoObj.add("memory", memoryJsonArr)
                    }catch (e:Exception){
                        Timber.e("[msg] %s", e.message)
                    }


                    // info 2 저장소 정보, 내부, 외부
                    val pathList = AppUtil.Memory.getStoragePathList(context)
                    val internalPathList = mutableListOf<Info>()
                    val externalPathList = mutableListOf<Info>()
                    val internalName = context.getString(R.string.txt_internal_name)
                    pathList.forEach { info ->
                        if(info.name.contains(internalName)){
                            internalPathList.add(info)
                        }else {
                            externalPathList.add(info)
                        }
                    }
                    val internalInfoList:List<StorageInfo> = AppUtil.Memory
                        .getInternalStorageInfo(internalPathList.toTypedArray())
                    try {
                        val internalObjList = mutableListOf<JsonObject>()
                        internalInfoList.forEach { sInfo ->
                            val jsonObj = sInfo.toJsonObject()
                            internalObjList.add(jsonObj)
                        }
                        val internalJsonArr = AppUtil.Json.toJsonArray(internalObjList)
                        allInfoObj.add("internal", internalJsonArr)
                    }catch (e:Exception){
                        Timber.e("[msg] %s", e.message)
                    }

                    val externalInfoList:List<StorageInfo> = AppUtil.Memory
                        .getExternalStorageInfo(externalPathList.toTypedArray())
                    try {
                        val externalObjList = mutableListOf<JsonObject>()
                        externalInfoList.forEach { sInfo ->
                            val jsonObj = sInfo.toJsonObject()
                            externalObjList.add(jsonObj)
                        }
                        val extenalJsonArr = AppUtil.Json.toJsonArray(externalObjList)
                        allInfoObj.add("external", extenalJsonArr)
                    }catch (e:Exception){
                        Timber.e("[msg] %s", e.message)
                    }

                    // Sensor
                    val sensorInfoList = AppUtil.Sensor.getSensorInfo(context)
                    try {
                        val sensorObjList = mutableListOf<JsonObject>()
                        sensorInfoList.forEach { sensor ->
                            val jsonObj = sensor.toJsonObject()
                            sensorObjList.add(jsonObj)
                        }
                        val sensorJsonArr = AppUtil.Json.toJsonArray(sensorObjList)
                        allInfoObj.add("sensor", sensorJsonArr)
                    }catch (e:Exception){
                        Timber.e("[msg] %s", e.message)
                    }

                    Timber.i("[all] %s", allInfoObj)

                }
                else ->{ // 0, 기본은 txt
                    AppUtil.File.saveAllHardwareInfoForText(this@MainActivity, this@MainActivity)
                    val deviceContent = prefs.getValue("Device")
                    val processorContent = prefs.getValue("Processor")
                    val memoryContent = prefs.getValue("Memory")
                    val sensorContent = prefs.getValue("Sensor")
                    val builder = StringBuilder()
                    builder.append(deviceContent).append("\n")
                        .append(processorContent).append("\n")
                        .append(memoryContent).append("\n")
                        .append(sensorContent).append("\n")
                    val totalData = builder.toString()
                    AppUtil.File.saveData(this@MainActivity, totalData)
                }
            }
        }

    }
    /*
     Toast.makeText(this@MainActivity,
                getString(R.string.ts_guide_save_info), Toast.LENGTH_SHORT).show()
            AppUtil.File.saveAllHardwareInfo(this@MainActivity, this@MainActivity)
            val deviceContent = prefs.getValue("Device")
            val processorContent = prefs.getValue("Processor")
            val memoryContent = prefs.getValue("Memory")
            val sensorContent = prefs.getValue("Sensor")
            val builder = StringBuilder()
            builder.append(deviceContent).append("\n")
                .append(processorContent).append("\n")
                .append(memoryContent).append("\n")
                .append(sensorContent).append("\n")
            val totalData = builder.toString()
            AppUtil.File.saveData(this@MainActivity, totalData)

     */
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if(hasFocus){
            super.hideTopBar()
        }
    }

    private fun requestStoragePermission(){
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            // 읽고 쓰기 권한 요청
            val permissions = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
            )
            //        // CoroutineScope
            val scope = CoroutineScope(Dispatchers.Main)
            scope.launch {
                TedPermission.create()
                    .setPermissionListener(object : PermissionListener {
                        override fun onPermissionGranted() {
                        }
                        override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                        }
                    })
                    .setDeniedMessage("권한을 허용해주세요. [설정] > [앱 및 알림] > [고급] > [앱 권한]")
                    .setPermissions(*permissions)
                    .check()
            }
        }
    }
}
