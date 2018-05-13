package com.nickkbright.lastfmplayer.fragments;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nickkbright.lastfmplayer.MainActivity;
import com.nickkbright.lastfmplayer.R;
import com.nickkbright.lastfmplayer.adapters.TrackListAdapter;
import com.nickkbright.lastfmplayer.models.Audio;
import com.nickkbright.lastfmplayer.services.MediaPlayerService;
import com.nickkbright.lastfmplayer.utilities.CustomTouchListener;
import com.nickkbright.lastfmplayer.utilities.StorageUtil;
import com.nickkbright.lastfmplayer.utilities.onItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.os.Build.VERSION.SDK_INT;


public class PlayerFragment extends Fragment {
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    public static final String Broadcast_PLAY_NEW_AUDIO = "com.nickkbright.lastfmplayer.PlayNewAudio";
    private ArrayList<Audio> mTrackList;
    public RecyclerView mRecyclerView;
    private View view;
    Context appContext = MainActivity.getContextOfApplication();
    private MediaPlayerService player;
    boolean serviceBound = false;
    private int prevPlayingIndex = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_player, container, false);
        if (checkAndRequestPermissions()) {
            loadAudioList();
        }
        return view;
    }

    private void loadAudioList() {
        loadAudio();
        initRecyclerView();
    }

    public void loadAudio() {
        ContentResolver contentResolver = appContext.getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.ARTIST + " ASC";

        Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);

        if (cursor != null && cursor.getCount() > 0) {
            mTrackList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));

                // Save to audioList
                mTrackList.add(new Audio(data, title, album, artist));
            }
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    private void initRecyclerView() {
        if (mTrackList != null && mTrackList.size() > 0) {
            mRecyclerView = (RecyclerView) view.findViewById(R.id.track_list);
            TrackListAdapter adapter = new TrackListAdapter(mTrackList);
            mRecyclerView.setAdapter(adapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mRecyclerView.addOnItemTouchListener(new CustomTouchListener(getContext(), new onItemClickListener() {
                @Override
                public void onClick(View view, int index) {
                    if (prevPlayingIndex != index) {
                        playAudio(index);

                        if (prevPlayingIndex != -1) {
                            View pauseBtn = mRecyclerView.getChildAt(prevPlayingIndex).findViewById(R.id.pause_btn);
                            View playBtn = mRecyclerView.getChildAt(prevPlayingIndex).findViewById(R.id.play_btn);
                            pauseBtn.setVisibility(View.INVISIBLE);
                            playBtn.setVisibility(View.VISIBLE);
                        }

                        View pauseBtn = mRecyclerView.getChildAt(index).findViewById(R.id.pause_btn);
                        View playBtn = mRecyclerView.getChildAt(index).findViewById(R.id.play_btn);
                        pauseBtn.setVisibility(View.VISIBLE);
                        playBtn.setVisibility(View.INVISIBLE);

                        prevPlayingIndex = index;
                    } else {
                        if (player.isPlaying()) {
                            player.pausePlayer();

                            View pauseBtn = mRecyclerView.getChildAt(index).findViewById(R.id.pause_btn);
                            View playBtn = mRecyclerView.getChildAt(index).findViewById(R.id.play_btn);
                            pauseBtn.setVisibility(View.INVISIBLE);
                            playBtn.setVisibility(View.VISIBLE);
                        } else {
                            player.startPlayer();

                            View pauseBtn = mRecyclerView.getChildAt(index).findViewById(R.id.pause_btn);
                            View playBtn = mRecyclerView.getChildAt(index).findViewById(R.id.play_btn);
                            pauseBtn.setVisibility(View.VISIBLE);
                            playBtn.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }));
        }
    }

    private boolean checkAndRequestPermissions() {
        if (SDK_INT >= Build.VERSION_CODES.M) {
            int permissionReadPhoneState = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE);
            int permissionStorage = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
            List<String> listPermissionsNeeded = new ArrayList<>();

            if (permissionReadPhoneState != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
            }

            if (permissionStorage != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        String TAG = "LOG_PERMISSION";
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);

                    if (perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            ) {
                        Log.d(TAG, "Phone state and storage permissions granted");
                        loadAudioList();
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_PHONE_STATE)) {
                            showDialogOK("Phone state and storage permissions required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    break;
                                            }
                                        }
                                    });
                        }
                        else {
                            Toast.makeText(getContext(), "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };


    public void playAudio(int audioIndex) {
        if (!serviceBound) {
            StorageUtil storage = new StorageUtil(getActivity().getApplicationContext());
            storage.storeAudio(mTrackList);
            storage.storeAudioIndex(audioIndex);

            Intent playerIntent = new Intent(getActivity(), MediaPlayerService.class);
            getActivity().startService(playerIntent);
            getActivity().bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            StorageUtil storage = new StorageUtil(getActivity().getApplicationContext());
            storage.storeAudioIndex(audioIndex);

            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            getActivity().sendBroadcast(broadcastIntent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            getActivity().unbindService(serviceConnection);
            player.stopSelf();
        }
    }
}


