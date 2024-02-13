package kr.co.devicechecker.data.dto

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject

data class CpuCoreInfo(
    val name:String,
    val minHz:String,
    val maxHz:String,
    val transitionLatency:String
){
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
    fun fromJson(jsonString: String):CpuCoreInfo{
        val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            .create()
        return gson.fromJson(jsonString, CpuCoreInfo::class.java)
    }
}