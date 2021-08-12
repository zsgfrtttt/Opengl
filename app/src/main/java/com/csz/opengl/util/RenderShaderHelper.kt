package com.csz.opengl.util

import android.app.ActivityManager
import android.content.Context
import android.opengl.GLES20
import android.os.Build
import android.util.Log
import androidx.annotation.RawRes
import com.csz.opengl.BuildConfig
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder

object RenderShaderHelper {

    fun compileVertexShader(shaderCode:String):Int{
        return compileShader(GLES20.GL_VERTEX_SHADER,shaderCode)
    }
    fun compileFragmentShader(shaderCode:String):Int{
        return compileShader(GLES20.GL_FRAGMENT_SHADER,shaderCode)
    }

    /**
     * @return 着色器id
     */
    private fun compileShader(type:Int,shaderCode:String):Int{
        val shaderId = GLES20.glCreateShader(type)
        if (shaderId ==0){
            Log.i("csz","could not create shader " + GLES20.glGetError())
            return 0
        }
        GLES20.glShaderSource(shaderId,shaderCode)
        GLES20.glCompileShader(shaderId)

        val compileStatus = IntArray(1)
        GLES20.glGetShaderiv(shaderId,GLES20.GL_COMPILE_STATUS,compileStatus,0)
        if (BuildConfig.DEBUG){
            Log.i("csz","compile info :\n ${shaderCode} \n : ${GLES20.glGetShaderInfoLog(shaderId)}")
        }
        if (compileStatus[0] == 0){
            //编译失败
            GLES20.glDeleteShader(shaderId)
            Log.i("csz","compile result : fail")
            return  0
        }
        return shaderId
    }

    fun linkProgram(vertexShaderId:Int,fragmentShaderId:Int):Int{
        val programId = GLES20.glCreateProgram()
        if (programId ==0){
            Log.i("csz","could not create Program " + GLES20.glGetError())
            return 0
        }
        GLES20.glAttachShader(programId,vertexShaderId)
        GLES20.glAttachShader(programId,fragmentShaderId)
        GLES20.glLinkProgram(programId) //链接程序

        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(programId,GLES20.GL_LINK_STATUS,linkStatus,0)
        if (BuildConfig.DEBUG){
            Log.i("csz","link Program info :\n  ${GLES20.glGetProgramInfoLog(programId)}")
        }
        if (linkStatus[0]== 0){
            //链接失败
            GLES20.glDeleteProgram(programId)
            Log.i("csz","link Program result : fail")
            return  0
        }
        return programId
    }

    fun validateProgram(programId:Int):Boolean{
        GLES20.glValidateProgram(programId)

        val validateStatus = IntArray(1)
        GLES20.glGetProgramiv(programId,GLES20.GL_VALIDATE_STATUS,validateStatus,0)
        if (BuildConfig.DEBUG){
            Log.i("csz","validate Program info :\n  ${GLES20.glGetProgramInfoLog(programId)}")
        }
        if (validateStatus[0]== 0){
            //链接失败
            GLES20.glDeleteProgram(programId)
            Log.i("csz","validate Program result : fail")
            return  false
        }
        return true
    }

}