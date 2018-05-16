package com.nickkbright.lastfmplayer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ag.lfm.Lfm;
import com.ag.lfm.LfmError;
import com.ag.lfm.LfmParameters;
import com.ag.lfm.LfmRequest;
import com.ag.lfm.Session;
import com.ag.lfm.api.LfmApi;
import com.nickkbright.lastfmplayer.MainActivity;
import com.nickkbright.lastfmplayer.R;
import com.nickkbright.lastfmplayer.activities.AlbumInfoActivity;
import com.nickkbright.lastfmplayer.activities.ArtistInfoActivity;
import com.nickkbright.lastfmplayer.activities.FullGridViewActivity;
import com.nickkbright.lastfmplayer.activities.LoginActivity;
import com.nickkbright.lastfmplayer.adapters.GridViewAdapter;
import com.nickkbright.lastfmplayer.adapters.RecentTracksAdapter;
import com.nickkbright.lastfmplayer.loaders.ImageLoadTask;
import com.nickkbright.lastfmplayer.models.GridItem;
import com.nickkbright.lastfmplayer.models.RecentTrack;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class ProfileFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private ArrayList<GridItem> mTopArtists = new ArrayList<>();
    private ArrayList<GridItem> mTopAlbums = new ArrayList<>();
    private ArrayList<RecentTrack> mRecentTracks = new ArrayList<>();
    private ImageView mProfileImage;
    private String trackAlbumCoverURL;
    private String trackName;
    private String trackArtist;
    private String trackInfo;
    private String trackDate;
    private String trackImageURL;
    private String profileImageUrl;
    private String itemName;
    private String ImageUrl;
    private String artistPlaycount;
    private String profileName;
    private String playcount;
    private String subscribercount;
    private String artistName;
    private TextView mPlaycount;
    private TextView mSubscriberCount;
    private TextView mUsername;
    private JSONArray tracks;
    private JSONArray artists;
    private JSONArray albums;
    private GridView artistGridview;
    private GridView albumsGridView;
    private RecyclerView tracksRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private final Intent showMoreIntent = new Intent(MainActivity.getContextOfApplication(), FullGridViewActivity.class);
    private final Intent albumInfoIntent = new Intent(MainActivity.getContextOfApplication(), AlbumInfoActivity.class);
    private final Intent artistInfoIntent = new Intent(MainActivity.getContextOfApplication(), ArtistInfoActivity.class);

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        tracksRecyclerView = view.findViewById(R.id.recent_tracks_rview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        tracksRecyclerView.setLayoutManager(layoutManager);
        mSwipeRefreshLayout =  view.findViewById(R.id.swipe_container);
        artistGridview = view.findViewById(R.id.artist_grid_view);
        albumsGridView = view.findViewById(R.id.albums_grid_view);
        Button mLogout = view.findViewById(R.id.logout_btn);
        Button mShowMoreArtists = view.findViewById(R.id.show_all_artists);
        Button mShowMoreAlbums = view.findViewById(R.id.show_all_albums);
        artistGridview.setVerticalScrollBarEnabled(false);
        albumsGridView.setVerticalScrollBarEnabled(false);
        mProfileImage = view.findViewById(R.id.profile_image);
        mUsername = view.findViewById(R.id.user_profile_name);
        mPlaycount = view.findViewById(R.id.playcount);
        mSubscriberCount = view.findViewById(R.id.artistcount);

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Lfm.logout();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        getUserInfo();
        getUserRecentTracks();
        getUserTopArtists();
        getUserTopAlbums();
        mShowMoreArtists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMoreIntent.putExtra("EXTRA_ITEM_TYPE", "artist");
                startActivity(showMoreIntent);
            }
        });

        mShowMoreAlbums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMoreIntent.putExtra("EXTRA_ITEM_TYPE", "albums");
                startActivity(showMoreIntent);
            }
        });


        mSwipeRefreshLayout.setOnRefreshListener(ProfileFragment.this);
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

            }

            @Override
            public void onError (LfmError error) {
                Log.e("ERROR", error.errorMessage);
            }
        });
    }

    public void getUserTopArtists () {
        LfmParameters pTopArtists = new LfmParameters();
        pTopArtists.put("user", Session.username);
        pTopArtists.put("limit", "3");

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

                    artistGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            artistInfoIntent.putExtra("EXTRA_ARTIST_NAME", mTopArtists.get(position).getName());
                            startActivity(artistInfoIntent);
                        }
                    });
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
        pTopAlbums.put("user", Session.username);
        pTopAlbums.put("limit", "3");

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

                    albumsGridView.setAdapter(new GridViewAdapter(getContext(), mTopAlbums));

                    albumsGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            albumInfoIntent.putExtra("EXTRA_ALBUM_NAME", mTopAlbums.get(position).getName());
                            albumInfoIntent.putExtra("EXTRA_ARTIST_NAME", mTopAlbums.get(position).getArtistName());
                            startActivity(albumInfoIntent);
                        }
                    });
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
    public void getUserRecentTracks() {
        LfmParameters pRecentTracks = new LfmParameters();
        pRecentTracks.put("user", Session.username);
        pRecentTracks.put("limit", "5");

        LfmRequest RecentTracksRequest = LfmApi.user().getRecentTracks(pRecentTracks);
        RecentTracksRequest.executeWithListener(new LfmRequest.LfmRequestListener() {
            @Override
            public void onComplete(JSONObject response) {
                tracks = response.optJSONObject("recenttracks").optJSONArray("track");
                for (int i = 0; i < tracks.length(); i++) {
                    trackName = tracks
                            .optJSONObject(i)
                            .optString("name");
                    trackArtist = tracks
                            .optJSONObject(i)
                            .optJSONObject("artist")
                            .optString("#text");
                    trackDate = tracks
                            .optJSONObject(i)
                            .optString("date");
                    int index = trackDate.indexOf("#text");
                    trackDate = index != -1 ? trackDate.substring(index + 9, index + 26) : "-";
                    trackInfo = trackName + " - " + trackArtist;
                    getTrackAlbumCover(trackArtist, trackName, trackInfo, trackDate);
                }
            }

            @Override
            public void onError(LfmError error) {

            }
        });
    }

    public void getTrackAlbumCover(String artistName, final String trackName, final String trackInfo, final String trackDate) {
        LfmParameters pTrackCover = new LfmParameters();
        pTrackCover.put("track", trackName);
        pTrackCover.put("artist", artistName);

        LfmRequest TrackCoverRequest = LfmApi.track().getInfo(pTrackCover);
        TrackCoverRequest.executeWithListener(new LfmRequest.LfmRequestListener() {
            @Override
            public void onComplete(JSONObject response) {
                JSONObject temp = response
                        .optJSONObject("track");
                if (temp.optJSONObject("album") != null) {
                    temp = temp
                            .optJSONObject("album")
                            .optJSONArray("image")
                            .optJSONObject(2);
                    trackAlbumCoverURL = temp.optString("#text");
                } else {
                    trackAlbumCoverURL = "-";
                }


                mRecentTracks.add(new RecentTrack(trackInfo, trackDate, trackAlbumCoverURL));
                Log.d("IMAGE", trackAlbumCoverURL);
                tracksRecyclerView.setAdapter(new RecentTracksAdapter(mRecentTracks));
            }

            @Override
            public void onError(LfmError error) {

            }
        });
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);

        mRecentTracks.clear();
        getUserInfo();
        getUserRecentTracks();
        getUserTopArtists();
        getUserTopAlbums();
    }
}
