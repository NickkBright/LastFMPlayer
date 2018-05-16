package com.nickkbright.lastfmplayer.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;


import com.ag.lfm.LfmError;
import com.ag.lfm.LfmParameters;
import com.ag.lfm.LfmRequest;
import com.ag.lfm.Session;
import com.ag.lfm.api.LfmApi;
import com.nickkbright.lastfmplayer.R;
import com.nickkbright.lastfmplayer.adapters.GridViewAdapter;
import com.nickkbright.lastfmplayer.models.GridItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FullGridViewActivity extends AppCompatActivity {
    private Intent albumInfoIntent;
    private Intent artistInfoIntent;
    private Toolbar mToolbar;
    private TextView mToolbarTitle;
    private String mItemType;
    private String artistName;
    private String artistPlaycount;
    private GridView mGridView;
    private ArrayList<GridItem> mTopArtists = new ArrayList<>();
    private ArrayList<GridItem> mTopAlbums = new ArrayList<>();
    private JSONArray artists;
    private JSONArray albums;
    private String itemName;
    private String ImageUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_grid_view);
        mToolbar = findViewById(R.id.toolbar);
        mToolbarTitle = findViewById(R.id.toolbar_title);
        mGridView = findViewById(R.id.item_grid_view);
        mGridView.setVerticalScrollBarEnabled(true);
        setSupportActionBar(mToolbar);
        albumInfoIntent = new Intent(this, AlbumInfoActivity.class);
        artistInfoIntent = new Intent(this, ArtistInfoActivity.class);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mItemType = getIntent().getStringExtra("EXTRA_ITEM_TYPE");
        if (mItemType.equals("artist")) {
            mToolbarTitle.setText("Top Artists");
            getUserTopArtists();
        }
        if (mItemType.equals("albums")) {
            mToolbarTitle.setText("Top Albums");
            getUserTopAlbums();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void getUserTopArtists () {
        LfmParameters pTopArtists = new LfmParameters();
        pTopArtists.put("user", Session.username);

        LfmRequest TopArtistsRequest = LfmApi.user().getTopArtists(pTopArtists);
        TopArtistsRequest.executeWithListener(new LfmRequest.LfmRequestListener() {
            @Override
            public void onComplete (JSONObject response) {
                artists = response.optJSONObject("topartists").optJSONArray("artist");

                if (artists.length() > 0) {
                    for (int i = 0; i < artists.length(); i++) {
                        itemName = artists
                                .optJSONObject(i)
                                .optString("name");
                        ImageUrl = artists
                                .optJSONObject(i)
                                .optJSONArray("image")
                                .optJSONObject(2)
                                .optString("#text");
                        artistPlaycount = artists
                                .optJSONObject(i)
                                .optString("playcount");

                        mTopArtists.add(new GridItem(itemName, artistPlaycount, ImageUrl));
                    }

                    mGridView.setAdapter(new GridViewAdapter(getApplicationContext(), mTopArtists));
                    mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            artistInfoIntent.putExtra("EXTRA_ARTIST_NAME", mTopArtists.get(position).getName());
                            startActivity(artistInfoIntent);
                        }
                    });
                } else {
                    mGridView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onError (LfmError error) {
                Log.e("ERROR", error.errorMessage);
            }
        });
    }

    public void getUserTopAlbums () {
        LfmParameters pTopAlbums = new LfmParameters();
        pTopAlbums.put("user", Session.username);

        LfmRequest TopAlbumsRequest = LfmApi.user().getTopAlbums(pTopAlbums);
        TopAlbumsRequest.executeWithListener(new LfmRequest.LfmRequestListener() {
            @Override
            public void onComplete (JSONObject response) {
                albums = response.optJSONObject("topalbums").optJSONArray("album");

                if (albums.length() > 0) {
                    for (int i = 0; i < albums.length(); i++) {
                        artistName = albums
                                .optJSONObject(i)
                                .optJSONObject("artist")
                                .optString("name");
                        itemName = albums
                                .optJSONObject(i)
                                .optString("name");
                        ImageUrl = albums
                                .optJSONObject(i)
                                .optJSONArray("image")
                                .optJSONObject(2)
                                .optString("#text");
                        artistPlaycount = albums
                                .optJSONObject(i)
                                .optString("playcount");


                        mTopAlbums.add(new GridItem(itemName, artistPlaycount, ImageUrl, artistName));

                    }

                    mGridView.setAdapter(new GridViewAdapter(getApplicationContext(), mTopAlbums));

                    mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            albumInfoIntent.putExtra("EXTRA_ALBUM_NAME", mTopAlbums.get(position).getName());
                            albumInfoIntent.putExtra("EXTRA_ARTIST_NAME", mTopAlbums.get(position).getArtistName());
                            startActivity(albumInfoIntent);
                        }
                    });
                } else {
                    mGridView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onError (LfmError error) {
                Log.e("ERROR", error.errorMessage);
            }
        });

    }
}
