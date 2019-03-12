package com.sap.uncolor.equalizer.services.download;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.sap.uncolor.equalizer.application.App;
import com.sap.uncolor.equalizer.models.BaseMusic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;

/**
 * Created by Uncolor on 21.10.2018.
 */

public class NewMusicService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnPreparedListener {


    public static final String ACTION_PLAY = "com.sap.uncolor.equalizer.action._PLAY";
    public static final String ACTION_PREVIOUS = "com.sap.uncolor.equalizer.action._PREVIOUS";
    public static final String ACTION_NEXT = "com.sap.uncolor.equalizer.action._NEXT";
    public static final String ACTION_CLOSE = "com.sap.uncolor.equalizer.action._CLOSE";
    public static final String ACTION_PAUSE_OR_RESUME = "com.sap.uncolor.equalizer.action._PAUSE_RESUME";
    public static final String ACTION_BEGIN_PLAYING = "com.sap.uncolor.equalizer.action._BEGIN_PLAYING";
    public static final String ACTION_TRACK_DELETED = "com.sap.uncolor.equalizer.action._TRACK_DELETED";
    public static final String ACTION_MUSIC_PLAYER_STARTED = "com.sap.uncolor.equalizer.action._MUSIC_PLAYER_STARTED";

    private static final int SHUFFLE_SEED = 42;

    private IntentFilter intentFilterForAudioStreamReceiver = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    private BecomingNoisyReceiver myNoisyAudioStreamReceiver = new BecomingNoisyReceiver();

    private MediaPlayer mediaPlayer;
    private ArrayList<BaseMusic> playlist = new ArrayList<>();
    private int playlistPosition;
    private BaseMusic music;
    private int playbackPosition;
    private boolean isLooping = false;
    private boolean isShuffling = false;
    private boolean isPreparing = false;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat mediaController;
    private AudioManager audioManager;
    private EqualizerController equalizerController;

    private static final int TIME_LIMIT_FOR_TURN_PREVIOUS = 5000;


    private final MediaMetadataCompat.Builder metadataBuilder = new MediaMetadataCompat.Builder();

    private MusicBinder binder = new MusicBinder();

    public static final String ARG_PLAYLIST = "playlist";
    public static final String ARG_MUSIC = "music";
    public static final String ARG_POSITION = "position";


    private final PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder()
            .setActions(PlaybackStateCompat.ACTION_PLAY
                    | PlaybackStateCompat.ACTION_STOP
                    | PlaybackStateCompat.ACTION_PAUSE
                    | PlaybackStateCompat.ACTION_PLAY_PAUSE
                    | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);


    private AudioManager.OnAudioFocusChangeListener focusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                public void onAudioFocusChange(int focusChange) {
                    switch (focusChange) {
                        case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK):
                            App.Log("can duck");
                            mediaPlayer.setVolume(0.5f, 0.5f);
                            break;
                        case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT):
                            App.Log("loss transient");
                            pause();
                            showMediaPlayerNotification();
                            mediaController.getTransportControls().pause();
                            break;

                        case (AudioManager.AUDIOFOCUS_LOSS):
                            App.Log("loss audiofocus");
                            if (mediaPlayer == null) {
                                return;
                            }
                            pause();
                            showMediaPlayerNotification();
                            mediaController.getTransportControls().pause();
                            break;

