package com.adequatesoftware.hiya.calllog;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.adequatesoftware.hiya.calllog.datamodel.CallLogItem;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;


/**
 *
 * TODO:
 *
 * Permissions
 * animations
 * live-listening to changes in log
 * scroll to new item?
 * onsavedinstantstate
 * javadoc for everying
 * format date before display
 * styles for fonts
 * define empty state for no call log
 * error handling
 * analytics?
 */

public class MainActivity extends AppCompatActivity {

    private static int MY_PERMISSIONS_REQUEST_READ_LOGS = 22;
    private ArrayList<CallLogItem> data;
    private CallLogAdapter adapter;
    private RecyclerView callLogList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        callLogList = (RecyclerView) findViewById(R.id.mainList);
        callLogList.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CallLogAdapter(data);
        callLogList.setAdapter(adapter);

    }

    @Override
    public void onResume() {
        super.onResume();

        if (data != null){
            displayData();
        } else {
            fetchCallLogData();
        }
    }

    private void displayData(){
        adapter = new CallLogAdapter(data);
        callLogList.setAdapter(adapter);

    }

    private void fetchCallLogData(){

        //check permission
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            fetchData();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG}, MY_PERMISSIONS_REQUEST_READ_LOGS);

            /**
             * //TODO permission check failed
             * https://developer.android.com/training/permissions/requesting.html
             */
        }

    }

    //this is what gets called when permissions have been granted
    private void fetchData(){
        ArrayList<CallLogItem> data = new ArrayList<>();

        try {
            Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);

            int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = cursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);

            int count = 0;

            while (cursor.moveToNext() && count < 50) {

                //get type of call
                int typeV = cursor.getInt(type);
                String typeValStr = "Unknown";

                switch (typeV) {
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

                //get date

                Date callDate = new Date(Long.valueOf(cursor.getString(date)));

                CallLogItem item = new CallLogItem(cursor.getString(number), cursor.getString(duration), typeValStr, callDate);
                data.add(item);
                count++;
            }
        } catch (SecurityException e){
            //TODO log somehow we got here without being given permission. wtf?
        }

        this.data = data;

        if (this.data != null){
            displayData();
        }
    }





    ///// CallBacks ////

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (MY_PERMISSIONS_REQUEST_READ_LOGS == requestCode) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                fetchData();

            } else {

                //TODO permission denied, disable feature or ask user again
            }
        }

    }

}
