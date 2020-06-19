package com.flightfight.flightfight.yankunwei;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GameMusicManager {

    public static final String SOUND_EXPLOSION = "sounds.effect.explosion";

    private Map<String, MediaPlayer> soundNameMap;
    private MediaPlayer bgmMediaPlayer;
    private static final GameMusicManager GAME_MUSIC_MANAGER = new GameMusicManager();
    private boolean mute = false;
    private boolean ready = false;

    public static synchronized GameMusicManager getInstance() {
        return GAME_MUSIC_MANAGER;
    }

    private GameMusicManager() {
        this.soundNameMap = new HashMap<>();
    }

    public synchronized void init(Context context) {
        loadSoundEffect(context);
        bgmMediaPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor assetFileDescriptor = context.getApplicationContext().getAssets().openFd("sounds/background_music.mp3");
            bgmMediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
            bgmMediaPlayer.prepare();
            bgmMediaPlayer.setLooping(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ready = true;
        setMute(mute);
    }

    private synchronized void checkReady() {
        if (!ready) {
            throw new IllegalStateException("GameMusicManager not ready yet.");
        }
    }

    private synchronized void loadSoundEffect(Context context) {
        release();
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor assetFileDescriptor = context.getApplicationContext().getAssets().openFd("sounds/blast.wav");
            mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
            mediaPlayer.prepare();
            soundNameMap.put(SOUND_EXPLOSION, mediaPlayer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void playBGM() {
        checkReady();
        bgmMediaPlayer.start();
    }

    public synchronized void setBGMVolume(int volume) {
        checkReady();
        bgmMediaPlayer.setVolume((float) volume / 100.0F, (float) volume / 100.0F);
    }

    public synchronized void pauseBGM() {
        checkReady();
        bgmMediaPlayer.pause();
    }

    public synchronized void playOrPauseBGM() {
        checkReady();
        if (bgmMediaPlayer.isPlaying()) {
            bgmMediaPlayer.pause();
        } else {
            bgmMediaPlayer.start();
        }
    }

    public synchronized void restartBGM() {
        checkReady();
        bgmMediaPlayer.pause();
        bgmMediaPlayer.seekTo(0);
        bgmMediaPlayer.start();
    }

    public synchronized void setMute(boolean mute) {
        checkReady();
        if (mute) {
            bgmMediaPlayer.start();
            bgmMediaPlayer.setVolume(0.0F, 0.0F);
            bgmMediaPlayer.pause();
            for (Map.Entry<String, MediaPlayer> musicEntry : soundNameMap.entrySet()) {
                musicEntry.getValue().start();
                musicEntry.getValue().setVolume(0.0F, 0.0F);
                musicEntry.getValue().pause();
            }
        } else {
            bgmMediaPlayer.start();
            bgmMediaPlayer.setVolume(0.3F, 0.3F);
            bgmMediaPlayer.pause();
            for (Map.Entry<String, MediaPlayer> musicEntry : soundNameMap.entrySet()) {
                musicEntry.getValue().start();
                musicEntry.getValue().setVolume(1.0F, 1.0F);
                musicEntry.getValue().pause();
            }
        }
        this.mute = mute;
    }

    public synchronized boolean isMute() {
        checkReady();
        return mute;
    }

    public synchronized void play(String soundType) {
        play(soundType, false);
    }

    public synchronized void play(String soundType, boolean loop) {
        checkReady();
        MediaPlayer mediaPlayer = soundNameMap.get(soundType);
        if (mediaPlayer == null) {
            throw new IllegalArgumentException("Sound type: " + soundType + " not exist");
        }
        mediaPlayer.seekTo(0);
        mediaPlayer.setLooping(loop);
        mediaPlayer.start();
    }

    public synchronized void setVolume(String soundType, int volume) {
        checkReady();
        MediaPlayer mediaPlayer = soundNameMap.get(soundType);
        if (mediaPlayer == null) {
            throw new IllegalArgumentException("Sound type: " + soundType + " not exist");
        }
        mediaPlayer.setVolume((float) volume / 100, (float) volume / 100);
    }

    public synchronized void release() {
        if (bgmMediaPlayer != null) {
            bgmMediaPlayer.release();
            bgmMediaPlayer = null;
        }
        for (Map.Entry<String, MediaPlayer> entry : soundNameMap.entrySet()) {
            entry.getValue().release();
        }
        soundNameMap.clear();
        ready = false;
    }
}

