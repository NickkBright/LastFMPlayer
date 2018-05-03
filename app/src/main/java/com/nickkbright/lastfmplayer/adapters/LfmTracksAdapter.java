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
    private String trackNumber;
    private Context context;

    public LfmTracksAdapter(Context context,List<LfmTrack> trackList) {
        this.trackList = trackList;
        this.context = context;
    }

    @Override
    public LfmTracksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lfm_track_item,parent,false);
        return new LfmTracksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LfmTracksViewHolder holder, int position) {
        trackNumber = Integer.toString(position+1);
        holder.number.setText(trackNumber);
        holder.duration.setText(trackList.get(position).getDuration());
        holder.track.setText(trackList.get(position).getTrack());

        Picasso.get().load(trackList.get(position).getImageURL()).fit().into(holder.album);
    }

    @Override
    public int getItemCount() {
        return trackList.size();
    }

    public class LfmTracksViewHolder extends RecyclerView.ViewHolder {

        public TextView track,duration, number;
        public ImageView album;

        public LfmTracksViewHolder(View view) {
            super(view);

            duration = (TextView) view.findViewById(R.id.duration);
            track = (TextView) view.findViewById(R.id.track_name);
            album = (ImageView)view.findViewById(R.id.album_image);
            number = (TextView)view.findViewById(R.id.track_number);

        }

    }

}

