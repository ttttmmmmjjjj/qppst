package com.hsic.appupdate.updater;


import com.hsic.appupdate.net.HttpManager;
import com.hsic.appupdate.net.INetManager;

public class AppUpdater {
    private static AppUpdater  mInstance ;

    private INetManager mNetManager = new HttpManager();

    public void setNetManager(INetManager mNetManager)
    {
        this.mNetManager = mNetManager ;
    }
    public INetManager getNetManager()
    {
        return mNetManager ;
    }
    private AppUpdater()
    {

    }
     public static AppUpdater getInstance()
     {
         if(mInstance == null)
         {
             synchronized (AppUpdater.class)
             {
                 if(mInstance == null)
                 {
                     mInstance = new AppUpdater();
                 }
             }
         }

         return mInstance ;
     }

}
