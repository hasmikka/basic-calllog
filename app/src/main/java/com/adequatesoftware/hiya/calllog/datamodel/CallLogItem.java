package com.adequatesoftware.hiya.calllog.datamodel;

import java.util.Date;

public class CallLogItem {
    private String phoneNumber;
    private String callDuration;
    private String callType;
    private Date date;

    public CallLogItem(String number, String duration, String type, Date date){
        this.phoneNumber = number;
        this.callDuration = duration;
        this.callType = type;
        this.date = date;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCallDuration() {
        return callDuration;
    }

    public String getCallType() {
        return callType;
    }

    public Date getTime() {
        return date;
    }
}
