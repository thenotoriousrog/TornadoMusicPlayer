package com.example.thenotoriousrog.tornadomusicplayer.Activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.RelativeLayout;

import com.example.thenotoriousrog.tornadomusicplayer.Fragments.LoadingScreenFragment;
import com.example.thenotoriousrog.tornadomusicplayer.Fragments.MainUIFragment;
import com.example.thenotoriousrog.tornadomusicplayer.R;
import com.example.thenotoriousrog.tornadomusicplayer.Backend.SerializeObject;
import com.example.thenotoriousrog.tornadomusicplayer.Backend.SongInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by thenotoriousrog on 8/24/17.
 * This class is in charge of scanning all of the files within the phone itself and also in charge of populating and setting up the mainUIFragment and display all of users songs and info to the user.
 * This is going to be a pretty intense class and will be done primarily through an AsyncTask.
 *
 * **WARNING: This activity also takes over for the music player activity. So just know that all of the specialized functions in the music player will no longer work and we will have to implement them here,
 *
 * Or better yet, we can just have the MusicPlayer activity do the scanning for us and we will just send a special message telling it that the user wants to scan and there is no special path that we need to be using.
 * todo: we should have the music player activity take care of this for us actually to ensure that the app works as smooth as it can.
 */

public class EF5ScanActivity extends Activity {

    // TODO: instructions on what is needed to have in this class are below.
    ArrayList<String> songPaths = new ArrayList<>(); // list to hold all of the song paths in the list.
    ArrayList<String> folders = new ArrayList<>(); // holds all of the folders in this list.
    ArrayList<SongInfo> songs = new ArrayList<>(); // holds the list of songInfos that the MainUIFragment is going to use.
    private MainUIFragment mainUIFragment; // a public copy of the mainUIFragment so that we can control what happens when the user presses the back button.
    final String Memory_Path = Environment.getExternalStorageDirectory().getPath() + "/"; // the path to main memory so that we can scan the entire phone in its entirety.

    // need to also send a list of playlists, although we may want to modify that so that the MainUIFragment can take an empty list of playlists and be okay with that.
    // need to also send in the music folder file path, which may not be possible because we are searching the entire phone for songs and not necessarily a single list at a time.




    // This method will scan a file and determine if it is a directory or not. If it is a directory it will continue scanning until it hits a file that is not a directory and then decide if it is a song.
    // if it is a song, then the addSongToList method will also add the song and the associated directory into their proper lists.
    private void scanDirectory(File directory)
    {
        if(directory != null)
        {
            File[] filesList = directory.listFiles(); // list the files within that directory.

            // make sure that the list of files exists and also ensure that there is at least one item in that directory before trying to extract anything from it.
            if(filesList != null && filesList.length > 0)
            {
                // for each file in the filesList, check if it is a directory or if it is a file.
                for(File file: filesList)
                {
                    if(file.isDirectory())
                    {
                        scanDirectory(file); // recursively loop back to this method and take the new file and search for songs within it
                    }
                    else // file is not a directory meaning it could be a song. Send to the add SongToList method to determine if that is something that we want to do or not.
                    {
                        addSongToList(file, directory); // send in the current file, and also send in the last directory received by the method to add that directory to the list of folders.
                    }
                }
            }
        }
    }

    // tries to find the folder in the list of folders and reports if the folder exists or not in the list.
    private boolean isDirectoryInFolders(File directory)
    {
        for(int i = 0; i < folders.size(); i++)
        {
            if(folders.get(i).equals(directory.getPath())); // the folder IS in the list of folders thus we need to add it.
            {
                return true; // this directory does exist in the list, report this to the calling method.
            }
        }

        return false; // the directory was not found in the folders list, report this to the calling method.
    }

