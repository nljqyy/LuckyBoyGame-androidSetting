package com.jhz.luckyboyunity;

import android.os.Environment;

import com.efrobot.library.mvp.utils.L;
import com.efrobot.library.urlconfig.UrlConstants;

import junit.framework.Test;

import java.io.File;

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
}
