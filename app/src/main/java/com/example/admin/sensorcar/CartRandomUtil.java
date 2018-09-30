package com.example.admin.sensorcar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 作者：小民
 * 功能：xxxx
 * 时间：2017/11/5
 */

public class CartRandomUtil {
    //屏幕的宽高
    private int mScreenWidth;
    private int mScreenHeight;
    //车的宽度
    private float mCartWidth;
    //随机
    private Random mRandom;

    public CartRandomUtil(int screenWidth,int screenHeight, float cartWidth) {
        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;
        mCartWidth = cartWidth;
        mRandom = new Random();
    }


    /**
     * 随机位置,当然这个位置不能离自己太近
     * @param config  车子的位置,经过这个方法,会被修改  endX
     * @return  速度  -3 / 3
     */
    public float random(CartConfig config){
        int section = section(config);
        //不随机 离自己太近的章节
        List<Float> floats = new ArrayList<>(Arrays.asList(0.15f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f));
        floats.remove(section);
        //随机
        int position = mRandom.nextInt(floats.size());
        float v = floats.get(position);
        float endX = asmRandomX(v);
        //赋值新的 EndX
        config.setEndX(endX);
        //计算前进还是后退
        float speed = 5;
        if (endX < config.getCurX()){
            config.setSpeed(-speed);
            return -speed;
        }else{
            config.setSpeed(speed);
            return speed;
        }
    }

    public float randomY(CartConfig config){
        int section = sectionY(config);
        //不随机 离自己太近的章节
        List<Float> floats = new ArrayList<>(Arrays.asList(0.15f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f));
        floats.remove(section);
        //随机
        int position = mRandom.nextInt(floats.size());
        float v = floats.get(position);
        float endY = asmRandomY(v);
        //赋值新的 EndY
        config.setEndY(endY);
        //计算前进还是后退
        float speed = 5;
        if (endY < config.getCurY()){
            config.setSpeed(-speed);
            return -speed;
        }else{
            config.setSpeed(speed);
            return speed;
        }
    }

    /** 获取当前车子处于哪个区间 */
    private int section(CartConfig config){
        return (int) (config.getCurX() * 1.0f / mScreenWidth) * 10;
    }
    private int sectionY(CartConfig config){
        return (int)(config.getCurY()*1.0f/mScreenHeight)*10;
    }
    /** 根据屏幕的百度比 计算出 End 坐标 */
    private float asmRandomX(float v){
        return v * mScreenWidth;
    }
    private float asmRandomY(float v){
        return v*mScreenHeight;
    }

}
