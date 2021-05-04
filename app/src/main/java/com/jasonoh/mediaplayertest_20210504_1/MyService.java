package com.jasonoh.mediaplayertest_20210504_1;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class MyService extends Service implements MediaPlayer.OnPreparedListener {

    private final String ACTION_PLAY = "com.jasonoh.action.PLAY";
    private MediaPlayer mediaPlayer = null;
    private String url3 ="android.resource://" + getPackageName() + "/raw/sample_mp4_file";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        switch (action){
            case ACTION_PLAY:
                mediaPlayer = new MediaPlayer();
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }
}
