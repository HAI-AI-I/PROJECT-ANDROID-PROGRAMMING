package com.group_7.library_management.data.local.preferences

import android.content.Context
import android.content.SharedPreferences

class CheckLogin(context: Context) {
    private val pref: SharedPreferences=context.getSharedPreferences("check_login", Context.MODE_PRIVATE)

    companion object{
        private const val key_is_logged="is_login"
        private const val key_is_user_id="user_id"
    }
    fun saveLogin(userId:String){
        pref.edit().apply {
            putBoolean(key_is_logged,true)
            putString(key_is_user_id,userId)
            apply()
        }
    }
    fun clearLogin(){
        pref.edit().apply{
            clear()
            apply()
        }
    }
    fun isLogin(): Boolean{
        return pref.getBoolean(key_is_logged,false)
    }
}