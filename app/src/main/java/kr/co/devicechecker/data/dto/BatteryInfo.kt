package kr.co.devicechecker.data.dto

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject


data class BatteryInfo (
    val isCharging:Boolean = false, // 배터리 충전 상태
    val chargeType:String = "Unknown", // 배터리 충전 방식
    val level:Float = 0.0f,
    val capacity:Long = 0L,
)
{
    fun toJsonString():String{
        val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            .create()
        return gson.toJson(this)
    }

    fun toJsonObject(): JsonObject {
        val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            .create()
        return gson.toJsonTree(this).asJsonObject
    }

    fun fromJson(jsonString: String):Info{
        val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            .create()
        return gson.fromJson(jsonString, Info::class.java)
    }
}