package com.example.thenotoriousrog.tornadomusicplayer;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RelativeLayout;

import com.example.thenotoriousrog.myapplication.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by thenotoriousrog on 7/6/17.
 * This class is in charge of loading up and displaying and loading up songs for the music player itself.
 */

public class MusicPlayer extends Activity {

    private ArrayList<SongInfo> songInfos; // list of all the SongInfo used throughout the app.
    private ArrayList<String> songPaths; // this is the list of the raw song files.
    private ArrayList<String> originalSongPaths; // this arraylist holds the entire list of songs. This is important to realize
    private ArrayList<String> unmodifiedSongPaths; // holds the list of unmodified songs in a list.
    private Boolean shufflingNow = false; // this boolean helps us know if the SelectedSongPlayer is indeed shuffling now or not.
    private MainUIFragment mainUIFragment; // a public copy of the mainUIFragment so that we can control what happens when the user presses the back button.


    // First method called when this activity is created.
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // todo: we may have to reset this layout as this is going to be called when the notification is called again. This is a problem.
        setContentView(com.example.thenotoriousrog.myapplication.R.layout.startup_layout); // only setting this for the purpose of resetting it later.

        Intent intent = getIntent(); // get the intent that started this activity.

        // TODO: Check the SharedPreferences to decide if we need to load the app or simply rebuild the MainUIFragment

        String path = intent.getStringExtra("path"); // get the path from the user.
        File musicDirectory = new File(path); // convert the string into a path.
        ArrayList<String> songs = intent.getStringArrayListExtra("songs");
        ArrayList<String> folders = intent.getStringArrayListExtra("folders");


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean startupDisplayed = preferences.getBoolean("FirstTimeStarting", false);


