package com.adequatesoftware.hiya.calllog;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.provider.CallLog;

import com.adequatesoftware.hiya.calllog.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DisplayUtil {
    private static SimpleDateFormat formatter = new SimpleDateFormat("K:mm a M/dd/yyyy");

    /**
     * Given a int value, returns string based on CallLog.Calls types
     * @param value
     * @return
     */
    public static String getTypeOfCall(int value){
        String typeValStr = "Unknown";

        switch (value) {
            case CallLog.Calls.OUTGOING_TYPE:
                typeValStr = "Outgoing";
                break;
            case CallLog.Calls.INCOMING_TYPE:
                typeValStr = "Incoming";
                break;
            case CallLog.Calls.MISSED_TYPE:
                typeValStr = "Missed";
                break;
        }

        return typeValStr;
    }

    /**
     * Formatting the date for display
     * @param dateStr string of long value
     * @return fromatted date string
     */
    public static String getDate(String dateStr){
        Date date = new Date(Long.valueOf(dateStr));

        return formatter.format(date);
    }

    /**
     * Formats phone numbers, assuming US locale and phone number length
     * @param phone unfrommated number
     * @return formatted with dashes
     */
    public static String formatNumber(String phone){
        String formatted = phone;
        int len = formatted.length();

        //avoiding small number formatting, such as for calling 411
        if (phone.length() > 9){
            formatted = formatted.substring(0, len - 4) + "-" + formatted.substring(len - 4);
            formatted = formatted.substring(0, len - 7) + "-" + formatted.substring(len - 7);
        }

        if (phone.startsWith("+1")){
            formatted = formatted.substring(0, 2) + "-" + formatted.substring(2);
        }
        return formatted;
    }

    public static AlertDialog getPermissionDialog(final Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(activity.getString(R.string.alert_title))
               .setTitle(R.string.alert_message);

        // Add the buttons
        builder.setPositiveButton(R.string.common_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                activity.finish();
            }
        });
       return builder.create();

    }
}
