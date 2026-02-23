package com.rohan.fablefit.Cache

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.lang.reflect.Type

class CacheRepository {
    val gson= Gson()
    fun writeJson(context: Context,key: String,json: String){
        try {
            val filesDir=context.filesDir;
            val fileObj=File(filesDir,"$key.json")
            fileObj.writeText(json)
            Log.d("CACHE","Saved json as : $key.json at $fileObj.")
        }
        catch (e: Exception){
            e.printStackTrace()
        }
    }
    inline fun <reified T>readJson(context: Context,key: String):T?{
        return try {
            val filesDir=context.filesDir;
            val fileObj= File(filesDir,"$key.json")
            if(!fileObj.exists()){
                return null;
            }
            Log.d("custom Cache","Reading $key.json")
            val json: String=fileObj.readText();
            val type: Type = object : TypeToken<T>() {}.type
            gson.fromJson(json,type)
        }
        catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

}