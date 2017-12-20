package com.example.thenotoriousrog.tornadomusicplayer.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

;
import com.example.thenotoriousrog.tornadomusicplayer.Backend.Pair;
import com.example.thenotoriousrog.tornadomusicplayer.Backend.SongFinder;
import com.example.thenotoriousrog.tornadomusicplayer.Backend.SongInfo;
import com.example.thenotoriousrog.tornadomusicplayer.R;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;
import java.util.ArrayList;

// make sure to get this done before releasing the app to the App store. This is crucial!
public class MainActivity extends Activity {

    private String playListName = ""; // holds the name of a playlist, but this can be changed multiple times.
    private String path = ""; // this is the path that the user has entered showing there music directory.
    private File musicDirectory; // this holds the music directory itself.

    ArrayList<String> songs = null;//new ArrayList<String>(); // this will collect all the names of the songs. This is needed also for list view.
    ArrayList<String> folders = null;//new ArrayList<String>(); // this will collect the folder names. This is needed for list view.
    ArrayList<SongInfo> songInfoList = null; // this list holds our songInfo list holds all of the attributes of a song file.
    ArrayList<Pair<String, ArrayList<String>>> playLists = new ArrayList<Pair<String, ArrayList<String>>>(); // holds the ArrayList of Pair objects that will let us a unique playlist name and its songs.
    private boolean startupScreenDisplayed = false; // tells us if the app has started up for the first time or not.

    public final int USER_INPUTED_PATH = 300; // result code so that we can get the path from the user.
    public final int DIRECTORY_CHOSEN = 201; // result code that tells that the users have chosen to select the directory that their music exists in.
    public final int EF5_SCAN = 400; // result code that tells us that the users have chosen to scan their phone automatically.
    public final int REQUEST_READ_STORAGE_PERMISSION = 4000;
    public final int REQUEST_WRITE_STORAGE_PERMISSION = 4001;

    private SlidingUpPanelLayout slidingLayout; // controls out sliding up layout

    // ** Very beginning of app when it is started.
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        startupScreenDisplayed  = preferences.getBoolean("FirstTimeStarting", false); // grab the boolean and see if the screen has already been displayed or not.

