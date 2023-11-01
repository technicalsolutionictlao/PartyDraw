package com.ictlao.android.app.partydraw;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

class InternetProvider {
    private Context mContext;

    public InternetProvider(Context context){
        mContext = context;
    }

    public boolean isConnected(){
        boolean isConnected = false;
        try {
            ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            if(info != null && info.isConnected()){
                isConnected = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return isConnected;
    }
}
