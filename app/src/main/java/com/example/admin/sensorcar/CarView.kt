package com.example.admin.sensorcar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.ImageView

/**
 * Created by zjw on 2018/9/26.
 */
class CarView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr), SurfaceHolder.Callback, Runnable, SensorEventListener {


    //当前线程的状态
    private var isThreadFlag: Boolean = false
    // 获得画布对象，开始对画布画画
    private var mCanvas: Canvas? = null
    //当前布局状态
    private var mCurrentCartState = ISurfaceCartState.ready
    //游戏状态
    private var mCurGameState=ISurfaceCartState.START

    private var mSurfaceRunwayUtil: SurfaceRunwayUtil? = null

    private var mSurfaceCarUtil: SurfaceCartUtil? = null

    private val mPaint = Paint()

    //屏幕宽高
    private var mScreenWidth: Int = 0
    private var mScreenHeight: Int = 0


    //跑车的速度
    private var mCarSpeed: Float = 0f
    //跑车的位移
    private var mDistence: Int = 0

    //获取传感器manager
    private val mSensorManager by lazy {
        context.getSystemService(Context.SENSOR_SERVICE)
    }

    //获取加速度传感器
    private val mSensor by lazy {
        (mSensorManager as SensorManager).getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    private val handler = @SuppressLint("HandlerLeak")
    object : android.os.Handler() {

        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when (msg?.what) {
                0 -> {
                    //弹框
                    mCurGameState=ISurfaceCartState.ONCE_AGAIN
                    showTip(mCurGameState)
                }
                1->{
                    //弹框
                    mCurGameState=ISurfaceCartState.WIN
                    showTip(mCurGameState)
                }
            }
        }
    }

    init {
        holder.addCallback(this)
        isFocusable = true
        keepScreenOn = true
    }


    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        Log.d("eee=changed","-----------changed---------")
        mScreenWidth = width
        mScreenHeight = height
        //画跑道
        if (mSurfaceRunwayUtil == null)
            mSurfaceRunwayUtil = SurfaceRunwayUtil(context, width, height)
        //画赛车
        if (mSurfaceCarUtil == null)
            mSurfaceCarUtil = SurfaceCartUtil(context, width, height)
        //碰撞监听
        mSurfaceCarUtil?.setCarHitListener(object : SurfaceCartUtil.CarHitListen {
            override fun hit() {
                handler.sendEmptyMessage(0)
            }
        })
        //跑完全程监听
        mSurfaceCarUtil?.setFinishListener(object : SurfaceCartUtil.FinishListen{
            override fun win() {
                handler.sendEmptyMessage(1)
            }

        })
    }

    //成功或者失败弹框
    private fun showTip(curGameState:ISurfaceCartState) {
        isThreadFlag=false
        val instance = PopupWindowUtils.getInstance()
        val popWindow = instance.showPopWindow(R.layout.popup_window_tip, this, context)
        val start = popWindow.findViewById<ImageView>(R.id.iv_once_again)
        val over = popWindow.findViewById<ImageView>(R.id.loser)
        when(curGameState){
            ISurfaceCartState.START->{
                start.setImageResource(R.drawable.start)
                over.visibility= View.INVISIBLE
            }
            ISurfaceCartState.ONCE_AGAIN->{
                over.setImageResource(R.drawable.loser)
                start.setImageResource(R.drawable.once_again)
                over.visibility= View.VISIBLE
            }
            ISurfaceCartState.WIN->{
                over.setImageResource(R.drawable.win)
                start.setImageResource(R.drawable.once_again)
                over.visibility= View.VISIBLE
            }
        }
        start.setOnClickListener {
            mCurGameState=ISurfaceCartState.START
            instance.popupWindow.dismiss()
            isThreadFlag = true
            Thread(this).start()
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        Log.d("eee=Destroyed","----------Destroyed----------")
        isThreadFlag = false
        holder?.removeCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        Log.d("eee=surfaceCreated","------surfaceCreated--------------")
    }


    /**
     * 开始跑
     */
    fun start() {
        isThreadFlag=true
        mCurGameState=ISurfaceCartState.START
        Thread(this).start()
        mCurrentCartState = ISurfaceCartState.run
        //注册加速度传感器
        (mSensorManager as SensorManager).registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI)
        showTip(mCurGameState)
    }

    //注销加速度传感器监听
    fun unregisterSensorListener() {
        (mSensorManager as SensorManager).unregisterListener(this)
    }

    override fun run() {
        val name = Thread.currentThread().name
        Log.d("eee=run","------run--$name--")
        while (isThreadFlag) {
//            Log.d("eee=run","------running----")
            val start = System.currentTimeMillis()
            try {
                synchronized(CarView::class.java) {
                    mCanvas = holder.lockCanvas()
                    if(mCanvas!=null){
                        //绘制背景
                        mSurfaceRunwayUtil?.draw(mCanvas!!, mCurrentCartState, mPaint)
                        //绘制赛车
                        mSurfaceCarUtil?.draw(mCanvas!!, mCurrentCartState, mPaint, mDistence)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (mCanvas != null) {
                    holder.unlockCanvasAndPost(mCanvas)//结束锁定画图，并提交改变。
                }
            }
            val end = System.currentTimeMillis()
            // 让线程休息100毫秒
            if (end - start < 30) {
                try {
                    Thread.sleep(30 - (end - start))
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
//        Log.d("eee=event", "------------")
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            //根据左右方向的加速度计算速度和偏移量
            mDistence = -(mCarSpeed + x).toInt() * 4
            mCarSpeed *= x
//            Log.d("eee=distance", "distance=$mDistence")
        }
    }

    //界面销毁的时候注销回调
    fun release() {
        holder.removeCallback(this)
    }
}