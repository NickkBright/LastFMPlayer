package com.nickkbright.lastfmplayer.fragments;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nickkbright.lastfmplayer.adapters.AlbumsAdapter;
import com.nickkbright.lastfmplayer.adapters.ArtistsAdapter;
import com.nickkbright.lastfmplayer.adapters.SongsAdapter;
import com.nickkbright.lastfmplayer.fastscroller.FastScrollerRecyclerView;
import com.nickkbright.lastfmplayer.fastscroller.FastScrollerView;
import com.nickkbright.lastfmplayer.loaders.AlbumProvider;
import com.nickkbright.lastfmplayer.loaders.ArtistProvider;
import com.nickkbright.lastfmplayer.models.Album;
import com.nickkbright.lastfmplayer.models.Artist;
import com.nickkbright.lastfmplayer.models.Song;
import com.nickkbright.lastfmplayer.playback.EqualizerUtils;
import com.nickkbright.lastfmplayer.playback.MusicNotificationManager;
import com.nickkbright.lastfmplayer.playback.MusicService;
import com.nickkbright.lastfmplayer.playback.PlaybackInfoListener;
import com.nickkbright.lastfmplayer.playback.PlayerAdapter;
import com.nickkbright.lastfmplayer.slidinguppanel.SlidingUpPanelLayout;
import com.nickkbright.lastfmplayer.utilities.AndroidVersion;
import com.nickkbright.lastfmplayer.utilities.PermissionDialog;
import com.nickkbright.lastfmplayer.utilities.SettingsUtils;

import com.nickkbright.lastfmplayer.R;

import java.util.List;

