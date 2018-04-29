package com.nickkbright.lastfmplayer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ag.lfm.Lfm;
import com.ag.lfm.LfmError;
import com.ag.lfm.LfmParameters;
import com.ag.lfm.LfmRequest;
import com.ag.lfm.api.LfmApi;
import com.nickkbright.lastfmplayer.R;
import com.nickkbright.lastfmplayer.activities.LoginActivity;
import com.nickkbright.lastfmplayer.adapters.GridViewAdapter;
import com.nickkbright.lastfmplayer.models.GridItem;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class ProfileFragment extends Fragment {
    private ArrayList<GridItem> mTopArtists = new ArrayList<>();
    private ArrayList<GridItem> mTopAlbums = new ArrayList<>();
    private Button mLogout;
    private ImageView mProfileImage;
    private String profileImageUrl;
    private String itemName;
    private String ImageUrl;
    private String artistPlaycount;
    private String profileName;
    private String playcount;
    private String subscribercount;
    private TextView mPlaycount;
    private TextView mSubscriberCount;
    private TextView mUsername;
    private JSONArray artists;
    private JSONArray albums;
    private GridView artistGridview;
    private GridView albumsGridView;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        artistGridview = (GridView) view.findViewById(R.id.artist_grid_view);
        albumsGridView = (GridView) view.findViewById(R.id.albums_grid_view);
        artistGridview.setVerticalScrollBarEnabled(false);
        albumsGridView.setVerticalScrollBarEnabled(false);
        mProfileImage = (ImageView) view.findViewById(R.id.profile_image);
        mUsername = (TextView) view.findViewById(R.id.user_profile_name);
        mPlaycount = (TextView) view.findViewById(R.id.playcount);
        mSubscriberCount = (TextView) view.findViewById(R.id.artistcount);

        getUserInfo();

        return view;
    }

    public void getUserInfo () {
        LfmRequest UserInfoRequest = LfmApi.user().getInfo();

        UserInfoRequest.executeWithListener(new LfmRequest.LfmRequestListener() {
            @Override
            public void onComplete (JSONObject response) {
                profileImageUrl = response
                        .optJSONObject("user")
                        .optJSONArray("image")
                        .optJSONObject(2)
                        .optString("#text");
                profileName = response
                        .optJSONObject("user")
                        .optString("name");
                playcount = response
                        .optJSONObject("user")
                        .optString("playcount");
                subscribercount = response
                        .optJSONObject("user")
                        .optString("subscriber");

                Picasso.get().load(profileImageUrl).into(mProfileImage);
                mUsername.setText(profileName);
                mPlaycount.setText(playcount);
                mSubscriberCount.setText(subscribercount);

                Log.d("USER_NAME", profileName);
                getUserTopArtists();
                getUserTopAlbums();
            }

            @Override
            public void onError (LfmError error) {
                Log.e("ERROR", error.errorMessage);
            }
        });
    }

    public void getUserTopArtists () {
        LfmParameters pTopArtists = new LfmParameters();
        pTopArtists.put("user", profileName);
        pTopArtists.put("limit", "6");

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

                    artistGridview.setAdapter(new GridViewAdapter(getContext(), mTopArtists));
                } else {
                    artistGridview.setVisibility(View.INVISIBLE);
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
        pTopAlbums.put("user", profileName);
        pTopAlbums.put("limit", "3");

        LfmRequest TopAlbumsRequest = LfmApi.user().getTopAlbums(pTopAlbums);
        TopAlbumsRequest.executeWithListener(new LfmRequest.LfmRequestListener() {
            @Override
            public void onComplete (JSONObject response) {
                albums = response.optJSONObject("topalbums").optJSONArray("album");

                if (albums.length() > 0) {
                    for (int i = 0; i < albums.length(); i++) {
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

                        mTopAlbums.add(new GridItem(itemName, artistPlaycount, ImageUrl));
                    }

                    albumsGridView.setAdapter(new GridViewAdapter(getContext(), mTopAlbums));
                } else {
                    artistGridview.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onError (LfmError error) {
                Log.e("ERROR", error.errorMessage);
            }
        });

    }
}
