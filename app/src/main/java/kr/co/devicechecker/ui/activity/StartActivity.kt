package kr.co.devicechecker.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.ui.BaseActivity
import kr.co.devicechecker.databinding.ActivtyStartBinding

class StartActivity : BaseActivity<ActivtyStartBinding>() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.activty_start)
    }

    override fun init(savedInstanceState: Bundle?) {
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
    }


    // 액션 바 메뉴
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        // menuInflater.inflate(R.menu., menu)
        return true
    }


    // 액션 바 메뉴 리스너
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id = item.itemId
        return when(id) {

            /*
            R.id.action_settings -> {
                // 세팅 페이지 호출
                // NavController에 접근
                val navHostFragment = supportFragmentManager
                    .findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
                val navController = navHostFragment.navController
                navController.navigate(R.id.nav_setting) // Setting 이동
                true
            }
             */
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
    }
}