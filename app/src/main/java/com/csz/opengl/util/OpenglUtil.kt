package com.csz.opengl.util

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import androidx.annotation.RawRes
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder

object OpenglUtil {
    fun supportEs2(context: Context):Boolean{
        val service = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val sopportEs2 = service.deviceConfigurationInfo.reqGlEsVersion >= 0x20000
                || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                && (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")))
        return sopportEs2
    }

    fun readGlSLFile(context: Context,@RawRes id:Int):String{
        val stringBuild = StringBuilder()
        val inputStream = context.resources.openRawResource(id)
        val reader = InputStreamReader(inputStream)
        val bufferReader = BufferedReader(reader)
        var line:String? =null
        while ((bufferReader.readLine().also { line = it }) != null){
            stringBuild.append(line)
            stringBuild.append('\n')
        }
        return stringBuild.toString()
    }
}