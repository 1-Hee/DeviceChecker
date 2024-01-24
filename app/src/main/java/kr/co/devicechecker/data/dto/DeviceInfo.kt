package kr.co.devicechecker.data.dto

data class DeviceInfo(
    var modelName:String, // 모델명
    var androidVersion:String, // 안드로이드 버전
    var chipSet:String, // 칩셋 정보(CPU), 프로세서 정보
    var memoryInfo:String, // 메모리 (RAM) 정보
    var storageInfo:String,// 기억장치 (ROM) 정보
    var androidSecurity:String, // 안드로이드 보안 패치 수준
    var kernelVersion:String, // 커널 버전
    var storage:String, // RAM / ROM
    var networkAddress:String, // network 주소
    var boardName:String, // 보드 명
    var brandName:String, // 브랜드 명
    var displayBuildID:String, // 사용자 노출 빌드 ID,
    var uniqueBuildID:String, // 고유 빌드 ID,
    var buildID:String, // 빌드 ID
    var manufacture:String, // 제조사
    var productName:String, // 제품 명
)


//data class (
//    val modelName:String,
//    val androidVersion:String,
//    val firmWareVersion:String,
//    val buildNumber:String,
//    val processor:String,
//    var memoryInfo:String,
//    var storageInfo:String
//)
