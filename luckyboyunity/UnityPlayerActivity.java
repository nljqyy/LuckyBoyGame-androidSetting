package com.jhz.luckyboyunity;

import com.efrobot.library.RobotManager;
import com.efrobot.library.task.SpeechGroupManager;
import com.unity3d.player.*;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.efrobot.library.RobotState;
import com.efrobot.library.mvp.utils.L;
import com.efrobot.library.net.BaseSendRequestListener;
import com.efrobot.library.net.NetClient;
import com.efrobot.library.net.NetMessage;
import com.efrobot.library.net.TextMessage;
import com.efrobot.library.net.utils.NetUtil;
import com.efrobot.library.urlconfig.UrlConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;
import com.efrobot.claw.game.sdk.*;



public class UnityPlayerActivity extends Activity
{
    protected UnityPlayer mUnityPlayer; // don't change the name of this variable; referenced from native code


    private   AlertObject alerObj;
    private   BaseHandler mBaseHandler;
    private   boolean canPlay = false;
    private  boolean isPay=false;
    private  boolean isForeground=true;
    private  int times=10;
    private  Timer timer;
    private  String carwTime="";
    private  boolean isCarw=false;
    private  String orderNumber="";
    private  String openId="";
    private SpeechGroupManager mGroupManager;
    private int count;
    // Setup activity layout
    @Override protected void onCreate(Bundle savedInstanceState)
    {
        L.d(TAG, "---------------onCreate------------");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        count=0;
        mUnityPlayer = new UnityPlayer(this);
        setContentView(mUnityPlayer);
        mUnityPlayer.requestFocus();
        mGroupManager=SpeechGroupManager.getInstance(RobotManager.getInstance(this));
        ClawGameManager.getInstance(getApplicationContext()).init();//初始化
        alerObj=new AlertObject(this);
        timer = new Timer();// 实例化Timer类
    }

    @Override protected void onNewIntent(Intent intent)
    {
        // To support deep linking, we need to make sure that the client can get access to
        // the last sent intent. The clients access this through a JNI api that allows them
        // to get the intent set on launch. To update that after launch we have to manually
        // replace the intent with the one caught here.
        setIntent(intent);
    }

    // Quit Unity
    @Override protected void onDestroy ()
    {
        L.d(TAG, "---------------onDestroy------------");
        mUnityPlayer.quit();
        super.onDestroy();
    }

    // Pause Unity
    @Override protected void onPause()
    {
        L.d(TAG, "---------------onPause------------");
		mGroupManager.reset();
        super.onPause();
        mUnityPlayer.pause();
    }

    // Resume Unity
    @Override protected void onResume()
    {
        L.d(TAG, "---------------onResume------------");
        super.onResume();
        mUnityPlayer.resume();
    }

