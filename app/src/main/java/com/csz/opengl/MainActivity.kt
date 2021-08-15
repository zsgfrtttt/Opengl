package com.csz.opengl

import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.csz.opengl.render.AirHockeyRender
import com.csz.opengl.render.AirHockeyRenderV2
import com.csz.opengl.render.FirstRender
import com.csz.opengl.util.OpenglUtil

class MainActivity : AppCompatActivity() {
    private lateinit var glSurfaceView:GLSurfaceView
    private var render:Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        glSurfaceView = GLSurfaceView(this)

        if (OpenglUtil.supportEs2(this)){
            glSurfaceView.setEGLContextClientVersion(2)
            airHockeyRender()
            render = true
            setContentView(glSurfaceView)
        }
    }

    fun firstRedner(){
        glSurfaceView.setRenderer(FirstRender())
    }

    fun airHockeyRender(){
        glSurfaceView.setRenderer(AirHockeyRenderV2(this))
    }

    override fun onResume() {
        super.onResume()
        if (render){
            glSurfaceView.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (render){
            glSurfaceView.onPause()
        }
    }


}