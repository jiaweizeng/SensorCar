package com.example.admin.sensorcar;

import android.graphics.Bitmap;

/**
 * 作者：小民
 * 功能：车子的配置信息
 * 时间：2017/10/26
 */

public class CartConfig {
    /**
     * 当前车的号码
     */
    private int number;
    /**
     * 当前X坐标
     */
    private float curX;
    /**
     * 终点X坐标
     */
    private float endX;
    /**
     * 当前Y坐标
     */
    private float curY;
    /**
     * 终点Y坐标
     */
    private float endY;
    /**
     * 速度
     */
    private float mSpeed;
    /**
     * 绑定的Bitmap对象
     */
    private Bitmap mBitmap;
    /**
     * 指示器
     */
    private Bitmap mIndicatorBmp;
    /**
     * 宽度
     */
    private int width;
    /**
     * 高度
     */
    private int height;
    /**
     * 已计算玩终点
     */
    private boolean isEnd;
    private boolean isEndY;
    /**
     * 当前喷火的等级
     */
    private int fire;

    public CartConfig(int number, float curX,float curY, Bitmap bitmap, Bitmap indicatorBmp) {
        this.number = number;
        this.curX = curX;
        this.curY = curY;
        mBitmap = bitmap;
        width = mBitmap.getWidth();
        height=mBitmap.getHeight();
        mIndicatorBmp = indicatorBmp;
        mSpeed = -2;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }


    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public float getCurX() {
        return curX;
    }

    public void setCurX(float curX) {
        this.curX = curX;
    }

    public float getCurY() {
        return curY;
    }

    public void setCurY(float curY) {
        this.curY = curY;
    }

    public float getEndX() {
        return endX;
    }

    public void setEndX(float endX) {
        this.endX = endX;
    }

    public float getEndY() {
        return endY;
    }

    public void setEndY(float endY) {
        this.endY = endY;
    }

    public float getSpeed() {
        return mSpeed;
    }

    public void setSpeed(float speed) {
        mSpeed = speed;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }


    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }


    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public boolean isEndY() {
        return isEndY;
    }

    public void setEndY(boolean end) {
        isEndY = end;
    }

    public int getFire() {
        return fire;
    }

    public void setFire(int fire) {
        this.fire = fire;
    }

    public Bitmap getIndicatorBmp() {
        return mIndicatorBmp;
    }

    public void setIndicatorBmp(Bitmap indicatorBmp) {
        mIndicatorBmp = indicatorBmp;
    }

    /**
     * 切换到下一个喷火等级,最多 三级
     */
    public int nextFire() {
        fire += 5;
        fire = fire > 30 ? 1 : fire;
        return fire;
    }
}
