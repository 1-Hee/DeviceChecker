package kr.co.devicechecker.data.dto

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject

data class StorageInfo(
    val name:String,
    val available:String,
    val total:String
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

    fun fromJson(jsonString: String):StorageInfo{
        val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            .create()
        return gson.fromJson(jsonString, StorageInfo::class.java)
    }
}
