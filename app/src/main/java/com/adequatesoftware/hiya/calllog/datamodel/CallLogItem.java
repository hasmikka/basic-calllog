package com.adequatesoftware.hiya.calllog.datamodel;

/**
 * Data model to store calllog information that will be used
 */
public class CallLogItem {
    private String phoneNumber;
    private String callDuration;
    private String callType;
    private String date;

    /**
     * Public constructor
     * @param number the phone number
     * @param duration duration of the call
     * @param type type of the call in string format
     * @param date formatted date
     */
    public CallLogItem(String number, String duration, String type, String date){
        this.phoneNumber = number;
        this.callDuration = duration;
        this.callType = type;
        this.date = date;
    }

    /**
     * Fetch the stored phone number
     * @return phoen num
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * String call type
     * @return  "Incoming", "Outgoing", or "Missed"
     */
    public String getCallType() {
        return callType;
    }

    /**
     * Returns the time the call was placed
     * @return formatted date string
     */
    public String getTimeString() {
        return date;
    }
}
