package com.example.thenotoriousrog.tornadomusicplayer;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by thenotoriousrog on 8/7/17.
 * This class will be a data structure that simply holds the name of the playlist we are trying to create and the list of songs (In SongInfo data) itself.
 * This class is serializable to allow for the class to be stored into main memory.
 */

public class Playlist implements Parcelable, Serializable {

    private final String name; // the name of the playlist itself.
    private ArrayList<SongInfo> songs; // holds the songs of the playlist.

    // Constructor has to be used in order to set the data of the songs that we want to use.
    public Playlist(String playlistName, ArrayList<SongInfo> playlistSongs)
    {
        name = playlistName;
        songs = playlistSongs;
    }

    // handle this type of constructor when we need to make some work with it.
    private Playlist(Parcel in)
    {
        songs = new ArrayList<SongInfo>(); // create a new list that can be filled once the class loads everything correctly.
        name = in.readString(); // read in the name of the playlist from the parcel.
        in.readList(songs, SongInfo.class.getClassLoader());
    }

    // returns the name of the playlist.
    public String name()
    {
        return name;
    }

    // returns the songs of the playlist.
    public ArrayList<SongInfo> songs()
    {
        return songs;
    }


    // This method takes a list of songs and replaces the current list of songs for the specific playlist.
    public void setSongs(ArrayList<SongInfo> newSongs)
    {
        songs = newSongs; // sets the new list of songs.
    }

    @Override
    public int describeContents() {
        return 0;
    }


    // Writes the correct data to the parcel that the app will be reading.
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(name); // write the name of the playlist to the parcel
        dest.writeList(songs); // write songs to the parcel
    }

    // This will control how the Playlist object is created when Parcelable is created.
    public static final Creator<Playlist> CREATOR = new Creator<Playlist>()
    {
        @Override
        public Playlist createFromParcel(Parcel in)
        {
            return new Playlist(in);
        }

        @Override
        public Playlist[] newArray(int size)
        {
            return new Playlist[size];
        }
    };
}
