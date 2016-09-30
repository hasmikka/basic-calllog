package com.adequatesoftware.hiya.calllog;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adequatesoftware.hiya.calllog.datamodel.CallLogItem;

import java.util.ArrayList;

public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.CallLogViewHolder> {
    private ArrayList<CallLogItem> data;

    public CallLogAdapter(ArrayList<CallLogItem> data){
        this.data = data;

    }

    @Override
    public CallLogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        // Inflate the custom layout
        View logView = LayoutInflater.from(context).inflate(R.layout.call_log_row, parent, false);

        // Return a new holder instance
        CallLogViewHolder callLogViewHolder = new CallLogViewHolder(logView);
        return callLogViewHolder;
    }

    @Override
    public void onBindViewHolder(CallLogViewHolder holder, int position) {
        if (data != null) {
            CallLogItem item = data.get(position);

            holder.number.setText(item.getPhoneNumber());
            holder.type.setText(item.getCallType());
            holder.date.setText(item.getTime().toString());
        }
    }

    @Override
    public int getItemCount() {
        return (null != data ? data.size() : 0);
    }


    ///// View Holder ////

    //used to cache views for faster access time
    public static class CallLogViewHolder extends RecyclerView.ViewHolder {

        public TextView number;
        public TextView type;
        public TextView date;


        public CallLogViewHolder(View itemView) {
            super(itemView);

            number = (TextView) itemView.findViewById(R.id.call_log_row_number);
            type = (TextView) itemView.findViewById(R.id.call_log_row_type);
            date = (TextView) itemView.findViewById(R.id.call_log_row_date);
        }
    }
}
