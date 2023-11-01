package com.ictlao.android.app.partydraw;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ictlao.partydraw.R;

import java.util.ArrayList;

class ArrayAdapterInfoJokerDraw extends BaseAdapter {
    // list information group
    private ArrayList<InfoJokerDrawItems> mArrayListInfo;
    // Activity context
    private Activity mContext;
    // holder UI
    static class HolderUI {
        public TextView mPlayerName;
        public TextView mStatus;
    }
    // adapter constructor
    public ArrayAdapterInfoJokerDraw(Activity context, ArrayList<InfoJokerDrawItems> arrayList){
        // get context
        this.mContext = context;
        // get list item
        this.mArrayListInfo = arrayList;
    }
    @Override
    public int getCount() {
        // return size of the list
        return mArrayListInfo.size();
    }
    @Override
    public Object getItem(int position) {
        // get each position from the list
        return mArrayListInfo.get(position);
    }
    @Override
    public long getItemId(int position) {
        // return default position
        return 0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // add convertView to view
        View view = convertView;
        // get each item by index position
        InfoJokerDrawItems items = (InfoJokerDrawItems) getItem(position);
        // initialize view if equal to null
        if(view == null){
            // get layoutInflater
            LayoutInflater inflater = mContext.getLayoutInflater();
            // get Layout from resource
            view = inflater.inflate(R.layout.info_joker_draw_list_layout, null);
            // use HolderUI class
            HolderUI holderUI = new HolderUI();
            // Initialize UI in holderUI class
            holderUI.mPlayerName = view.findViewById(R.id.PlayerName);
            holderUI.mStatus = view.findViewById(R.id.PlayerStatus);
            // set Flag to view
            view.setTag(holderUI);
        }
        // get Flag from view to holderUI
        HolderUI holderUI = (HolderUI) view.getTag();
        // get name from item to display
        holderUI.mPlayerName.setText(items.getPlayerName());
        // get status from item to display
        holderUI.mStatus.setText(items.getStatusTurn());
        // return view to display on ListView in the class that used it.
        return view;
    }
}
