package com.ictlao.android.app.partydraw;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ictlao.partydraw.R;

import java.util.ArrayList;
// Custom adapter class
public class CustomAdapter extends BaseAdapter {
    private final Activity mContext;     // set context to get default context
    private ArrayList<Item> mArrayListNames = new ArrayList<>();  // declare Array list Item;

    // create class to hold view variable
    static class ViewHolder{
        public TextView textView;
        public ImageView imageView;
    }
    // call this method to adapt data
    public CustomAdapter (Activity context, ArrayList<Item> names){
        this.mContext = context;
        this.mArrayListNames = names;
    }

    @Override
    public int getCount() {
        return mArrayListNames.size();
    }   // override this method to return int of data size

    // override this method to get the integer index of data
    @Override
    public Object getItem(int position) {
        return mArrayListNames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        Item item = (Item) getItem(position);
        if(rowView == null){
            LayoutInflater inflater = mContext.getLayoutInflater();
            rowView = inflater.inflate(R.layout.custom_list_adapter, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) rowView.findViewById(R.id.textViewList);
            viewHolder.imageView = (ImageView) rowView.findViewById(R.id.imageViewList);
            rowView.setTag(viewHolder);
        }
        ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.textView.setText(item.getNames());
        holder.imageView.setImageResource(item.getImage());

        return rowView;
    }
}
