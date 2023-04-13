package coatocl.exaatocl.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.imageview.ShapeableImageView;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class musicplayer extends AppCompatActivity {

    TextView startSeek;
    TextView endSeek;
    TextView title;
    TextView artistName123;
    ImageButton playButton,forWard,backWard,previous,next;
    SeekBar seekBar;
    ShapeableImageView imageView;
    double startTime = 0;
    double endTime = 0;
    int forwardTime = 15000;
    int backwardTime = 15000;
    int oneTime = 0;
    Handler handler = new Handler();
    MediaPlayer mediaPlayer;
    Uri uri;
    int position;
    String sName,aName;
    Runnable UpdateSongTime;
    private ArrayList<CustomModel> mySongs;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musicplayer);

        startSeek = findViewById(R.id.startSeek);
        endSeek = findViewById(R.id.endSeek);
        title = findViewById(R.id.title);
        artistName123 = findViewById(R.id.artistName123);

        seekBar = findViewById(R.id.seekBar);
        imageView = findViewById(R.id.imageView);
        forWard = findViewById(R.id.forWard);
        backWard = findViewById(R.id.backWard);
        playButton = findViewById(R.id.playButton);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);

        seekBar.setClickable(true);

        if(mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

//        intent for get data
        Intent i = getIntent();
        Bundle bundle = i.getBundleExtra("BUNDLE");
        mySongs = (ArrayList<CustomModel>) bundle.getSerializable("song");
        position = bundle.getInt("pos",0);
        sName= (String) bundle.get("songName");
        aName = (String) bundle.get("artistNm");

        title.setText(sName);
        artistName123.setText(aName);

//        convert string to uri
        String outputFile = mySongs.get(position).getPath();
        Log.d("checkPath",""+outputFile);
        uri = FileProvider.getUriForFile(getApplicationContext(),BuildConfig.APPLICATION_ID + ".provider",new File(outputFile));
        mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);

        handler.postDelayed(UpdateSongTime,100);
//        set text of seek bar's both sided textView
        endTime = mediaPlayer.getDuration();
        startTime =mediaPlayer.getCurrentPosition();

        endSeek.setText(String.format(" %02d : %02d ",TimeUnit.MILLISECONDS.toMinutes((long) endTime),
                TimeUnit.MILLISECONDS.toSeconds((long) endTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) endTime))) );

        startSeek.setText(String.format("%02d : %02d ",TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                TimeUnit.MILLISECONDS.toSeconds((long) startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime))) );

         UpdateSongTime= new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                startTime = mediaPlayer.getCurrentPosition();
                startSeek.setText(String.format("%02d : %02d ",TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)))
                );
                seekBar.setProgress((int) startTime);
                handler.postDelayed(this, 100);
            }
    };

        mediaPlayer.start();
        handler.postDelayed(UpdateSongTime,100);
        if (oneTime == 0) {
            seekBar.setMax((int) endTime);
            oneTime = 1;
        }

        playButton.setOnClickListener(v ->
        {
            if(mediaPlayer.isPlaying())
            {
                Toast.makeText(musicplayer.this, "PAUSE", Toast.LENGTH_SHORT).show();
                playButton.setImageResource(R.drawable.ic_pause_black_24dp);
                mediaPlayer.pause();
            }

            else
            {
                mediaPlayer.start();
                playButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                Toast.makeText(musicplayer.this, "PLAYING", Toast.LENGTH_SHORT).show();

                endTime = mediaPlayer.getDuration();
                startTime = mediaPlayer.getCurrentPosition();

//                if (oneTime == 0) {
//                    seekBar.setMax((int) endTime);
//                    oneTime = 1;
//                }

                endSeek.setText(String.format(" %02d : %02d ",
                        TimeUnit.MILLISECONDS.toMinutes((long) endTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) endTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) endTime))));

                startSeek.setText(String.format("%02d : %02d ",
                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime))));

            }
            seekBar.setProgress((int)startTime);
            handler.postDelayed(UpdateSongTime,100);
        });

        forWard.setOnClickListener(v -> {

                int temp = (int)startTime;

                if((temp+forwardTime)<=endTime)
                {
                    startTime = startTime + forwardTime;
                    mediaPlayer.seekTo((int) startTime);
                    Toast.makeText(getApplicationContext(),"Jumped FW 15 seconds",Toast.LENGTH_SHORT).show();
                }
                else
                    {
                    Toast.makeText(getApplicationContext(),"Not able to jump FW 15 seconds",Toast.LENGTH_SHORT).show();
                    }
        });

        backWard.setOnClickListener(v -> {

                int temp = (int)startTime;

                if((temp-backwardTime)>0){
                    startTime = startTime - backwardTime;
                    mediaPlayer.seekTo((int) startTime);
                    Toast.makeText(getApplicationContext(),"Jumped BW 15 seconds",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Not able to jump BW 15 seconds",Toast.LENGTH_SHORT).show();
                }
        });

        previous.setOnClickListener(v ->
        {
            mediaPlayer.stop();
            mediaPlayer.release();
//            position =((position-1)%mySongs.size());
            position =((position-1)<0)?(mySongs.size()-1):position-1;
            String outputFile2 = mySongs.get(position).getPath();
            Uri uriPrevious = FileProvider.getUriForFile(getApplicationContext(),BuildConfig.APPLICATION_ID + ".provider",new File(outputFile2));
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uriPrevious);
            sName= mySongs.get(position).getTitle();
            aName =mySongs.get(position).getArtist();

            title.setText(sName);
            artistName123.setText(aName);

            mediaPlayer.start();
            endTime = mediaPlayer.getDuration();
            endSeek.setText(String.format(" %02d : %02d ",TimeUnit.MILLISECONDS.toMinutes((long) endTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) endTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) endTime))) );
        });

        next.setOnClickListener(v ->
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            position =((position+1)%mySongs.size());
            String outputFile2 = mySongs.get(position).getPath();
            Uri uriNext = FileProvider.getUriForFile(getApplicationContext(),BuildConfig.APPLICATION_ID + ".provider",new File(outputFile2));
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uriNext);
            sName= mySongs.get(position).getTitle();
            aName =mySongs.get(position).getArtist();

            title.setText(sName);
            artistName123.setText(aName);

            mediaPlayer.start();
            endTime = mediaPlayer.getDuration();
            endSeek.setText(String.format(" %02d : %02d ",TimeUnit.MILLISECONDS.toMinutes((long) endTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) endTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) endTime))) );
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if (mediaPlayer != null && fromUser)
                {
                    mediaPlayer.seekTo(progress);
                    seekBar.setProgress(progress);
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

    @Override
    public void onBackPressed ()
    {
        if (mediaPlayer != null)
            mediaPlayer.stop();
        super.onBackPressed();
    }

}

