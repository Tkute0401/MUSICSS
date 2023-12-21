package com.example.onlinemusicapp;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import com.example.onlinemusicapp.Model.Getsongs;
import com.example.onlinemusicapp.OfflineMusicPlayer.MyMediaPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class OnlineMediaPlayer extends AppCompatActivity {

    TextView titleTv, currentTimeTv, totalTimeTv;
    SeekBar seekBar;
    ImageView pausePlay, nextBtn, previousBtn, musicIcon;
    ArrayList<Getsongs> songsList;
    Getsongs currentSong;
    MediaPlayer mediaPlayer;
    int x = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_media_player);

        titleTv = findViewById(R.id.song_titleol);
        currentTimeTv = findViewById(R.id.current_timeol);
        totalTimeTv = findViewById(R.id.total_timeol);
        seekBar = findViewById(R.id.seek_barol);
        pausePlay = findViewById(R.id.pause_playol);
        nextBtn = findViewById(R.id.nextol);
        previousBtn = findViewById(R.id.previousol);
        musicIcon = findViewById(R.id.music_icon_bigol);

        titleTv.setSelected(true);
        songsList = (ArrayList<Getsongs>) getIntent().getSerializableExtra("LIST");

        // Use MyMediaPlayer.getInstance() instead of creating a new instance
        mediaPlayer = MyMediaPlayer.getInstance();

        setResourcesWithMusic();

        OnlineMediaPlayer.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    int totalDuration = mediaPlayer.getDuration();

                    seekBar.setMax(totalDuration);
                    seekBar.setProgress(currentPosition);
                    currentTimeTv.setText(convertToMMSS(currentPosition + ""));
                    totalTimeTv.setText(convertToMMSS(totalDuration + ""));

                    if (mediaPlayer.isPlaying()) {
                        pausePlay.setImageResource(R.drawable.ic_pause);
                        musicIcon.setRotation(x++);
                    } else {
                        pausePlay.setImageResource(R.drawable.ic_play);
                        musicIcon.setRotation(0);
                    }
                }
                new Handler().postDelayed(this, 100);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        Button btnShowBottomSheet = findViewById(R.id.btnShowBottomSheet);
        btnShowBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialogFragment bottomSheetFragment = new BottomSheetFragment();
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
            }

        });
    }

    void setResourcesWithMusic() {
        currentSong = songsList.get(MyMediaPlayer.currentIndex);

        titleTv.setText(currentSong.getSongTitle());
        totalTimeTv.setText(convertToMMSS(currentSong.getsongDuration()));

        pausePlay.setOnClickListener(v -> pausePlay());
        nextBtn.setOnClickListener(v -> playNextSong());
        previousBtn.setOnClickListener(v -> playPreviousSong());

        playMusic();
    }

    private void playMusic() {
        if (mediaPlayer == null) {
            mediaPlayer = MyMediaPlayer.getInstance();
        }

        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getSonglink());
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                    seekBar.setProgress(0);
                    //seekBar.setMax(mediaPlayer.getDuration());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playNextSong() {
        if (MyMediaPlayer.currentIndex == songsList.size() - 1)
            return;
        MyMediaPlayer.currentIndex += 1;
        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    private void playPreviousSong() {
        if (MyMediaPlayer.currentIndex == 0)
            return;
        MyMediaPlayer.currentIndex -= 1;
        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    private void pausePlay() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
        else
            mediaPlayer.start();
    }

    public static String convertToMMSS(String duration) {
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }

    //@Override
    //protected void onDestroy() {
      //  super.onDestroy();
        //releaseMediaPlayer();
    //}

    //private void releaseMediaPlayer() {
    //    if (mediaPlayer != null) {
    //        mediaPlayer.release();
    //        //mediaPlayer = null;
    //    }
    //admin@gmail/com}
}
