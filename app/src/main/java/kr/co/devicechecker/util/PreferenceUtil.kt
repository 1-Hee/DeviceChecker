package kr.co.devicechecker.util

import android.content.Context
import android.content.SharedPreferences
import kr.co.devicechecker.R

class PreferenceUtil {
    private var mContext:Context
    private val prefs:SharedPreferences
    private var defaultValue:String

    constructor(context: Context, name:String = ""){
        this.mContext = context
        val prefsTag= name.ifEmpty { mContext.getString(R.string.app_name) }
        this.prefs = this.mContext.getSharedPreferences(prefsTag, Context.MODE_PRIVATE)
        this.defaultValue = ""
    }


    // prefs에서 값을 입출력하는 메서드
    // getter
    fun getValue(key:String):String{
        return prefs.getString(key, this.defaultValue).toString()
    }

    // setter
    fun setValue(key:String, value:String){
        prefs.edit().putString(key, value).apply()
    }

    fun clearAll(){
        prefs.edit().clear().commit();
    }
}