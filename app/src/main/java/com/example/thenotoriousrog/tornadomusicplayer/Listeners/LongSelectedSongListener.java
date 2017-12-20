package com.example.thenotoriousrog.tornadomusicplayer.Listeners;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.thenotoriousrog.tornadomusicplayer.R;
import com.example.thenotoriousrog.tornadomusicplayer.Backend.SongInfo;;

import org.cmc.music.common.ID3WriteException;
import org.cmc.music.metadata.MusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by thenotoriousrog on 6/22/17.
 * This class is in charge of listening to what happens to one of the items when a user long clicks a song.
 */

public class LongSelectedSongListener extends Activity implements AdapterView.OnItemLongClickListener {

    private ArrayList<SongInfo> songsToPick = null; // this will hold the song that a user can choose from.

    public LongSelectedSongListener(ArrayList<SongInfo> listOfSongs)
    {
        songsToPick = listOfSongs; // set the arraylist.
    }

    // I don't believe that this method is needed.
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }


    public boolean isReadyToChange(String str1, String str2, String str3)
    {
        if( (str1.length() >= 1) && (str2.length() >= 1) && (str3.length() >= 1) ) // all fields are filled out and ready to be saved.
        {
            return true; // user has entered something in all three fields.
        }
        else // one or more of the fields is blank, must fix.
        {
            return false; // inform user to add something into the fields.
        }
    }

    // This method will control the creation of the dialog box to allow the users to select an action based on the long click actions they have chosen.
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id)
    {

        System.out.println("Long clicked selected!");

        final Dialog dialog = new Dialog(view.getContext()); // create a one time only dialog. So that multiple copies do not exist.
        dialog.setContentView(com.example.thenotoriousrog.tornadomusicplayer.R.layout.dialog_layout); // set the dialog layout that we have created.
        dialog.setTitle("Select an Action...");

        // Set behavior for the button on the dialogue controls.
        Button editTagsButton = (Button) dialog.findViewById(com.example.thenotoriousrog.tornadomusicplayer.R.id.dialogButtonEditTags); // our Edit Tags button
        Button addToPlaylistButton = (Button) dialog.findViewById(com.example.thenotoriousrog.tornadomusicplayer.R.id.dialogButtonAddToPlaylist); // the addToPlaylist Button.
        Button cancelButton = (Button) dialog.findViewById(com.example.thenotoriousrog.tornadomusicplayer.R.id.cancel); // cancel button on the dialog

        dialog.show(); // show the dialog box.

        // control the behavior of the button when user presses cancel.
        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                dialog.dismiss(); // close the dialog button
            }
        });

        // control the behavior for when a user chooses to add to playlist.
        addToPlaylistButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                System.out.println("Add to playlist button was pressed.");
                // todo: implement this.
            }
        });

        // control the behavior for when a user chooses to edit tags of a song.
        editTagsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                System.out.println("Edit tags button was pressed.");

                // TODO: When a user long presses a song, have that song be shown on the hints of the edit fields so the user knows what is made and what is not.
                // TODO: also show the filename of the song in the blank space above the dialog box so users can see what they have selected!

                // create an additional dialog box that will let the user enter the song fields, and then we want to modify the song based on that.
                final Dialog editDialog = new Dialog(v.getContext()); // create the dialog for the user to be able to edit the tags of the song.
                editDialog.setContentView(com.example.thenotoriousrog.tornadomusicplayer.R.layout.edit_tags_layout); // set the layout for when users want to change the tags of the song itself.
                editDialog.setTitle("Fix it up!");

                Button saveButton = (Button) editDialog.findViewById(com.example.thenotoriousrog.tornadomusicplayer.R.id.editSaveButton); // save button that will change the tags of the song based on what is selected.
                Button cancelButton = (Button) editDialog.findViewById(com.example.thenotoriousrog.tornadomusicplayer.R.id.editCancelButton); // cancel button that will let people
                final EditText songTitle = (EditText) editDialog.findViewById(com.example.thenotoriousrog.tornadomusicplayer.R.id.editSongTitle); // song title edit field.
                final EditText songArtist = (EditText) editDialog.findViewById(com.example.thenotoriousrog.tornadomusicplayer.R.id.editSongArtist); // song artist edit field.
                final EditText songAlbum = (EditText) editDialog.findViewById(R.id.editSongAlbum); // song album edit field.

                editDialog.show(); // show the edit dialog.

                // set the behavior for when the user selects save. This is when we will pull the data from the edit fields and will change the tags of the song itself.
                saveButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v)
                    {
                        System.out.println("Save the fields was selected.");

                        // pull data from the entered fields.
                        String enteredTitle = songTitle.getText().toString(); // grab the text that the user entered.
                        String enteredArtist = songArtist.getText().toString(); // grab the artist name that the user entered.
                        String enteredAlbum = songAlbum.getText().toString(); // grab the album name that the user entered.

                        if(isReadyToChange(enteredTitle, enteredArtist, enteredAlbum) == true) // all fields are entered and ready to be updated.
                        {
                            File chosenSong = new File(songsToPick.get(position).getSongPath()); // convert the song path to be edited.
                            MusicMetadataSet srcSet = null;

                            try // attempt to set the source of the song file to be edited.
                            {
                                srcSet = new MyID3().read(chosenSong); // set the source to be edited.
                            }
                            catch (IOException ex) {
                                System.out.println("received error while editing song tags: " + ex.getMessage());
                                // may need to print stack trace if error becomes too difficult to understand.
                            }

                            if(srcSet == null) // if null, there is a problem, or something failed.
                            {
                                Toast.makeText(v.getContext(), "I could find your song :(", Toast.LENGTH_SHORT).show(); // let the user know that something failed.
                                System.out.println("srcSet is null for some reason, figure out why.");
                            }
                            else // nothing went wrong, we can edit the tags of the song.
                            {
                                // change the song information to what the user entered.
                                MusicMetadata songMetaData = new MusicMetadata("name");
                                songMetaData.setSongTitle(enteredTitle); // change the title of the song.
                                songMetaData.setArtist(enteredArtist); // change the artist of the song.
                                songMetaData.setAlbum(enteredAlbum); // change the album name of the song.

                                // Overwrite the song in the same location that it was grabbed from.
                                try
                                {
                                    new MyID3().update(chosenSong, srcSet, songMetaData); // overwrite the song that the user has chosen to overwrite.

                                } catch (UnsupportedEncodingException ueex) {
                                    System.out.println("Caught an UnsupportedEncodingException: " + ueex.getMessage());
                                    Toast.makeText(v.getContext(), "I could not understand what you wrote so I couldn't update your song :(", Toast.LENGTH_SHORT).show(); // let the user know that something failed.
                                } catch (ID3WriteException id3wex) {
                                    System.out.println("Caught ID3WriteException: " + id3wex.getMessage());
                                    Toast.makeText(v.getContext(), "Something happened when I was changing your song, please try again!", Toast.LENGTH_SHORT).show(); // let the user know that something failed.
                                } catch (IOException ex) {
                                    System.out.println("Caught IOException while writing song: " + ex.getMessage());
                                    Toast.makeText(v.getContext(), "I could not edit your song :(", Toast.LENGTH_SHORT).show(); // let the user know that something failed.
                                }

                                editDialog.dismiss(); // close edit tags dialog.
                                dialog.dismiss(); // close the long press dialog.

                                Toast.makeText(v.getContext(), "Once I restart your song will be changed! :D", Toast.LENGTH_SHORT).show(); // let the user know that the changes went through and when they should see it.
                            }
                        }
                        else // user did not enter all fields.
                        {
                            Toast.makeText(v.getContext(), "You have to fill out all of the fields silly :P", Toast.LENGTH_SHORT).show(); // let the user know that something failed.
                        }
                    }
                });

                // set the behavior for when the user selects to cancel the action. Also, close this dialog when this happens.
                cancelButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v)
                    {
                        editDialog.dismiss(); // close the dialog.
                    }
                });
            }
        });

        return false;
    }
}
