package com.example.admin.sensorcar

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
class MainActivity : AppCompatActivity(){

    private val carView by lazy {
        findViewById<CarView>(R.id.carSurfaceView)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //要在设置布局之前设置
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)
        carView.post {
            carView.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        carView.release()
        carView.unregisterSensorListener()
    }

}
