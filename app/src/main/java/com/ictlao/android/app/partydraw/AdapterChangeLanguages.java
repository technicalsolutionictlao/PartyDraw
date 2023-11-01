package com.ictlao.android.app.partydraw;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ictlao.partydraw.R;

class AdapterChangeLanguages extends BaseAdapter {

    private Context mContext = null;
    private String[] mCountryNames = null;
    private int[] mCountryFlags = null;
    private LayoutInflater mInflater = null;

    public AdapterChangeLanguages(Context context, int[] countryFlags, String[] countryNames)
    {
        this.mContext = context;
        this.mCountryFlags = countryFlags;
        this.mCountryNames = countryNames;
        mInflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return mCountryFlags.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = mInflater.inflate(R.layout.language_adater_layout, null);
        ImageView flag = (ImageView) view.findViewById(R.id.flagImageView);
        TextView country = (TextView) view.findViewById(R.id.CountryNameTextView);
        flag.setImageResource(mCountryFlags[i]);
        country.setText(mCountryNames[i]);
        return view;
    }
}
