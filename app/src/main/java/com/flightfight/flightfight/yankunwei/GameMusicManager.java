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

    public void init(Context context) {
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
    }

    private void checkReady() {
        if (!ready) {
            throw new IllegalStateException("GameMusicManager not ready yet.");
        }
    }

    private void loadSoundEffect(Context context) {
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

    public void playBGM() {
        playBGM(50);
    }

    public void playBGM(int volume) {
        checkReady();
        setBGMVolume(volume);
        bgmMediaPlayer.start();
    }

    public void setBGMVolume(int volume) {
        checkReady();
        bgmMediaPlayer.setVolume((float) volume / 100.0F, (float) volume / 100.0F);
    }

    public void pauseBGM() {
        checkReady();
        bgmMediaPlayer.pause();
    }

    public void playOrPauseBGM() {
        checkReady();
        if (bgmMediaPlayer.isPlaying()) {
            bgmMediaPlayer.pause();
        } else {
            bgmMediaPlayer.start();
        }
    }

    public void restartBGM() {
        checkReady();
        bgmMediaPlayer.pause();
        bgmMediaPlayer.seekTo(0);
        bgmMediaPlayer.start();
    }

    public void setMute(boolean mute) {
        checkReady();
        if (mute) {
            bgmMediaPlayer.pause();
            for (Map.Entry<String, MediaPlayer> musicEntry : soundNameMap.entrySet()) {
                musicEntry.getValue().pause();
            }
        } else {
            bgmMediaPlayer.pause();
            for (Map.Entry<String, MediaPlayer> musicEntry : soundNameMap.entrySet()) {
                musicEntry.getValue().pause();
            }
        }
        this.mute = mute;
    }

    public boolean isMute() {
        checkReady();
        return mute;
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
        checkReady();
        MediaPlayer mediaPlayer = soundNameMap.get(soundType);
        if (mediaPlayer == null) {
            throw new IllegalArgumentException("Sound type: " + soundType + " not exist");
        }
        mediaPlayer.seekTo(0);
        mediaPlayer.setVolume((float) leftVolume / 100.0F, (float) rightVolume / 100.0F);
        mediaPlayer.setLooping(loop);
        mediaPlayer.start();
    }

    public void release() {
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
