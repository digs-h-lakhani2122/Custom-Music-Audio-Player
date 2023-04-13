package coatocl.exaatocl.musicplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>
{
    private ArrayList<CustomModel>musicList2;
    private Context context;
    private int playingID = -1;
    private MediaPlayer mediaPlayer;

    Adapter(MediaPlayer mediaPlayer,ArrayList<CustomModel> musicList2, Context context)
    {
        this.mediaPlayer = mediaPlayer;
        this.musicList2 = musicList2;
        this.context =context;
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater =LayoutInflater.from(parent.getContext());
        View view =layoutInflater.inflate(R.layout.show,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position)
    {
        CustomModel customModel = musicList2.get(position);
        holder.music_name.setText(customModel.getTitle());
        holder.music_duration.setText(getDuration(customModel.getDuration()));
        holder.music_size.setText(getSize(customModel.getSize()));
        holder.music_artist_name.setText(customModel.getArtist());

        if(playingID == position)
        {
            holder.image.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        }
        else
        {
            holder.image.setImageResource(R.drawable.ic_music_note_black_24dp);
        }

        @SuppressLint("SimpleDateFormat")
        String dateAsText = new SimpleDateFormat("dd-MM-yyyy   hh:mm a").format(new Date(Integer.parseInt(String.valueOf(customModel.getDate()))  * 1000L));
        holder.music_date.setText(dateAsText);

        holder.image.setOnClickListener(v -> {

                    if(mediaPlayer.isPlaying())
                    {
                        if(playingID == position)
                        {
                            mediaPlayer.pause();
                            mediaPlayer.reset();//call when media player gets free
                            playingID = -1;
                            holder.image.setImageResource(R.drawable.ic_pause_black_24dp);
                            Toast.makeText(v.getContext(), "pause...", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            mediaPlayer.pause();
                            mediaPlayer.reset();
                            try
                            {
                                mediaPlayer.setDataSource(musicList2.get(position).getPath());
                                mediaPlayer.prepare();
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }

                            mediaPlayer.start();
                            playingID = position;
                            holder.image.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                            Toast.makeText(v.getContext(),"playing...",Toast.LENGTH_SHORT).show();

                        }
                    }

                    else
                    {

                        try
                        {
                            mediaPlayer.setDataSource(musicList2.get(position).getPath());
                            mediaPlayer.prepare();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        mediaPlayer.start();
                        playingID = position;
                        holder.image.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                        Toast.makeText(v.getContext(),"playing...",Toast.LENGTH_SHORT).show();
                    }

            notifyDataSetChanged();
    });


        holder.itemView.setOnClickListener(v -> {

            if(mediaPlayer.isPlaying())
            {
                mediaPlayer.pause();
                mediaPlayer.reset();//call when media player gets free
            }

                String songName = customModel.getTitle();
                String artistName = customModel.getArtist();

                Intent intentShow =new Intent(v.getContext(),musicplayer.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("songName",songName);
                bundle.putSerializable("artistNm",artistName);
                bundle.putSerializable("song",musicList2);
                intentShow.putExtra("BUNDLE",bundle);
                bundle.putInt("pos",position);
                context.startActivity(intentShow);

                Toast.makeText(v.getContext(),"Song's Title:"+ (customModel.getTitle()),Toast.LENGTH_SHORT).show();

        });
    }

    @Override
    public int getItemCount()
    {
        return musicList2.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageButton image;
        TextView music_name, music_duration, music_size,music_date,music_artist_name;
        LinearLayout linearLayout;

        private ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            image =itemView.findViewById(R.id.logo);
            linearLayout=itemView.findViewById(R.id.linear);
            music_name = itemView.findViewById(R.id.name);
            music_duration = itemView.findViewById(R.id.duration);
            music_size = itemView.findViewById(R.id.size);
            music_date = itemView.findViewById(R.id.date);
            music_artist_name = itemView.findViewById(R.id.artist_name);
        }
    }

    @SuppressLint("DefaultLocale")
    private String getDuration(int totalDuration)
    {
        String totalDurationText;
        int hr =totalDuration/(1000*60*60);
        int min =(totalDuration % (1000*60*60))/(1000*60);
        int sec =(((totalDuration % (1000*60*60)) % (1000*60*60)) % (1000*60))/1000 ;

        if(hr<1)
        {
            totalDurationText =String.format("%02d : %02d",min,sec);
        }
        else
        {
            totalDurationText =String.format("%02d : %02d : %02d",hr,min,sec);
        }

         return totalDurationText;
    }

    private String getSize(long bytes)
    {
        String muSize;

        double k = bytes/1024.0;
        double m = (bytes/1024.0)/1024.0;
        double g = ((bytes/1024.0)/1024.0)/1024.0;
        double t = (((bytes/1024.0)/1024.0)/1024.0)/1024.0;

        DecimalFormat dec =new DecimalFormat("0.00");

        if(t>1)
        {
            muSize = dec.format(t).concat("  TB");
        }
        else if (g>1)
        {
            muSize = dec.format(g).concat("  GB");
        }
        else if (m>1)
        {
            muSize = dec.format(m).concat("  MB");
        }
        else if (k>1)
        {
            muSize = dec.format(k).concat("  KB");
        }
        else
        {
            muSize = dec.format(g).concat("  Bytes");
        }

        return muSize;
    }

}