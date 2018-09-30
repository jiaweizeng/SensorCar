package com.example.admin.sensorcar

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint

/**
 * Created by zjw on 2018/9/26.
 * 创建中间跑到
 */
class SurfaceRunwayUtil constructor(mContext: Context, private val mScreenWidth: Int,
                                    private val mScreenHeight: Int){

    //移动
    private var mOffsetY: Int = 0
    //绘制过准备布局
    private var isReady = false
    //起点终点
//    private var mEndRunwayBitmap: Bitmap
    //中路跑段
    private val mMiddleRunwayBitmap by lazy {
        BitmapUtil().scaleBitmap(BitmapFactory.decodeResource(mContext.resources, R.drawable.middle_runway),
                mScreenWidth, mScreenHeight)
    }


    /**
     * 绘制
     *
     * @param canvas     画笔
     * @param iCartState 状态
     */
    fun draw(canvas: Canvas, iCartState: ISurfaceCartState,paint: Paint) {
        if (canvas!= null){
            //保存画布
            canvas.save()
            when (iCartState) {
                ISurfaceCartState.ready ->
                    //准备中
                    drawReady(canvas,paint)
                ISurfaceCartState.end ->
                    //绘制终点
                    drawEnd(canvas,paint)
                else ->
                    //绘制跑道
                    drawRunway(canvas,paint)
            }
            //恢复画布
            canvas.restore()
        }
    }

    /**
     * 绘制准备
     */
    private fun drawReady(canvas: Canvas,paint: Paint) {
        //跑道
        isReady = true
//        canvas.drawBitmap(mEndRunwayBitmap, 0f, 0f, paint)
        //屏幕外的一段
        canvas.drawBitmap(mMiddleRunwayBitmap, (-mScreenWidth).toFloat(), 0f, paint)
    }

    /**
     * 绘制终点
     */
    private fun drawEnd(canvas: Canvas,paint: Paint) {
        mOffsetY += 20
        if (mOffsetY > mScreenWidth * 2) {
            mOffsetY = mScreenWidth * 2
        }
        //移动画布
        canvas.translate(mOffsetY.toFloat(), 0f)
        //跑道
        canvas.drawBitmap(mMiddleRunwayBitmap, 0f, 0f, paint)
        //屏幕外的一段
        canvas.drawBitmap(mMiddleRunwayBitmap, (-mScreenWidth).toFloat(), 0f, paint)
        //终点
//        canvas.drawBitmap(mEndRunwayBitmap, (-mScreenWidth * 2).toFloat(), 0f, paint)
    }

    /**
     * 绘制跑道
     */
    private fun drawRunway(canvas: Canvas,paint: Paint) {
        if (mOffsetY == mScreenHeight) {
            mOffsetY = 0
            if (isReady) isReady = false
        } else {
            mOffsetY += 10
            if (mOffsetY > mScreenHeight) {
                mOffsetY = mScreenHeight
            }
        }
        //移动画布
        canvas.translate(0f, mOffsetY.toFloat())
        //是否是刚开始
        if (isReady) {
            //起点
//            canvas.drawBitmap(mEndRunwayBitmap, 0f, 0f, paint)
        } else {
            //跑道
            canvas.drawBitmap(mMiddleRunwayBitmap, 0f, 0f, paint)
        }
        //屏幕外的一段
        canvas.drawBitmap(mMiddleRunwayBitmap, 0f, (-mScreenHeight).toFloat(), paint)
    }

    /**
     * 重置
     */
    fun reset() {
        isReady = false
        mOffsetY = 0
    }
}