package com.nickkbright.lastfmplayer.adapters;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nickkbright.lastfmplayer.R;
import com.nickkbright.lastfmplayer.loaders.ImageLoadTask;
import com.nickkbright.lastfmplayer.models.RecentTrack;

import java.util.List;

public class RecentTracksAdapter extends RecyclerView.Adapter<RecentTracksAdapter.ViewHolder> {
    private List<RecentTrack> mTrackList;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTrackInfo;
        TextView mDate;
        ImageView mImageURL;

        ViewHolder(View itemView) {
            super(itemView);
            mTrackInfo = itemView.findViewById(R.id.track_info);
            mDate = itemView.findViewById(R.id.date);
            mImageURL = itemView.findViewById(R.id.album_image);
        }
    }
    public RecentTracksAdapter(List<RecentTrack> tracklist) {
        mTrackList = tracklist;
    }

    @Override
    public RecentTracksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_track, parent, false);
        RecentTracksAdapter.ViewHolder holder = new RecentTracksAdapter.ViewHolder(v);
        return holder;

    }

    @Override
    public void onBindViewHolder(RecentTracksAdapter.ViewHolder holder, int position) {
        holder.mTrackInfo.setText(mTrackList.get(position).getTrackInfo());
        holder.mDate.setText(mTrackList.get(position).getDate());
        new ImageLoadTask(mTrackList.get(position).getImageURL(), holder.mImageURL).execute();
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