    // This method will check to see if the file is a song or not. If the file is a song it will add the
    private void addSongToList(File song, File directory)
    {
        // check if the directory has been added to the list of folders if it has not, then add it otherwise skip adding it. Also, check if null, if null just check to see if it is a song then just add it.

        if(directory != null) // directory is not null meaning we have to check if the list exits and if it does then we want to add it into the list of songs.
        {
            if( isDirectoryInFolders(directory) == false ) // folder is new, check for song to be legit before adding to list of folders.
            {
                if(song.getName().endsWith(".mp3")) // this file is a song, add to the list of songs.
                {
                    songPaths.add(song.getPath()); // add the song path to the list.
                    folders.add(directory.getPath()); // add the directory path to the list of folders.
                }
            }
            else // folder already exists in the list, but we should double check that the specific file is a song and we should add that song to the list of songs.
            {
                if(song.getName().endsWith(".mp3")) // this file is a song, add to the list of songs.
                {
                    songPaths.add(song.getPath()); // add the song path to the list.
                }
            }
        }
        else // directory is null, we should just check to see if this is a song and add it to the list of songs.
        {
            if(song.getName().endsWith(".mp3")) // check to see if the file is indeed a song.
            {
                songPaths.add(song.getPath()); // add the song path into the list of song paths.
            }
        }

        // add the song file to the list of song paths.
    }

