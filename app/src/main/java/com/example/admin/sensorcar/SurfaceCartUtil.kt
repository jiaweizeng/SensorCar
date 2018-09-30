package com.example.admin.sensorcar

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import java.util.*

/**
 * Created by zjw on 2018/9/26.
 */
class SurfaceCartUtil constructor(mContext: Context, private val mScreenWidth: Int,
                                  private val mScreenHeight: Int) {


    //屏幕的宽度
    //偏移bitmap头部的距离
    private var mOffsetY: Int = 0
    //跑车
    private val mCartIds = intArrayOf(R.drawable.c1/*, R.drawable.c2, R.drawable.c3, R.drawable.c4*/)
    private val mCartConfigs = ArrayList<CartConfig>()
    private val mFireBitmaps = arrayOfNulls<Bitmap>(3)
    //车的宽度
    private var mCartWidth: Float = 0f
    //车的高度
    private var mCartHeight: Float = 0f
    //喷火的高度
    private var mFirePosY: Float = 0f
    //随机工具类
    private val mCartRandomUtil: CartRandomUtil
    //终点的结果列表
    private var mEndResultList: List<Int> = ArrayList()
    //已经完成
    private var mCallBack: CallBack? = null
    //障碍物赛车
    private var mBadCar: Bitmap? = null
    //障碍车的位移
    private var badDistance = 0f
    //记录障碍车随机的x坐标
    private var badCarX = (mScreenWidth / 5).toFloat()
    //赢时候的白色跑道
    private val winWay by lazy {
        BitmapUtil().scaleBitmap(BitmapFactory.decodeResource(mContext.resources, R.drawable.qdzd), mScreenWidth * 3 / 5, 200)
    }

    init {
        mBadCar = BitmapUtil().scaleBitmap(BitmapFactory.decodeResource(mContext.resources, R.drawable.w), 100, 200)
        badDistance = (-mBadCar!!.height).toFloat()
        //偏移头部的数据
        mOffsetY = 135
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.RGB_565//只有RGB，没有透明度
        //初始化赛车
        initCartBitmaps(mContext, mScreenWidth, options)
        //赛车的位置
        val cartBitmap = mCartConfigs[0].bitmap
        mCartWidth = cartBitmap.width.toFloat()
        mCartHeight = cartBitmap.height.toFloat()
        //喷火
        val fireResources = intArrayOf(R.drawable.ic_fire_01, R.drawable.ic_fire_02, R.drawable.ic_fire_03)
        for (i in fireResources.indices) {
//            mFireBitmaps[i] = BitmapUtil().scaleBitmap(BitmapFactory.decodeResource(mContext.resources, fireResources[i], options),70,250)
            mFireBitmaps[i] = BitmapFactory.decodeResource(mContext.resources, fireResources[i], options)
        }
        //火出现的位置
        val cartCenterY = (cartBitmap.height / 2.0).toFloat()
        val firCenterY = (mFireBitmaps[0]!!.height / 2.0).toFloat()
        mFirePosY = cartCenterY - firCenterY + 3
        //随机工具类
        mCartRandomUtil = CartRandomUtil(mScreenWidth, mScreenHeight, mCartWidth)

    }

    /**
     * 初始化赛车
     */
    private fun initCartBitmaps(context: Context, screenWidth: Int, options: BitmapFactory.Options) {
        for (i in mCartIds.indices) {
            val bitmap = BitmapUtil().scaleBitmap(BitmapFactory.decodeResource(context.resources, mCartIds[i], options), 100, 200)
            val startX = (mScreenWidth / 2 - bitmap!!.width / 2).toFloat()
            val startY = (mScreenHeight - bitmap.height * 2).toFloat()
            val cartConfig = CartConfig(i + 1, startX, startY, bitmap, null)
            mCartConfigs.add(cartConfig)
        }
    }


    /**
     * 绘制赛车
     */
    fun draw(canvas: Canvas, iCartState: ISurfaceCartState, paint: Paint, distance: Int) {
        val cartConfigs = mCartConfigs
        val size = mCartIds.size
        val offsetY = mOffsetY
        val height = (cartConfigs[0].bitmap.height / 0.78).toFloat()

        if (canvas != null) {
            when (iCartState) {
                ISurfaceCartState.ready ->
                    //准备中的赛车
                    drawReady(canvas, size, cartConfigs, height, offsetY, paint)
                else -> {
                    //绘制赛车
                    drawRunway(canvas, size, cartConfigs, height, offsetY, paint, distance)
                }
            }
        }
    }


    /**
     * 绘制准备中
     */
    private fun drawReady(canvas: Canvas, size: Int, cartConfigs: List<CartConfig>, height: Float, offsetY: Int, paint: Paint) {
        //绘制赛车
        for (i in 0 until size) {
            val bean = cartConfigs[i]
            val bitmap = bean.bitmap
            //车子Y轴 重叠位置
            val y = i * height + offsetY
            //当前坐标
            val curX = bean.curX
            //每辆车的车道大小
            val carWay = mScreenWidth / 5
            canvas.drawBitmap(bitmap, (carWay + carWay * i).toFloat(), (mScreenHeight - bean.bitmap.height).toFloat(), paint)
            //需要喷火
            val cartFire = getCartFire(bean)
            if (cartFire !== ICartFire.None) {
                val fireBitmap = getFireBitmap(cartFire)
                canvas.drawBitmap(fireBitmap, curX + fireBitmap.width * 2 / 3 - 15, y + mFirePosY + fireBitmap.height * 2 / 3 + 20, paint)
            }
        }
    }

    //障碍车总量
    private var badCarCount = 3
    //障碍车数量
    private var badCarNumber = 0
    //白色跑道的位移
    private var winDistance = (-winWay!!.height).toFloat()

    /**
     * 绘制正在赛跑的过程
     */
    private fun drawRunway(canvas: Canvas, size: Int, cartConfigs: List<CartConfig>, height: Float, offsetY: Int, paint: Paint, distance: Int) {
        //繪製障礙車
        if (badCarNumber < badCarCount) {
            canvas.drawBitmap(mBadCar!!, badCarX, badDistance, paint)
        } else {
            //绘制赢得跑道
            canvas.drawBitmap(winWay!!, (mScreenWidth / 5).toFloat(), winDistance, paint)
        }
        //游戏是否结束 有3 个 完成就 ok
        var isEndCount = cartConfigs.size
        //绘制赛车
        for (i in 0 until size) {
            val bean = cartConfigs[i]
            val bitmap = bean.bitmap

            //当前坐标
            val curX = bean.curX
            val curY = bean.curY
            //结束的坐标
            asmEndXY(bean)
            // 车子X轴
            var newCurX = curX
            // 车子Y轴
            var newCurY = curY
            newCurX += distance

            val rightBoundary = mScreenWidth * 4 / 5 - bitmap.width
            if (newCurX > rightBoundary) {
                newCurX = rightBoundary.toFloat()
            } else if (newCurX < mScreenWidth / 5) {
                newCurX = (mScreenWidth / 5).toFloat()
            }
            //已经到达终点
            if (bean.speed != 0f) {
                isEndCount--
//                newCurX += bean.speed
                newCurY += bean.speed
            }
            // 更新最新的值
            bean.curX = newCurX
            bean.curY = newCurY
            canvas.drawBitmap(bitmap, newCurX, newCurY, paint)
            //碰撞检测
            hit(badCarX, badDistance, newCurX, newCurY)
            //跑完全程检测
            if (newCurY+bitmap.height<winDistance+winWay!!.height){
                finishListener?.win()
                initAgain()
            }
            //需要喷火
            val cartFire = getCartFire(bean)
            if (cartFire !== ICartFire.None) {
                val fireBitmap = getFireBitmap(cartFire)
                canvas.drawBitmap(fireBitmap, newCurX + fireBitmap.width * 2 / 3 - 15, newCurY + bean.height, paint)
            }
        }
        //完成回调
        if (isEndCount > 3 && mCallBack != null) {
            mCallBack?.finish()
            mCallBack = null
        }

        //连续五辆障碍车都没撞上就算赢了
        if (badCarNumber < badCarCount) {
            //計算障礙車的下次位置
            badDistance += 10
            //障礙車移動
            if (badDistance > mScreenHeight) {
                //製作新的障礙車
                badCarNumber++
                badCarX = (Random().nextInt(mScreenWidth * 4 / 5 - mBadCar!!.width - mScreenWidth / 5) + mScreenWidth / 5).toFloat()
                badDistance = (-mBadCar!!.height).toFloat()

            }
        } else {
            badDistance = 0f
            winDistance += 10
        }

    }


    /**
     * 指定一个终点
     */
    private fun getRangeEndY(bf: Float): Float {
        return (bf / 10.0 * (mScreenHeight - mCartHeight)).toFloat()
    }

    /**
     * 到达这个终点的最佳速度
     */
    private fun getOptimumSpeedY(config: CartConfig): Float {
        val curY = config.curY
        val endY = config.endY
        //最佳速度
        var v = Math.abs(curY - endY) / mScreenHeight * 100
        if (v > 30) v = 30f
        return if (endY < curY) {
            -v
        } else {
            v
        }
    }

    /**
     * 计算终点坐标
     */
    private fun asmEndXY(bean: CartConfig) {
        val newEndY = bean.endY
        if (newEndY == 0f) {
            val speed = mCartRandomUtil.randomY(bean)
            if (speed > 0) {
                //                KLog.e("号码：" + bean.getNumber() + " ->往后");
            } else {
                //                KLog.e("号码：" + bean.getNumber() + " ->往前");
            }
        } else {
            //还没有找到坐标
            if (mEndResultList.isEmpty()) {
                // -> 方向
                if (bean.speed > 0) {
                    if (bean.curY > newEndY) {
                        val speed = mCartRandomUtil.randomY(bean)
                        if (speed > 0) {
                            //                            KLog.e("号码：" + bean.getNumber() + " ->往后");
                        } else {
                            //                            KLog.e("号码：" + bean.getNumber() + " ->往前");
                        }
                    }
                } else {
                    if (bean.curY < newEndY) {
                        val speed = mCartRandomUtil.randomY(bean)
                        if (speed > 0) {
                            //                            KLog.e("号码：" + bean.getNumber() + " ->往后");
                        } else {
                            //                            KLog.e("号码：" + bean.getNumber() + " ->往前");
                        }
                    }
                }
            } else {
                if (!bean.isEndY) {
                    bean.isEndY = true
                    val position = mEndResultList.indexOf(bean.number) + 1
                    val rangeEndY = getRangeEndY(position.toFloat()) + mScreenHeight / 8.0f
                    bean.endY = rangeEndY
                    //最佳速度
                    val optimumSpeedY = getOptimumSpeedY(bean)
                    bean.speed = optimumSpeedY
                } else {
                    //已经在终点了,设置为 0
                    val speed = bean.speed
                    if (speed != 0f) {
                        //还没有冲出屏幕
                        if (bean.curY > -mCartHeight) {
                            //到达终点
                            var isEnd = false
                            if (speed < 0 && bean.curY < bean.endY) {
                                isEnd = true
                            } else if (speed > 0 && bean.curY > bean.endY) {
                                isEnd = true
                            }
                            if (isEnd) {
                                //到终点
//                                KLog.e("号码：" + bean.getNumber() + " ->到终点")
                                //冲出屏幕
                                bean.speed = -10f
                                bean.endY = -mCartHeight
                            }
                        } else {
                            bean.speed = 0f
                        }
                    }
                }
            }
        }
    }

    /**
     * 是否需要喷火
     */
    private fun getCartFire(bean: CartConfig): ICartFire {
        return if (bean.speed > 0 || bean.curX < 0 || bean.curY < 0) {
            //往后退,不需要喷火
            ICartFire.None
        } else {
            //当前坐标(600) - 终点坐标(200) = 400 /   屏幕 1200 / 6 == 200         400 / 200 = 2
            val dw = bean.nextFire()
            when {
                dw > 20 -> ICartFire.Large
                dw > 10 -> ICartFire.Middle
                else -> ICartFire.Small
            }
        }
    }

    /**
     * 获取喷火的绘制图
     */
    private fun getFireBitmap(cartFire: ICartFire): Bitmap {
        return when {
            cartFire === ICartFire.Large -> mFireBitmaps[0]!!
            cartFire === ICartFire.Middle -> mFireBitmaps[1]!!
            else -> mFireBitmaps[2]!!
        }
    }


    /** 碰撞算法  */
    private fun hit(badX: Float, badY: Float, x: Float, y: Float) {

        val x1 = badX - 100 / 2                 //x坐标最小距离
        val x2 = badX + 100 / 2 + mBadCar!!.width  //x坐标最大距离
        val y1 = badY - 200 / 2                //y坐标最小距离
        val y2 = badY + 200 / 2 + mBadCar!!.height //y坐标最大距离

        val herox = x + 100 / 2               //英雄机x坐标中心点距离
        val heroy = y + 200 / 2              //英雄机y坐标中心点距离

        //区间范围内为撞上了
        if (herox > x1 && herox < x2 && heroy > y1 && heroy < y2) {
            hitListener?.hit()
            Log.d("eee=hit", "------------------")
            //重新初始化数据
            initAgain()
        }
    }

    /**
     * 重新初始化数据
     */
    private fun initAgain() {

        badDistance = (-mBadCar!!.height).toFloat()
        //记录障碍车随机的x坐标
        badCarX = (mScreenWidth / 5).toFloat()
        mCartConfigs[0].curX = (mScreenWidth / 2 - mBadCar!!.width / 2).toFloat()
        mCartConfigs[0].curY = (mScreenHeight - mBadCar!!.height * 2).toFloat()
        //障碍车数量
        badCarNumber = 0
        //白色跑道的位移
        winDistance = (-winWay!!.height).toFloat()
    }

    /**
     *       碰撞监听
     */
    interface CarHitListen {
        fun hit()
    }

    private var hitListener: CarHitListen? = null
    fun setCarHitListener(listen: CarHitListen) {
        hitListener = listen
    }
    /**
     * 跑完全程监听
     */
    interface FinishListen{
        fun win()
    }
    private var finishListener:FinishListen?=null
    fun setFinishListener(listen: FinishListen){
        finishListener=listen
    }


    /**
     * 完成的时候回调
     */
    interface CallBack {
        //完成
        fun finish()
    }
}