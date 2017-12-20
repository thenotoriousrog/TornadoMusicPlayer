package com.example.thenotoriousrog.tornadomusicplayer.Backend;

import android.os.Environment;
import android.widget.Toast;

import com.example.thenotoriousrog.tornadomusicplayer.Activities.MainActivity;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Created by thenotoriousrog on 5/30/17.
 *
 * This class is simply a Thread to grab and collect songs. The hope is that it will speed up the app so that it is not skipping frames when searching for songs.
 */

public class SongFinder extends Thread {

    private File musicDirectory; // the directory where our songs reside.
    private MainActivity mainActivity; // an instance of the MainActivity to send back the calculated information
    private ArrayList<String> songs = new ArrayList<>(); // holds all songs found in this directory.
    private ArrayList<String> folders = new ArrayList<>(); // holds all folders found in this directory.

    public SongFinder(File dir, MainActivity main)
    {
        musicDirectory = dir;
        mainActivity = main;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable()
    {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    // this method will find songs within the directory passed into it and return with a File[] full of .mp3 files.
    protected File[] getSongsInDirectory(File dir)
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
    protected void addSongsToList(File[] songsToAdd)
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

    // this is the method that is ran when the Thread is started. This will grab all the songs and send them back to MainActivity to be used.
    public void run()
    {
        boolean readyToRead = false; // initially set to false because we don't know if the SD card is ready to be read or not.

        while(readyToRead == false) // continue to wait until the SD card is ready for reading.
        {
            // this will only cause the while loop to stop running and will then have the app look for songs.
            if(isExternalStorageReadable() == true)
            {
                System.out.println("The SD card is readable.");
                readyToRead = true;
            }

        } // end of while loop.

        File[] songFiles = musicDirectory.listFiles(); // this will hold all song files OR it will holds the names of song Folders within the directory depending on how the user organizes things.

        File[] filteredSongFiles = getSongsInDirectory(musicDirectory); // try to find and get songs in the directory obtained from the user.

        if(filteredSongFiles.length == 0 && songFiles.length == 0) // no songs were detected, display message to user.
        {
            Toast.makeText(mainActivity, "No Songs or Song Folders were found :(", Toast.LENGTH_SHORT).show();

            // send back null lists to handle the problem
            mainActivity.setFoldersList(null);
            mainActivity.setSongsList(null);
        }

        // if this occurs, it is likely that the app itself will crash
        if(filteredSongFiles == null || songFiles == null) // something failed, tell the user and recommend to check their directory.
        {
            // this toast tells the user that something failed and they should check their directory one more time.
            Toast.makeText(mainActivity, "I failed while looking for your songs :( check your directory.", Toast.LENGTH_SHORT).show();

            // send back null lists to handle the problem
            mainActivity.setFoldersList(null);
            mainActivity.setSongsList(null);
        }

        if(filteredSongFiles.length == 0 && songFiles.length > 0) // this means no songs are in the directory, but folders do since songFiles is greater than 0. Scan all folders for songs.
        {
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

            // set folders and songs, be sure that mainActivity checks for null.
            mainActivity.setSongsList(songs);
            mainActivity.setFoldersList(folders);
        }
        else if(songFiles.length > filteredSongFiles.length) // we have a mixture of songs that are in folders and that are not in folders in this directory. Scan folders for other songs.
        {
            // search through each of songFiles and lets check to see if it is a song or a directory.
            for(int i = 0; i < songFiles.length; i++)
            {
                if(songFiles[i].isDirectory()) // search the directory for songs and add them as they are being found.
                {
                    File[] folderSongs = getSongsInDirectory(songFiles[i]); // get the songs in this folder/directory.
                    addSongsToList(folderSongs); // add the songs to the song list.

                    // manually add the folder in which we found the songs.
                    folders.add(songFiles[i].toString()); // add the folder to the list.
                }
                else if(songFiles[i].toString().contains(".mp3")) // this file is a song, add it manually into the song arraylist.
                {
                    songs.add(songFiles[i].toString()); // add this song to the songs list. We want to parse this song however and make it look better.
                }
                else // the item in this arraylist is not a song. Don't do anything with it.
                {
                    // do nothing, it is not a song.
                }
            }

            mainActivity.setSongsList(songs);
            mainActivity.setFoldersList(folders);

        }
        else // songFiles.length == filteredSongFiles.length meaning, there are no folders in this directory, only songs themselves.
        {
            addSongsToList(songFiles);
            mainActivity.setSongsList(songs); // only the songs files are set
            mainActivity.setFoldersList(null); // have mainActivity check for this as this is not good to have happen. If this happens, we must make sure to not to use these folders.
        }


    }

}
