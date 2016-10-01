package com.adequatesoftware.hiya.calllog;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.adequatesoftware.hiya.calllog.datamodel.CallLogItem;
import com.adequatesoftware.hiya.calllog.datamodel.DisplayUtil;

import java.util.ArrayList;


/**
 *
 * TODO:
 *
 * Permissions
 * animations
 * live-listening to changes in log
 * scroll to new item?
 * javadoc for everying
 * styles for fonts
 * define empty state for no call log
 * error handling
 * analytics?
 * cursor adapter?
 * swipe to refresh
 */

public class MainActivity extends AppCompatActivity {

    private static int MY_PERMISSIONS_REQUEST_READ_LOGS = 22;
    private static int MAXIMUM_NUMBER_LOG_REQUESTED = 50;
    private ArrayList<CallLogItem> mData;
    private CallLogAdapter mAdapter;
    private RecyclerView mCallListRecycleView;
    private SwipeRefreshLayout mSwipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCallListRecycleView = (RecyclerView) findViewById(R.id.main_list);
        mCallListRecycleView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new CallLogAdapter();
        mCallListRecycleView.setAdapter(mAdapter);

        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.main_swipe);

        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //to prevent swipe
                mSwipeLayout.setEnabled(false);
                fetchCallLogData();
            }

        });

    }

    @Override
    public void onStart() {
        super.onStart();

        if (mData != null){
            displayData();
        } else {
            fetchCallLogData();
        }
    }

    private void displayData(){
        mAdapter.setData(mData);
        mSwipeLayout.setRefreshing(false);
        mSwipeLayout.setEnabled(true);
    }

    private void fetchCallLogData(){

        //check permission
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            fetchData();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG}, MY_PERMISSIONS_REQUEST_READ_LOGS);
        }

    }

    //this is what gets called when permissions have been granted
    private void fetchData(){
        ArrayList<CallLogItem> data = new ArrayList<>();

        try {
            //setting order desc because otherwise we get the first 50 calls made on a phone
            Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, "date DESC");

            int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = cursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);

            int count = 0;

            while (cursor.moveToNext() && count < MAXIMUM_NUMBER_LOG_REQUESTED) {

                //get type of call
                String typeValStr = DisplayUtil.getTypeOfCall(cursor.getInt(type));

                //get date
                String dateStr = DisplayUtil.getDate(cursor.getString(date));

                //get number
                String num = cursor.getString(number);

                CallLogItem item = new CallLogItem(DisplayUtil.formatNumber(num), cursor.getString(duration), typeValStr, dateStr);
                data.add(item);
                count++;
            }
        } catch (SecurityException e){
            //TODO log somehow we got here without being given permission. wtf?
        }

        this.mData = data;

        if (this.mData != null){
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
                AlertDialog dialog = DisplayUtil.getPermissionDialog(this);
                dialog.show();
            }
        }

    }

}
