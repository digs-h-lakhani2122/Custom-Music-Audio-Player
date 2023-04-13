package coatocl.exaatocl.musicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    public static final int REQUEST = 1;
    RecyclerView recycler;
    ArrayList<CustomModel> musicList2;
    Adapter adapter;
    MediaPlayer mediaPlayer;
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mediaPlayer=new MediaPlayer();
        recycler = findViewById(R.id.recycler);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        else
        {
            fetchMusicList();
        }

        musicList2 = new ArrayList<>();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void fetchMusicList()
    {
        ArrayList<CustomModel> musicList2 = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = new String[0];
        if (android.os.Build.VERSION.SDK_INT >= 30)
        {
            projection = new String[]{ MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA
                    , MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.DATE_MODIFIED, MediaStore.Audio.Media.SIZE, MediaStore.Audio.Media.ARTIST };
        }
        String orderBy = MediaStore.Audio.Media.DURATION + " DESC";
        Cursor cursor = getContentResolver().query(uri, projection, null, null, orderBy);
        assert cursor != null;

        int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
        int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
        int dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED);

        while (cursor.moveToNext())
        {
            String title = cursor.getString(1);
            int duration = cursor.getInt(durationColumn);
            String path = cursor.getString(2);
            String artist = cursor.getString(6);
            int size = cursor.getInt(sizeColumn);
            int date = cursor.getInt(dateColumn);

            title = title.substring(0, title.indexOf("."));
            CustomModel customModel = new CustomModel(path, title, artist, duration, size, date);
            musicList2.add(customModel);
        }
        showSong(musicList2);
        cursor.close();
    }

    private void showSong(ArrayList<CustomModel> musicList2)
    {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 1);
        recycler.setLayoutManager(gridLayoutManager);

        adapter = new Adapter(mediaPlayer,musicList2, MainActivity.this);
        recycler.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                fetchMusicList();
            }
            else
            {
                Toast.makeText(MainActivity.this, "Give Permission for Access storage", Toast.LENGTH_SHORT).show();
            }
        }
    }
}