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
   //是否是五局模式
    public  static String  GetGameModeData()
    {
        File filePath = new File(FiveRoundConfig);
        String defualStr="0|3|10|10";// 支付模式|三局|10个题目|pass 10
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
                    JSONObject jsonObject = new JSONObject(linStr);
                    if (jsonObject.has("model")&&jsonObject.has("mission")&&jsonObject.has("question")&&jsonObject.has("pass")) {
                        String  model=jsonObject.optString("model");
                        String misson = jsonObject.optString("mission");
                        String question = jsonObject.optString("question");
                        String pass = jsonObject.optString("pass");
                        return model+"|"+misson+"|"+question+"|"+pass ;
                    }
                }
                return defualStr;
            }
            catch(Exception e)
            {
                L.d(TAG, "--GetGameModeData--Read error-");
                e.printStackTrace();
            }
        }
        return  defualStr;
    }
}
