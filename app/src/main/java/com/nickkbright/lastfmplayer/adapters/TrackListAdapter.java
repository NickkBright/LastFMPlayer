package com.nickkbright.lastfmplayer.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nickkbright.lastfmplayer.R;
import com.nickkbright.lastfmplayer.models.Audio;

import java.util.List;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.ViewHolder> {
    List<Audio> mTrackList;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTitle;
        TextView mArtist;

        ViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mArtist = (TextView) itemView.findViewById(R.id.artist);
        }
    }
    public TrackListAdapter(List<Audio> tracklist) {
        mTrackList = tracklist;
    }


    @Override
    public TrackListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_item, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;

    }

    @Override
    public void onBindViewHolder(TrackListAdapter.ViewHolder holder, int position) {
        holder.mTitle.setText(mTrackList.get(position).getTitle());
        holder.mArtist.setText(mTrackList.get(position).getArtist());
    }

    @Override
    public int getItemCount() {
        return mTrackList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
