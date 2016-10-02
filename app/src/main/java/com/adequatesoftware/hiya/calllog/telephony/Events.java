package com.adequatesoftware.hiya.calllog.telephony;

public class Events {

    public static class PhoneCallStateChangeEvent {
        public final String message;

        public PhoneCallStateChangeEvent(String message) {
            this.message = message;
        }
    }
}
