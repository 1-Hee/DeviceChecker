package kr.co.devicechecker.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import kr.co.devicechecker.BR
import kr.co.devicechecker.BuildConfig
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.listener.ViewClickListener
import kr.co.devicechecker.base.ui.BaseActivity
import kr.co.devicechecker.data.dto.Permission
import kr.co.devicechecker.databinding.ActivitySettingBinding
import timber.log.Timber

class SettingActivity : BaseActivity<ActivitySettingBinding>() {
    // 다른 액티비티 이동후 결과 값을 받아 핸들링할 런쳐
    private lateinit var launcher: ActivityResultLauncher<Intent>;
    override fun getDataBindingConfig(): DataBindingConfig {
        val version = BuildConfig.VERSION_NAME
        return DataBindingConfig(R.layout.activity_setting)
            .addBindingParam(BR.pStroage, null)
            .addBindingParam(BR.version, version)
            .addBindingParam(BR.click, viewClickListener)
    }

    override fun init(savedInstanceState: Bundle?) {
        launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
        ) { // 액티비티 종료시 결과릴 리턴받기 위한 콜백 함수
            if (it.resultCode == Activity.RESULT_OK) {
                // 데이터 찍어볼경우!
                /*
                val intent = result.data
                val resultState = intent?.getStringExtra("newAlbumName")
                Timber.i("resultState : %s", resultState)
                 */
            }
        }

        val iconDrawable: Drawable? = ContextCompat.getDrawable(this, R.drawable.ic_folder)
        val pStroage = Permission(
            name = "사진 및 동영상",
            guideText = "앱에서 사용할 사진 및 동영상 데이터의 접근을 허용합니다.",
            iconDrawable = iconDrawable
        )
        mBinding.pStroage = pStroage
        mBinding.notifyChange()

        // 사진 및 동영상 권한이 불러와졌는지 체크
        // 저장소 권한
        val storagePermission = arrayOf(
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        var flag = false;
        storagePermission.forEach { it ->
            flag = ContextCompat.checkSelfPermission(
                this, it
            ) == PackageManager.PERMISSION_GRANTED || flag
        }
        // 스위치 값 세팅
        mBinding.stroagePermisson.swPermission.isChecked = flag
        Timber.i("토글 상태 : %s", flag)

        // 스위치 클릭 시 권한 설정 창으로 팝업

        mBinding.stroagePermisson.swPermission.setOnCheckedChangeListener { button, state ->
            mBinding.stroagePermisson.swPermission.isChecked = flag
            // 권한 변경 페이지로 이동
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", this.packageName, null)
            intent.data = uri
            Toast.makeText(this, getString(R.string.txt_guide_modify_permission), Toast.LENGTH_SHORT).show()
            launcher.launch(intent)
        }
    }

        val viewClickListener = object : ViewClickListener{
        override fun onViewClick(view: View) {
            when(view.id){
                R.id.iv_back -> { // 뒤로가기
                    setResult(RESULT_CANCELED)
                    finish()
                }
                R.id.tv_license -> { // 오픈소스 라이센스
                    val intent = Intent(this@SettingActivity, OssLicensesMenuActivity::class.java)
                    launcher.launch(intent)
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
}