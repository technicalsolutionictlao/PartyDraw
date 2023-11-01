package com.ictlao.android.app.partydraw;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ictlao.partydraw.R;

import java.util.ArrayList;

class InvitationArrayAdapter extends BaseAdapter {

    private ArrayList<InviteMessageItems> mArrayListInviteMessageItems;
    private Activity mContext;

    public InvitationArrayAdapter(Activity context, ArrayList<InviteMessageItems> arrayList){
        this.mContext = context;
        this.mArrayListInviteMessageItems = arrayList;
    }

    public class InviteMessageHolderView{
        public TextView mInviteName;
        public TextView mInviteTime;
    }

    @Override
    public int getCount() {
        return mArrayListInviteMessageItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrayListInviteMessageItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View mConvertView = convertView;
        InviteMessageItems inviteMessageItems = (InviteMessageItems) getItem(position);
        if(mConvertView == null){
            LayoutInflater layoutInflater = mContext.getLayoutInflater();
            mConvertView = layoutInflater.inflate(R.layout.invited_message_list_layout, null);
            InviteMessageHolderView inviteMessageHolderView = new InviteMessageHolderView();
            inviteMessageHolderView.mInviteName = mConvertView.findViewById(R.id.message_name);
            inviteMessageHolderView.mInviteTime = mConvertView.findViewById(R.id.message_time);
            mConvertView.setTag(inviteMessageHolderView);
        }
        InviteMessageHolderView inviteMessageHolderView = (InviteMessageHolderView) mConvertView.getTag();
        inviteMessageHolderView.mInviteName.setText(inviteMessageItems.getInviteName());
        inviteMessageHolderView.mInviteTime.setText(inviteMessageItems.getInviteTime());
        return mConvertView;
    }
}