    @Override protected void onStart()
    {
        L.d(TAG, "---------------onStart------------");
        if(isForeground) {
            super.onStart();
            mUnityPlayer.start();
        }
        else
        {
            onDestroy();
            timer.cancel();
            Intent intent = new Intent();
            PackageManager packageManager = getPackageManager();
            intent = packageManager.getLaunchIntentForPackage("com.jhz.luckyboyunity");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP) ;
            startActivity(intent);
        }
    }

    @Override protected void onStop()
    {
        L.d(TAG, "---------------onStop------------");
        super.onStop();
        mUnityPlayer.stop();
    }

    // Low Memory Unity
    @Override public void onLowMemory()
    {
        super.onLowMemory();
        mUnityPlayer.lowMemory();
    }

    // Trim Memory Unity
    @Override public void onTrimMemory(int level)
    {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_RUNNING_CRITICAL)
        {
            mUnityPlayer.lowMemory();
        }
    }

    // This ensures the layout will be correct.
    @Override public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mUnityPlayer.configurationChanged(newConfig);
    }

    // Notify Unity of the focus change.
    @Override public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        mUnityPlayer.windowFocusChanged(hasFocus);
    }

    // For some reason the multiple keyevent type is not supported by the ndk.
    // Force event injection by overriding dispatchKeyEvent().
    @Override public boolean dispatchKeyEvent(KeyEvent event)
    {
        if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
            return mUnityPlayer.injectEvent(event);
        return super.dispatchKeyEvent(event);
    }

    // Pass any events not handled by (unfocused) views straight to UnityPlayer
    @Override public boolean onKeyUp(int keyCode, KeyEvent event)     { return mUnityPlayer.injectEvent(event); }
    @Override public boolean onKeyDown(int keyCode, KeyEvent event)   { return mUnityPlayer.injectEvent(event); }
    @Override public boolean onTouchEvent(MotionEvent event)          { return mUnityPlayer.injectEvent(event); }
    /*API12*/ public boolean onGenericMotionEvent(MotionEvent event)  { return mUnityPlayer.injectEvent(event); }

    public void GetPayStatus(String orderNo,boolean isFirst) {
        L.d("GetPayStatus", "unity调用了GetPayStatus方法   orderNo:"+orderNo);
        final boolean  isfirst=isFirst;
        if (!NetUtil.checkNet(this)){
            L.d(TAG, "no network");
            if(isfirst) {
                uspeak("没有网络，请联网后再试");
                handler.sendEmptyMessageDelayed(3, 3 * 1000);
            }
            return;
        }
        try {
            orderNumber=orderNo;
            TextMessage message = new TextMessage();
            message.setRequestMethod(TextMessage.REQUEST_METHOD_POST);
            message.setUrl(Constants.GetIPAddress(Constants.IpTypeLuck.PayStatus));
            message.append("robotId", getRobotId());
            message.append("orderNo", orderNo);
			message.setEncryption(true);
            NetClient.getInstance(this).sendNetMessage(message, new BaseSendRequestListener() {
                @Override
                public void onSuccess(NetMessage message, String result) {
                    super.onSuccess(message, result);
                    L.d(TAG, "GetPayStatus  result=" + result);
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(result);
                        if (jsonObject.has("resultCode") && jsonObject.getString("resultCode").equals("SUCCESS")) {
                            String status = jsonObject.optString("status");
                            if (!status.isEmpty() && status.equals("1")) {//支付完成 开始游戏
                                isPay=true;
                                count=0;
                                openId=jsonObject.optString("openId");
                                String paytime=jsonObject.optString("winningLevel")+"|"+openId;
                                if(isForeground)
                                {
                                    UnityPlayer.UnitySendMessage("SDKManager","PaySuccess",paytime);
                                }
                            }
                            else
                            {
                                if(isfirst)
                                   UnityPlayer.UnitySendMessage("SDKManager","AndroidCall","NoPay");
                            }
                        }
                        else
                        {
                            if(isfirst)
                                UnityPlayer.UnitySendMessage("SDKManager","AndroidCall","NoPay");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        L.d(TAG, "GetPayStatus  解析错误:"+e.getMessage());
                    }
                }

                @Override
                public void onFail(NetMessage message, int errorCode, String errorMessage) {
                    super.onFail(message, errorCode, errorMessage);
                    L.d(TAG, "GetPayStatus  onFail  errorCode=" + errorCode+" errorMessage="+errorMessage);
                    if(!isfirst)return;
                    if(count!=3) {
                        count++;
                        handler.sendEmptyMessageDelayed(2, 3 * 1000);
                    }else{
                        UnityPlayer.UnitySendMessage("SDKManager","AndroidCall","Error");
                        count=0;
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            //UnityPlayer.UnitySendMessage("SDKManager","AndroidCall","Error");
        }

    }

    public void GetDrawQrCode() {
        L.d("GetDrawQrCode", "unity调用了GetDrawQrCode方法");
        if (!NetUtil.checkNet(this)) {
            L.d(TAG, "no network");
            return;
        }
        try {
            TextMessage message = new TextMessage();
            message.setRequestMethod(TextMessage.REQUEST_METHOD_POST);
            message.setUrl(Constants.GetIPAddress(Constants.IpTypeLuck.QrCode));
            message.append("robotId", getRobotId());
            NetClient.getInstance(this).setConnectTimeout(3, TimeUnit.SECONDS);
			message.setEncryption(true);
            NetClient.getInstance(this).sendNetMessage(message, new BaseSendRequestListener() {
                @Override
                public void onSuccess(NetMessage message, String result) {
                    super.onSuccess(message, result);
                    L.d(TAG, "GetDrawQrCode result=" + result);
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(result);
                        if (jsonObject.has("resultCode") && jsonObject.getString("resultCode").equals("SUCCESS")) {
                            String urlStr = jsonObject.optString("qrUrl");
                            String orderNo = jsonObject.optString("orderNo");
                            String msg=urlStr+"|"+orderNo+"|"+getRobotId();
                            UnityPlayer.UnitySendMessage("SDKManager", "QRCodeCall", msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        L.d(TAG, "GetDrawQrCode  解析错误:"+e.getMessage());
                    }
                }

                @Override
                public void onFail(NetMessage message, int errorCode, String errorMessage) {
                    super.onFail(message, errorCode, errorMessage);
                    L.d(TAG, "GetDrawQrCode  onFail   errorCode=" + errorCode+" errorMessage="+errorMessage);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

   //获得概率值
    public void GetProbabilityValue()
    {
        L.d("GetProbabilityValue", "unity调用了GetProbabilityValue方法");
        if (!NetUtil.checkNet(this)) {
            L.d(TAG, "no network");
            return;
        }
        try {
            TextMessage message = new TextMessage();
            message.setRequestMethod(TextMessage.REQUEST_METHOD_POST);
            message.setUrl(Constants.GetIPAddress(Constants.IpTypeLuck.Probability));
            message.append("robotId", getRobotId());
			message.setEncryption(true);
            NetClient.getInstance(this).sendNetMessage(message, new BaseSendRequestListener() {
                @Override
                public void onSuccess(NetMessage message, String result) {
                    super.onSuccess(message, result);
                    L.d(TAG, "GetProbabilityValue result=" + result);
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(result);
                        if (jsonObject.has("resultCode") && jsonObject.getString("resultCode").equals("SUCCESS")) {
                            String data=jsonObject.optString("data");//概率值
                            jsonObject = new JSONObject(data);
                            String probaValue = jsonObject.optString("captureProb");//概率值
                            String winging = jsonObject.optString("captureStepNum");//中奖值
                            String carwBasic = jsonObject.optString("captureStepLenght");//基数
                            String money=jsonObject.optString("qrAmount");//金额
                            String pwVV=probaValue+"|"+winging+"|"+carwBasic+"|"+money;
                            UnityPlayer.UnitySendMessage("SDKManager","GetProbabilityCall",pwVV);
                        }
                        else if(jsonObject.has("resultCode") && jsonObject.getString("resultCode").equals("NO_DOLL_ROBOT"))
                        {
                            UnityPlayer.UnitySendMessage("SDKManager","AndroidCall","NoBind");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        L.d(TAG, "GetProbabilityValue  解析错误:"+e.getMessage());
                    }
                }

                @Override
                public void onFail(NetMessage message, int errorCode, String errorMessage) {
                    super.onFail(message, errorCode, errorMessage);
                    L.d(TAG, "GetProbabilityValue  onFail  errorCode=" + errorCode+" errorMessage="+errorMessage);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
           // UnityPlayer.UnitySendMessage("SDKManager","AndroidCall","Error");
        }

    }

    //上报抓娃娃记录
    public void SendCatchRecord(boolean isCatch, String catchTime)
    {
        L.d("SendCatchRecord", "unity调用了SendCatchRecord方法");
        isCarw=isCatch;
        carwTime=catchTime;
        int number=isCatch?1:0;
        if (!NetUtil.checkNet(this)) {
            L.d(TAG, "no network");
            return;
        }
        try {
            TextMessage message = new TextMessage();
            message.setRequestMethod(TextMessage.REQUEST_METHOD_POST);
            message.setUrl(Constants.GetIPAddress(Constants.IpTypeLuck.Record));
            message.append("robotId", getRobotId());
            message.append("status",number);
            message.append("reportTime",catchTime);
            message.append("openId",openId);
            message.append("applyRechargeId",orderNumber);
			message.setEncryption(true);
            L.d(TAG, "SendCatchRecord status=" +number+" reportTime="+catchTime+" openId="+openId);
            NetClient.getInstance(this).sendNetMessage(message, new BaseSendRequestListener() {
                @Override
                public void onSuccess(NetMessage message, String result) {
                    super.onSuccess(message, result);
                    L.d(TAG, "SendCatchRecord result=" + result);
                    JSONObject jsonObject = null;
                    count=0;
                    try {
                        jsonObject = new JSONObject(result);
                        if (jsonObject.has("resultCode") && jsonObject.getString("resultCode").equals("SUCCESS")) {
                            isCarw=false;
                            carwTime="";
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        L.d(TAG, "SendCatchRecord  解析错误:"+e.getMessage());
                    }
                }

                @Override
                public void onFail(NetMessage message, int errorCode, String errorMessage) {
                    super.onFail(message, errorCode, errorMessage);
                    L.d(TAG, "SendCatchRecord  onFail  errorCode=" + errorCode+" errorMessage="+errorMessage);
                    UnityPlayer.UnitySendMessage("SDKManager", "AndroidCall", "UpRecordFail");
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void SendCatchRecordList(String  catchlist)
    {
        L.d("SendCatchRecordList", "unity调用了SendCatchRecordList方法");
        if (!NetUtil.checkNet(this)) {
            L.d(TAG, "no network");
            return;
        }
        try {
            TextMessage message = new TextMessage();
            message.setRequestMethod(TextMessage.REQUEST_METHOD_POST);
            message.setUrl(Constants.GetIPAddress(Constants.IpTypeLuck.RecordList));
            JSONArray array=new JSONArray(catchlist);
            message.append("list", array);
            message.setEncryption(true);
            L.d(TAG, "SendCatchRecordList list="+array);
            NetClient.getInstance(this).sendNetMessage(message, new BaseSendRequestListener() {
                @Override
                public void onSuccess(NetMessage message, String result) {
                    super.onSuccess(message, result);
                    L.d(TAG, "SendCatchRecordList result=" + result);
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(result);
                        if (jsonObject.has("resultCode") && jsonObject.getString("resultCode").equals("SUCCESS")) {
                            UnityPlayer.UnitySendMessage("SDKManager", "AndroidCall", "UpRecordListSuccess");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        L.d(TAG, "SendCatchRecordList  解析错误:"+e.getMessage());
                    }
                }

                @Override
                public void onFail(NetMessage message, int errorCode, String errorMessage) {
                    super.onFail(message, errorCode, errorMessage);
                    L.d(TAG, "SendCatchRecordList  onFail  errorCode=" + errorCode+" errorMessage="+errorMessage);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    Handler handler=new Handler(){
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what){
                case 1:
                    SendCatchRecord(isCarw,carwTime);
                    break;
                case 2:
                    GetPayStatus(orderNumber,true);
                    break;
                case 3:
                    finish();
                    break;
            }
        }
    };
    public String getRobotId() {
        //获取小胖唯一编码
        String number = RobotState.getInstance(getApplicationContext()).getRobotNumber();
        if (number == null) {
            number = "";
        }
        return number;
    }

    public void uspeak(String msg)
    {
        L.d("speak", "unity调用了speak方法");
        alerObj.speak(msg);
    }

    public void uwave(int time)
    {
        L.d("wave", "unity调用了wave方法");
        alerObj.wave(time);
    }
    public void ulight(boolean n, int time)
    {
        L.d("light", "unity调用了light方法");
        alerObj.light(n,time);
    }

    public  void wonDoll(boolean state)
    {
        L.d("wonDoll", "unity调用了wonDoll方法");
        alerObj.wonDoll(state);
    }

    /**
     * 初始化一个Handler，如果需要使用Handler，先调用此方法，
     */
    public  void initHandler( ) {
        mBaseHandler = new BaseHandler(this);
    }

    /**
     * 返回Handler，在此之前确定已经调用initHandler（）
     *
     * @return Handler
     */
    public  Handler getHandler() {
        initHandler();
        return mBaseHandler;
    }

    protected   class BaseHandler extends Handler {
        private final WeakReference<UnityPlayerActivity> mObjects;

        public BaseHandler(UnityPlayerActivity mPresenter) {
            mObjects = new WeakReference<UnityPlayerActivity>(mPresenter);
        }

        @Override
        public void handleMessage(Message msg) {
            UnityPlayerActivity mPresenter = mObjects.get();
            if (mPresenter != null)
            mPresenter.handleMessage(msg);
}
}
        private void handleMessage(Message msg) {

        }

//自动出礼物
    public  void autoPresent()
    {
        //ClawGameManager.getInstance(getApplicationContext()).getControlManagerInstance(getApplicationContext()).pushRod();
        ClawGameManager.getInstance(getApplicationContext()).getControlManagerInstance(getApplicationContext()).trackForwardOnce(2);//抓成功出娃娃
    }
    //是否能玩
    public  boolean isCanPlay()
    {
        if(ClawGameManager.getInstance(getApplicationContext()).checkDollExit() ==  ClawGameStatus.VALUE_DOLL_NO_HAS)
        {
          //UnityPlayer.UnitySendMessage("SDKManager","AndroidCall","NoHas");
            return false;
        }
        return  true;
    }
   //出口是否有东西
    public  boolean isTakeAway()
    {
        if(ClawGameManager.getInstance(getApplicationContext()).checkDollTakeAway() != ClawGameStatus.VALUE_DOLL_ALL_TAKE_AWAY)
        {
            return  false;
        }
        return  true;
    }

    public void CustomQuit()
    {
        L.d("CustomQuit", "开始退出");
        moveTaskToBack(true);
        isForeground=false;
        timer.schedule(new TimerTask() {
            public void run() {
                if(times<=0&&!isPay)
               {
                  L.d("CustomQuit", "没有支付游戏推出");
                  finish();
               }
               else
               {
                   if(!isPay) {
                       GetPayStatus(orderNumber,false);
                   }
                   else {
                       L.d("timer", "已支付完成");
                       MoveForeground();
                       timer.cancel();
                   }
               }
                times=times-1;
            }
        },0, 1000);// 这里百毫秒

    }
  //切到前台
    private  void  MoveForeground()
    {
        L.d("MoveForeground", "切到前台");
        isForeground=true;
        ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        am.moveTaskToFront(getTaskId(), ActivityManager.MOVE_TASK_WITH_HOME);
        UnityPlayer.UnitySendMessage("SDKManager","AndroidCall","PaySuccess");
    }
}
