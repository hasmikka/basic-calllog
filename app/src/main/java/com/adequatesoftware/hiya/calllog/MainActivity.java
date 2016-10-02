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
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.adequatesoftware.hiya.calllog.datamodel.CallLogItem;
import com.adequatesoftware.hiya.calllog.telephony.Events;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static int PERMISSIONS_REQUEST_READ_LOGS = 22;
    private static int PERMISSIONS_REQUEST_PHONE_STATE = 21;
    private static int MAXIMUM_NUMBER_LOG_REQUESTED = 50;
    private ArrayList<CallLogItem> mData;
    private CallLogAdapter mAdapter;
    private RecyclerView mCallListRecycleView;
    private SwipeRefreshLayout mSwipeLayout;
    private TextView mWarningText;
    private boolean mCanCheckForCalls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWarningText = (TextView) findViewById(R.id.main_warning);

        mCallListRecycleView = (RecyclerView) findViewById(R.id.main_list);
        mCallListRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mCallListRecycleView.setHasFixedSize(true);

        mAdapter = new CallLogAdapter(this);
        mCallListRecycleView.setAdapter(mAdapter);

        setupSwipeRefresh();

        fetchCallLogData();


    }

    private void setupSwipeRefresh() {
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.main_swipe);

        if (mSwipeLayout != null) {
            mSwipeLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark);
        }

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //to prevent swipe
                mSwipeLayout.setEnabled(false);
                mWarningText.setText(R.string.please_wait);
                mWarningText.setVisibility(View.VISIBLE);
                fetchCallLogData();
            }

        });
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private void displayData() {
        if (mData.isEmpty()) {
            displayEmptyState();
        } else {
            mWarningText.setVisibility(View.GONE);
        }

        mAdapter.setData(mData);
        mSwipeLayout.setRefreshing(false);
        mSwipeLayout.setEnabled(true);
    }

    private void displayEmptyState() {
        mWarningText.setText(R.string.no_calls);

    }

    private void fetchCallLogData() {

        //check permission
        int callLogPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG);

        if (callLogPermissionCheck == PackageManager.PERMISSION_GRANTED) {
            fetchData(null);
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_CALL_LOG}, PERMISSIONS_REQUEST_READ_LOGS);
        }
    }

    //this is what gets called when permissions have been granted
    private void fetchData(Events.PhoneCallStateChangeEvent event) {
        ArrayList<CallLogItem> data = new ArrayList<>();

        try {
            //setting order desc because otherwise we get the first 50 calls made on a phone
            Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, "date DESC");

            int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = cursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
            int photoURI = cursor.getColumnIndex(CallLog.Calls.CACHED_PHOTO_URI);

            int count = 0;

            while (cursor.moveToNext() && count < MAXIMUM_NUMBER_LOG_REQUESTED) {

                //get type of call
                String typeValStr = DisplayUtil.getTypeOfCall(cursor.getInt(type));

                //get date
                String dateStr = DisplayUtil.getDate(cursor.getString(date));

                //get number
                String numVal = cursor.getString(number);
                String num = numVal.isEmpty() ? "Unknown" : numVal;

                //TODO add pictures
                CallLogItem item = new CallLogItem(DisplayUtil.formatNumber(num), cursor.getString(duration), typeValStr, dateStr);
                item.setPhotoURI(cursor.getString(photoURI));

                data.add(item);
                count++;
            }
        } catch (SecurityException e) {
            Log.e("CallLog", "Unable to fetch call log data. Have permissions been granted?");
        }

        this.mData = data;

        displayData();
    }


    ///// CallBacks ////

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (PERMISSIONS_REQUEST_READ_LOGS == requestCode) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                fetchData(null);

            } else {
                AlertDialog dialog = DisplayUtil.getPermissionReadLogDialog(this);
                dialog.show();
            }
        } else if (PERMISSIONS_REQUEST_PHONE_STATE == requestCode){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mCanCheckForCalls = true;
            } else {
                mCanCheckForCalls = false;
            }
        }
    }

    @Subscribe
    public void onPhoneCallStateChanged(Events.PhoneCallStateChangeEvent event) {
        fetchData(event);
    }

}
