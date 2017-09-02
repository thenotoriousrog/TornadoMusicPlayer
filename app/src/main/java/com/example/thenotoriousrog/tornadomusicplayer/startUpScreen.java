package com.example.thenotoriousrog.tornadomusicplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.thenotoriousrog.myapplication.R;

/**
 * Created by thenotoriousrog on 6/11/17.
 * This class is in charge of
 */

// TODO: convert this class to a fragment. If needed.
public class startUpScreen extends Activity {

    private Button manualEntry; // button for users to manually enter their directory.
    private Button selectSDCARD; // button for SD card selection
    private Button selectInternal; // button for internal storage selection.
    private Button EF5Scan; // This is the button for users to be able to scan their entire phone at once rather than having to search their phone for their songs using one of the startup areas.

    public final int DIRECTORY_CHOSEN = 201; // tells us that the user is attempting to select a music directory.
    public final int USER_INPUTED_PATH = 300; // result code so that we can get the path from the user.
    public final int EF5_SCAN = 400; // result code that tells us that the users have chosen to scan their phone automatically.

    // allows us to create the startup layout.
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(com.example.thenotoriousrog.myapplication.R.layout.startup_layout);
        LoadingView loadingView = (LoadingView) findViewById(com.example.thenotoriousrog.myapplication.R.id.gifView);

        EF5Scan = (Button) findViewById(com.example.thenotoriousrog.myapplication.R.id.EF5Scan); // grab our button
        EF5Scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // TODO: Create a new intent here that will start our new class and will search everything for us
                // This method should grab folders and songs and should populate the songs and folders lists in terms of just arraylists

                // We will have the rest of the application convert everything into SongInfo as well as create the rest of the application for the users. The app should find everything, and it should seemlessly build
                // and setup the rest of the app while we are waiting for the app to finish everything up so that the app can biuld
                Intent EF5ScanIntent = new Intent(startUpScreen.this, EF5ScanActivity.class); // create the activity. We do not need to have any response back from this activity, it is going to scan the phone and make the mainUIFragment.
                startActivity(EF5ScanIntent); // start the activity now.

                // need to somehow set the result or something for the mainActivity.
            }
        });

        // create and define behavior for Select Internal button
        selectInternal = (Button) findViewById(com.example.thenotoriousrog.myapplication.R.id.SelectInternal); // button used to select internal storage.
        selectInternal.setOnClickListener(new View.OnClickListener() {

            // this uses the storage/emulated/0 directory to search inside the directory for us to be able to find songs.
            @Override
            public void onClick(View v) {
                Intent pickFolder = new Intent(startUpScreen.this, DirectoryPicker.class); // creates the new intent to allow us to pick a folder for our thingy.
                startActivityForResult(pickFolder, DIRECTORY_CHOSEN);
            }
        });

        // create and define behavior for Select SDCARD button
        selectSDCARD = (Button) findViewById(com.example.thenotoriousrog.myapplication.R.id.SelectSDCARD); // button for selecting SD card storage
        selectSDCARD.setOnClickListener(new View.OnClickListener() {

            // this will require that we use the default "/storage/" directory in hopes that we will be able to find the SD card.
            @Override
            public void onClick(View v) {
                Intent pickFolder = new Intent(startUpScreen.this, DirectoryPicker.class); // creates the new intent to allow us to pick a folder for our thingy.
                pickFolder.putExtra(DirectoryPicker.START_DIR, "/storage/");
                pickFolder.putExtra("true", DirectoryPicker.SHOW_HIDDEN);
                startActivityForResult(pickFolder, DIRECTORY_CHOSEN);
            }
        });

        // todo: ** this may not work unless we move the correct behaviors into the correct methods, so be sure to test this like nothing else.
        // create and define behavior for Manual Entry button
        manualEntry = (Button) findViewById(R.id.ManualEntry); // creates the button for manual entry.
        manualEntry.setOnClickListener(new View.OnClickListener() {

            // when clicked we start the new activity to let the users enter their directory manually.
            @Override
            public void onClick(View v) {
                Intent manEntryIntent = new Intent(startUpScreen.this, ManualEntry.class);
                startActivityForResult(manEntryIntent, USER_INPUTED_PATH); // CAUTION: I'm not sure what the point of the number is during the startActivityForResult
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent)
    {
        if(requestCode == DIRECTORY_CHOSEN) // user has selected/entered their music directory.
        {
            System.out.println("Start up screen Activity result method.....");
            System.out.println("it seems that a file was indeed selected. Will it show me what?");
            Bundle extras = resultIntent.getExtras();
            String path = (String) extras.get(DirectoryPicker.CHOSEN_DIRECTORY); // hoping this will hold the data that we are requesting.
            System.out.println("User has selected this folder: " + path); // for testing to see what is returned from the user

            Intent result = new Intent();
            result.putExtra("dirPath", path); // pair the path with the intent so that the MainActivity can read the directory and begin to do some work.
            setResult(DIRECTORY_CHOSEN, result); // send the result back to MainActivity.

            finish(); // quite the startup activity. We do not need it anymore. Close it permanently. **should prevent that stupid moving the phone to the side bug we were experiencing earlier.
        }
    }

}
