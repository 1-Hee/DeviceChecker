package kr.co.devicechecker

/*
  private lateinit var mBinding: ActivityMainBinding
    private lateinit var deviceInfo:DeviceInfo
    private var requestCode1 = 1001
    private var requestCode2 = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val modelName = "모델 명 | ${Build.MODEL}"
        val androidVersion = "안드로이드 버전 | Android ${Build.VERSION.RELEASE} ( ${Build.VERSION.SDK_INT} )"
        val firmwareInfo = "펌웨어 버전 | "
        val buildNumber = "빌드 번호 | ${Build.FINGERPRINT}"
        val processorInfo = "칩셋 / 프로세서 정보 | ${Build.HARDWARE}"

        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        val totalRAM = memoryInfo.totalMem
        val totalRAMGB = totalRAM / (1024 * 1024).toFloat()
        val displayRAMGB = ceil(totalRAMGB.toDouble()).toInt()
        val memoryInfoString = "RAM | $displayRAMGB MB"
        val storageInfo = "저장 장치 (여유/전체) | "


        deviceInfo = DeviceInfo(
            modelName,
            androidVersion,
            firmwareInfo,
            buildNumber,
            processorInfo,
            memoryInfoString,
            storageInfo
        )

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mBinding.lifecycleOwner = this
        mBinding.setVariable(BR.deviceInfo, deviceInfo)
        mBinding.setVariable(BR.path, "저장 경로 : ")
        mBinding.notifyChange()

        mBinding.btnSaveDeviceInfo.setOnClickListener { saveData() }

        Timber.plant(Timber.DebugTree())
    }

    override fun onResume() {
        super.onResume()
        checkStorageRead()
        checkStorageWrite()
    }

    private fun checkStorageRead() {
        // 액티비티나 프래그먼트 내에서 권한 체크 및 요청
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            // 권한이 이미 허용된 경우
            setStorageInfo(deviceInfo, true)
        } else {
            // 사용자에게 권한 요청
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode1)
        }
    }

    private fun checkStorageWrite() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한이 없으면 권한 요청
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                requestCode2
            )
        }
    }

    private fun setStorageInfo(deviceInfo: DeviceInfo, isAllow:Boolean) {
        val storageInfo:String = if (isAllow) {
            val path = Environment.getExternalStorageDirectory().absolutePath
            val stat = StatFs(path)
            // 블록 크기를 가져와서 MB로 변환
            val blockSize = stat.blockSizeLong / (1024 * 1024).toFloat()

            // 전체 블록 수 및 사용 가능한 블록 수
            val totalBlocks = stat.blockCountLong
            val availableBlocks = stat.availableBlocksLong

            // 전체 용량 및 사용 가능한 용량을 MB로 변환
            val totalStorageMB = totalBlocks * blockSize
            val availableStorageMB = availableBlocks * blockSize

            val dpTotalStorage = ceil(totalStorageMB.toDouble()).toInt()
            val dpAvailStorage = ceil(availableStorageMB.toDouble()).toInt()

            "저장 장치 (여유/전체) | $dpAvailStorage MB/ $dpTotalStorage MB "
        }else {
            "저장 장치 (여유/전체) | (표시할 수 없음)"
        }
        deviceInfo.storageInfo = storageInfo
        mBinding.deviceInfo = deviceInfo
        mBinding.notifyChange()
    }

    private fun saveData() {
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
        mBinding.setVariable(BR.path, "저장 경로 : $path")
        mBinding.notifyChange()

        val fileName = "device_info.txt"
        deviceInfo.toString()
        val content = "${deviceInfo.modelName}\n" +
                "${deviceInfo.androidVersion}\n" +
                "${deviceInfo.firmWareVersion}\n" +
                "${deviceInfo.buildNumber}\n" +
                "${deviceInfo.processor}\n" +
                "${deviceInfo.memoryInfo}\n" +
                "${deviceInfo.storageInfo}\n"

        val downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDirectory, fileName)

        try {
            // 파일 쓰기
            FileOutputStream(file).use{ fileOutputStream ->
                OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8).use { outputStreamWriter ->
                    outputStreamWriter.write(content)
                }
            }
            Toast.makeText(this, "파일 저장 완료 ( ${file.absolutePath} )", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "파일 저장 실패", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            requestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한이 허용된 경우
                    when(requestCode){
                        1001 -> {
                            setStorageInfo(deviceInfo, true)
                        }
                        1002 -> { // 쓰기 허용
                        }
                    }
                } else {
                    when(requestCode){
                        1001 -> { // 쓰기 허용
                            setStorageInfo(deviceInfo, false)
                        }
                        1002 -> { // 쓰기 거절

                        }
                    }
                }
            }
        }
    }

 */

/*
 private void requestPermission() {
        String[] permissionList = {};

        // 33 버전 이상 권한 목록
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionList = READ_STORAGE_33;
        } else {
            // 33버전 미만 권한 목록
            permissionList = READ_STORAGE;
        }

        // TedPermission 라이브러리를 이용해 권한 체크 함.
        TedPermission.create()
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        // 권한 허용되었을 경우,
                        // 설정 값 초기화
                        settingViewModel.initSetting(requireContext());

                        int navigateId = R.id.homeFragment;

                        // 자동 재생 OFF 가 아닌 경우,
                        if(!settingViewModel.isAutoPlayOFF()) {
                            String autoPlaySchedule = settingViewModel.getAutoPlaySchedule();
                            // 자동 재생 목록이 존재하면 재생화면으로 이동,
                            // 자동 재생 목록이 없으면 Home화면으로 이동
                            navigateId = playListViewModel.isExistPlayList(settingViewModel, autoPlaySchedule) ? R.id.playViewFragment : R.id.homeFragment;
                        }

                        int finalNavigateId = navigateId;
                        new Handler().postDelayed(() -> {
                            // 2초 뒤에 Intro 화면 닫고,
                            nav().popBackStack();
                            // 재생화면 또는 홈화면으로 이동
                            nav().navigate(finalNavigateId);
                        }, 2000);
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        // 권한 허용안했을 경우, 토스트 메시지 노출
                        Toast.makeText(requireContext().getApplicationContext(), "권한을 허용해주세요. ", Toast.LENGTH_SHORT).show();
                    }
                })
                .setDeniedMessage("권한을 허용해주세요. [설정] > [앱 및 알림] > [고급] > [앱 권한]")
                .setPermissions(permissionList)// 얻으려는 권한(여러개 가능)
                .check();
    }

 */