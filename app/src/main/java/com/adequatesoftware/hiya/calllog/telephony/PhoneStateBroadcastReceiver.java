package com.adequatesoftware.hiya.calllog.telephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class PhoneStateBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

        CallPhoneStateListener listener = new CallPhoneStateListener();

        //Register to listen for event
        manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

    }
}
