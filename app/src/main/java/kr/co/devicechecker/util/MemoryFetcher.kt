package kr.co.devicechecker.util

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.os.StatFs
import kr.co.devicechecker.R
import kr.co.devicechecker.data.dto.Info
import kr.co.devicechecker.data.dto.MemInfo
import kr.co.devicechecker.data.dto.StorageInfo
import timber.log.Timber


/**
 * This object is a separately designed class
 * To extract and calculate the information needed by the fragment class.
 *
 * Ultimately, it is used to implement "DI(Dependency Injection)."
 *
 * For detailed usage examples, please refer to the MemoryInfoFragment class.
 */
object MemoryFetcher {
    fun getMemoryInfoList(context: Context):List<Info>{
        val emptyValue = context.resources.getString(R.string.txt_unknown)
        val memoryInfoList = mutableListOf<Info>()
        val memoryMap = hashMapOf<String, String>()
        val memoryFieldMap = hashMapOf<String, String>()
        val memoryHeaderList = context.resources.getStringArray(R.array.memory_headers)
        val memoryKeyList = context.resources.getStringArray(R.array.memory_key_list)
        memoryKeyList.forEachIndexed { index, key ->
            memoryFieldMap[key] = memoryHeaderList[index]
        }
        val cmd = "cat /proc/meminfo"
        val mInfo = Command.execute(cmd)
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
                        Info(it, emptyValue)
                    }else {
                        val parsedValue = AppConverter.parseKbUnit(value)
                        Info(it, parsedValue)
                    }
                    memoryInfoList.add(info)
                }
            }
        }
        return memoryInfoList
    }
    // data 2. storage info (internel, externel)
    fun getStoragePathList(activity: Activity):List<Info>{
        val internalStorageURI = "/storage/emulated/0/Android/data/kr.co.devicechecker/files"
        val externalFilesDirs = activity.getExternalFilesDirs(null)
        val internalPath = mutableListOf<Info>()
        val externalPath = mutableListOf<Info>()
        val pathList = mutableListOf<Info>()
        for (externalFilesDir in externalFilesDirs) {
            Timber.i("path :${externalFilesDir.path}")
            if(externalFilesDir.path.equals(internalStorageURI)){
                val info = Info(
                    activity.getString(R.string.txt_internal_name),
                    externalFilesDir.path
                )
                internalPath.add(info)
            }else {
                val pathArr = externalFilesDir.path.split("/")
                val pathName = try {
                    "${pathArr[2]}"
                }catch (e:Exception){
                    activity.getString(R.string.txt_unknown)
                }
                val info = Info(pathName, externalFilesDir.path)
                externalPath.add(info)
            }
        }
        pathList.addAll(internalPath)
        pathList.addAll(externalPath)
        return pathList
    }

    fun getStorageInfo(pathList:Array<Info>):List<StorageInfo>{
        val internalStorageList = mutableListOf<StorageInfo>()
        pathList.forEach { info ->
            val statFs = StatFs(info.value)
            val iInternalAvailable = statFs.freeBytes
            val iInternalTotal = statFs.totalBytes
            val parsedAvailable = AppConverter.parseByteUnit(iInternalAvailable)
            val parseTotal = AppConverter.parseByteUnit(iInternalTotal)
            val storageInfo = StorageInfo(info.name, parsedAvailable, parseTotal)
            internalStorageList.add(storageInfo)
        }
        return internalStorageList
    }

    fun getInternalStorageInfo(pathList:Array<Info>):List<StorageInfo>{
        val internalStorageList = mutableListOf<StorageInfo>()
        pathList.forEach { info ->
            val statFs = StatFs(info.value)
            val iInternalAvailable = statFs.freeBytes
            val iInternalTotal = statFs.totalBytes
            val parsedAvailable = AppConverter.parseByteUnit(iInternalAvailable)
            val parseTotal = AppConverter.parseByteUnit(iInternalTotal)
            val storageInfo = StorageInfo(info.name, parsedAvailable, parseTotal)
            internalStorageList.add(storageInfo)
        }
        return internalStorageList
    }
    fun getExternalStorageInfo(pathList:Array<Info>):List<StorageInfo>{
        val externalStorageList = mutableListOf<StorageInfo>()
        pathList.forEach { info ->
            val statFs = StatFs(info.value)
            val iExternalAvailable = statFs.freeBytes
            val iExternalTotal = statFs.totalBytes
            val parsedAvailable = AppConverter.parseByteUnit(iExternalAvailable)
            val parseTotal = AppConverter.parseByteUnit(iExternalTotal)
            val storageInfo = StorageInfo(info.name, parsedAvailable, parseTotal)
            externalStorageList.add(storageInfo)
        }
        return externalStorageList
    }
    fun saveMemoryInfo(
        context: Context,
        memoryInfoList:List<Info>,
        internalStorageList:List<StorageInfo>,
        externalStorageList:List<StorageInfo>
    ){
        val prefs = PreferenceUtil(context)
        val tag = "Memory"
        val builder = StringBuilder()
        builder.append(context.getString(R.string.txt_h_save_memory)).append("\n")
        memoryInfoList.forEach { info ->
            builder.append("${info.name} : ${info.value}\n")
        }
        val nameHeader = context.getString(R.string.txt_h_storage_name)
        val availHeader = context.getString(R.string.txt_h_storage_avail)
        val totalHeader = context.getString(R.string.txt_h_storage_total)
        builder.append(context.getString(R.string.txt_h_save_storage)).append("\n")
        internalStorageList.forEach { info ->
            builder.append("$nameHeader\n${info.name}\n$availHeader\t${info.available}\n$totalHeader\t${info.total}\n")
        }
        externalStorageList.forEach { info ->
            builder.append("$nameHeader\n${info.name}\n$availHeader\t${info.available}\n$totalHeader\t${info.total}\n")
        }
        prefs.setValue(tag, builder.toString())
    }

    // 메모리 사이즈 가져오는 메서드
    fun getMemoryInfo(context: Context) : MemInfo {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        // Available memory (MB)
        val availableMemory:Long = memoryInfo.availMem / 1048576L // MB 단위로 변환
        val totalMemory:Long = memoryInfo.totalMem / 1048576L // MB 단위로 변환

        // Low Memory 여부
        val isLowMemory:Boolean = memoryInfo.lowMemory

        return MemInfo(availableMemory, totalMemory, totalMemory-availableMemory, isLowMemory)
    }

}