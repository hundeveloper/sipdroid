package org.sipdroid.sipua;

import android.app.Application;

import org.sipdroid.sipua.di.HandlerBase;


public class NtApplication extends Application {
    public static HandlerBase handlerViewModel;

    @Override
    public void onCreate() {
        super.onCreate();

        if(handlerViewModel == null){
            handlerViewModel = new HandlerBase(this.getApplicationContext());
        }
    }
}
