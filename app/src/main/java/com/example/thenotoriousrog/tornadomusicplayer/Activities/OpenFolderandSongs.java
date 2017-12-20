package com.example.thenotoriousrog.tornadomusicplayer.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Created by thenotoriousrog on 5/25/17.
 *
 * This class will be what's called when an item is clicked in the folder listview in MainActivity. It will take the folder that was clicked and pass it to this Activity which will update the list of songs
 * for the user to choose from.
 */
public class OpenFolderandSongs extends Activity {

    // variables
    ArrayList<String> folders; // holds all the names of the folders passed in
    ArrayList<String> folderSongs = new ArrayList<String>(); // holds all the songs in the selected folder.
    int selectedFolderPosition; // holds the position of the folder in the ArrayList that was selected.
    ArrayAdapter<String> songsInFolderAdapter; // holds the songs inside the selected folder allowing it to be displayed in the listView.
    int OPEN_FOLDER_INTENT = 100; // this is the result code that we want to pass back to the intent that way we know to look for specific intent codes.


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


    // necessary for us to perhaps update the list view oursevles in this activity. As of right now, I am not exactly sure what we should, if we should update the list view ourselves or what exactly.

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent openFolderandDisplaySongs = getIntent();
        Bundle intentBundle = openFolderandDisplaySongs.getBundleExtra("FolderListBundle");

        folders = intentBundle.getStringArrayList("FoldersList");
        System.out.println("is folders null? " + folders);
        selectedFolderPosition = intentBundle.getInt("SelectedFolderPosition"); // gets the position of the item that was selected. Used to get the filename/path to get the songs in that folder.

        // now we want to get the songs in the folder.
        String foldername = folders.get(selectedFolderPosition); // grab the filename of the folder that was selected.
        File folderDirectory = new File(foldername); // convert the foldername into a file that we can use to open up and get the songs in that folder.
        File[] songs; // will hold all the .mp3 files in this folder.

        if(folderDirectory.isDirectory()) // check to make sure that this is a valid directory before we go searching through it.
        {
            // get the songs in this directory and add it to the arrayList of folderSongs
            songs = getSongsInDirectory(folderDirectory); // get all of the songs in the folder.

            // TODO: check to make sure that their songs in the folder before trying to add them all or the app will crash. I know it works in mine, but it may not for other users.

            // add songs into the arrayList.
            for(int i = 0; i < songs.length; i++)
            {
                folderSongs.add(songs[i].toString()); // adds all the song names into the arraylist of folder songs.
            }
        }
        else
        {
            // TODO: create a toast to let the user know that selecting this folder has failed. Also check if it exists, if it doesn't tell the user that the folder no longer exists.
        }

        System.out.println("We are getting ready to send the result back now! We found all of the songs!");

        // may have to create some method in MainActivity that will be called to allow the folderClick to pass the data to MainActivity to set the data that is needed.
        Intent intentResult = new Intent(); // this is the intent that will allow us to send the data back.
        Bundle data = new Bundle(); // holds the data that we are going to pass to intentResult.
        data.putStringArrayList("SongsInFolder", folderSongs); // send in the arrayList with the songs in the folder so that we may be able to update the list view we are trying to get.

        intentResult.putExtra("resultBundle", data);

        setResult(OPEN_FOLDER_INTENT, intentResult); // send the result to be finished.
        finish(); // finish up this intent which will send it back to the calling function

    }

}
