package kr.co.devicechecker.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
    // 다른 액티비티 이동후 결과 값을 받아 핸들링할 런쳐
    private lateinit var launcher: ActivityResultLauncher<Intent>;
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
        requestStoragePermission(this)
        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { // 액티비티 종료시 결과릴 리턴받기 위한 콜백 함수
                result -> Timber.d("onActivityResult.......")
            if (result.resultCode == Activity.RESULT_OK) {
            }
        }
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
                R.id.iv_setting -> {
                    val intent = Intent(this@MainActivity, SettingActivity::class.java)
                    launcher.launch(intent)
                }
            }
        }
    }
    // 다이얼로그 리스너
    private val dialogListener = object : SelectTypeDialog.OnCheckDialogListener {
        override fun onSelect(index: Int) {
            when(index){
                1-> { // to HTML
                    val context = this@MainActivity
                    saveHTMLString(context)
                }
                2 -> { // to JSON
                    val context = this@MainActivity
                    saveJsonString(context)
                }
                else ->{ // 0, 기본은 txt
                    val context = this@MainActivity
                    saveTextString(context)
                }
            }
        }
    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if(hasFocus){
            super.hideTopBar()
        }
    }

    private fun requestStoragePermission(context:Context){
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
        }else {
            val ALL_PERMISSION:Array<String> = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            )
            // 권한 요청 함수
            val permissionsToRequest = mutableListOf<String>()

            // 필요한 권한 중에서 아직 허용되지 않은 권한을 확인
            for (permission in ALL_PERMISSION) {
                if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionsToRequest.add(permission)
                }
            }

            // 권한 요청이 필요한 경우 요청
            if (permissionsToRequest.isNotEmpty()) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    permissionsToRequest.toTypedArray(),
                    1001
                )
            }

        }
    }


    private fun getHTMLString(
        headerString:String,
        infoList:List<Info>
    ):String{
        val sb = StringBuilder()
        val tableStart = "    <h2>$headerString</h2>\n" +
                "    <table>\n"
        sb.append(tableStart)
        infoList.forEach { info ->
            sb.append("        <tr><th>${info.name}</th><td>${info.value}</td></tr>\n")
        }
        val tableEnd = "    </table>\n"
        sb.append(tableEnd)
        return sb.toString()
    }
    // html string 파싱 및 저장 메서드
    private fun saveHTMLString(context:Activity){
        val header = context.getString(R.string.html_start)
        val cBuilder = StringBuilder()
        // display,
        val displayInfoList = AppUtil.Device.getDisplayInfo(context)
        val displayHeader = context.getString(R.string.txt_header_display_info)
        val displayString = getHTMLString(displayHeader, displayInfoList)
        cBuilder.append(displayString)
        // device
        val deviceInfoList = AppUtil.Device.getDeviceInfo(context)
        val deviceHeader = context.getString(R.string.txt_header_device_info)
        val deviceString = getHTMLString(deviceHeader, deviceInfoList)
        cBuilder.append(deviceString)
        // processor
        val processorInfoList:List<Info> = AppUtil.Processor.getProcessorInfo(context)
        val processorHeader = context.getString(R.string.txt_header_processor_info)
        val processorString = getHTMLString(processorHeader, processorInfoList)
        cBuilder.append(processorString)
        // cpuCore
        val coreInfo = AppUtil.Command
            .executeAdbCommand("cat sys/devices/system/cpu/present").split("-")
        val corNumber = coreInfo[0].trim().toInt() + coreInfo[1].trim().toInt() + 1 // number
        val coreInfoList:List<CpuCoreInfo> = AppUtil.Processor.getCpuCoreInfo(corNumber, context)
        val mCoreList = mutableListOf<Info>()
        coreInfoList.forEach { cInfo ->
            val mList = listOf(
                Info("no", cInfo.name),
                Info("min HZ", cInfo.minHz),
                Info("max Hz", cInfo.maxHz),
                Info("transition latency", cInfo.transitionLatency)
            )
            mCoreList.addAll(mList)
        }
        val coreHeader = context.getString(R.string.txt_header_cpu_core_info)
        val coreString = getHTMLString(coreHeader, mCoreList)
        cBuilder.append(coreString)
        // java
        val javaInfoList:List<Info> = AppUtil.Processor.getJavaInformation(context)
        val javaHeader = context.getString(R.string.txt_header_java_info)
        val javaString = getHTMLString(javaHeader, javaInfoList)
        cBuilder.append(javaString)
        // memory
        val memoryInfoList:List<Info> = AppUtil.Memory.getMemoryInfo(context)
        val memoryHeader = context.getString(R.string.txt_header_main_memory)
        val memoryString = getHTMLString(memoryHeader, memoryInfoList)
        cBuilder.append(memoryString)
        // 저장소 전체
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
        val storageName = context.getString(R.string.txt_h_storage_name).replace(" :", "")
        val availHeader = context.getString(R.string.txt_h_storage_avail).replace(" :", "")
        val totalHeader = context.getString(R.string.txt_h_storage_total).replace(" :", "")
        // internal
        val internalInfoList:List<StorageInfo> = AppUtil.Memory
            .getInternalStorageInfo(internalPathList.toTypedArray())
        val mInternalList = mutableListOf<Info>()
        internalInfoList.forEach { s ->
            val mList = listOf(
                Info(storageName, s.name),
                Info(availHeader, s.available),
                Info(totalHeader, s.total)
            )
            mInternalList.addAll(mList)
        }
        val internalHeader = context.getString(R.string.txt_header_internal_storage)
        val internalString = getHTMLString(internalHeader, mInternalList)
        cBuilder.append(internalString)

        // external
        val externalInfoList:List<StorageInfo> = AppUtil.Memory
            .getExternalStorageInfo(externalPathList.toTypedArray())
        val dExternalHeader = context.getString(R.string.txt_header_external_storage)
        externalInfoList.forEach { s ->
            val mHeader = dExternalHeader + "( ${s.name} )"
            val mList = listOf(
                Info(storageName, s.name),
                Info(availHeader, s.available),
                Info(totalHeader, s.total)
            )
            val externalString = getHTMLString(mHeader, mList)
            cBuilder.append(externalString)
        }
        // sensor
        val sensorInfoList = AppUtil.Sensor.getSensorInfo(context)
        val mSensorList = mutableListOf<Info>()
        val strName = context.getString(R.string.txt_h_sensor_name).replace(" :", "")
        val strType = context.getString(R.string.txt_h_sensor_type).replace(" :", "")
        val strVendor = context.getString(R.string.txt_h_sensor_vendor).replace(" :", "")
        sensorInfoList.forEach { sensor ->
            val mList = listOf(
                Info(strName, sensor.sensorName),
                Info(strType, sensor.sensorType.toString()),
                Info(strVendor, sensor.sensorVendor)
            )
            mSensorList.addAll(mList)
        }
        val sensorHeader = context.getString(R.string.txt_header_sensor_info)
        val sensorString = getHTMLString(sensorHeader, mSensorList)
        cBuilder.append(sensorString)
        val end = context.getString(R.string.html_end)
        val htmlString = header + cBuilder.toString() + end
        AppUtil.File.saveData(context, htmlString, ".html")
    }

    // json으로 저장하는 메서드
    private fun saveJsonString(context:Activity){
        val allInfoObj = JsonObject()
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
        AppUtil.File.saveData(context, allInfoObj.toString(), ".json")
    }

    private fun saveTextString(activity: Activity){
        AppUtil.File.saveAllHardwareInfoForText(activity)
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
        AppUtil.File.saveData(activity, totalData)
    }
}
