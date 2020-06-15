package com.flightfight.flightfight.yankunwei;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MusicManager {

    public static final String SOUND_EXPLOSION = "sounds.effect.explosion";

    private Map<String, MediaPlayer> soundNameMap;
    private MediaPlayer bgmMediaPlayer;
    private static final MusicManager musicManager = new MusicManager();

    public static synchronized MusicManager getInstance() {
        return musicManager;
    }

    private MusicManager() {
        this.soundNameMap = new HashMap<>();
    }

    public void init(Context context) {
        loadSoundEffect(context);
        bgmMediaPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor assetFileDescriptor = context.getApplicationContext().getAssets().openFd("sounds/blast.wav");
            bgmMediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
            bgmMediaPlayer.prepare();
            bgmMediaPlayer.setLooping(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSoundEffect(Context context) {
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

    public void playBGM() {
        bgmMediaPlayer.start();
    }

    public void pauseBGM() {
        bgmMediaPlayer.pause();
    }

    public void playOrPauseBGM() {
        if (bgmMediaPlayer.isPlaying()) {
            bgmMediaPlayer.pause();
        } else {
            bgmMediaPlayer.start();
        }
    }

    public void restartBGM() {
        bgmMediaPlayer.pause();
        bgmMediaPlayer.seekTo(0);
        bgmMediaPlayer.start();
    }

    public void play(String soundType) {
        play(soundType, 100);
    }

    public void play(String soundType, int volume) {
        play(soundType, volume, false);
    }

    public void play(String soundType, int volume, boolean loop) {
        play(soundType, volume, volume, loop);
    }

    public void play(String soundType, int leftVolume, int rightVolume, boolean loop) {
        MediaPlayer mediaPlayer = soundNameMap.get(soundType);
        if (mediaPlayer == null) {
            throw new IllegalArgumentException("Sound type: " + soundType + " not exist");
        }
        mediaPlayer.seekTo(0);
        mediaPlayer.setVolume( (float) leftVolume /100.0F, (float) rightVolume / 100.0F);
        mediaPlayer.setLooping(loop);
        mediaPlayer.start();
    }

    public void release() {
        bgmMediaPlayer.release();
        for (Map.Entry<String, MediaPlayer> entry : soundNameMap.entrySet()) {
            entry.getValue().release();
        }
    }
}
