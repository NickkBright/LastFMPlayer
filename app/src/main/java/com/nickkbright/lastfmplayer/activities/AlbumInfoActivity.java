package com.nickkbright.lastfmplayer.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ag.lfm.LfmError;
import com.ag.lfm.LfmParameters;
import com.ag.lfm.LfmRequest;
import com.ag.lfm.api.LfmApi;
import com.nickkbright.lastfmplayer.R;
import com.nickkbright.lastfmplayer.adapters.GridViewAdapter;
import com.nickkbright.lastfmplayer.adapters.LfmTracksAdapter;
import com.nickkbright.lastfmplayer.models.GridItem;
import com.nickkbright.lastfmplayer.models.LfmTrack;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class AlbumInfoActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<LfmTrack> mTracklist = new ArrayList<>();
    private TextView mArtistName;
    private TextView mTitle;
    private TextView mPlaycount;
    private TextView mListeners;
    private ImageView mArtwork;
    private String artistName;
    private String albumName;
    private String listenersCount;
    private String albumPlaycount;
    private String trackName;
    private String trackDuration;
    private String ImageURL;
    private JSONArray tracklist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_info);


        mToolbar = (Toolbar) findViewById(R.id.info_toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_tracklist);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mArtwork = (ImageView) findViewById(R.id.info_main_image);
        mArtistName = (TextView) findViewById(R.id.album_artist_name);
        mTitle = (TextView) findViewById(R.id.album_name);
        mPlaycount = (TextView) findViewById(R.id.playcount_info_album);
        mListeners = (TextView) findViewById(R.id.listenerscount_album);
        albumName = getIntent().getStringExtra("EXTRA_ALBUM_NAME");
        Log.d ("album name", albumName);
        artistName = getIntent().getStringExtra("EXTRA_ARTIST_NAME");
        Log.d ("artist name", artistName);
        mTitle.setText(albumName);
        mArtistName.setText(artistName);

        getAlbumTracks(artistName, albumName);
    }



    public void getAlbumTracks (String artistName, String albumName) {
        LfmParameters pAlbum = new LfmParameters();
        pAlbum.put("artist", artistName);
        pAlbum.put("album", albumName);


        LfmRequest TopAlbumsRequest = LfmApi.album().getInfo(pAlbum);
        TopAlbumsRequest.executeWithListener(new LfmRequest.LfmRequestListener() {
            @Override
            public void onComplete (JSONObject response) {
                tracklist = response.optJSONObject("album").optJSONObject("tracks").optJSONArray("track");
                ImageURL = response
                        .optJSONObject("album")
                        .optJSONArray("image")
                        .optJSONObject(2)
                        .optString("#text");
                listenersCount = response
                        .optJSONObject("album")
                        .optString("listeners");
                albumPlaycount = response
                        .optJSONObject("album")
                        .optString("playcount");
                for (int i = 0; i < tracklist.length(); i++) {
                    trackName = tracklist
                            .optJSONObject(i)
                            .optString("name");
                    trackDuration = tracklist
                            .optJSONObject(i)
                            .optString("duration");
                    trackDuration = timeConversion(trackDuration);
                    mTracklist.add(new LfmTrack(trackName, trackDuration, ImageURL));
                }

                mPlaycount.setText(albumPlaycount);
                mListeners.setText(listenersCount);
                Picasso.get().load(ImageURL).into(mArtwork);

                mRecyclerView.setAdapter(new LfmTracksAdapter(getApplicationContext(), mTracklist));
            }

            @Override
            public void onError (LfmError error) {
                Log.e("ERROR", error.errorMessage);
            }
        });

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private static String timeConversion(String totalSeconds) {

        final int SECONDS_IN_A_MINUTE = 60;
        int secondsNum = Integer.parseInt(totalSeconds);
        int seconds = secondsNum % SECONDS_IN_A_MINUTE;
        int minutes = secondsNum / SECONDS_IN_A_MINUTE;
        if (seconds < 10)
            return minutes + ":0" + seconds;
        else
            return minutes + ":" + seconds;
    }
}
