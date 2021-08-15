package com.csz.opengl.render

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
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
class AirHockeyRenderV2(context: Context) : GLSurfaceView.Renderer {
    val U_MATRIX = "u_Matrix"
    var uMatrixLocation = 0
    var projectionMatrix = FloatArray(16)

    val A_COLOR = "a_Color"
    var aColorLocation = 0

    val A_POSITION = "a_Position"
    var aPositionLocation = 0

    val BYTE_REF_FLOAT = 4 // 1float = 4byte
    val PSITION_COMPONENT_COUNT = 2 //每个顶点有两个分量
    val COLOR_COMPONENT_COUNT = 3
    val STRIDE = (PSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * 4

    /** val tablePoints = floatArrayOf(
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
    ) **/
    val tablePoints = floatArrayOf(
        0f, 0f, 1f, 1f, 1f,
        -0.5f, -0.8f,0.7f,0.7f,0.7f,
        0.5f, -0.8f,0.7f,0.7f,0.7f,
        0.5f, 0.8f,0.7f,0.7f,0.7f,
        -0.5f, 0.8f,0.7f,0.7f,0.7f,
        -0.5f, -0.8f,0.7f,0.7f,0.7f,

        //line
        -0.5f,0f,1f,0f,0f,
        0.5f,0f,1f,0f,0f,

        //point
        0f,-0.4f,0f,0f,1f,
        0f,0.4f,1f,0f,0f,
    )
    var vertexData: FloatBuffer
    var vertexCode: String
    var fragmentCode: String
    var vertextShaderId: Int = 0
    var fragmentShaderId: Int = 0

    init {
        //本地内存，不受Dalvik虚拟机管理 allocateDirect 不是 allocate
        vertexData = ByteBuffer.allocateDirect(tablePoints.size * BYTE_REF_FLOAT)
            .order(ByteOrder.nativeOrder()) //高低位跟系统排序一致
            .asFloatBuffer()
        vertexData.put(tablePoints)

        vertexCode = OpenglUtil.readGlSLFile(context, R.raw.air_hovkey_vertex_v2)
        fragmentCode = OpenglUtil.readGlSLFile(context, R.raw.air_hovkey_fragment_v2)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        vertextShaderId = RenderShaderHelper.compileVertexShader(vertexCode)
        fragmentShaderId = RenderShaderHelper.compileFragmentShader(fragmentCode)
        if (vertextShaderId != 0 && fragmentShaderId != 0) {
            GLES20.glClearColor(0f, 0f, 0f, 0f)
            val programId = RenderShaderHelper.linkProgram(vertextShaderId, fragmentShaderId)
            val valid = RenderShaderHelper.validateProgram(programId)
            if (valid) {
                GLES20.glUseProgram(programId)
                aColorLocation = GLES20.glGetAttribLocation(programId, A_COLOR)
                aPositionLocation = GLES20.glGetAttribLocation(programId, A_POSITION)
                uMatrixLocation = GLES20.glGetUniformLocation(programId, U_MATRIX)

                vertexData.position(0)
                //从vertexData读取数据给glsl的属性赋值
                GLES20.glVertexAttribPointer(
                    aPositionLocation,
                    PSITION_COMPONENT_COUNT,
                    GLES20.GL_FLOAT,
                    false,
                    STRIDE,
                    vertexData
                )
                GLES20.glEnableVertexAttribArray(aPositionLocation)

                vertexData.position(PSITION_COMPONENT_COUNT)
                GLES20.glVertexAttribPointer(
                    aColorLocation,
                    COLOR_COMPONENT_COUNT,
                    GLES20.GL_FLOAT,
                    false,
                    STRIDE,
                    vertexData
                )
                GLES20.glEnableVertexAttribArray(aColorLocation)
            }
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val aspectRatio = if(width > height) 1f * width / height else height * 1f / width
        if (width > height){
            Matrix.orthoM(projectionMatrix,0,-aspectRatio,aspectRatio,-1f,1f,-1f,1f)
        }else{
            Matrix.orthoM(projectionMatrix,0,-1f,1f,-aspectRatio,aspectRatio,-1f,1f)
        }
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glUniformMatrix4fv(uMatrixLocation,1,false,projectionMatrix,0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6) //以第一个点为中心，两两相临的点为顶点绘制三角扇

        GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2)

        GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 1)

        GLES20.glDrawArrays(GLES20.GL_POINTS, 9, 1)
    }
}