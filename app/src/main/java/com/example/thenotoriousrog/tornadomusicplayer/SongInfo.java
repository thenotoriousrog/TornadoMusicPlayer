package com.example.thenotoriousrog.tornadomusicplayer;

import android.app.Activity;
import android.media.MediaMetadataRetriever;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * Created by thenotoriousrog on 5/27/17.
 * This class is in charge of getting a song, and extracting all of its data and setting the associated fields.
 * Class is parcelable to allow the list to be able be sent using Bundles and intents, etc.
 * If we do send the SongInfo as parcelable we have to be aware that the album art will NOT be sent over.
 * Activity is extended to allow us to be able to get resources to set album art details and what not.
 */
public class SongInfo implements Parcelable, Serializable{

    private String songName = ""; // holds the name of a song on the mp3 file.
    private String artistName = ""; // holds the artist name of on an mp3 file.
    private String songDuration = ""; // holds the time of an mp3 file in milliseconds. This is useful for when I have a countdown timer and what not.
   // private Bitmap songImage; // this will hold the image of the song for me to be able to display it on the layout.
    private String songPath = ""; // set the path of the song itself.
    private String songTime = ""; // this will display the time of the song in in minutes:seconds format i.e. 3:34
    private Activity currentActivity; // this allows for whatever current activity to be used when creating a copy of the SongInfo class.

    //MediaMetadataRetriever mmr; // used to extract song info from an mp3 file.

    // constructor for when we want to send this class a parcelable arraylist. This will create our items to be used by the system.
    private SongInfo(Parcel in)
    {
        songName = in.readString(); // read in the songname
        artistName = in.readString(); // read in the artist name.
        songDuration = in.readString(); // read in the song duration.
        songPath = in.readString(); // read in the song path
        songTime = in.readString(); // read in the song time.
    }

    // default constructor, this should ONLY be used for when we do NOT need to have any album art just basic information to be extracted!
    public SongInfo() {
        // this will allow for a general creation of the SongInfo list.
    }


    // sets the name of the song.
    public void setSongName(String name) {
        songName = name;
    }

    // sets the artist name of the song.
    public void setArtistName(String name) {
        artistName = name;
    }

    // sets the duration of the song itself. **WARNING: we do not know exactly how the measurements of the song is configured. Look into this before trying to apply the duration of the song.
    public void setSongDuration(String time) {
        songDuration = time;
    }

    public void setSongPath(String path) {
        songPath = path;
    }

    /* not needed anymore we will process images on a need by need basis
    public void setSongImage(Bitmap image)
    {
        songImage = image;
    }
    */

    public void setSongTime(String time)
    {
        songTime = time;
    }


    public String getSongName() {
        return songName;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getSongDuration() {
        return songDuration;
    }

    /* not needed, we will process images on a need by need basis in the future.
    public Bitmap getSongImage()
    {
        return songImage;
    }
    */

    public String getSongTime()
    {
        return songTime;
    }

    public String getSongPath()
    {
        return songPath;
    }

    // converts the time of a song into real minutes and seconds.
    public String convertToRealTime(int milliseconds)
    {
        String ms = String.format("%2d:%02d", TimeUnit.MILLISECONDS.toMinutes(milliseconds),
               // TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds)), // should not need this line to get the correct format for time.
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
        System.out.println("Time for this song is: " + ms);
        System.out.println("Song title is: " + getSongName());

        return ms; // return the newly formated string.
    }

    // this method takes in a song file path and will extract the song metadata and will also set it in the correct fields.
    protected void getandSetSongInfo(String songPath)
    {
        setSongPath(songPath);
        System.out.println("we have received a songpath in SongInfo that is: " + songPath);
        File pathToSong = new File(songPath);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever(); // class used to retrieve data from a song.
       // mmr.setDataSource(pathToSong.getAbsolutePath()); // takes the song path that was received to begin extraction.

        try
        {
            if(pathToSong.isFile() && pathToSong.exists()) // make sure the path is a file and it exists
            {
                mmr.setDataSource(pathToSong.getAbsolutePath());
                String songname = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE); // gets the song name
                String artistname = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST); // gets the artist of the song.
                String songduration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); // gets the song duration in the form of a string.
                String realTime = convertToRealTime(Integer.parseInt(songduration)); // convert to an actual int in order to get the full song and play it.


                // set the fields of SongInfo.
                setSongName(songname);
                setArtistName(artistname);
                setSongDuration(songduration);
                setSongPath(songPath);
                setSongTime(realTime);
                //setSongImage(image);
            }
        }
        catch (RuntimeException ex) {
            System.out.println("File: " + songPath + " may be corrupted");
            setSongName(null);
        }
        finally {

            try
            {
                mmr.release(); // release the MediaMetadataRetriever to free up valuable memory on the device.
            }
            catch (RuntimeException ex) {
                // ignore any errors that we get while releasing.
            }
        }


        mmr.release(); // be done with this file once we are done.
        // todo: decide if we want to extract the song image from the mp3 or not. It is definitely possible.
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Writes information to the parcel to allow for the same amount of information to be sent back.
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        // write the Strings to the destination.
        dest.writeString(songName);
        dest.writeString(artistName);
        dest.writeString(songDuration);
        dest.writeString(songPath);
        dest.writeString(songTime);
    }

    // This will control how we create the SongInfo again after our creation, this is important for when we want to send this as a Parcelable object.
    public static final Creator<SongInfo> CREATOR = new Creator<SongInfo>()
    {
        @Override
        public SongInfo createFromParcel(Parcel in)
        {
            return new SongInfo(in);
        }

        @Override
        public SongInfo[] newArray(int size)
        {
            return new SongInfo[size];
        }
    };
}
