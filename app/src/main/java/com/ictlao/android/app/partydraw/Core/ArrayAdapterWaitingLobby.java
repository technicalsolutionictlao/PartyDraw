package com.ictlao.android.app.partydraw.Core;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ictlao.android.app.partydraw.Core.Models.LobbyItems;
import com.ictlao.partydraw.R;

import java.util.ArrayList;

public class ArrayAdapterWaitingLobby extends BaseAdapter {
    // ArrayList variable
    private final ArrayList<LobbyItems> mArrayListLobbyItems;
    // Activity context
    private final Activity mContext;
    // Adapter constructor
    public ArrayAdapterWaitingLobby(Activity context, ArrayList<LobbyItems> ListLobbyItems ){
        // get list item from activity
        this.mArrayListLobbyItems = ListLobbyItems;
        // get context from activity
        this.mContext = context;
    }
    // HoldView Class
    public static class LobbyHoldView{
        public TextView mTextViewPlayerName;
        public TextView mTextViewStatus;
    }
    // return size of list
    @Override
    public int getCount() {
        return mArrayListLobbyItems.size();
    }
    // return item from position index
    @Override
    public Object getItem(int position) {
        return mArrayListLobbyItems.get(position);
    }
    // set default position to 0
    @Override
    public long getItemId(int position) {
        return 0;
    }
    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get convertView to view
        View mView = convertView;
        // get item from position index
        LobbyItems lobbyItems = (LobbyItems) getItem(position);
        // initialize view if equal to null
        if(mView == null){
            // get layoutInflater from activity
            LayoutInflater layoutInflater = mContext.getLayoutInflater();
            // get layout from resource
            mView = layoutInflater.inflate(R.layout.player_name_list_waiting_layout, null);
            // use holderView
            LobbyHoldView lobbyHoldView = new LobbyHoldView();
            // initialize UI
            lobbyHoldView.mTextViewPlayerName = (TextView) mView.findViewById(R.id.textView_PlayerName);
            lobbyHoldView.mTextViewStatus = (TextView) mView.findViewById(R.id.textViewStatus);
            // set flag to view
            mView.setTag(lobbyHoldView);
        }
        // get flag from view
        LobbyHoldView lobbyHoldView = (LobbyHoldView) mView.getTag();
        // set item from position index to UI
        lobbyHoldView.mTextViewPlayerName.setText(lobbyItems.getPlayerName());
        lobbyHoldView.mTextViewStatus.setText(lobbyItems.getStatus());
        // return view to display to ListView in the class that use it
        return mView;
    }
}