        // Check if the permission to read the internal memory has been granted have been granted by the user.
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            System.out.println("Permission has not yet been granted.");
            // Should we show an explanation here?
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                // Not exactly sure that this does here.
                System.out.println("Should something be shown to the user while trying to get storage read permissions?");
                //ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_STORAGE_PERMISSION);
            }
            else // no explanation needed, just request the permission.
            {
                System.out.println("requesting permissions for read storage now");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_STORAGE_PERMISSION);
            }
        }

        // TODO: decide if we need to also have write permissions granted so the app can be able to change tags on a song. Unless the permission has already been granted.
        /* Removed for now until we can get the app to perform in the way that it should.
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            // Should we show an explanation here?
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                // Not exactly sure that this does here.
                System.out.println("Should something be shown to the user while tring to get write storage permissions?");
            }
            else // no explanation needed, just request the permission.
            {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE_PERMISSION);
            }
        }
        */

        /* removed because we want to start this after the user accepts the permission then we can begin to load the app.
        if(startupScreenDisplayed == false)
        {
            setContentView(R.layout.startup_layout);

            Intent startUp = new Intent(MainActivity.this, startUpScreen.class);
            startActivityForResult(startUp, DIRECTORY_CHOSEN);
            System.out.println("HAVE I REACHED THIS AFTER DOING AN ACTIVITY FOR RESULT??");
        }
        else
        {
            System.out.println("startup screen is already displayed.");

            // start the Intent need to reload the music player here. Everything should be here, we should not have to reload this part of the fragment.
            Intent LoadMusicPlayerIntent = new Intent(this, MusicPlayer.class);
            LoadMusicPlayerIntent.putExtra("path", path);
            LoadMusicPlayerIntent.putExtra("songs", songs);
            LoadMusicPlayerIntent.putExtra("folders", folders);

            startActivity(LoadMusicPlayerIntent); // start the activity
        }
        */
    }

    // This is the method that controls what happens after a user decides on a permission at runtime.
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode)
        {
            case REQUEST_READ_STORAGE_PERMISSION:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {

                    // permission was granted, yay! We can use the app.
                    if(startupScreenDisplayed == false)
                    {
                        setContentView(R.layout.startup_layout);

                        Intent startUp = new Intent(MainActivity.this, startUpScreen.class);
                        startActivityForResult(startUp, DIRECTORY_CHOSEN);
                        System.out.println("HAVE I REACHED THIS AFTER DOING AN ACTIVITY FOR RESULT??");
                    }
                    else
                    {
                        System.out.println("startup screen is already displayed.");

                        // start the Intent need to reload the music player here. Everything should be here, we should not have to reload this part of the fragment.
                        Intent LoadMusicPlayerIntent = new Intent(this, MusicPlayer.class);
                        LoadMusicPlayerIntent.putExtra("path", path);
                        LoadMusicPlayerIntent.putExtra("songs", songs);
                        LoadMusicPlayerIntent.putExtra("folders", folders);

                        startActivity(LoadMusicPlayerIntent); // start the activity
                    }

                }
                else {
                    // permission denied, boo! the app is going to crash now. Toast message tells the user that this permission is needed for the app to run.

                                                                                                                              // crying face.
                    Toast.makeText(getApplicationContext(), "I'm dying ahhhh! I need this permission to play your music! " + ("\ud83d\ude22"), Toast.LENGTH_LONG).show();
                    finish(); // end the activity.
                }
                return;
            }
            case REQUEST_WRITE_STORAGE_PERMISSION: // may be able to remove this decide if this permission is even needed.
            {
                // If request is cancelled, the result arrays are empty.
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // permission was granted, yay! we can use the app.
                }
                else
                {
                    // permission denied, the app will not work, users should be aware of this.
                }

                return;
                // other 'case' lines to check for other
                // permissions this app might request
            }

        }
    }


    // sets the music directory given by the user.
    public void setMusicDirectory(File dir)
    {
        musicDirectory = dir;
    }

    // sets the music folder path indicated by the user.
    public void setUserPath(String str)
    {
        path = str;
    }


    // sets the arrayList of songs
    public synchronized void setSongsList(ArrayList<String> list)
    {
        songs = list;
    }

    // sets the arrayList of folders.
    public synchronized void setFoldersList(ArrayList<String> list)
    {
        folders = list;
    }


    // This method is in charge of retrieving any result from an activity. Different codes tell us where the result is coming from!
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intentResult)
    {
        System.out.println("we are in activity result now");
        super.onActivityResult(requestCode, resultCode, intentResult);

        if(requestCode == DIRECTORY_CHOSEN) // this currently works whether the user selects Internal or SDcard search! :)
        {
            final String path = intentResult.getStringExtra("dirPath");
            System.out.println("User has selected this folder: " + path); // for testing to see what is returned from the user

            final File musicDirectory = new File(path); // creates a file from the path that we are trying to work with.
            setMusicDirectory(musicDirectory); // set the music directory.

            setUserPath(path); // set the path of the music directory here.

            Thread songFinderThread = new Thread(new SongFinder(musicDirectory, MainActivity.this)); // create a Thread for finding the songs.
            songFinderThread.start(); //start the thread

            // while the song finder thread is running it is populating the song lists as well as the folder lists. Only after these lists are populated do we bother to then convert them into songInfoLists.
            while(songFinderThread.isAlive()) {
                // wait until all songs have been found.
            }

            // Create a the musicplayer activity and send the correct information that is needed.
            Intent LoadMusicPlayerIntent = new Intent(this, MusicPlayer.class);
            LoadMusicPlayerIntent.putExtra("path", path);
            LoadMusicPlayerIntent.putExtra("songs", songs);
            LoadMusicPlayerIntent.putExtra("folders", folders);

            startActivity(LoadMusicPlayerIntent); // start the activity

        }
        else if(resultCode == USER_INPUTED_PATH) // for when a user manually enters their path!
        {
            final String path = intentResult.getStringExtra("dirPath");
            System.out.println("User has selected this folder: " + path); // for testing to see what is returned from the user

            final File musicDirectory = new File(path); // creates a file from the path that we are trying to work with.

            Thread songFinderThread = new Thread(new SongFinder(musicDirectory, MainActivity.this)); // create a Thread for finding the songs.
            songFinderThread.start(); //start the thread

            // while the song finder thread is running it is populating the song lists as well as the folder lists. Only after these lists are populated do we bother to then convert them into songInfoLists.

            while(songFinderThread.isAlive()) {
                // wait until all songs have been found.
            }

            // Create a the musicplayer activity and send the correct information that is needed.
            Intent LoadMusicPlayerIntent = new Intent(this, MusicPlayer.class);
            LoadMusicPlayerIntent.putExtra("path", path);
            LoadMusicPlayerIntent.putExtra("songs", songs);
            LoadMusicPlayerIntent.putExtra("folders", folders);

            startActivity(LoadMusicPlayerIntent); // start the activity


        }
        else if(requestCode == EF5_SCAN)
        {
            // TODO: add the code that will just start the new activity. We are not getting any results back, this activity will continue with everything and will also replace and remove the start up screen on its
            // todo (cont): own after scanning has found the paths, songs, and folders. The path that we may send in may not be helpful so we may need to actually configure everything for the MainUIFragment.

            // todo: really think about the design here, the users should have a seemless integration while the phone is scanning and while the users are searching for everything, while the phone is scanning,
            // todo (cont): we should be throwing up other prompts to let the user know that Torwald is almost done scanning and everything. This is going to be a far better design idea than what we had before.

            // todo: We should still keep the ability for users to be able to have the ability to set up different ways, but definitely be able to keep the scan in its correct place.

            // TODO: after scanning we may want to figure out a better way to get the names of the folders. That is a better way to trim off the '/'s so that the name of the Folder's seem more human like. Very crucial.

            System.out.println("We have received input from the EF5_Scan intent trying to start the activity now... ");
            Intent EF5ScanIntent = new Intent(this, EF5ScanActivity.class); // create the activity. We do not need to have any response back from this activity, it is going to scan the phone and make the mainUIFragment.
            startActivity(EF5ScanIntent); // start the activity now.
        }
        else // this will become an else-if that will allow us to look for different request codes depending on the code that we need. We need this to get the app to perform different things.
        {
            System.out.println("result code was grabbed incorrectly");
            System.out.println("the requestCode = " + requestCode);
            System.out.println("the resultCode = " + resultCode);
        }

    }

    // TODO: I need to set a function for onResume right here because I have to somehow tell the MainUIFragment to load correctly because it's just not doing what it is suppose to.
    // TODO: figure out where the right way for this thing to go is. Something is just not adding up right.

    protected void onResume()
    {
        super.onResume();

        System.out.println("We are on in onResume in the mainactivity. Attempting to start and load the music player right now...");


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean startupScreenDisplayed  = preferences.getBoolean("FirstTimeStarting", false); // grab the boolean and see if the screen has already been displayed or not.

        if(startupScreenDisplayed == true) // start up screen has not been loaded yet.
        {
            System.out.println("The loading screen has already been displayed, attempting to display the information now.");
            // todo: Save the above information to main memory so that we can display the songs correctly.


            ArrayList<String> songList = new ArrayList<String>(preferences.getStringSet("Songs", null));
            ArrayList<String> folderList = new ArrayList<String>(preferences.getStringSet("Folders", null));
            ArrayList<String> playlistNames;

            if(preferences.getStringSet("playlistNames", null) == null)
            {
                playlistNames = new ArrayList<>();
            }
            else
            {
                playlistNames = new ArrayList<>(preferences.getStringSet("playlistNames", null));
            }

            String path = preferences.getString("Path", null); // grab the path that we want to use.


            // start the Intent need to reload the music player here. Everything should be here, we should not have to reload this part of the fragment.
            Intent LoadMusicPlayerIntent = new Intent(this, MusicPlayer.class);
            LoadMusicPlayerIntent.putExtra("path", path);
            LoadMusicPlayerIntent.putExtra("songs", songList);
            LoadMusicPlayerIntent.putExtra("folders", folderList);
            LoadMusicPlayerIntent.putExtra("playlistNames", playlistNames); // send in the playlist names for the list.

            // for testing.
            System.out.println("is songs null?: " + songList);
            System.out.println("is folders null?: " + folderList);
            System.out.println("is the path null?: " + path);
            System.out.println("Is the playlistNames null?: " + playlistNames);

            startActivity(LoadMusicPlayerIntent); // start the activity
        }
        else {} // do nothing, the loading display has not been displayed yet.


    }
}


