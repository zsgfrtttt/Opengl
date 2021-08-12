package com.csz.opengl.render

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import com.csz.opengl.R
import com.csz.opengl.util.OpenglUtil
import com.csz.opengl.util.RenderShaderHelper
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * (0,14)         (9,14)
 * ---------------
 * |             |
 * |             |
 * |             |
 * ---------------
 * (0,0)         (9,0)
 * @author caishuzhan
 * 空气曲棍球
 * opengl 只能绘制 ：点，直线，三角形
 *
 * 逆时针 曲卷顺序 可以优化性能
 */
class AirHockeyRender(context: Context) : GLSurfaceView.Renderer {
    val U_COLOR = "u_Color"
    var uColorLocation = 0

    val A_POSITION = "a_Position"
    var aPositionLocation = 0

    val BYTE_REF_FLOAT = 4 // 1float = 4byte
    val PSITION_COMPONENT_COUNT =2 //每个顶点有两个分量
    val tablePoints = floatArrayOf(
        0f, 0f,
        9f, 14f,
        0f, 14f,

        0f, 0f,
        9f, 0f,
        9f, 14f,

        0f, 7f,
        9f, 7f,

        4.5f, 2f,
        4.5f, 12f,
    )
    var vertexData: FloatBuffer
    var vertexCode: String
    var fragmentCode: String
    var vertextShaderId: Int =0
    var fragmentShaderId: Int =0

    init {
        //本地内存，不受Dalvik虚拟机管理 allocateDirect 不是 allocate
        vertexData = ByteBuffer.allocateDirect(tablePoints.size * BYTE_REF_FLOAT)
            .order(ByteOrder.nativeOrder()) //高低位跟系统排序一致
            .asFloatBuffer()
        vertexData.put(tablePoints)

        vertexCode = OpenglUtil.readGlSLFile(context, R.raw.air_hovkey_vertex)
        fragmentCode = OpenglUtil.readGlSLFile(context, R.raw.air_hovkey_fragment)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        vertextShaderId = RenderShaderHelper.compileVertexShader(vertexCode)
        fragmentShaderId = RenderShaderHelper.compileFragmentShader(fragmentCode)
        if (vertextShaderId != 0 && fragmentShaderId != 0) {
            GLES20.glClearColor(0f,0f,0f,0f)
            val programId = RenderShaderHelper.linkProgram(vertextShaderId, fragmentShaderId)
            val valid = RenderShaderHelper.validateProgram(programId)
            if (valid) {
                GLES20.glUseProgram(programId)
                uColorLocation = GLES20.glGetUniformLocation(programId,U_COLOR)
                aPositionLocation = GLES20.glGetAttribLocation(programId,A_POSITION)

                vertexData.position(0)
                //从vertexData读取数据给glsl的属性赋值
                GLES20.glVertexAttribPointer(aPositionLocation,PSITION_COMPONENT_COUNT,GLES20.GL_FLOAT,false,0,vertexData)
                GLES20.glEnableVertexAttribArray(aPositionLocation)
            }
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glUniform4f(uColorLocation,1f,1f,1f,1f)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,6) //绘制6个顶点

        GLES20.glUniform4f(uColorLocation,1f,0f,0f,1f)
        GLES20.glDrawArrays(GLES20.GL_LINES,6,2)

        GLES20.glUniform4f(uColorLocation,0f,0f,1f,1f)
        GLES20.glDrawArrays(GLES20.GL_LINES,8,1)

        GLES20.glUniform4f(uColorLocation,1f,0f,0f,1f)
        GLES20.glDrawArrays(GLES20.GL_LINES,9,1)
    }
}