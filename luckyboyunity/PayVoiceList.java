package com.jhz.luckyboyunity;

import java.util.List;

/**
 * Created by lhy on 2018/10/18
 *
 * @Link
 * @description
 */
public class PayVoiceList {
    private List<PayVoiceData> plist;

    public void SetList(List<PayVoiceData> _plist)
    {
        this.plist=_plist;
    }
    public  int Lenght()
    {
        if(plist!=null)
            return plist.size();
        return  0;
    }
}
