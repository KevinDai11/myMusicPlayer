package com.example.mymusicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class playerActivity extends AppCompatActivity {
    ImageButton btnPlay, btnNext, btnPrev, btnFf, btnFr,shuffle,repeat;
    TextView txtsname, txtstart, txtstop;
    SeekBar seekM;
    BarVisualizer visualizer;
    ImageView image;


    Thread updateSeekBar;
    String sname;
    boolean shuffleClicked, repeatClicked;
    public static final String EXTRA_NAME = "song_name";
    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> mySongs;

   @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if(visualizer!= null){
            visualizer.release();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnPlay = findViewById(R.id.playButton);
        btnNext = findViewById(R.id.nextButton);
        btnPrev = findViewById(R.id.prevButton);
        btnFf = findViewById(R.id.forwButton);
        btnFr = findViewById(R.id.rewindButton);
        shuffle=findViewById(R.id.shuffleButton);
        txtsname = findViewById(R.id.songname);
        txtstart = findViewById(R.id.sstart);
        txtstop = findViewById(R.id.sstop);
        seekM = findViewById(R.id.seekbar);
        visualizer = findViewById(R.id.visualizer);
        image = findViewById(R.id.songimage);
        repeat = findViewById(R.id.repeatButton);
        shuffleClicked=false;
        repeatClicked=false;
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        mySongs = (ArrayList) bundle.getParcelableArrayList("songs");
        String songName = i.getStringExtra("songname");
        position = bundle.getInt("pos");
        txtsname.setSelected(true);
        Uri u = Uri.parse(mySongs.get(position).toString());
        sname = mySongs.get(position).getName();
        txtsname.setText(sname);



        mediaPlayer = MediaPlayer.create(getApplicationContext(),u);
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.start();
            }
        },1500);

        updateSeekBar = new Thread(){
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int curr =0;
                seekM.setProgress(curr);
                while(curr<totalDuration){
                    try{
                        sleep(500);
                        curr = mediaPlayer.getCurrentPosition();
                        seekM.setProgress(curr);
                    } catch (InterruptedException| IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        seekM.setMax(mediaPlayer.getDuration());
        updateSeekBar.start();

        seekM.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        String endtime = createTime(mediaPlayer.getDuration());
        txtstop.setText(endtime);

        final Handler hand = new Handler();
        hand.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = createTime(mediaPlayer.getCurrentPosition());
                txtstart.setText(currentTime);
                hand.postDelayed(this,1000);
            }
        },1000);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    btnPlay.setBackgroundResource(R.drawable.play);
                    mediaPlayer.pause();
                }
                else{
                    btnPlay.setBackgroundResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position=((position+1)%mySongs.size());
                Uri temp = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),temp);
                sname=mySongs.get(position).getName();
                txtsname.setText(sname);
                String endtime = createTime(mediaPlayer.getDuration());
                txtstop.setText(endtime);
                seekM.setMax(mediaPlayer.getDuration());
                updateSeekBar.start();
                mediaPlayer.start();
                btnPlay.setBackgroundResource(R.drawable.pause);
                startAnimation(image);
                int audiosessionId = mediaPlayer.getAudioSessionId();
                if(audiosessionId!=-1){
                    visualizer.setAudioSessionId(audiosessionId);
                }


            }
        });
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position= (position-1)<0 ? mySongs.size()-1 : position-1;
                Uri temp = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),temp);
                sname=mySongs.get(position).getName();
                txtsname.setText(sname);

                mediaPlayer.start();
                btnPlay.setBackgroundResource(R.drawable.pause);
                startAnimation(image);
                int audiosessionId = mediaPlayer.getAudioSessionId();
                if(audiosessionId!=-1){
                    visualizer.setAudioSessionId(audiosessionId);
                }

            }
        });
        int audiosessionId = mediaPlayer.getAudioSessionId();
        if(audiosessionId!=-1){
            visualizer.setAudioSessionId(audiosessionId);
        }
        btnFf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+5000);
                }
            }
        });
        btnFr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-5000);
                }
            }
        });
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shuffleClicked){
                    shuffle.setBackgroundResource(R.drawable.ic_baseline_shuffle_24);
                }
                else{
                    shuffle.setBackgroundResource(R.drawable.ic_baseline_shuffle_24_clicked);
                }
                shuffleClicked=!shuffleClicked;
            }
        });
        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(repeatClicked){
                    repeat.setBackgroundResource(R.drawable.ic_baseline_repeat_24);
                }
                else{
                    repeat.setBackgroundResource(R.drawable.ic_baseline_repeat_24_clicked);
                }
                repeatClicked=!repeatClicked;
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(repeatClicked){
                    position = position-1;
                }
                else if(shuffleClicked){
                    final int random = new Random().nextInt(mySongs.size());
                    position = random-1;
                }

                btnNext.performClick();
            }
        });
    }

    public void startAnimation(View v){
        ObjectAnimator anime = ObjectAnimator.ofFloat(image, "rotation", 0f,360f);
        anime.setDuration(1000);
        AnimatorSet as = new AnimatorSet();
        as.playTogether(anime);
        as.start();
    }


    public String createTime(int dur){
        StringBuilder time = new StringBuilder();
        int min = dur/1000/60;
        int sec = dur/1000%60;
        time.append(min);
        time.append(":");
        if(sec<10){
            time.append("0");
        }
        time.append(sec);
        return time.toString();
    }


}