        if(startupDisplayed == true) // the startup screen has been loaded already I need to get this thing working correctly.
        {

            // Need to grab the songs that we want to work with again. REMOVE THESE IF THE PROBLEM STILL PERSISTS.
            ArrayList<String> songList = new ArrayList<String>(preferences.getStringSet("Songs", null));
            ArrayList<String> folderList = new ArrayList<String>(preferences.getStringSet("Folders", null));
            ArrayList<String> playlistNames = new ArrayList<>(preferences.getStringSet("playlistNames", null));

            // System.out.println("Startup screen has been displayed already. Should we show the mainDisplay only?");
            //displayMusicPlayer(songs, folders, path); // old way.
            // todo: send in the playlist to do be displayed to the main set up. Very important.
            displayMusicPlayer(songList, folderList, path, playlistNames); // the main U.I. has already been loaded, I just need to extract and load the information again.
        }
        else // the startup screen has been displayed for the first time, load the app like normal.
        {
            Set<String> songSet = new HashSet<>(songs); // create a song out of all the songs that we want to use.
            Set<String> folderSet = new HashSet<>(folders); // create a folder of all the songs that want to use.

            System.out.println("Is the songset that we are using null: " + songSet);
            System.out.println("Is the folderSet that we are using null: " + folderSet);

            // save details into main memory.
            SharedPreferences.Editor editor = preferences.edit(); // create an editor to make changes to what is saved to main memory.
            editor.putStringSet("Songs", songSet);
            editor.putStringSet("Folders", folderSet);
            editor.putString("Path", path);
            editor.commit(); // commit the changes to the application so that the app can reload properly.

            loadMusicPlayer(musicDirectory, path, songs, folders); // load the music player along with all of the listeners and what not.
        }

    }

    // PopulateSongInfoList class calls this method.
    public void setSongInfoList(ArrayList<SongInfo> list)
    {
        songInfos = list;
    }

    // MusicPlayer calls this it retrieves what PopulateSongInfoList creates. The list is unorganized.
    public ArrayList<SongInfo> getSongInfoList()
    {
        return songInfos;
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

    // this method is called only when the list has been loaded and we have saved memory in the system. All this will do is simply send the items to the MainUIFragment to be use
    public void displayMusicPlayer(ArrayList<String> songs, ArrayList<String> folders, String path, ArrayList<String> playlistNames)
    {
        mainUIFragment = new MainUIFragment(); // create a copy of the main U.I. fragment here.

        System.out.println("Trying to display the music player, it has already been loaded.");


        //ArrayList<SongInfo> alphabeticalSongInfoList = alphabetizeSongs(getSongInfoList()); // returns a new arraylist that has been alphabetized according to song name.
       //fragmentArgs.putParcelableArrayList("songInfoList", alphabeticalSongInfoList); // let us send the alphabetical SongInfo list to the MainUIFragment!

        ArrayList<SongInfo> songInfoAlphabetized = new ArrayList<>(); // this is the list that we will want to fill.
        //ArrayList<Pair<String, ArrayList<SongInfo>>> playlists = new ArrayList<>(); // a recreated list.

        // read what was wrote in MainMemory
        String ser = SerializeObject.ReadSettings(getBaseContext(), "SongInfoList.dat");
        if(ser != null && !ser.equalsIgnoreCase(""))
        {
            Object obj = SerializeObject.stringToObject(ser); // grab the object that was read in main memory.

            // cast the object to the correct type of arraylist.
            if(obj instanceof ArrayList)
            {
                songInfoAlphabetized = (ArrayList<SongInfo>) obj; // set our songInfo list.
            }
        }

        // send the arguments that will be used by the MainUIFragment.
        Bundle fragmentArgs = new Bundle(); // this will allow us to set arguments for the main layout.
        fragmentArgs.putStringArrayList("songs", songs); // send the arraylist of song paths.
        fragmentArgs.putStringArrayList("folders", folders); // send in the arraylist of folders.
        fragmentArgs.putParcelableArrayList("songInfoList", songInfoAlphabetized); // set our grabbed songIno list to send off to the MainUIFragment.
        fragmentArgs.putString("musicFolderPath",  path); // send in the music file path.
        fragmentArgs.putStringArrayList("playlistNames", playlistNames); // TODO: determine if this is causing the app to fail or not.


        // READ the playlist data object from main memory if it exists.
        ArrayList<Playlist> playlists = new ArrayList<>();
        String ser1 = SerializeObject.ReadSettings(getBaseContext(), "playlists.dat"); // attempt to find this in main memory.
        if(ser1 != null && !ser1.equalsIgnoreCase(""))
        {
            Object obj = SerializeObject.stringToObject(ser1); // grab the object that was read in main memory.

            // cast the object to the correct type of arraylist.
            if(obj instanceof ArrayList)
            {
                playlists = (ArrayList<Playlist>) obj; // set our songInfo list.
                //Pair<String, ArrayList<SongInfo>> newPair = new Pair<>(playlistNames.get(i), playlistSongs); // create a new pair to be used by the list here.
                fragmentArgs.putParcelableArrayList("playlists", playlists); // send in the parcelable arraylist here.
            }
        }

        // test to see if the playlist list is null after reading from main memory.
        System.out.println("Is the playlist arraylist null after reading from main memory? " + playlists);

        mainUIFragment.setArguments(fragmentArgs); // set the arguments for the fragment.

        RelativeLayout startupLayout = (RelativeLayout) findViewById(com.example.thenotoriousrog.myapplication.R.id.startup_layout); // grab the startup layout so we can remove it when we are finished.
        startupLayout.removeAllViews(); // remove all views from this view.
        startupLayout.setBackground(null); // ensure no background is present after we have loaded the mainUIFragment.

        // begin to display the fragment again.
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        //transaction.show(mainFragment);
        transaction.replace(com.example.thenotoriousrog.myapplication.R.id.startup_layout, mainUIFragment); // replace the loading screen with the main layout.
        transaction.addToBackStack(null); // nothing to go back when the user hits the back button.
        transaction.commit(); // commit the fragment to be shown.

    }

    // this method is in charge of loading the actual music player itself.
    public void loadMusicPlayer(File musicDirectory, final String path, final ArrayList<String> songs, final ArrayList<String> folders)
    {

        System.out.println("original song list that we are using when loading songs is " + songs);

        // todo: make the startup layout to be a fragment so that we can get rid of this. This is horrible design principle, but this works for now anyway.
        RelativeLayout startuplayout = (RelativeLayout) findViewById(com.example.thenotoriousrog.myapplication.R.id.startup_layout); // get the startup layout here.
        startuplayout.removeAllViews(); // remove all the views from this layout.
        startuplayout.setBackground(null); // this completely removes the background of our startup layout. Which does not fix our activity.

        final FragmentManager fragmentManager = getFragmentManager();
        final FragmentTransaction mainLayoutTransaction = fragmentManager.beginTransaction(); // start a FragmentTransaction for the mainlayout.
        final LoadingScreenFragment loadingScreenFragment = new LoadingScreenFragment();

        final Thread convertToSongInfoThread = new Thread(new PopulateSongInfoList(songs, folders, MusicPlayer.this, musicDirectory)); // create a Thread to have the lists become SongInfo's instead of paths.

        // this will allow us to show the loading screen while we wait for the thread to finish its work in the background.
        AsyncTask<Void, Void, Boolean> waitForCompletion  = new AsyncTask<Void, Void, Boolean>() {

            // this method controls what is done in the background.
            @Override
            protected Boolean doInBackground(Void... params)
            {

                mainLayoutTransaction.replace(com.example.thenotoriousrog.myapplication.R.id.startup_layout, loadingScreenFragment);
                //mainLayoutTransaction.addToBackStack(null);
                mainLayoutTransaction.show(loadingScreenFragment);
                mainLayoutTransaction.commit(); // show this fragment while waiting for the songsInfo list to be set.

                convertToSongInfoThread.start(); // start the thread

                while(convertToSongInfoThread.isAlive()){
                    // do nothing until the thread has finished.
                }

                mainLayoutTransaction.remove(loadingScreenFragment); // remove the loading fragment after we have finished with our loading screen.
                return true;
            }

            // controls what is being done after the background task has finished.
            @Override
            protected void onPostExecute(Boolean result)
            {
                System.out.println("result was true!");

                // TODO: check to see if this is a good place to place these pieces of code here.
                // tell the app that the app has loaded for the first and that there is no need to do it agin.
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit(); // edit the base preferences to help the app
                editor.putBoolean("FirstTimeStarting", true); // tell the app that the app has started loading for the first time.
                editor.putString("MusicDirectoryPath", path); // set the path to be used for the app.
                editor.commit(); // commit the changes.

                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                mainUIFragment = new MainUIFragment();

                Bundle fragmentArgs = new Bundle(); // this will allow us to set arguments for the main layout.
                fragmentArgs.putStringArrayList("songs", songs); // send the arraylist of song paths.
                fragmentArgs.putStringArrayList("folders", folders); // send in the arraylist of folders.

                ArrayList<SongInfo> alphabeticalSongInfoList = alphabetizeSongs(getSongInfoList()); // returns a new arraylist that has been alphabetized according to song name.
                fragmentArgs.putParcelableArrayList("songInfoList", alphabeticalSongInfoList); // let us send the alphabetical SongInfo list to the MainUIFragment!
                fragmentArgs.putString("musicFolderPath",  path); // send in the music file path.

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

    @Override
    public void onBackPressed()
    {
        if(mainUIFragment.isSlidingPanelExpanded() == true) // have the panel fly down when the user hits the back button.
        {
            mainUIFragment.slidePanelDown(); // have the sliding layout fall down.
        }
        else // perform other back button behaviors.
        {
            if(mainUIFragment.getCurrentPageItem() == 0) // in the songs tab/page
            {
                // do nothing.
            }
            else if(mainUIFragment.getCurrentPageItem() == 1) // in the folders tab/page
            {
                mainUIFragment.closeFolder(); // close the playlist and show the folders again.
            }
            else if(mainUIFragment.getCurrentPageItem() == 2) // in the playlists tab/page.
            {
                mainUIFragment.closePlaylist(); // close the playlist and show list of playlists again.
            }
        }



    }
}