public class PlayerFragment
    extends Fragment
    implements
        LoaderManager.LoaderCallbacks,
        SongsAdapter.SongSelectedListener,
        AlbumsAdapter.AlbumSelectedListener,
        ArtistsAdapter.ArtistSelectedListener {

    private LinearLayoutManager mArtistsLayoutManager;
    private ArtistsAdapter mArtistsAdapter;
    private int mAccent;
    private FastScrollerRecyclerView mArtistsRecyclerView;
    private RecyclerView mAlbumsRecyclerView, mSongsRecyclerView;
    private AlbumsAdapter mAlbumsAdapter;
    private SongsAdapter mSongsAdapter;
    private TextView mPlayingAlbum, mPlayingSong, mDuration, mSongPosition, mArtistAlbumCount, mSelectedAlbum;
    private SeekBar mSeekBarAudio;
    private LinearLayout mControlsContainer;
    private View mSettingsView;
    private SlidingUpPanelLayout mSlidingUpPanel;
    private ImageButton mPlayPauseButton, mResetButton, mArrowUp, mSkipNextButton, mSkipPrevButton;
    private int mThemeContrast;
    private PlayerAdapter mPlayerAdapter;
    private boolean mUserIsSeeking = false;
    private String mSelectedArtist;
    private boolean sExpandPanel = false;
    private MusicService mMusicService;
    private PlaybackListener mPlaybackListener;
    private MusicNotificationManager mMusicNotificationManager;
    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            mMusicService = ((MusicService.LocalBinder) iBinder).getInstance();
            mPlayerAdapter = mMusicService.getMediaPlayerHolder();
            mMusicNotificationManager = mMusicService.getMusicNotificationManager();
            mMusicNotificationManager.setAccentColor(mAccent);

            if (mPlaybackListener == null) {
                mPlaybackListener = new PlaybackListener();
                mPlayerAdapter.setPlaybackInfoListener(mPlaybackListener);
            }
            checkReadStoragePermissions();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mMusicService = null;
        }
    };
    private boolean mIsBound;
    private Parcelable savedRecyclerLayoutState;

    @Override
    public void onPause() {
        super.onPause();
        if (mArtistsLayoutManager != null) {
            savedRecyclerLayoutState = mArtistsLayoutManager.onSaveInstanceState();
        }
        if (mPlayerAdapter != null && mPlayerAdapter.isMediaPlayer()) {
            mPlayerAdapter.onPauseActivity();
        }
    }

    private void checkReadStoragePermissions() {
        if (AndroidVersion.isMarshmallow()) {
            if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                PermissionDialog.showPermissionDialog(getActivity().getSupportFragmentManager());
            } else {

                onPermissionGranted();
            }
        } else {
            onPermissionGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            PermissionDialog.showPermissionDialog(getActivity().getSupportFragmentManager());
        } else {
            onPermissionGranted();
        }
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.player_fragment, container, false);

        mThemeContrast = SettingsUtils.getContrast(getActivity());
        mAccent = SettingsUtils.getAccent(getActivity());

        SettingsUtils.retrieveTheme(getActivity(), mThemeContrast, mAccent);


        getViews(view);

        mSlidingUpPanel.setSlidingUpPanel(mSongsRecyclerView, mArtistsRecyclerView, mArrowUp);
        mSlidingUpPanel.setGravity(Gravity.BOTTOM);

        setSlidingUpPanelHeight();

        initializeSeekBar();

        doBindService();

        return view;
    }

    private void getViews(View view) {
        mSlidingUpPanel = view.findViewById(R.id.sliding_panel);

        mControlsContainer = view.findViewById(R.id.controls_container);

        mArrowUp = view.findViewById(R.id.arrow_up);
        mPlayPauseButton = view.findViewById(R.id.play_pause);
        mResetButton = view.findViewById(R.id.replay);
        mSeekBarAudio = view.findViewById(R.id.seekTo);
        mSkipNextButton = view.findViewById(R.id.skip_next);
        mSkipPrevButton = view.findViewById(R.id.skip_prev);

        mPlayingSong = view.findViewById(R.id.playing_song);
        mPlayingAlbum = view.findViewById(R.id.playing_album);
        mDuration = view.findViewById(R.id.duration);
        mSongPosition = view.findViewById(R.id.song_position);
        mArtistAlbumCount = view.findViewById(R.id.artist_album_count);
        mSelectedAlbum = view.findViewById(R.id.selected_disc);

        mArtistsRecyclerView = view.findViewById(R.id.artists_rv);
        mAlbumsRecyclerView = view.findViewById(R.id.albums_rv);
        mSongsRecyclerView = view.findViewById(R.id.songs_rv);

        mSettingsView = View.inflate(getContext(), R.layout.settings_popup, null);

        mArrowUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                expandPanel(view);
            }
        });
        mSkipNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                skipNext(view);
            }
        });
        mSkipPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                skipPrev(view);
            }
        });
        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                reset(view);
            }
        });
        mPlayPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                resumeOrPause(view);
            }
        });
    }

    private void setSlidingUpPanelHeight() {
        final ViewTreeObserver observer = mControlsContainer.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mSlidingUpPanel.setPanelHeight(mControlsContainer.getHeight());
                mControlsContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }


    private void setScrollerIfRecyclerViewScrollable() {

        // ViewTreeObserver allows us to measure the layout params
        final ViewTreeObserver observer = mArtistsRecyclerView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                int h = mArtistsRecyclerView.getHeight();
                mArtistsRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (mArtistsRecyclerView.computeVerticalScrollRange() > h) {
                    FastScrollerView fastScrollerView = new FastScrollerView(mArtistsRecyclerView, mArtistsAdapter, mArtistsLayoutManager, ContextCompat.getColor(getContext(), mAccent), mThemeContrast);
                    mArtistsRecyclerView.setFastScroller(fastScrollerView);
                }
            }
        });
    }

    private void setArtistsRecyclerView(List<Artist> data) {

        mArtistsLayoutManager = new LinearLayoutManager(getContext());
        mArtistsRecyclerView.setLayoutManager(mArtistsLayoutManager);
        mArtistsAdapter = new ArtistsAdapter(PlayerFragment.this, data);
        mArtistsRecyclerView.setAdapter(mArtistsAdapter);

        if (savedRecyclerLayoutState != null) {
            mArtistsLayoutManager.onRestoreInstanceState(savedRecyclerLayoutState);
        }

        // Set the FastScroller only if the RecyclerView is scrollable;
        setScrollerIfRecyclerViewScrollable();
    }

    private void initializeSeekBar() {
        mSeekBarAudio.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int userSelectedPosition = 0;
                    int currentPositionColor = mSongPosition.getCurrentTextColor();

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        mUserIsSeeking = true;
                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                        if (fromUser) {
                            userSelectedPosition = progress;
                            mSongPosition.setTextColor(ContextCompat.getColor(getContext(), mAccent));
                        }
                        mSongPosition.setText(Song.formatDuration(progress));
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                        if (mUserIsSeeking) {
                            mSongPosition.setTextColor(currentPositionColor);
                        }
                        mUserIsSeeking = false;
                        mPlayerAdapter.seekTo(userSelectedPosition);
                    }
                });
    }

    public void reset(View v) {
        if (checkIsPlayer()) {
            mPlayerAdapter.reset();
            updateResetStatus(false);
        }
    }

    public void skipPrev(View v) {
        if (checkIsPlayer()) {
            mPlayerAdapter.instantReset();
        }
    }

    public void resumeOrPause(View v) {
        if (checkIsPlayer()) {
            mPlayerAdapter.resumeOrPause();
        }
    }

    public void skipNext(View v) {
        if (checkIsPlayer()) {
            mPlayerAdapter.skip(true);
        }
    }



    public void expandPanel(View v) {
        SlidingUpPanelLayout.PanelState panelState = mSlidingUpPanel.getPanelState() != SlidingUpPanelLayout.PanelState.EXPANDED ? SlidingUpPanelLayout.PanelState.EXPANDED : SlidingUpPanelLayout.PanelState.COLLAPSED;
        mSlidingUpPanel.setPanelState(panelState);
    }

    private boolean checkIsPlayer() {

        boolean isPlayer = mPlayerAdapter.isMediaPlayer();
        if (!isPlayer) {
            EqualizerUtils.notifyNoSessionId(getContext());
        }
        return isPlayer;
    }


    private void onPermissionGranted() {
        getActivity().getSupportLoaderManager().initLoader(ArtistProvider.ARTISTS_LOADER, null, this);
    }

    private void updateResetStatus(boolean onPlaybackCompletion) {

        int themeColor = mThemeContrast != SettingsUtils.THEME_LIGHT ? ContextCompat.getColor(getContext(), R.color.grey_200) : ContextCompat.getColor(getContext(), R.color.grey_900_darker);
        int color = onPlaybackCompletion ? themeColor : mPlayerAdapter.isReset() ? ContextCompat.getColor(getContext(), mAccent) : themeColor;
        mResetButton.getDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    private void updatePlayingStatus() {
        int drawable = mPlayerAdapter.getState() != PlaybackInfoListener.State.PAUSED ? R.drawable.ic_pause : R.drawable.ic_play;
        mPlayPauseButton.setImageResource(drawable);
    }

    private void updatePlayingInfo(boolean restore, boolean startPlay) {

        if (startPlay) {
            mPlayerAdapter.getMediaPlayer().start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMusicService.startForeground(MusicNotificationManager.NOTIFICATION_ID, mMusicNotificationManager.createNotification());
                }
            }, 250);
        }

        final Song selectedSong = mPlayerAdapter.getCurrentSong();

        mSelectedArtist = selectedSong.artistName;
        final int duration = selectedSong.duration;
        mSeekBarAudio.setMax(duration);
        mDuration.setText(Song.formatDuration(duration));

        Spanned spanned = AndroidVersion.isNougat() ?
                Html.fromHtml(getString(R.string.playing_song, mSelectedArtist, selectedSong.title), Html.FROM_HTML_MODE_LEGACY) :
                Html.fromHtml(getString(R.string.playing_song, mSelectedArtist, selectedSong.title));
        mPlayingSong.setText(spanned);
        mPlayingAlbum.setText(selectedSong.albumName);

        if (restore) {
            mSeekBarAudio.setProgress(mPlayerAdapter.getPlayerPosition());
            updatePlayingStatus();
            updateResetStatus(false);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //stop foreground if coming from pause state
                    if (mMusicService.isRestoredFromPause()) {
                        mMusicService.stopForeground(false);
                        mMusicService.getMusicNotificationManager().getNotificationManager().notify(MusicNotificationManager.NOTIFICATION_ID, mMusicService.getMusicNotificationManager().getNotificationBuilder().build());
                        mMusicService.setRestoredFromPause(false);
                    }
                }
            }, 250);
        }
    }

    private void restorePlayerStatus() {

        mSeekBarAudio.setEnabled(mPlayerAdapter.isMediaPlayer());

        //if we are playing and the activity was restarted
        //update the controls panel
        if (mPlayerAdapter != null && mPlayerAdapter.isMediaPlayer()) {

            mPlayerAdapter.onResumeActivity();
            updatePlayingInfo(true, false);
        }
    }

    private void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        getActivity().bindService(new Intent(getActivity(),
                MusicService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;

        Intent startNotStickyIntent = new Intent(getActivity(), MusicService.class);
        getActivity().startService(startNotStickyIntent);
    }

    private void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            getActivity().unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    @NonNull
    public Loader onCreateLoader(int id, Bundle args) {
        return id != AlbumProvider.ALBUMS_LOADER ? new ArtistProvider.AsyncArtistLoader(getContext()) : new AlbumProvider.AsyncAlbumsForArtistLoader(getContext(), mSelectedArtist);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onLoadFinished(@NonNull Loader loader, Object data) {

        switch (loader.getId()) {
            case ArtistProvider.ARTISTS_LOADER:

                //get loaded artist list and set the artists recycler view
                List<Artist> artists = (List<Artist>) data;

                if (artists.isEmpty()) {

                    Toast.makeText(getContext(), getString(R.string.error_no_music), Toast.LENGTH_SHORT)
                            .show();
                    getActivity().finish();

                } else {

                    setArtistsRecyclerView(artists);

                    mSelectedArtist = mPlayerAdapter.getSelectedAlbum() != null ? mPlayerAdapter.getSelectedAlbum().getArtistName() : artists.get(0).getName();

                   getActivity().getSupportLoaderManager().initLoader(AlbumProvider.ALBUMS_LOADER, null, this);
                }
                break;

            case AlbumProvider.ALBUMS_LOADER:

                //get loaded albums for artist
                Pair<Artist, List<Album>> albumsForArtist = (Pair<Artist, List<Album>>) data;

                List<Album> albums = albumsForArtist.second;

                if (mAlbumsAdapter != null) {
                    //only notify recycler view of item changed if an adapter already exists
                    mAlbumsAdapter.swapArtist(albums);
                } else {
                    LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                    mAlbumsRecyclerView.setLayoutManager(horizontalLayoutManager);
                    mAlbumsAdapter = new AlbumsAdapter(PlayerFragment.this, albums, mPlayerAdapter);
                    mAlbumsRecyclerView.setAdapter(mAlbumsAdapter);
                }

                int albumCount = albumsForArtist.second.size();
                int artistAlbumCount = albumCount > 1 ? R.string.albums : R.string.album;
                mArtistAlbumCount.setText(getString(artistAlbumCount, mSelectedArtist, albumCount));

                if (sExpandPanel) {
                    mSlidingUpPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    sExpandPanel = false;
                } else {
                    restorePlayerStatus();
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlaybackListener = null;
        doUnbindService();
    }

    @Override
    public void onSongSelected(Song song, Album album) {

        if (!mSeekBarAudio.isEnabled()) {
            mSeekBarAudio.setEnabled(true);
        }
        mPlayerAdapter.setCurrentSong(song, album.songs);
        mPlayerAdapter.initMediaPlayer();
    }

    @Override
    public void onArtistSelected(String artist) {

        if (!mSelectedArtist.equals(artist)) {

            //make the panel expandable
            sExpandPanel = true;

            mPlayerAdapter.setSelectedAlbum(null);

            //load artist albums only if not already loaded
            mSelectedArtist = artist;

            getActivity().getSupportLoaderManager().restartLoader(AlbumProvider.ALBUMS_LOADER, null, this);
        } else {
            //if already loaded expand the panel
            mSlidingUpPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        }
    }

    @Override
    public void onAlbumSelected(Album album) {
        mSelectedAlbum.setText(album.getTitle());
        mPlayerAdapter.setSelectedAlbum(album);
        if (mSongsAdapter != null) {
            mSongsAdapter.swapSongs(album);
        } else {
            LinearLayoutManager songsLayoutManager = new LinearLayoutManager(getContext());
            mSongsRecyclerView.setLayoutManager(songsLayoutManager);
            mSongsAdapter = new SongsAdapter(PlayerFragment.this, album);
            mSongsRecyclerView.setAdapter(mSongsAdapter);
        }
    }

    public class PlaybackListener extends PlaybackInfoListener {

        @Override
        public void onPositionChanged(int position) {
            if (!mUserIsSeeking) {
                mSeekBarAudio.setProgress(position);
            }
        }

        @Override
        public void onStateChanged(@State int state) {

            updatePlayingStatus();
            if (mPlayerAdapter.getState() != State.RESUMED && mPlayerAdapter.getState() != State.PAUSED) {
                updatePlayingInfo(false, true);
            }
        }

        @Override
        public void onPlaybackCompleted() {
            updateResetStatus(true);
        }
    }
}
