package com.ictlao.android.app.partydraw;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.ictlao.partydraw.R;

import java.util.ArrayList;
import java.util.List;

class InvitationFilterAdapter extends BaseAdapter implements Filterable {
    private List<ItemFilterInvitation> mOriginalDataList;
    private List<ItemFilterInvitation> mFilterDataList;
    private LayoutInflater mLayoutInflater;
    private ItemFilter mItemFilter = new ItemFilter();

    public InvitationFilterAdapter(Context context, ArrayList<ItemFilterInvitation> data){
        this.mOriginalDataList = new ArrayList<>(data);
        this.mFilterDataList = new ArrayList<>(data);
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mFilterDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFilterDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View mView = convertView;
        ItemFilterInvitation mItemFilterInvitation = (ItemFilterInvitation) getItem(position);
        HolderView mHolderView;
        if(mView == null){
            mView = mLayoutInflater.inflate(R.layout.list_invitation_adapter, null);
            mHolderView = new HolderView();
            mHolderView.mTextViewDisplayName = (TextView) mView.findViewById(R.id.textView_list);
            mHolderView.mImageViewDisplayBell = (ImageView) mView.findViewById(R.id.imageView_List);
            mView.setTag(mHolderView);
        }

        mHolderView = (HolderView) mView.getTag();

        mHolderView.mTextViewDisplayName.setText(mItemFilterInvitation.getName());
        mHolderView.mImageViewDisplayBell.setImageResource(mItemFilterInvitation.getImageResource());

        return mView;
    }

    @Override
    public Filter getFilter() {
        return mItemFilter;
    }

    private class ItemFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<ItemFilterInvitation> list = new ArrayList<>(mOriginalDataList);

            int count = list.size();

            final List<ItemFilterInvitation> mList = new ArrayList<>(count);

            String filterableString;
            int filterableInteger;

            for(int i = 0; i < count; i++){
                filterableString = list.get(i).getName();
                filterableInteger = list.get(i).getImageResource();
                if(filterableString.toLowerCase().contains(filterString)){
                    mList.add(new ItemFilterInvitation(filterableString,filterableInteger));
                }
            }

            results.values = mList;
            results.count = mList.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFilterDataList = (ArrayList<ItemFilterInvitation>) results.values;
            if(results.count > 0) {
                notifyDataSetChanged();
            }else{
                notifyDataSetInvalidated();
            }
        }
    }

    public static class HolderView{
        TextView mTextViewDisplayName;
        ImageView mImageViewDisplayBell;
    }
}
