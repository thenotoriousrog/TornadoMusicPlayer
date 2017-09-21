package com.example.thenotoriousrog.tornadomusicplayer;

import android.app.Dialog;
import android.content.Context;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.thenotoriousrog.tornadomusicplayer.R;;

import org.cmc.music.common.ID3WriteException;
import org.cmc.music.metadata.MusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by thenotoriousrog on 8/15/17.
 * This class is in charge of controlling what happens when a user selects one of the options from the popup menu for a specific song.
 */

public class SongPopupMenuClickListener implements PopupMenu.OnMenuItemClickListener {

    private Context context; // the context that is used for pinning popup menu items to views.
    private ImageView songOptionsMenuView; // holds the three dot image that we can bind other pop messages too.
    private MainUIFragment mainUIFragment; // the main ui fragment needed in order to get the appropriate data that our users are going to need.
    private SongInfo song; // the song that was chosen by the user.

    // This constructor may have to take more arguments in order to get all of the options to work correctly.
    public SongPopupMenuClickListener(Context c, ImageView optionsView, MainUIFragment fragment, SongInfo songChosen)
    {
        context = c;
        songOptionsMenuView = optionsView;
        mainUIFragment = fragment;
        song = songChosen;
    }

    // Writes the playlists to MainMemory
    public void writePlaylistsToMainMemory(View view, ArrayList<Playlist> playlists)
    {
        // WRITE the playlist object to main memory.
        String ser = SerializeObject.objectToString(playlists); // Trying to serialize the entire Playlist Arraylist, Not sure if it is possible or not yet.
        if(ser != null && !ser.equalsIgnoreCase(""))
        {
            String savedPlaylistFileName = "playlists.dat"; // should be something like "Playlist 1.dat"
            SerializeObject.WriteSettings(view.getContext(), ser, savedPlaylistFileName); // write the item to main memory.
        }
        else // Writing the obeject failed. Think of a better way to handle this if at all.
        {
            System.out.println("WE DID NOT WRITE THE LIST CORRECTLY, SOMETHING BAD HAPPENED.");
            SerializeObject.WriteSettings(view.getContext(), "", "playlists.dat"); // we should be getting this list if we are something bad has happened.
        }

    }

    // This method sets the listener for when a user chooses the playlist item that they wanted to add the song to, very important!
    public void listenForSelectedPlaylist(PopupMenu playlistToPickMenu)
    {
        System.out.println("Setting click listeners now");
        playlistToPickMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {


            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                System.out.println("A playlist item was clicked");
                String playlistName = item.getTitle().toString(); // get the name of the playlist that was chosen.

                ArrayList<SongInfo> songs = null; // holds all of the songs of the playlist that we want to add to.
                ArrayList<Playlist> updatedList = mainUIFragment.getPlaylists(); // the list playlist that we are going to update once the user finds the songs that they are looking for.
                int playlistPos = 0; // holds the position of the playlist to ensure that when updated, the playlist is modified in the correct order and saved in the correct order.

                // loop through the playlists and find the playlist with the name of the playlist that we are trying to work on.
                for(int i = 0; i < mainUIFragment.getPlaylists().size(); i++)
                {
                    System.out.println("looping...");
                    if(mainUIFragment.getPlaylists().get(i).name().equals(playlistName)) // we have found the playlist that we want to work with.
                    {
                        System.out.println("the name of the playlist that the user chose is: " + playlistName);
                        songs = mainUIFragment.getPlaylists().get(i).songs(); // set the songs that we are going to add to this playlist.
                        songs.add(song); // add the song that the user has chosen to add to the playlist.
                        playlistPos = i; // the position of the playlist that is being updated.
                    }
                }

                // TODO: have a test to make sure that the songs is not null and if it is null we need to return and quit this action and tell the user why for whatever reason.

                Playlist updatedPlaylist = new Playlist(playlistName, songs); // update the playlist.

                updatedList.remove(playlistPos); // remove the previous version of the playlist in this position.
                updatedList.add(playlistPos, updatedPlaylist); // readd the playlist in the same position but with the song added this time.

                writePlaylistsToMainMemory(mainUIFragment.getView(), updatedList); // write the updated playlists to main memory again!

                return true; // tell the user now that everything is correct! todo: display a snackbar message to alert the user that their song has been added successfully to their playlist.
            }
        });
    }

    // This message makes sure that all of the items in the fields are filled out with something before changing tags so users do not mess up their songs.
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

    // This method decides what action to take place when the user clicks the option that they want to do.
    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
        if(item.getTitle().toString().equalsIgnoreCase("Add to playlist...")) // user has chosen to add this specific song to a playlist.
        {
            // generate another popup menu on the options view that lists the playlists.
            PopupMenu playlistToPickMenu = new PopupMenu(context, songOptionsMenuView); // bind the new options menu to the same menu view.

            // Loop through every playlist and add each playlist name to the new popup playlist item.
            for(int i = 0; i < mainUIFragment.getPlaylists().size(); i++)
            {
                playlistToPickMenu.getMenu().add(mainUIFragment.getPlaylists().get(i).name()); // add the name of the playlist to the menu
            }

            // replace the popup with the popup of the list menu itself.
            MenuInflater inflater = playlistToPickMenu.getMenuInflater();
            inflater.inflate(com.example.thenotoriousrog.tornadomusicplayer.R.menu.playliststoaddfrom_popup_menu, playlistToPickMenu.getMenu()); // get the menu
            listenForSelectedPlaylist(playlistToPickMenu); // listens for a user to add songs to their playlist and then does that.
            playlistToPickMenu.show(); // show this menu now very important.

            // todo:  create a snackbar message telling the user that the option they wanted to do was successful.
        }
        else if(item.getTitle().toString().equalsIgnoreCase("Edit tags")) // user chose to edit the tags on this song.
        {
            // dismiss the popup menu

            // create an additional dialog box that will let the user enter the song fields, and then we want to modify the song based on that.
            final Dialog editDialog = new Dialog(context); // create the dialog for the user to be able to edit the tags of the song.
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
                        File chosenSong = new File(song.getSongPath()); // convert the song that was chosen to the songPath to be edited by the app.
                        //File chosenSong = new File(songsToPick.get(position).getSongPath()); // convert the song path to be edited.
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

                            Toast.makeText(v.getContext(), "Once I restart your song will be changed! :D", Toast.LENGTH_SHORT).show(); // let the user know that the changes went through and when they should see it.
                        }
                    }
                    else // user did not enter all fields.
                    {
                        Toast.makeText(v.getContext(), "You have to fill out all of the fields silly :P", Toast.LENGTH_SHORT).show(); // let the user know that something failed.
                    }
                }
            });

            // When the user presses the cancel button, close the dialog box.
            cancelButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v)
                {
                    editDialog.dismiss(); // close the dialog.
                }
            });

            // todo: fix the tags issue, when editing tags, the tags are not working whenever you try to modify tags that are on an SD card. Very important to get that fixed.
        }

        return true; // tells the system that an option was indeed selected.
    }
}
