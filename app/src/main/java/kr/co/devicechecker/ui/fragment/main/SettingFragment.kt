package kr.co.devicechecker.ui.fragment.main

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import kr.co.devicechecker.BR
import kr.co.devicechecker.BuildConfig
import kr.co.devicechecker.R
import kr.co.devicechecker.base.bind.DataBindingConfig
import kr.co.devicechecker.base.listener.ViewClickListener
import kr.co.devicechecker.base.ui.BaseFragment
import kr.co.devicechecker.data.dto.Info
import kr.co.devicechecker.data.dto.Permission
import kr.co.devicechecker.databinding.FragmentSettingBinding

class SettingFragment : BaseFragment<FragmentSettingBinding>() {

    // 다른 액티비티 이동후 결과 값을 받아 핸들링할 런쳐
    private lateinit var launcher: ActivityResultLauncher<Intent>;

    private lateinit var permissionInfoList:MutableList<Permission>
    private lateinit var settingOptionList:MutableList<Info>

    override fun initViewModel() {
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        permissionInfoList = mutableListOf()
        settingOptionList = mutableListOf()

        // TODO user Permission
        val context = requireContext()
        val iconDrawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_folder)
        val permissionStorage = Permission(
            name = "저장소 권한",
            guideText = "앱에서 외부 저장소의 접근을 허용합니다.",
            iconDrawable = iconDrawable
        )
        permissionInfoList.add(permissionStorage)

        // Setting Option...
        val settingArray:Array<String> = context.resources
            .getStringArray(R.array.arr_setting_option); // 메뉴 명
        // Option 1. 언어 설정
        val langInfo = Info(settingArray[0], "한국어") // TODO : 실제 설정 다이얼로그와 연계하여 기능 구현...

        // Option 2. 앱 업데이트
        val versionInfo = Info(settingArray[1], BuildConfig.VERSION_NAME)

        // Option 3. 오픈소스 라이선스
        val ossInfo = Info(settingArray[2], "")

        settingOptionList.add(langInfo)
        settingOptionList.add(versionInfo)
        settingOptionList.add(ossInfo)

        return DataBindingConfig(R.layout.fragment_setting)
            .addBindingParam(BR.settingOptionList, settingOptionList)
            .addBindingParam(BR.permissionInfoList, permissionInfoList)
            .addBindingParam(BR.pClick, permissionClickListener)
            .addBindingParam(BR.sClick, settingClickListener)
    }

    override fun initView() {
        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { // 액티비티 종료시 결과릴 리턴받기 위한 콜백 함수
            val cancelFlag = it.resultCode == Activity.RESULT_CANCELED;
            if (it.resultCode == Activity.RESULT_OK) {
                // 데이터 찍어볼경우!
                /*
                val intent = result.data
                val resultState = intent?.getStringExtra("newAlbumName")
                Timber.i("resultState : %s", resultState)
                 */
            }
        }

        // 세팅 메뉴 숨김
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(@NonNull menu: Menu, @NonNull menuInflater: MenuInflater) {}
            override fun onPrepareMenu(@NonNull menu: Menu) {
                val item = menu.findItem(R.id.action_settings)
                item?.setVisible(false)
            }

            override fun onMenuItemSelected(@NonNull menuItem: MenuItem): Boolean {
                //...
                return false
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED)
    }

    // Listener
    // 사용자 권한 클릭 리스너
    private val permissionClickListener = object : ViewClickListener {
        override fun onViewClick(view: View) {


        }
    }

    // 세팅 클릭 리스너
    private val settingClickListener = object : ViewClickListener {
        override fun onViewClick(view: View) {
            val context = view.context
            val settingArray:Array<String> = context.resources
                .getStringArray(R.array.arr_setting_option); // 세팅 옵션 명

            when(view.tag){
                settingArray[0] -> { // 언어 설정
                    Toast.makeText(context, "언어설정 다이얼로그...", Toast.LENGTH_SHORT).show()
                }
                settingArray[1] -> { // 앱 업데이트
                    Toast.makeText(context, "앱 업데이트 이동...", Toast.LENGTH_SHORT).show()
                }
                settingArray[2] -> { // 오픈 소스 라이선스 ...
                    val intent = Intent(requireActivity(), OssLicensesMenuActivity::class.java)
                    launcher.launch(intent)
                }
                else -> {

                }
            }

        }

    }
}