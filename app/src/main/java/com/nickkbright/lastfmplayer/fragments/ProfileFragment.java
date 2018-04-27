package com.nickkbright.lastfmplayer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import org.json.JSONObject;

import java.util.ArrayList;


public class ProfileFragment extends Fragment {
    private ArrayList<GridItem> mTopArtists;
    private Button mLogout;
    private ImageView mProfileImage;
    private String profileImageUrl;
    private String artistName;
    private String artistImageUrl;
    private String profileName;
    private String playcount;
    private String subscribercount;
    private TextView mPlaycount;
    private TextView mSubscriberCount;
    private TextView mUsername;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        final GridView artistGridview = (GridView) view.findViewById(R.id.artist_grid_view);
        final GridView albumsGridView = (GridView) view.findViewById(R.id.albums_grid_view);
        mProfileImage = (ImageView)view.findViewById(R.id.profile_image);
        mUsername = (TextView) view.findViewById(R.id.user_profile_name);
        mPlaycount = (TextView) view.findViewById(R.id.playcount);
        mSubscriberCount = (TextView) view.findViewById(R.id.artistcount);


        GridViewAdapter topArtistsAdapter = new GridViewAdapter(getContext(), mTopArtists);

        LfmRequest UserInfoRequest = LfmApi.user().getInfo();
        UserInfoRequest.executeWithListener(new LfmRequest.LfmRequestListener() {
            @Override
            public void onComplete(JSONObject response) {
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

            }

            @Override
            public void onError(LfmError error) {

            }
        });
        LfmParameters pTopArtists = new LfmParameters();
        pTopArtists.put("user", profileName);
        pTopArtists.put("limit", "5");

        LfmRequest TopArtistsRequest = LfmApi.user().getTopArtists(pTopArtists);
        TopArtistsRequest.executeWithListener(new LfmRequest.LfmRequestListener() {
            @Override
            public void onComplete(JSONObject response) {
                GridItem artist;
                for (int i = 0; i <5 ;i++) {
                    artistName = response
                            .optJSONObject("topartists")
                            .optJSONArray("artist")
                            .optJSONObject(i)
                            .optString("name");
                    artistImageUrl = response
                            .optJSONObject("topartists")
                            .optJSONArray("artist")
                            .optJSONObject(i)
                            .optJSONArray("image")
                            .optJSONObject(2)
                            .optString("#text");

                    artist = new GridItem(artistName, artistImageUrl);
                    mTopArtists.add(artist);
                }
                GridViewAdapter topArtistsAdapter = new GridViewAdapter(getContext(), mTopArtists);
                artistGridview.setAdapter(topArtistsAdapter);

            }

            @Override
            public void onError(LfmError error) {

            }
        });

        return view;

    }
}