                        case (AudioManager.AUDIOFOCUS_GAIN):
                            App.Log("audiofocus gain");
                            mediaPlayer.setVolume(1f, 1f);
                            break;
                        default:
                            break;
                    }
                }
            };


    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        equalizerController = new EqualizerController(mediaPlayer.getAudioSessionId());
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mediaSession = new MediaSessionCompat(this, "PlayerService");
        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                        | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mediaSession.setCallback(mediaSessionCallback);
        try {
            mediaController = new MediaControllerCompat(
                    this, mediaSession.getSessionToken());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        onPlayerStarted();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                App.Log("Action: " + action);
                Bundle extras = intent.getExtras();
                int result = audioManager.requestAudioFocus(focusChangeListener,
                        AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN);

                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

                    App.Log("focus granted");

                    if (Objects.equals(action, ACTION_PLAY)) {
                        onActionPlay(extras);
                    }

                    if (Objects.equals(action, ACTION_PAUSE_OR_RESUME)) {
                        if (isPlaying()) {
                            pause();
                        } else {
                            resume();
                        }
                        onActionPauseOrResume();
                    }

                    if (Objects.equals(action, ACTION_NEXT)) {
                        next(true);
                        onActionNext();
                    }

                    if (Objects.equals(action, ACTION_PREVIOUS)) {
                        previous();
                        onActionPrevious();
                    }

                    if (Objects.equals(action, ACTION_CLOSE)) {
                        App.Log("close new");
                        onActionClose();
                    }
                }
                showMediaPlayerNotification();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void onActionPauseOrResume() {
        Intent musicIntent = new Intent(ACTION_PAUSE_OR_RESUME);
        sendBroadcast(musicIntent);
    }

    private void onPlayerStarted() {
        Intent musicIntent = new Intent(ACTION_MUSIC_PLAYER_STARTED);
        sendBroadcast(musicIntent);
    }

    private void onActionPrevious() {
        Intent musicIntent = new Intent(ACTION_PREVIOUS);
        musicIntent.putExtra(ARG_MUSIC, music);
        sendBroadcast(musicIntent);
    }

    private void onActionNext() {
        Intent musicIntent = new Intent(ACTION_NEXT);
        musicIntent.putExtra(ARG_MUSIC, music);
        sendBroadcast(musicIntent);
    }

    public void shuffle() {
        Collections.shuffle(playlist, new Random(SHUFFLE_SEED));
        isShuffling = true;
    }

    public void unshuffle() {
        Random rnd = new Random(SHUFFLE_SEED);
        int[] seq = new int[playlist.size()];
        for (int i = seq.length; i >= 1; i--) {
            seq[i - 1] = rnd.nextInt(i);
        }
        for (int i = 0; i < seq.length; i++) {
            Collections.swap(playlist, i, seq[i]);
        }
        isShuffling = false;
    }

    private void onActionPlay(Bundle extras) {
        App.Log("new onActionPlay");
        if (extras != null) {
            music = extras.getParcelable(ARG_MUSIC);
            playlist = extras.getParcelableArrayList(ARG_PLAYLIST);
            playlistPosition = extras.getInt(ARG_POSITION);
            if (isShuffling) {
                Collections.shuffle(playlist);
            }
            if (music != null) {
                if (music.getDownload() != null) {
                    App.Log("download url: " + music.getDownload());
                    try {
                        playAudio(getMusicPath(music));
                        mediaController.getTransportControls().play();
                        Intent musicIntent = new Intent(ACTION_PLAY);
                        musicIntent.putExtra(ARG_MUSIC, music);
                        sendBroadcast(musicIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void onActionClose() {
        NotificationManager notificationManager
                = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            App.Log("check close");
            notificationManager.cancel(MediaPlayerNotification.NOTIFICATION_ID);
            Intent musicIntent = new Intent(ACTION_CLOSE);
            sendBroadcast(musicIntent);
            stopSelf();
        }
    }

    private void playAudio(final String path) throws Exception {
        App.Log("play audio");
        isPreparing = true;
        playbackPosition = 0;
        mediaPlayer.reset();
        App.Log("path: " + path);
        mediaPlayer.setDataSource(path);
        updateMetadataInLockPlayerPanel();
        mediaPlayer.prepareAsync();
    }

    private String getMusicPath(BaseMusic music) {
        switch (music.getState()) {
            case BaseMusic.STATE_DEFAULT:
            case BaseMusic.STATE_DOWNLOADING:
                return music.getDownload();
            case BaseMusic.STATE_COMPLETED:
                return music.getLocalPath();
        }
        return null;
    }

    public void resume() {
        mediaPlayer.seekTo(playbackPosition);
        mediaPlayer.start();
        showMediaPlayerNotification();
    }

    public void pause() {
        mediaPlayer.pause();
        playbackPosition = mediaPlayer.getCurrentPosition();
        showMediaPlayerNotification();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public boolean isShuffling() {
        return isShuffling;
    }

    public boolean isLooping() {
        return isLooping;
    }

    public void setLooping(boolean isLooping) {
        this.isLooping = isLooping;
    }

    public int getCurrentPlaybackPosition() {
        return mediaPlayer.getCurrentPosition() / 1000;
    }

    public void seekTo(int playbackPosition) {
        mediaPlayer.seekTo(playbackPosition * 1000);
        this.playbackPosition = mediaPlayer.getCurrentPosition();
    }

    public BaseMusic getCurrentMusic() {
        return music;
    }

    public BaseMusic next(boolean fromUser) {
        App.Log("onActionNext");
        BaseMusic nextMusic = null;
        if (fromUser) {
            App.Log("Action next from user");
            App.Log("Playlist position: " + playlistPosition);
            App.Log("Playlist size: " + playlist.size());
            if (playlistPosition != playlist.size() - 1) {
                playlistPosition++;
                if (!playlist.isEmpty()) {
                    nextMusic = playlist.get(playlistPosition);
                    App.Log(nextMusic.getArtist());
                    App.Log(nextMusic.getTitle());
                    try {
                        music = nextMusic;
                        playAudio(getMusicPath(nextMusic));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return nextMusic;
        } else {
            App.Log("Action next not from user");
            pause();
            onActionPauseOrResume();
            seekTo(0);
        }
        if (isLooping) {
            if (!playlist.isEmpty()) {
                nextMusic = playlist.get(playlistPosition);
                App.Log(nextMusic.getArtist());
                App.Log(nextMusic.getTitle());
                try {
                    music = nextMusic;
                    playAudio(getMusicPath(nextMusic));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return nextMusic;
        } else {
            if (playlistPosition != playlist.size() - 1) {
                playlistPosition++;
                if (!playlist.isEmpty()) {
                    nextMusic = playlist.get(playlistPosition);
                    App.Log(nextMusic.getArtist());
                    App.Log(nextMusic.getTitle());
                    try {
                        updateMetadataInLockPlayerPanel();
                        music = nextMusic;
                        playAudio(getMusicPath(nextMusic));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return nextMusic;
        }
    }

    public BaseMusic previous() {
        BaseMusic previousMusic;
        int currentPosition = mediaPlayer.getCurrentPosition();
        if (currentPosition < TIME_LIMIT_FOR_TURN_PREVIOUS) {
            if (playlistPosition != 0) {
                playlistPosition--;
            }
            previousMusic = playlist.get(playlistPosition);
            try {
                music = previousMusic;
                playAudio(getMusicPath(previousMusic));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            previousMusic = playlist.get(playlistPosition);
            try {
                playAudio(getMusicPath(previousMusic));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return previousMusic;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        App.Log("onCompletion");
        App.Log("current position: " + mediaPlayer.getCurrentPosition());
        if (mediaPlayer.getCurrentPosition() != 0) {
            next(false);
            onActionNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        App.Log(Integer.toString(what));
        App.Log("onError");
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        App.Log("onPrepared");
        if (isPreparing) {
            isPreparing = false;
            mediaPlayer.start();
            showMediaPlayerNotification();
            Intent musicIntent = new Intent(ACTION_BEGIN_PLAYING);
            sendBroadcast(musicIntent);
        }
    }

    private void showMediaPlayerNotification() {
        if (playlist.isEmpty()) {
            return;
        }
        MediaPlayerNotification mediaPlayerNotification =
                new MediaPlayerNotification(
                        this,
                        playlist.get(playlistPosition),
                        mediaPlayer.isPlaying());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground(mediaPlayerNotification.getNotification());
        else {
            startForeground(MediaPlayerNotification.NOTIFICATION_ID, mediaPlayerNotification.getNotification());
        }
    }

    private void startMyOwnForeground(Notification notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel chan = new NotificationChannel(MediaPlayerNotification.NOTIFICATION_CHANNEL_ID,
                    MediaPlayerNotification.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);
            startForeground(MediaPlayerNotification.NOTIFICATION_ID, notification);
        }
    }

    public EqualizerController getEqualizerController() {
        return equalizerController;
    }

    public class MusicBinder extends Binder {
        public NewMusicService getService() {
            return NewMusicService.this;
        }

    }

    private class BecomingNoisyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            App.Log("on receive becoming noisy receiver");
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                pause();
                onActionPauseOrResume();
            }
        }
    }

    private MediaSessionCompat.Callback mediaSessionCallback = new MediaSessionCompat.Callback() {
        @Override
        public void onPlay() {
            App.Log("session on play");
            registerReceiver(myNoisyAudioStreamReceiver, intentFilterForAudioStreamReceiver);
            updateMetadataInLockPlayerPanel();
            mediaPlayer.start();
        }

        @Override
        public void onPause() {
            App.Log("session on pause ");
            mediaPlayer.pause();
            playbackPosition = mediaPlayer.getCurrentPosition();
            mediaSession.setPlaybackState(
                    stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                            PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1).build());
        }

        @Override
        public void onStop() {
            App.Log("session on stop");
            unregisterReceiver(myNoisyAudioStreamReceiver);
            mediaSession.setActive(false);
            mediaSession.setPlaybackState(
                    stateBuilder.setState(PlaybackStateCompat.STATE_STOPPED,
                            PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1).build());
        }

        @Override
        public void onSkipToNext() {
            App.Log("session on next");
            super.onSkipToNext();
            next(true);
            updateMetadataInLockPlayerPanel();
        }

        @Override
        public void onSkipToPrevious() {
            App.Log("session on previous");
            super.onSkipToPrevious();
            previous();
            updateMetadataInLockPlayerPanel();
        }
    };

    private void killMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        killMediaPlayer();
        mediaSession.release();
    }

    private void updateMetadataInLockPlayerPanel() {
        App.Log("update metadata");
        MediaMetadataCompat metadata = metadataBuilder
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, playlist.get(playlistPosition).getTitle())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, playlist.get(playlistPosition).getArtist())
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, playlist.get(playlistPosition).getArtist())
                .build();
        mediaSession.setActive(true);
        mediaSession.setPlaybackState(
                stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                        PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1).build());
        mediaSession.setMetadata(metadata);
    }

}
