package com.example.a143music;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    Button play,next,prev,fast_forward,fast_rewind;
    TextView textName,textStart,textStop;
    SeekBar seek;
    BarVisualizer bvis;
    ImageView iview;

    Thread  updateSeek;
    String  sname;
    public  static final String EXTRA_NAME="song_name";
    static  MediaPlayer mediaPlayer=null;
    int     position;
    ArrayList<File> mySongs;

    public static String time="";
    public static int cindex=0;
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if(item.getItemId()==android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onDestroy(){
        if(bvis!=null){
            bvis.release();
        }
        super.onDestroy();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        prev=findViewById(R.id.prev);
        next=findViewById(R.id.next);
        play=findViewById(R.id.play);
        fast_forward=findViewById(R.id.fast_forward);
        fast_rewind=findViewById(R.id.fast_rewind);
        textName=findViewById(R.id.txtsn);
        textStart=findViewById(R.id.txtstart);
        textStop=findViewById(R.id.txtStop);
        seek=findViewById(R.id.seekbar);
        bvis=findViewById(R.id.blast);
        iview=findViewById(R.id.imageview);
        Intent i=getIntent();
        Bundle bundle=i.getExtras();
        mySongs=(ArrayList)bundle.getParcelableArrayList("songs");
        String songName=i.getStringExtra("songname");
        position=bundle.getInt("pos",0);
        textName.setSelected(true);
        Uri uri=Uri.parse(mySongs.get(position).getPath());
        sname=mySongs.get(position).getName();
        textName.setText(sname);
        if(mediaPlayer!=null) {
            if(cindex!=position) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
                mediaPlayer.start();
            }
        }
        else
        {
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
        }
        cindex=position;
        updateSeek=new Thread(){
            public void run(){
                int totalDuration=mediaPlayer.getDuration();
                int currentPosition=0;
                while(currentPosition<totalDuration){
                    try{
                        sleep(500);
                        currentPosition=mediaPlayer.getCurrentPosition();
                        seek.setProgress(currentPosition);
                    }
                    catch(InterruptedException | IllegalStateException e){
                        e.printStackTrace();
                    }
                }
            }
        } ;
        seek.setMax(mediaPlayer.getDuration());
        updateSeek.start();
        seek.getProgressDrawable().setColorFilter(getResources().getColor(R.color.av_dark_blue), PorterDuff.Mode.MULTIPLY);
        seek.getThumb().setColorFilter(getResources().getColor(R.color.av_dark_blue), PorterDuff.Mode.SRC_IN);
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seek.getProgress());
            }
        });

        String endTime=createTime(mediaPlayer.getDuration());
        textStop.setText(endTime);
        final Handler handler=new Handler();
        final int delay=1000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String CurrentTime=createTime(mediaPlayer.getCurrentPosition());
                textStart.setText(CurrentTime);
                handler.postDelayed(this,delay);
            }
        },delay);

        play.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(mediaPlayer.isPlaying()){
                    play.setBackgroundResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                }
                else
                {
                    play.setBackgroundResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mediaPlayer.stop();
                mediaPlayer.release();
                position=((position+1)%mySongs.size());
                cindex=position;
                Uri uri1=Uri.parse(mySongs.get(position).getPath());
                mediaPlayer=MediaPlayer.create(getApplicationContext(),uri1);
                sname=mySongs.get(position).getName();
                textName.setText(sname);
                mediaPlayer.start();
                play.setBackgroundResource(R.drawable.ic_pause);
                startAnimation(iview);
                int audioSessionId=mediaPlayer.getAudioSessionId();
                if(audioSessionId!=-1)
                    bvis.setAudioSessionId(audioSessionId);
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position=((position-1)<0)?mySongs.size()-1:(position-1);
                cindex=position;
                Uri u =Uri.parse(mySongs.get(position).getPath());
                mediaPlayer=MediaPlayer.create(getApplicationContext(),u);
                sname=mySongs.get(position).getName();
                textName.setText(sname);
                mediaPlayer.start();
                play.setBackgroundResource(R.drawable.ic_pause);
                startAnimation(iview);
                int audioSessionId=mediaPlayer.getAudioSessionId();
                if(audioSessionId!=-1)
                    bvis.setAudioSessionId(audioSessionId);
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                next.performClick();
            }
        });
        fast_forward.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
                }
            }
        });
        fast_rewind.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
                }
            }
        });
        int audioSessionId=mediaPlayer.getAudioSessionId();
        if(audioSessionId!=-1)
        bvis.setAudioSessionId(audioSessionId);
    }
    public void startAnimation(View view)
    {
        ObjectAnimator animator= ObjectAnimator.ofFloat(view,"rotation",0f,360f);
        animator.setDuration(1000);
        AnimatorSet as=new AnimatorSet();
        as.playTogether(animator);
        as.start();
    }
    public String createTime(int duration){
        int minutes=(int) ((duration % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((duration % (1000 * 60 * 60)) % (1000 * 60)) / 1000);
        time=minutes+":";
        if(seconds<10)
            time+="0";
        time+=seconds;
        return time;
    }
}