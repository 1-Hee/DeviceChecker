package kr.co.devicechecker.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.MobileAds
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.ui.BaseActivity
import kr.co.devicechecker.databinding.ActivtyMainBinding
import timber.log.Timber

class MainActivity : BaseActivity<ActivtyMainBinding>() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.activty_main)
    }

    override fun init(savedInstanceState: Bundle?) {

        Timber.plant(Timber.DebugTree())

        setSupportActionBar(mBinding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = mBinding.drawerLayout
        val navView: NavigationView = mBinding.navigationView
        val navController = findNavController(R.id.nav_host_fragment_start)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_device_info, R.id.nav_system_perform,
                R.id.nav_memory_info, R.id.nav_sensor_info, R.id.nav_device_test
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        requestPermission(this)

        //admob init
        MobileAds.initialize(this) {}
    }

    override fun onResume() {
        super.onResume()
        checkAppUpdate()
    }

    // 액션 바 메뉴
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_app_bar, menu)
        return true
    }


    // 액션 바 메뉴 리스너
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id = item.itemId
        return when(id) {
            R.id.action_settings -> {
                // 세팅 페이지 호출
                // NavController에 접근
                val navHostFragment = supportFragmentManager
                    .findFragmentById(R.id.nav_host_fragment_start) as NavHostFragment
                val navController = navHostFragment.navController
                navController.navigate(R.id.nav_setting) // Setting 이동
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_start)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    //  권한 요청 함수
    private fun requestPermission(context: Context){
        /*
        val permissionList:Array<String> = arrayOf(
            Manifest.permission.POST_NOTIFICATIONS,
        )
        // 권한 요청 함수
        val permissionsToRequest = mutableListOf<String>()
        // 필요한 권한 중에서 아직 허용되지 않은 권한을 확인
        for (permission in permissionList) {
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
         */

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

    // For App Update
    // 업데이트 감지
    private fun checkAppUpdate() {
        val appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            // This example applies an immediate update. To apply a flexible update
            // instead, pass in AppUpdateType.FLEXIBLE
            val updateFlag = appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            if (updateFlag && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)){ // 즉시 업데이트
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    activityResultLauncher,
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build())
            }else if(updateFlag
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)){ // 유연한 업데이트
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    activityResultLauncher,
                    AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build())
            }
        }
//        // Create a listener to track request state updates.
//        val listener = InstallStateUpdatedListener { state ->
//            // (Optional) Provide a download progress bar.
//            if (state.installStatus() == InstallStatus.DOWNLOADING) {
//                val bytesDownloaded = state.bytesDownloaded()
//                val totalBytesToDownload = state.totalBytesToDownload()
//                // Show update progress bar.
//            }
//            // Log state or install the update.
//        }
//
//        // Before starting an update, register a listener for updates.
//        appUpdateManager.registerListener(listener)
    }
    private val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            result: ActivityResult ->
        // handle callback
        if (result.resultCode != RESULT_OK) {
            Timber.e("Update flow failed! Result code: " + result.resultCode);
            // If the update is canceled or fails,
            // you can request to start the update again.
        }
    }


}