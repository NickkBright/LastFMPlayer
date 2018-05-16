package com.nickkbright.lastfmplayer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nickkbright.lastfmplayer.R;
import com.nickkbright.lastfmplayer.models.GridItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class GridViewAdapter extends BaseAdapter {
    private ArrayList<GridItem> mGridItemData;
    private LayoutInflater mInflaterCatalogListItems;

    public GridViewAdapter (Context context, ArrayList<GridItem> gridItemData) {
        mGridItemData = gridItemData;
        mInflaterCatalogListItems = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount () {
        return mGridItemData.size();
    }

    @Override
    public Object getItem (int position) {
        return mGridItemData.get(position);
    }

    @Override
    public long getItemId (int position) {
        return position;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflaterCatalogListItems.inflate(R.layout.grid_item, null);
            holder.sName = convertView.findViewById(R.id.item_name);
            holder.sPlaycount = convertView.findViewById(R.id.item_playcount);
            holder.sImageURL = convertView.findViewById(R.id.item_image);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (mGridItemData.get(position) != null) {
            holder.sName.setText(mGridItemData.get(position).getName());
            holder.sPlaycount.setText(mGridItemData.get(position).getPlaycount()+" plays");
            Picasso.get().load(mGridItemData.get(position).getImageURL()).into(holder.sImageURL);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView sName;
        TextView sPlaycount;
        ImageView sImageURL;
    }
}