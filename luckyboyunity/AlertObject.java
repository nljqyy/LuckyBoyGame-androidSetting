package com.jhz.luckyboyunity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.efrobot.claw.game.listener.OnClawGameStatusChangeListener;
import com.efrobot.library.OnRobotStateChangeListener;
import com.efrobot.library.RobotManager;

import org.json.JSONException;
import org.json.JSONObject;

import com.efrobot.library.RobotState;
import com.efrobot.library.mvp.utils.L;
import com.unity3d.player.UnityPlayer;

import java.lang.ref.WeakReference;
import com.efrobot.claw.game.sdk.*;



/**
 * @项目名称:幸运抓娃娃
 * @类名称：AlertObject
 * @类描述：与js通信
 * @创建人：luyuqin
 * @创建时间：2017/11/1716:52
 * @修改时间：2017/11/1716:52
 * @备注：
 */
public class AlertObject implements OnRobotStateChangeListener ,OnClawGameStatusChangeListener {
    private static final int BRIGHT = 1;
    private static final int NO_BRIGHT = 2;
    private static final int WAVE_UP = 3;
    private static final int WAVE_DOWN = 4;
    private static final int WAVE_STOP = 5;
    private static final int STOP_BRIGHT = 6;
    private static final String TAG = AlertObject.class.getSimpleName();

    private   BaseHandler mBaseHandler;
    public  Context mContext;

    public AlertObject(Context context) {

        this.mContext = context;
        mBaseHandler = new BaseHandler(this);

         if (!RobotManager.getInstance(mContext).hasConnect()) {
          RobotManager.getInstance(mContext).registerOnInitCompleteListener(new RobotManager.OnInitCompleteListener() {
         @Override
           public void onInitComplete() {
            L.d(TAG, "@@@onInitComplete  @@@@");
             RobotManager.getInstance(mContext).enterGraspDoll();
           }
          });
          } else {
            RobotManager.getInstance(mContext).enterGraspDoll();
         }

         RobotManager.getInstance(mContext).registerHeadKeyStateChangeListener(this);

        ClawGameManager.getInstance(mContext).registerClawGameStatusChangeListener(this);



    }

    /**
     * 语音反馈
     *
     * @param msg
     */
    // 定义JS需要调用的方法
    // 被JS调用的方法必须加入@JavascriptInterface注解
    @JavascriptInterface
    public void speak(String msg) {
        //  L.d("AlertObject", "JS调用了Android的speak方法");
        hintSpeak(msg, mContext);
    }

    /**
     * 翅膀运动
     */
    @JavascriptInterface
    public void wave(long time) {
        // L.d("AlertObject", "JS调用了Android的wave方法");
        mBaseHandler.sendEmptyMessage(WAVE_UP);
        mBaseHandler.sendEmptyMessageDelayed(WAVE_STOP, time);

    }

    /**
     * 抓中娃娃特效
     * @param state
     */
    public void wonDoll(boolean state) {
        if (state) {
            mBaseHandler.sendEmptyMessage(WAVE_UP);
            mBaseHandler.sendEmptyMessage(BRIGHT);
        } else {
            mBaseHandler.sendEmptyMessage(WAVE_STOP);
            mBaseHandler.sendEmptyMessage(STOP_BRIGHT);
        }
    }

    /**
     * 灯带
     */
    public void light(boolean n, long time) {
        // L.d("AlertObject", "JS调用了Android的light方法");
        if (n) {//常亮
            RobotManager.getInstance(mContext).getControlInstance().setLightBeltBrightness(255);
        } else {//闪烁
            mBaseHandler.sendEmptyMessage(BRIGHT);
        }
        mBaseHandler.sendEmptyMessageDelayed(STOP_BRIGHT, time);
    }





    //发送语音
    private void hintSpeak(String content, Context context) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("content", content);
            jsonObject.put("modelType", "game");
            Intent mIntent = new Intent("com.efrobot.speech.voice.ACTION_TTS");
            mIntent.putExtra("data", jsonObject.toString());
            context.sendBroadcast(mIntent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //头部按下
     @Override
     public void onRobotSateChange(int robotStateIndex, int newState) {
        switch (robotStateIndex) {
         case RobotState.ROBOT_STATE_INDEX_HEAD_KEY://头部按钮
             Log.d(TAG, "头部按钮");
        if (newState != RobotState.HEADKEY_STATE_UP) {
               Log.d(TAG, "头部按钮按下");
                UnityPlayer.UnitySendMessage("SDKManager","AndroidCall","HeadDown");
         }
      break;
      }
     }

   //检测娃娃机状态
    @Override
    public void onStatusChange(int status, int num)
    {
      if(status==ClawGameStatus.INDEX_CHECK_DOLL_EXIT)//是否有
      {
         if(num==ClawGameStatus.VALUE_DOLL_HAS)
         {
             UnityPlayer.UnitySendMessage("SDKManager","AndroidCall","HasBoy");
         }
         else
         {
             UnityPlayer.UnitySendMessage("SDKManager","AndroidCall","NoHas");
         }
      }
      else if(status==ClawGameStatus.INDEX_CHECK_DOLL_TAKE_AWAY)//是否取走
      {
          if(num==ClawGameStatus.VALUE_DOLL_ALL_TAKE_AWAY)//已取走
          {
              UnityPlayer.UnitySendMessage("SDKManager","AndroidCall","TakeAway");
          }
          else
          {
              UnityPlayer.UnitySendMessage("SDKManager","AndroidCall","NoTakeAway");
          }
      }
    }


    protected static class BaseHandler extends Handler {
        private final WeakReference<AlertObject> mObjects;

        public BaseHandler(AlertObject mPresenter) {
            mObjects = new WeakReference<AlertObject>(mPresenter);
        }

        @Override
        public void handleMessage(Message msg) {
            AlertObject mPresenter = mObjects.get();
            if (mPresenter != null)
                mPresenter.handleMessage(msg);
        }
    }

    private void handleMessage(Message msg) {
        switch (msg.what) {
            case BRIGHT:
                RobotManager.getInstance(mContext).getControlInstance().setLightBeltBrightness(255);
                mBaseHandler.sendEmptyMessageDelayed(NO_BRIGHT, 1000);
                break;
            case NO_BRIGHT:
                RobotManager.getInstance(mContext).getControlInstance().setLightBeltBrightness(0);
                mBaseHandler.sendEmptyMessageDelayed(BRIGHT, 1000);
                break;
            case STOP_BRIGHT:
                // L.d(TAG, "^^^^^^STOP_BRIGHT^^^^^^");
                RobotManager.getInstance(mContext).getControlInstance().setLightBeltBrightness(0);
                mBaseHandler.removeMessages(NO_BRIGHT);
                mBaseHandler.removeMessages(BRIGHT);
                break;

            case WAVE_UP:
                RobotManager.getInstance(mContext).getWingInstance().moveUp(0);
                mBaseHandler.sendEmptyMessageDelayed(WAVE_DOWN, 1000);
                break;
            case WAVE_DOWN:
                RobotManager.getInstance(mContext).getWingInstance().moveDown(0);
                mBaseHandler.sendEmptyMessageDelayed(WAVE_UP, 1000);
                break;
            case WAVE_STOP:
                //  L.d(TAG, "^^^^^WAVE_STOP^^^^^");
                RobotManager.getInstance(mContext).getWingInstance().moveDown(0);
                mBaseHandler.removeMessages(WAVE_DOWN);
                mBaseHandler.removeMessages(WAVE_UP);
                break;

        }
    }

}
