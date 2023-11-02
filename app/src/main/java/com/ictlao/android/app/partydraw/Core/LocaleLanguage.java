package com.ictlao.android.app.partydraw.Core;

import android.app.Application;
import android.content.Context;

public class LocaleLanguage extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base,LocaleHelper.getLanguage(base)));
    }
}
