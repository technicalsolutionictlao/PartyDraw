package com.ictlao.android.app.partydraw.Core;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ictlao.android.app.partydraw.Core.Models.TwoItem;
import com.ictlao.partydraw.R;

import java.util.ArrayList;

// Custom adapter for swap game
public class CustomAdapterSwapGame extends BaseAdapter {
    private ArrayList<TwoItem> mArrayListTwoItems;
    private Activity mContext;
    // create class ViewHolder to hold  variable view
    static class ViewHolder{
        public TextView mTextViewLeft, mTextViewRight;
        public ImageView mImageView;
    }


    public CustomAdapterSwapGame (Activity context, ArrayList<TwoItem> arrayList){
        this.mArrayListTwoItems = arrayList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mArrayListTwoItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrayListTwoItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        TwoItem twoItem = (TwoItem) getItem(position);
        if(rowView == null){
            LayoutInflater layoutInflater = mContext.getLayoutInflater();
            rowView = layoutInflater.inflate(R.layout.give_present_layout, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.mTextViewLeft = (TextView) rowView.findViewById(R.id.Tleft);
            viewHolder.mTextViewRight = (TextView) rowView.findViewById(R.id.Tright);
            viewHolder.mImageView = (ImageView) rowView.findViewById(R.id.imageCenter);
            rowView.setTag(viewHolder);
        }
        ViewHolder viewHolder = (ViewHolder) rowView.getTag();
        viewHolder.mTextViewLeft.setText(twoItem.getGiveName());
        viewHolder.mTextViewRight.setText(twoItem.getReceiveName());
        viewHolder.mImageView.setImageResource(twoItem.getImageResId());
        return rowView;
    }
}
