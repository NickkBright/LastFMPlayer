package com.nickkbright.lastfmplayer.adapters;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nickkbright.lastfmplayer.R;
import com.nickkbright.lastfmplayer.models.Album;
import com.nickkbright.lastfmplayer.models.Song;

import java.util.List;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SimpleViewHolder> {

    private final SongSelectedListener mSongSelectedListener;
    private List<Song> mSongs;
    private Album mAlbum;

    public SongsAdapter(Fragment fragment, Album album) {
        mAlbum = album;
        mSongs = mAlbum.songs;
        mSongSelectedListener = (SongSelectedListener) fragment;
    }

    public void swapSongs(Album album) {
        mAlbum = album;
        mSongs = mAlbum.songs;
        notifyDataSetChanged();
    }

    @Override
    @NonNull
    public SimpleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_item, parent, false);

        return new SimpleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleViewHolder holder, int position) {
        Song song = mSongs.get(holder.getAdapterPosition());
        String songTitle = song.title;

        holder.title.setText(songTitle);
        holder.number.setText(Song.formatTrack(song.trackNumber));
        holder.duration.setText(Song.formatDuration(song.duration));
    }

    @Override
    public int getItemCount() {

        return mSongs.size();
    }

    public interface SongSelectedListener {
        void onSongSelected(Song song, Album album);
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView number, title, duration;

        SimpleViewHolder(View itemView) {
            super(itemView);

            number = itemView.findViewById(R.id.track);
            title = itemView.findViewById(R.id.title);
            duration = itemView.findViewById(R.id.duration);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Song song = mSongs.get(getAdapterPosition());
            Log.d("HERE", song.title);
            mSongSelectedListener.onSongSelected(song, mAlbum);
        }
    }
}