package com.jasonoh.mediaplayertest_20210504_1;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class MyService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    private final String ACTION_PLAY = "com.jasonoh.action.PLAY";
    private MediaPlayer mediaPlayer = null;
    private String url3 ="android.resource://" + getPackageName() + "/raw/sample_mp4_file";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        Uri uri = Uri.parse(url3);
        switch (action){
            case ACTION_PLAY:
                try{
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(MyService.this, uri);
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(this);
                }catch (Exception e){
                    e.printStackTrace();
                }

                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        return false;
    }

    //이 MusicService 객체의 메모리 주소(객체 참조값)을 리턴해주는 기능을 가진 Binder 클래스 설계
    class MyBinder extends Binder {
        //이 서비스 객체의 주소를 리턴해주는 메소드
        public MyService getService(){
            return MyService.this;
        }
    }//MyBinder inner class

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        MyBinder binder = new MyBinder();
        return binder;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

        Log.e("TAG", "media player prepared");
        mediaPlayer.start();
    }
}
