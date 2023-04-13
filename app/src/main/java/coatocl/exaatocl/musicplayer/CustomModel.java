package coatocl.exaatocl.musicplayer;

import java.io.Serializable;

public class CustomModel implements Serializable {
    private String path;
    private String title;
    private String artist;
    private int duration;
    private int size;
    private int  date;

    public CustomModel(String path, String title, String artist, int duration, int size, int date) {
        this.path = path;
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.size = size;
        this.date = date;
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public int getDuration() {
        return duration;
    }

    public int getSize() {
        return size;
    }

    public int getDate() {
        return date;
    }
}