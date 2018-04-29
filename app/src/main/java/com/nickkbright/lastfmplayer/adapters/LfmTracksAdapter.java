package com.nickkbright.lastfmplayer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nickkbright.lastfmplayer.R;
import com.nickkbright.lastfmplayer.models.LfmTrack;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LfmTracksAdapter extends RecyclerView.Adapter<LfmTracksAdapter.LfmTracksViewHolder> {


    private List<LfmTrack> trackList;
    private Context context;

    public LfmTracksAdapter(Context context,List<LfmTrack> trackList) {
        this.trackList = trackList;
        this.context = context;
    }

    @Override
    public LfmTracksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lfm_track_item,parent,false);
        return new LfmTracksViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LfmTracksViewHolder holder, int position) {
        holder.artist.setText(trackList.get(position).getArtist());
        holder.track.setText(trackList.get(position).getTrack());

        Picasso.get().load(trackList.get(position).getImageURL()).fit().into(holder.album);
    }

    @Override
    public int getItemCount() {
        return trackList.size();
    }

    public class LfmTracksViewHolder extends RecyclerView.ViewHolder {

        public TextView artist,track;
        public ImageView album;

        public LfmTracksViewHolder(View itemView) {
            super(itemView);

            artist = (TextView) itemView.findViewById(R.id.artist);
            track = (TextView) itemView.findViewById(R.id.track);
            album = (ImageView)itemView.findViewById(R.id.album_image);

        }

    }

}