    // This method begins searching for songs in the main memory and starts calling the helper functions to determine what is another directory and what is actually a song.
    private void searchForMusic()
    {
        System.out.println("The external memory path is: " + Memory_Path); // print the memory path that we are working with.

        // ** little piece of code I took from stack overflow by the author Harmeet Singh to start grabbing the songs out of the path. Thanks to him **.
        if (Memory_Path != null) {
            File home = new File(Memory_Path);
            File[] listFiles = home.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File file : listFiles) {
                    System.out.println(file.getAbsolutePath());
                    if (file.isDirectory()) {
                        scanDirectory(file);
                    } else {
                        addSongToList(file, null);
                    }
                }
            }
        }
    }

    // converts a list of songs and their paths into their SongInfo and returns a new list of SongInfo!
    // TODO: warning this is what is slowing the app way down when the app first loads for the very first time!! Find a way to get the app to display some type of loading screen.
    public ArrayList<SongInfo> convertToSongInfoList(ArrayList<String> songsList)
    {
        //System.out.println("Am I failing here?");
        ArrayList<SongInfo> songsInfo = new ArrayList<SongInfo>(); // holds the songs and their info into this list.

        // iterate through the arraylist of song paths and convert each one into a SongInfo, and add to list of SongInfos.
        for(int i = 0; i < songsList.size(); i++)
        {
            SongInfo newSong = new SongInfo(); // create a new SongInfo object.
            newSong.getandSetSongInfo(songsList.get(i)); // get the song path and send it to SongInfo to be parsed into it's song details.

            if(newSong.getSongName() == null || newSong.getSongName().equalsIgnoreCase("")) {
                // do nothing this item is null and should not be included into the list.
            }
            else {
                songsInfo.add(newSong); // add the new SongInfo into list of SongInfos.
            }
        }

        // System.out.println("Did I finish grabbing the info?");
        return songsInfo; // return this list back to caller. All song information has been parsed successfully.
    }

    // Takes a list of SongInfo and alphabetizes them according to their song name
    public ArrayList<SongInfo> alphabetizeSongs(ArrayList<SongInfo> unorderedList)
    {
        ArrayList<SongInfo> orderedList = unorderedList; // holds the newly organized song ArrayList.

        // sorts the orderedList by songname
        Collections.sort(orderedList, new Comparator<SongInfo>() {

            // controls the behavior for how the list is organized which is based upon song name.
            @Override
            public int compare(SongInfo o1, SongInfo o2) {
                return o1.getSongName().compareTo(o2.getSongName());
            }
        });

        return orderedList; // return the alphabetized songinfo list.
    }

    // First method called when this activity is created.
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startup_layout);

        final FragmentManager fragmentManager = getFragmentManager();
        final FragmentTransaction mainLayoutTransaction = fragmentManager.beginTransaction(); // start a FragmentTransaction for the mainlayout.
        final LoadingScreenFragment loadingScreenFragment = new LoadingScreenFragment();

        // create an AsynTask right here in onCreate to start the spinning logo while it tries to look for users songs and folders.
        // this will allow us to show the loading screen while we wait for the thread to finish its work in the background.
        AsyncTask<Void, Void, Boolean> waitForCompletion  = new AsyncTask<Void, Void, Boolean>() {

            // this method controls what is done in the background.
            @Override
            protected Boolean doInBackground(Void... params)
            {
                // TODO: tell the users that Torwald is searching for their songs now.

                // begin to show the spinning logo so users can see something while the app loads.
                mainLayoutTransaction.replace(R.id.startup_layout, loadingScreenFragment);
                mainLayoutTransaction.show(loadingScreenFragment);
                mainLayoutTransaction.commit(); // show this fragment while waiting for the songsInfo list to be set.

                searchForMusic(); // begin searching for songs and folders.

                // TODO: update the text telling the users that songs and folders were found and now the app is finishing everything up.

                songs = convertToSongInfoList(songPaths); // convert all of the song paths into a songInfo list to display to the users. Very important to get this working correctly.

                // when the files have finished grabbing everything in the phone completely, create the songInfo list from the song info.

                // make sure everything we need for the mainUIFragment is correctly populated, then we can finish the background thread, such as converting the song list into a list of songInfos.

                return true; // this tells the AsyncTask that the user has finished it's work. Very important.
            }

            // controls what is being done after the background task has finished.
            @Override
            protected void onPostExecute(Boolean result)
            {
                // we want to display the mainUIFragment here.

                System.out.println("result was true!");
                // todo: make the startup layout to be a fragment so that we can get rid of this. This is horrible design principle, but this works for now anyway.
                RelativeLayout startuplayout = (RelativeLayout) findViewById(R.id.startup_layout); // get the startup layout here.
                startuplayout.removeAllViews(); // remove all the views from this layout.
                startuplayout.setBackground(null); // this completely removes the background of our startup layout. Which does not fix our activity.

                // TODO: check to see if this is a good place to place these pieces of code here.
                // tell the app that the app has loaded for the first and that there is no need to do it agin.
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit(); // edit the base preferences to help the app
                editor.putBoolean("FirstTimeStarting", true); // tell the app that the app has started loading for the first time.
                editor.putString("MusicDirectoryPath", Memory_Path); // set the path to be used for the app.
                editor.commit(); // commit the changes.

                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                mainUIFragment = new MainUIFragment();

                Bundle fragmentArgs = new Bundle(); // this will allow us to set arguments for the main layout.
                fragmentArgs.putStringArrayList("songs", songPaths); // send the arraylist of song paths.
                fragmentArgs.putStringArrayList("folders", folders); // send in the arraylist of folders.

                ArrayList<SongInfo> alphabeticalSongInfoList = alphabetizeSongs(songs); // returns a new arraylist that has been alphabetized according to song name.
                fragmentArgs.putParcelableArrayList("songInfoList", alphabeticalSongInfoList); // let us send the alphabetical SongInfo list to the MainUIFragment!
                fragmentArgs.putString("musicFolderPath",  Memory_Path); // send in the music file path.

                // Write the arraylist object to main memory
                String ser = SerializeObject.objectToString(alphabeticalSongInfoList); // make our alphabetized list the object we want to save to main memory.
                if(ser != null && !ser.equalsIgnoreCase(""))
                {
                    SerializeObject.WriteSettings(getBaseContext(), ser, "SongInfoList.dat");
                }
                else
                {
                    System.out.println("WE DID NOT WRITE THE LIST CORRECTLY, SOMETHING BAD HAPPENED.");
                    SerializeObject.WriteSettings(getBaseContext(), "", "SongInfoList.dat"); // we should be getting this list if we are something bad has happened.
                }


                mainUIFragment.setArguments(fragmentArgs);
                // the .replace may break here.
                transaction.replace(R.id.startup_layout, mainUIFragment); // replace the loading screen with the main layout.
                transaction.addToBackStack(null); // nothing to go back when the user hits the back button.
                transaction.commit(); // commit the fragment to be shown.
            }
        };

        waitForCompletion.execute(null, null, null); // have the AsyncTask finishes it's work.

    }

}
