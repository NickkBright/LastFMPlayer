package com.nickkbright.lastfmplayer.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ag.lfm.LfmError;
import com.ag.lfm.LfmParameters;
import com.ag.lfm.LfmRequest;
import com.ag.lfm.Session;
import com.nickkbright.lastfmplayer.adapters.GridViewAdapter;
import com.nickkbright.lastfmplayer.api.LfmApi;
import com.nickkbright.lastfmplayer.R;
import com.nickkbright.lastfmplayer.models.GridItem;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ArtistInfoActivity extends AppCompatActivity {
    private Intent albumInfoIntent;
    private Intent artistInfoIntent;
    private ArrayList<GridItem> mSimilarArtists = new ArrayList<>();
    private ArrayList<GridItem> mTopAlbums = new ArrayList<>();
    private GridView albumsGridView;
    private GridView similarArtistsGridView;
    private Toolbar mToolbar;
    private String artistName;
    private String ImageURL;
    private String albumImageURL;
    private String artistImageURL;
    private String playcount;
    private String listenersCount;
    private String biography;
    private String albumName;
    private String similarArtistName;
    private String albumListeners;
    private TextView mArtistName;
    private TextView mListeners;
    private TextView mPlaycount;
    private TextView mBiography;
    private ImageView mArtistPhoto;
    private JSONArray albums;
    private JSONArray artists;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_and_album);
        albumsGridView = findViewById(R.id.top_albums);
        similarArtistsGridView = findViewById(R.id.similar_artists);
        mArtistName = findViewById(R.id.artist_info_name);
        mToolbar = findViewById(R.id.info_toolbar);
        mBiography = findViewById(R.id.biography);
        mPlaycount = findViewById(R.id.playcount);
        mListeners = findViewById(R.id.listenerscount);
        mArtistPhoto = findViewById(R.id.info_main_image);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        albumInfoIntent = new Intent(ArtistInfoActivity.this, AlbumInfoActivity.class);
        artistInfoIntent = new Intent(ArtistInfoActivity.this, ArtistInfoActivity.class);

        artistName = getIntent().getStringExtra("EXTRA_ARTIST_NAME");
        mArtistName.setText(artistName);
        getArtistInfo(artistName);
        getArtistTopAlbums();
        getSimilarArtists();
    }

    public void getArtistInfo (String artistName) {
        LfmParameters pArtist = new LfmParameters();
        pArtist.put("artist", artistName);


        LfmRequest TopAlbumsRequest = LfmApi.artist().getInfo(pArtist);
        TopAlbumsRequest.executeWithListener(new LfmRequest.LfmRequestListener() {
            @Override
            public void onComplete (JSONObject response) {
                ImageURL = response
                        .optJSONObject("artist")
                        .optJSONArray("image")
                        .optJSONObject(4)
                        .optString("#text");
                listenersCount = response
                        .optJSONObject("artist")
                        .optJSONObject("stats")
                        .optString("listeners");
                playcount = response
                        .optJSONObject("artist")
                        .optJSONObject("stats")
                        .optString("playcount");
                biography = response
                        .optJSONObject("artist")
                        .optJSONObject("bio")
                        .optString("content");


                mPlaycount.setText(playcount);
                mListeners.setText(listenersCount);
                mBiography.setText(biography);
                Picasso.get().load(ImageURL).into(mArtistPhoto);

            }

            @Override
            public void onError (LfmError error) {
                Log.e("ERROR", error.errorMessage);
            }
        });

    }

    public void getArtistTopAlbums () {
        LfmParameters pArtist = new LfmParameters();
        pArtist.put("artist", artistName);
        pArtist.put("limit", "3");

        LfmRequest TopAlbumsRequest = LfmApi.artist().getTopAlbums(pArtist);
        TopAlbumsRequest.executeWithListener(new LfmRequest.LfmRequestListener() {
            @Override
            public void onComplete (JSONObject response) {
                albums = response.optJSONObject("topalbums").optJSONArray("album");

                if (albums.length() > 0) {
                    for (int i = 0; i < albums.length(); i++) {
                        albumName = albums
                                .optJSONObject(i)
                                .optString("name");
                        albumImageURL = albums
                                .optJSONObject(i)
                                .optJSONArray("image")
                                .optJSONObject(2)
                                .optString("#text");
                        albumListeners = albums
                                .optJSONObject(i)
                                .optString("listeners");


                        mTopAlbums.add(new GridItem(albumName, albumListeners, albumImageURL));

                    }

                    albumsGridView.setAdapter(new GridViewAdapter(getApplicationContext(), mTopAlbums));

                    albumsGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            albumInfoIntent.putExtra("EXTRA_ALBUM_NAME", mTopAlbums.get(position).getName());
                            albumInfoIntent.putExtra("EXTRA_ARTIST_NAME", artistName);
                            startActivity(albumInfoIntent);
                        }
                    });
                } else {
                    albumsGridView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onError (LfmError error) {
                Log.e("ERROR", error.errorMessage);
            }
        });

    }

    public void getSimilarArtists () {
        LfmParameters pTopArtists = new LfmParameters();
        pTopArtists.put("artist", artistName);
        pTopArtists.put("limit", "3");

        LfmRequest SimilarArtistsRequest = LfmApi.artist().getSimilar(pTopArtists);
        SimilarArtistsRequest.executeWithListener(new LfmRequest.LfmRequestListener() {
            @Override
            public void onComplete (JSONObject response) {
                artists = response.optJSONObject("similarartists").optJSONArray("artist");

                if (artists.length() > 0) {
                    for (int i = 0; i < artists.length(); i++) {
                        similarArtistName = artists
                                .optJSONObject(i)
                                .optString("name");
                        artistImageURL = artists
                                .optJSONObject(i)
                                .optJSONArray("image")
                                .optJSONObject(2)
                                .optString("#text");

                        mSimilarArtists.add(new GridItem(similarArtistName, artistImageURL));
                    }

                    similarArtistsGridView.setAdapter(new GridViewAdapter(getApplicationContext(), mSimilarArtists));

                    similarArtistsGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            artistInfoIntent.putExtra("EXTRA_ARTIST_NAME", mSimilarArtists.get(position).getName());
                            startActivity(artistInfoIntent);
                        }
                    });
                } else {
                    similarArtistsGridView.setVisibility(View.INVISIBLE);
                }
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
}
