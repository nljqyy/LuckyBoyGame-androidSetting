package com.jhz.luckyboyunity;

import com.unity3d.player.*;

import android.annotation.SuppressLint;

import android.app.Activity;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;


import android.widget.ImageView;
import android.widget.ImageView.ScaleType;


public class Splash {

    private ImageView bgView = null;

    private UnityPlayer mUnityPlayer = null;



    private static Splash mInstance;

    public static Splash getInstance() {

        if (null == mInstance) {

            synchronized (Splash.class) {

                if (null == mInstance) {

                    mInstance = new Splash();

                }

            }

        }

        return mInstance;

    }


    public void onCreate(UnityPlayer up, Bundle savedInstanceState) {

        // TODO Auto-generated method stub

        mUnityPlayer = up ;
        onShowSplash();

    }


    @SuppressLint("NewApi")

    public void onShowSplash() {

        if (bgView != null)

            return;



        try {Resources r = UnityPlayer.currentActivity.getResources();

            bgView = new ImageView(UnityPlayer.currentActivity);

            bgView.setBackgroundResource(R.mipmap.app_icon);

            bgView.setScaleType(ScaleType.CENTER);

            mUnityPlayer.addView(bgView,r.getDisplayMetrics().widthPixels,

                    r.getDisplayMetrics().heightPixels);

        } catch (Exception e) {

           // error("[onShowSplash]"+e.toString());
            e.printStackTrace();

        }

    }



    public void onHideSplash() {

        try {

            if(bgView == null)

                return;

            UnityPlayer.currentActivity.runOnUiThread(new Runnable() {

                public void run() {

                    mUnityPlayer.removeView(bgView);

                    bgView = null;

                }

            });

        } catch (Exception e) {

            //error("[onHideSplash]"+e.toString());
            e.printStackTrace();
        }

    }

}
