package org.sipdroid.sipua.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import org.sipdroid.sipua.MyLogSupport;

import org.sipdroid.sipua.NtApplication;
import org.sipdroid.sipua.viewmodel.ReadViewModel;

import java.util.Timer;


public class ServiceReceiver extends BroadcastReceiver {

    private Context context;
    private ReadViewModel readViewModel = NtApplication.handlerViewModel.getReadViewModel();
    private Timer timer = new Timer();


    @Override
    public void onReceive(Context context, Intent intent) {
        MyLogSupport.log_print("intent_Action : " + intent.getAction());

        this.context = context;

        final Bundle extras = intent.getExtras();
        if (intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)){
            final String state = extras.getString(TelephonyManager.EXTRA_STATE);
            if ("IDLE".equals(state)){
                MyLogSupport.log_print("전화끊음!");
                readViewModel.autocall_start_and_stop.setValue(true);

            }
            if ("OFFHOOK".equals(state)){
                MyLogSupport.log_print("전화중!");
                timer.cancel();
            }
            if ("RINGING".equals(state)){
                MyLogSupport.log_print("전화울림!");
            }
        }

    }



}
