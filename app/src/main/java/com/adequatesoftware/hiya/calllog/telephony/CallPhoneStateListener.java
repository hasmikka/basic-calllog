package com.adequatesoftware.hiya.calllog.telephony;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import org.greenrobot.eventbus.EventBus;

public class CallPhoneStateListener extends PhoneStateListener {
    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);

        //if a phone call is received (successfully or missed) or made, afterwards state happens
        //since call keeps track of all calls, this is the only state we care about
        if (TelephonyManager.CALL_STATE_IDLE == state){
            //TODO refresh call log
            EventBus.getDefault().post(new Events.PhoneCallStateChangeEvent("phone call changed"));
        }
    }
}
