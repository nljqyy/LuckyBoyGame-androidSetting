package com.jhz.luckyboyunity;

import android.os.Environment;

import com.efrobot.library.mvp.utils.L;
import com.efrobot.library.net.utils.FileUtil;
import com.efrobot.library.urlconfig.UrlConstants;

import junit.framework.Test;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;


import static android.content.ContentValues.TAG;

/**
 * @项目名称:幸运抓娃娃
 * @类名称：Contants
 * @类描述：接口公共类
 * @创建人：luyuqin
 * @创建时间：2017/11/2314:51
 * @修改时间：2017/11/2314:51
 * @备注：
 */



public class Constants {
   // public static final String LUCKDRAWSTATUS = "/luckDraw/getLuckDrawStatus";

   // public static final String LUCKDRAWQRCODE = "/luckDraw/getLuckDrawQrCode";
   // public static final String LUCKDRAW = "/luckDraw/addLuckDrawNum";
    //public static final String LUCKWINGING="/luckDraw/getLuckDrawChance";

    public  enum  IpTypeLuck
    {
        PayStatus,
        QrCode,
        Probability,
        Record,
        RecordList,
    }

    private static final String ROOT_PATH = Environment.getExternalStorageDirectory() + "/efrobot/";
    private static final String URL_TYPE_IN_CONFIG_PATH= ROOT_PATH + "robot_url_in.config";
    private static final String URL_TYPE_OUT_CONFIG_PATH= ROOT_PATH + "robot_url_out.config";

    private  static final  String FiveRoundConfig=Environment.getExternalStorageDirectory() + "/.config/.efrobot/game_config";

    private static  final String Test_IP="http://39.106.250.170:8083/api/v2/interface/doll/";
    private static  final  String IP="http://backend.efrobot.com/api/v2/interface/doll/";

    public  static  final  String LuckPayStatus="getPayStatus";

    public  static  final  String LuckDrawQrCode="getPayQRCode";

    public  static  final  String LuckCatchProbability="getDollProbability";

    public  static  final  String LuckCatchRecord="reportCrawlRecord";

    public  static  final  String LuckCatchRecordList="reportBatchCrawlRecord";

    private  static   String    ReadSettingValue="";

    public  static String  GetIPAddress(IpTypeLuck iptype)
    {
        File filePath = new File(URL_TYPE_IN_CONFIG_PATH);
        String ip=filePath.exists()?Test_IP:IP;
        String ipadress="";
        switch (iptype)
        {
            case PayStatus:
                ipadress= ip+LuckPayStatus;
                break;
            case Probability:
                ipadress= ip+LuckCatchProbability;
                break;
            case QrCode:
                ipadress= ip+ LuckDrawQrCode;
                break;
            case Record:
                ipadress=  ip+ LuckCatchRecord;
                break;
            case RecordList:
                ipadress= ip+ LuckCatchRecordList;
                break;
        }
        L.d(TAG, "GetIPAddress  IpAddress="+ipadress);
        return ipadress;
    }
   //设置中的数据
    public  static String  GetGameModeData()
    {
        String defualStr="0|3|5|3|0";// 支付模式|三局|5个题目|pass 3|进入游戏
        try {
            if(ReadSettingValue==null||ReadSettingValue=="") {
                ReadSettingValue = ReadSettingValueFun();
            }
            if(ReadSettingValue!=null||ReadSettingValue!="")
            {
                JSONObject jsonObject = new JSONObject(ReadSettingValue);
                if (jsonObject.has("model")&&jsonObject.has("mission")&&jsonObject.has("question")
                        &&jsonObject.has("pass")&&jsonObject.has("gift_model")) {
                    String  model=jsonObject.optString("model");
                    String misson = jsonObject.optString("mission");
                    String question = jsonObject.optString("question");
                    String pass = jsonObject.optString("pass");
                    String gift_model = jsonObject.optString("gift_model");//是否游戏
                    return model+"|"+misson+"|"+question+"|"+pass+"|" +gift_model;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
         return  defualStr;
    }

  //是否玩游戏
    public static String GetIsGame()
    {
        String defualStr="0";// 进入游戏
        try {
            String settingValue=  ReadSettingValueFun();
            if(settingValue!=null||settingValue!="")
            {
                ReadSettingValue=settingValue;
                L.d(TAG, "ReadSettingValue ==="+settingValue);
                JSONObject jsonObject = new JSONObject(settingValue);
                if (jsonObject.has("gift_model")) {
                    return jsonObject.optString("gift_model");//是否游戏
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return  defualStr;
    }

    private static String ReadSettingValueFun()
    {
        File filePath = new File(FiveRoundConfig);
        if(filePath.exists())
        {
            try
            {
                StringBuilder localStr=new StringBuilder();
                InputStreamReader  inputRead=new InputStreamReader(new FileInputStream(filePath),"utf-8");
                BufferedReader  bufferReader=new BufferedReader(inputRead);
                String linStr=null;
                while ((linStr=bufferReader.readLine())!=null)
                {
                    localStr.append(linStr);
                }
                bufferReader.close();
                inputRead.close();
                linStr=localStr.toString();
                if(linStr!=null||linStr!="")
                {
                    return  linStr;
                }
                return null;
            }
            catch(Exception e)
            {
                L.d(TAG, "--ReadSettingValue error-");
                e.printStackTrace();
            }
        }
        return  null;
    }

}
