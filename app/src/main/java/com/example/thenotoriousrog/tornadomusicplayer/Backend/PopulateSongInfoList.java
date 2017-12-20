package com.example.thenotoriousrog.tornadomusicplayer.Backend;

import com.example.thenotoriousrog.tornadomusicplayer.Activities.MusicPlayer;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Created by thenotoriousrog on 7/2/17.
 * This class will convert the songs that were grabbed into the song InfoList and it will return this songInfo list to the MainActivity.
 */

public class PopulateSongInfoList extends Thread {

    private ArrayList<String> songs;
    private ArrayList<String> folders;
    private MusicPlayer musicPlayer; // allows us to set the folders and the SongInfo arraylist to be used by the user.
    private File musicDirectory; // allows us to set the music directory.

    // sets our variables to allow for the lists to be set an worked.
    public PopulateSongInfoList(ArrayList<String> SONGS, ArrayList<String> FOLDERS, MusicPlayer activity, File dir)
    {
        songs = SONGS;
        folders = FOLDERS;
        musicPlayer = activity;
        musicDirectory = dir;
    }

    // converts a list of songs and their paths into their SongInfo and returns a new list of SongInfo!
    private ArrayList<SongInfo> convertToSongInfoList(ArrayList<String> songsList)
    {
        //System.out.println("Am I failing here?");
        ArrayList<SongInfo> songsInfo = new ArrayList<SongInfo>(); // holds the songs and their info into this list.

        // iterate through the arraylist of song paths and convert each one into a SongInfo, and add to list of SongInfos.
        for(int i = 0; i < songsList.size(); i++)
        {
            SongInfo newSong = new SongInfo(); // create a new SongInfo object, passing in the activity itself.
            newSong.getandSetSongInfo(songsList.get(i)); // get the song path and send it to SongInfo to be parsed into it's song details.

            if(newSong.getSongName() == null) {
                // song is corrupted do not add this song.
            }
            else
            {
                songsInfo.add(newSong); // add the new SongInfo into list of SongInfos.
            }

        }

        // System.out.println("Did I finish grabbing the info?");
        return songsInfo; // return this list back to caller. All song information has been parsed successfully.
    }

    // this method will find songs within the directory passed into it and return with a File[] full of .mp3 files.
    private File[] getSongsInDirectory(File dir)
    {
        // filteredSongFiles holds all of the mp3 files that are found
        File[] filteredSongFiles = dir.listFiles(new FilenameFilter() {

            // This will search for songs and ensure that whatever is retrieved is an mp3 and nothing else.
            @Override
            public boolean accept(File dir, String songName) {
                return songName.contains(".mp3");
            }
        });

        return filteredSongFiles; // returns the File[] of filtered songs. It can return 0 if no songs were found.
    }

    // this method will add songs to the list associated with listing songs. This is the tricky part because we need to make sure it's the correct list we are adding it too. Look at the i.d.
    private void addSongsToList(File[] songsToAdd)
    {
        // loop through each file and add to the songs arrayList
        for(int i = 0; i < songsToAdd.length; i++)
        {
            songs.add(songsToAdd[i].toString()); // add the filename to the arraylist 'songs'. We will want to Parse the name to make it look better as we develop the app as we go. It's ugly right now.
        }
    }

    // this method will do similar behavior as addSongsToList except it will only add the folders into the list.
    private void addFoldersToList(File[] foldersToAdd)
    {
        // loop through each file and add to the songs arrayList
        for(int i = 0; i < foldersToAdd.length; i++)
        {
            folders.add(foldersToAdd[i].toString()); // add the filename to the arraylist 'songs'. We will want to Parse the name to make it look better as we develop the app as we go. It's ugly right now.
        }
    }

    // this method will be in control of grabbing and converting all songs into SongInfo's and returning that list to the MainActivity.
    public void run()
    {
        ArrayList<SongInfo> songInfoList = new ArrayList<>(); // create an arraylist of SongInfo so to be returned to the list.

        // todo: create customized messages to send back to the MainActivity that we can use to display messages if an error occurs.

        // populate the SongInfoList in here.
        if(songs == null && folders == null) // no songs were found.
        {
            // TODO: tell the user that we could not find any songs, use Toast
        }
        else if(songs != null && folders == null) // we only found songs, no folders were selected.
        {
            songInfoList = convertToSongInfoList(songs);
        }
        else if(songs != null && folders != null) // we have songs and folder found.
        {
            songInfoList = convertToSongInfoList(songs);
        }
        else if(songs == null && folders != null) // we did not find songs, but found only folders
        {
            File[] songFiles = musicDirectory.listFiles(); // get either song files or regular files.

            // Loop through each folder and search for songs in it.
            for(int i = 0; i < songFiles.length; i++)
            {
                File[] folderSongs = getSongsInDirectory(songFiles[i]); // songFiles[i] is one of the directories, thus, we need to check for songs in it.
                if(folderSongs.length == 0)
                {
                    // do nothing, there are no songs in this folder, or it is another folder inside, we are not going to look again.

                    // todo: explain in the help tab that only a maximum of one folder can be searched in a directory. Layered folder will not.
                }

                if(folderSongs.length > 0) // we found songs in this folder! Now we can add to our list.
                {
                    addSongsToList(folderSongs); // adds songs to the songs arraylist.
                }
            }

            addFoldersToList(songFiles); // find all of the folders and add them into the folders list.

            songInfoList = convertToSongInfoList(songs);
        }
        else // unknown result
        {
            // TODO: handle this type of condition if it ever occurs.
        }

        // todo: determine if I need to use the two method calls below if the app breaks this could be why.
      //  mainActivity.setSongsList(songs); // send songs back.
      //  mainActivity.setFoldersList(folders); // send folders back.
        musicPlayer.setSongInfoList(songInfoList); // send the songInfo list back to be used by the list itself.

    }
}
