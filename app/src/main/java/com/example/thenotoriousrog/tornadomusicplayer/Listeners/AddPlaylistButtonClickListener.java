package com.example.thenotoriousrog.tornadomusicplayer.Listeners;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.thenotoriousrog.tornadomusicplayer.Fragments.AddSongsToSinglePlaylistFragmentActivity;
import com.example.thenotoriousrog.tornadomusicplayer.Fragments.MainUIFragment;
import com.example.thenotoriousrog.tornadomusicplayer.Fragments.ModifyAllPlaylistsFragmentActivity;
import com.example.thenotoriousrog.tornadomusicplayer.Backend.Playlist;
import com.example.thenotoriousrog.tornadomusicplayer.R;
import com.example.thenotoriousrog.tornadomusicplayer.Backend.SongInfo;

import java.util.ArrayList;

/**
 * Created by thenotoriousrog on 8/21/17.
 * This class is in control of creating responding to click events on the playlist button and also starts the behavior for the new playlist
 */

public class AddPlaylistButtonClickListener implements FloatingActionButton.OnClickListener {

    private MainUIFragment mainUIFragment; // holds the mainUIFragment that is being used to ensure that the behavior of the class is working correctly.
    private ArrayList<Playlist> PlayLists; // holds all of the Playlists that we are working with.
    private ArrayList<String> rawSongs; // holds a list of all the raw songs that the playlist fragment will be using.
    private ArrayList<SongInfo> songs; // holds all of the Songs in the form of songInfo
    private ArrayList<String> folders; // holds all of the names of the folders that we are working with.

    public final int CREATE_PLAYLIST_INTENT = 200; // result code we want to send to the open playlist intent.

    public AddPlaylistButtonClickListener(MainUIFragment fragment, ArrayList<Playlist> playlists, ArrayList<String> songPaths, ArrayList<SongInfo> songInfos, ArrayList<String> Folders)
    {
        mainUIFragment = fragment;
        PlayLists = playlists;
        rawSongs = songPaths;
        songs = songInfos;
        folders = Folders;
    }

