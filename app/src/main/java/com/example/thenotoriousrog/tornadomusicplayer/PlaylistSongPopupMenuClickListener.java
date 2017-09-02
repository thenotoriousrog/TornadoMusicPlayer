package com.example.thenotoriousrog.tornadomusicplayer;

import android.app.Dialog;
import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.thenotoriousrog.myapplication.R;

import org.cmc.music.common.ID3WriteException;
import org.cmc.music.metadata.MusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by thenotoriousrog on 8/17/17.
 * This class controls the behavior of the popup for the songs that are in a specific playlist.
 */

public class PlaylistSongPopupMenuClickListener implements PopupMenu.OnMenuItemClickListener{

    private Context context; // the context that is used for pinning popup menu items to views.
    private ImageView songOptionsMenuView; // holds the three dot image that we can bind other pop messages too.
    private MainUIFragment mainUIFragment; // the main ui fragment needed in order to get the appropriate data that our users are going to need.
    private Playlist currentPlaylist; // the current playlist that we are working with.
    private SongInfo song; // the song that was chosen by the user.

    // This constructor may have to take more arguments in order to get all of the options to work correctly.
    public PlaylistSongPopupMenuClickListener(Context c, ImageView optionsView, MainUIFragment fragment, Playlist playlist, SongInfo songChosen)
    {
        context = c;
        songOptionsMenuView = optionsView;
        mainUIFragment = fragment;
        currentPlaylist = playlist;
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


    // This method controls the actions when a user selects an option from one of the items in the popup menu.
    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
        if(item.getTitle().toString().equalsIgnoreCase("Remove from playlist")) // user wants to remove this song from the playlist. Very important!
        {
            ArrayList<SongInfo> playlistSongs = currentPlaylist.songs(); // get the songs in the current playlist.

            // loop through all of the songs in the list and find the song that we are working with.
            for(int i = 0; i < playlistSongs.size(); i++)
            {
                if(playlistSongs.get(i) == song) // if the SongInfo's match each other than we know that we have found the song within the list that we want to remove from the playlist.
                {
                    playlistSongs.remove(i); // remove the song from this index because this song is no longer needed in the playlist.
                }
            }

            ArrayList<Playlist> playlists = mainUIFragment.getPlaylists(); // get the playlists that we are working with.

            // loop through the playlists and grab the correct playlist that we want to update here.
            for(int i = 0; i < playlists.size(); i++)
            {
                if(playlists.get(i).name().equals(currentPlaylist.name())) // if the playlist names are correct, we need to remove that playlist and update the correct one.
                {
                    playlists.remove(i); // remove this playlist as it has the old playlist with the songs in it not being current.
                    Playlist updatedPlaylist = new Playlist(currentPlaylist.name(), playlistSongs); // create a new Playlist with the updated information.

                    playlists.add(i, updatedPlaylist); // readd the playlist in the correct spot in the list and have it print the way that it should.
                }
            }

            writePlaylistsToMainMemory(mainUIFragment.getView(), playlists); // send the updated playlists to be written to main memory.

            // update the data that we are working with for the user to see the updates being made in real time.
            mainUIFragment.setPlaylists(playlists); // set the playlists that we are working with.
            mainUIFragment.refreshPlaylistAdapter(); // refresh the playlist adapter that we are working with.
            mainUIFragment.refreshPlaylistMusicAdapter(); // refresh the adapter of the songs that we are working with.

        }
        else if(item.getTitle().toString().equalsIgnoreCase("Edit tags"))
        {
            // create an additional dialog box that will let the user enter the song fields, and then we want to modify the song based on that.
            final Dialog editDialog = new Dialog(context); // create the dialog for the user to be able to edit the tags of the song.
            editDialog.setContentView(com.example.thenotoriousrog.myapplication.R.layout.edit_tags_layout); // set the layout for when users want to change the tags of the song itself.
            editDialog.setTitle("Fix it up!");

            Button saveButton = (Button) editDialog.findViewById(com.example.thenotoriousrog.myapplication.R.id.editSaveButton); // save button that will change the tags of the song based on what is selected.
            Button cancelButton = (Button) editDialog.findViewById(com.example.thenotoriousrog.myapplication.R.id.editCancelButton); // cancel button that will let people
            final EditText songTitle = (EditText) editDialog.findViewById(com.example.thenotoriousrog.myapplication.R.id.editSongTitle); // song title edit field.
            final EditText songArtist = (EditText) editDialog.findViewById(com.example.thenotoriousrog.myapplication.R.id.editSongArtist); // song artist edit field.
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
        }

        return true;
    }
}
