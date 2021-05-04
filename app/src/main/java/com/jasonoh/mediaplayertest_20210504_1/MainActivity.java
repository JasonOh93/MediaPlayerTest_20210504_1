package com.jasonoh.mediaplayertest_20210504_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView tvTime;
    TextureView ttvVideo;
    SeekBar skbVideo;
    String url, url2, url3;
    MediaPlayer mPlayer;
    ProgressBar progressBar;

    MyService myService;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.pgb_video);
        ttvVideo = findViewById(R.id.ttv_video);
        tvTime = findViewById(R.id.tv_time);
        skbVideo = findViewById(R.id.skb_video);

        Toast.makeText(this, getSharedPreferences("Login", MODE_PRIVATE).getString("pausedTime", null) + "", Toast.LENGTH_SHORT).show();

        // 비디오 나타내기
        playVideo();

    }// onCreate method

    private void playVideo(){

        url = "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4";
        url2 = "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4";
        url3 ="android.resource://" + getPackageName() + "/raw/sample_mp4_file";

        ttvVideo.setSurfaceTextureListener(ttvVideoListener);

        initSeekBar();

    }// playVideo method

    TextureView.SurfaceTextureListener ttvVideoListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {

            try {
//                Toast.makeText(MainActivity.this, "aa", Toast.LENGTH_SHORT).show();
                mPlayer = new MediaPlayer();
                Surface surface1 = new Surface(surface);
                mPlayer.setSurface(surface1);
                Uri uri = Uri.parse(url3);
                mPlayer.setDataSource(MainActivity.this, uri);
                mPlayer.prepareAsync();
                mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
//                        Toast.makeText(MainActivity.this, "Video Prepared", Toast.LENGTH_SHORT).show();
                        playStart();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

        }
    };

    private void initSeekBar(){
        skbVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    // 사용자가 시크바를 움직이면
                    mPlayer.seekTo(progress); // 재생위치를 바꿔준다 (움직인 곳에서의 재생)
                    mPlayer.start();
                    seekBarPlay();
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    private void seekBarPlay(){
        skbVideo.setProgress(mPlayer.getCurrentPosition());

        int min = mPlayer.getDuration()/60000;
        int sec = (mPlayer.getDuration()-60000*min)/1000;
        int min2 = mPlayer.getCurrentPosition()/60000;
        int sec2 = (mPlayer.getCurrentPosition()-60000*min2)/1000;

        tvTime.setText(min2 + " 분" + sec2 + " 초  //  " + min + " 분" + sec + " 초");

        if(mPlayer.isPlaying()){

            try {
//                Toast.makeText(this, mPlayer.getCurrentPosition() + "", Toast.LENGTH_SHORT).show();

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        seekBarPlay();
                    }
                };
                Handler handler = new Handler();
                handler.postDelayed(runnable, 1000);


//                tvTime.setText(min2 + " 분" + sec2 + " 초  //  " + min + " 분" + sec + " 초");
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("TAG", "destroy");
        preferences = getSharedPreferences("Login", MODE_PRIVATE);
        SharedPreferences.Editor editor =preferences.edit();
        editor.putString("pausedTime", mPlayer.getCurrentPosition() + "");
        editor.commit();
    }

    private void playStart(){
//        if(progressBar.getVisibility() == View.GONE){
//            progressBar.setVisibility(View.VISIBLE);
//            try {
////                playVideo();
//                mPlayer.prepareAsync();
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }

        if(mPlayer != null && !mPlayer.isPlaying()){

            if(getSharedPreferences("Login", MODE_PRIVATE).getString("pausedTime", null) != null){
                new AlertDialog.Builder(this).setMessage("이전에 보던 시간부터 보시겠습니까?").setNegativeButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        skbVideo.setMax(mPlayer.getDuration());
                        mPlayer.seekTo(Integer.parseInt(getSharedPreferences("Login", MODE_PRIVATE).getString("pausedTime", null)));
                        mPlayer.start();
                        seekBarPlay();
                    }
                }).setPositiveButton("거절", ((dialog, which) -> {

                    preferences = getSharedPreferences("Login", MODE_PRIVATE);
                    SharedPreferences.Editor editor =preferences.edit();
                    editor.putString("pausedTime", 0 + "");
                    editor.commit();

                    mPlayer.start();
                    skbVideo.setMax(mPlayer.getDuration());
                    seekBarPlay();
                })).create().show();
            }

            return;
//            mPlayer.prepareAsync();
        }
    }// playStart method

    private void playPause(){
        if(mPlayer != null && mPlayer.isPlaying()){
            preferences = getSharedPreferences("Login", MODE_PRIVATE);
            SharedPreferences.Editor editor =preferences.edit();
            editor.putString("pausedTime", mPlayer.getCurrentPosition() + "");
            editor.commit();
            mPlayer.pause();
        }

    }// playPause method

    private void playStop(){
        if(mPlayer != null){
            mPlayer.stop();
//            mPlayer.release();
//            mPlayer = null;
//            ttvVideo.setSurfaceTextureListener(null);
        }
    }// playStop method

//    버튼 클릭시 이루어짐
    public void clickBtn(View view) {
        switch (view.getId()){
            case R.id.btn_start:
                playStart();
                break;

            case R.id.btn_pause:
                playPause();
                break;

            case R.id.btn_stop:
                playStop();
                break;
        }// switch
    }//clickBtn method
}//MainActivity class