    // This method is in charge of creating a new playlist, however, it should be calling the new modifyAllPlaylistsFragmentActivity class that only allows for one playlist to be adding songs at a time instead of all of them.
    public void createNewPlaylist(Dialog playlistButtonDialog, View v)
    {
        // TODO: create the class that will allow for only modifying one playlists at a time instead of multiple ones at once, meaning all of this below has to be modified.

        playlistButtonDialog.dismiss(); // dismiss the first dialog because it is no longer needed now.

        final Dialog playListCreationDialog = new Dialog(v.getContext());
        playListCreationDialog.setContentView(com.example.thenotoriousrog.tornadomusicplayer.R.layout.new_playlist_dialog); // set the dialog for a user to create their playlist.
        //playListCreationDialog.setTitle("Creating a playlist..."); not needed.
        playListCreationDialog.show(); // show the dialog box!

        final EditText enterPlaylistNameField = (EditText) playListCreationDialog.findViewById(com.example.thenotoriousrog.tornadomusicplayer.R.id.playlistEnterField); // set the field that the user will enter their playlist name.
        Button playListConfirmButton = (Button) playListCreationDialog.findViewById(com.example.thenotoriousrog.tornadomusicplayer.R.id.playlistConfirmButton); // Confirm button for when a user Finishes entering the name of their playlist.

        // set behavior for when a user hits the confirm button.
        // TODO: we simply want to just modify only the first playlist!
        playListConfirmButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                playListCreationDialog.dismiss(); // close the dialog after the user enters a name of the arraylist.

                // todo: create another popup here asking if users want to add songs to the playlist. Once that is selected go from there.
                // if they say yes, the user will drag and drop songs into the playlist.
                // if they say no, an empty playlist will be created for them, it is crucial to get this one correct.


                final Dialog addSongsDialog = new Dialog((v.getContext()));
                addSongsDialog.setContentView(com.example.thenotoriousrog.tornadomusicplayer.R.layout.addsongsdialog); // set the view for this dialog.
                addSongsDialog.setTitle("Do you want to add songs to your playlist?");
                Button yesButton = (Button) addSongsDialog.findViewById(com.example.thenotoriousrog.tornadomusicplayer.R.id.yesButton); // grab yes button.
                Button noButton = (Button) addSongsDialog.findViewById(com.example.thenotoriousrog.tornadomusicplayer.R.id.noButton); // grab no button.
                addSongsDialog.show();

                // set behavior for when the user presses yes.
                yesButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        System.out.println("Yes was selected, we need to add songs to this playlist with the view that we want to have.");
                        addSongsDialog.dismiss(); // close the dialog after user says yes.

                        String str = enterPlaylistNameField.getText().toString(); // grab what the user enters for their playlist.
                        System.out.println("The name in the playlist field is... " + enterPlaylistNameField.getText().toString());

                        if (str.length() == 0) {
                            // TODO: decide if it is important to force a user to enter a name or let the app create one on its own. A small detail but an important one nonetheless.
                            str = "New Playlist"; // name the generic playlist for the user.
                        }

                        mainUIFragment.setPlayListName(str); // set the playlist name chosen by the user.

                        mainUIFragment.addEmptyPlaylist(); // todo: REMOVE THIS, but for now we have it here to allow users to be able to be able to see the playlist that they just created.
                       // mainUIFragment.updatePlaylistAdapter(); // removed because this will cause the app to crash because it sorts and sorting can cause big issues.

                        // TODO: We will want to create an Activity that will allow the creation of the playlist and will also allow us to send back the playlist once it is has been successfully completed.

                        // We will want to create a seperate activity that will take care of this for us.

                        /* Removed because we want to modify only the playlist that was created not all of them at once.
                        Intent addPlaylistActivity = new Intent(mainUIFragment.getContext(), ModifyAllPlaylistsFragmentActivity.class); // create the activity to allow us to have the activity do what it is supposed to.
                        Bundle playlistIntentBundle = new Bundle();
                        playlistIntentBundle.putStringArrayList("songs", rawSongs); // send in the songs to be used by the modifyAllPlaylistsFragmentActivity
                        playlistIntentBundle.putStringArrayList("playlistNames", mainUIFragment.getPlaylistNames()); // send in the playlist names.
                        playlistIntentBundle.putParcelableArrayList("songInfos", songs); // send in the songInfoList
                        playlistIntentBundle.putStringArrayList("folders", folders); // send in the folders.
                        playlistIntentBundle.putParcelableArrayList("playlists", PlayLists); // send in the playlists NOTE: remove this if we cannot do it this way as it could be causing problems.

                        addPlaylistActivity.putExtra("args", playlistIntentBundle); // send in the intent to grab the items for the activity.
                        */

                        mainUIFragment.refreshPlaylistAdapter();
                        mainUIFragment.updatePlaylistAdapter(); // this forces a sort making the list behave much better.

                        // Warning: if issues arise just uncomment the line below and pass in the viewPager via the constructor of this class from the mainUIFragment.
                        // mainUIFragment.updateViewPager(viewPager.getCurrentItem()); // update the view pager after the playlist was updated.

                        mainUIFragment.updateViewPager(mainUIFragment.getCurrentPageItem()); // this should work if not see the warning comment 3 lines up.

                        Intent addSongsToPlaylistActivity = new Intent(mainUIFragment.getContext().getApplicationContext(), AddSongsToSinglePlaylistFragmentActivity.class); // activity for adding songs to a single playlist.
                        Bundle playlistIntentBundle = new Bundle();
                        playlistIntentBundle.putStringArrayList("songs", rawSongs); // send in the songs to be used by the modifyAllPlaylistsFragmentActivity
                        playlistIntentBundle.putStringArrayList("playlistNames", mainUIFragment.getPlaylistNames()); // send in the playlist names.
                        playlistIntentBundle.putParcelableArrayList("songInfos", songs); // send in the songInfoList
                        playlistIntentBundle.putStringArrayList("folders", folders); // send in the folders.
                        playlistIntentBundle.putParcelableArrayList("playlists", PlayLists); // send in the playlists NOTE: remove this if we cannot do it this way as it could be causing problems.
                       // playlistIntentBundle.putInt("PlaylistPosition", PlayLists.size()-1); // send in the last playlist because that is the one we just added muahaha
                        playlistIntentBundle.putString("ChosenPlaylistName", str); // send in the name of the playlist that was just created.

                        System.out.println("The name of the playlist that the user created is: " + str);
                        addSongsToPlaylistActivity.putExtra("args", playlistIntentBundle); // send the arguments to the fragmentActivity.
                        mainUIFragment.startActivityForResult(addSongsToPlaylistActivity, CREATE_PLAYLIST_INTENT); // start the activity for the result of the songs in the playlist.



                    }
                });

                // set behavior for when a user presses no.
                noButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        System.out.println("No was selected, create an empty playlist");
                        // dismisses the dialog box and creates an empty arraylist of songs.
                        addSongsDialog.dismiss();

                        String str = enterPlaylistNameField.getText().toString(); // grab what the user enters for their playlist.
                        System.out.println("The name in the playlist field is... " + enterPlaylistNameField.getText().toString());

                        if (str.length() == 0) {
                            // TODO: decide if it is important to force a user to enter a name or let the app create one on its own. A small detail but an important one nonetheless.
                            str = "New Playlist"; // name the generic playlist for the user.
                        }

                        mainUIFragment.setPlayListName(str); // set the playlist name chosen by the user.
                        mainUIFragment.addEmptyPlaylist(); // creates an empty playlist with the name that was created.

                        mainUIFragment.refreshPlaylistAdapter();
                        mainUIFragment.updatePlaylistAdapter(); // force the playlist adapter to be updated and show hopefully it will allow us to keep track of the songs in the correct way.
                    }
                });
            }

        }); // end of playlist confirm buttin.
    }

    // This method is in charge of modifying all songs at once in the playlist. This will call the playlistfragment that allows for this to happen
    public void modifyAllSongs(Dialog playlistButtonDialog, View v)
    {
        // TODO: Test this part of the code like crazy, make sure that there are no issues from not creating a new playlist first. If so, edit the code to change this.

        playlistButtonDialog.dismiss(); // remove the dialog button it is no longer used.

        // This should call the activity that will allow users to modify all of their playlists at once.
        Intent addPlaylistActivity = new Intent(mainUIFragment.getContext(), ModifyAllPlaylistsFragmentActivity.class); // create the activity to allow us to have the activity do what it is supposed to.
        Bundle playlistIntentBundle = new Bundle();
        playlistIntentBundle.putStringArrayList("songs", rawSongs); // send in the songs to be used by the modifyAllPlaylistsFragmentActivity
        playlistIntentBundle.putStringArrayList("playlistNames", mainUIFragment.getPlaylistNames()); // send in the playlist names.
        playlistIntentBundle.putParcelableArrayList("songInfos", songs); // send in the songInfoList
        playlistIntentBundle.putStringArrayList("folders", folders); // send in the folders.
        playlistIntentBundle.putParcelableArrayList("playlists", PlayLists); // send in the playlists NOTE: remove this if we cannot do it this way as it could be causing problems.

        addPlaylistActivity.putExtra("args", playlistIntentBundle); // send in the intent to grab the items for the activity.
        mainUIFragment.startActivityForResult(addPlaylistActivity, CREATE_PLAYLIST_INTENT); // start the activity for the result of the songs in the playlist.

        mainUIFragment.refreshPlaylistAdapter();

        // Warning: if issues arise just uncomment the line below and pass in the viewPager via the constructor of this class from the mainUIFragment.
        // mainUIFragment.updateViewPager(viewPager.getCurrentItem()); // update the view pager after the playlist was updated.

        mainUIFragment.updateViewPager(mainUIFragment.getCurrentPageItem()); // this should work if not see the warning comment 3 lines up.
    }

    // Controls the behavior for when the plus button is pressed on in the playlists tab.
    @Override
    public void onClick(View v)
    {
        // TODO: create the new playlist_plusbutton_dialog here and ask users for their response.
        final Dialog playlistButtonDialog = new Dialog(v.getContext()); // this is the very first dialog when the button is pushed.
        playlistButtonDialog.setContentView(com.example.thenotoriousrog.tornadomusicplayer.R.layout.playlist_plusbutton_dialog);
        playlistButtonDialog.show(); // show the first dialog.

        Button newPlaylistButton = (Button) playlistButtonDialog.findViewById(com.example.thenotoriousrog.tornadomusicplayer.R.id.newPlaylistButton); // grab our button
        newPlaylistButton.setOnClickListener(new View.OnClickListener() {

            // calls the method that is in charge of creating a new playlist.
            @Override
            public void onClick(View v)
            {
                createNewPlaylist(playlistButtonDialog, v);
            }
        });

        Button modifyAllPlaylistsButton = (Button) playlistButtonDialog.findViewById(R.id.modifyAllPlaylistsButton); // grab our button.
        modifyAllPlaylistsButton.setOnClickListener(new View.OnClickListener() {

            // calls the method in charge of creating a new playlist.
            @Override
            public void onClick(View v)
            {
                modifyAllSongs(playlistButtonDialog, v);
            }
        });

    }
}
