package com.example.thenotoriousrog.tornadomusicplayer.Listeners;

import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.example.thenotoriousrog.tornadomusicplayer.Fragments.MainUIFragment;
import com.example.thenotoriousrog.tornadomusicplayer.Backend.Playlist;
import com.example.thenotoriousrog.tornadomusicplayer.Backend.SerializeObject;

import java.util.ArrayList;

/**
 * Created by thenotoriousrog on 8/17/17.
 * This class is to control what happens when the user wants to select options for playlists in general.
 */

public class PlaylistPopupMenuClickListener implements PopupMenu.OnMenuItemClickListener{

    private Context context; // the context that is used for pinning popup menu items to views.
    private ImageView songOptionsMenuView; // holds the three dot image that we can bind other pop messages too.
    private MainUIFragment mainUIFragment; // the main ui fragment needed in order to get the appropriate data that our users are going to need.
    private Playlist currentPlaylist; // the current playlist that we are working with.

    // This constructor may have to take more arguments in order to get all of the options to work correctly.
    public PlaylistPopupMenuClickListener(Context c, ImageView optionsView, MainUIFragment fragment, Playlist playlistChosen)
    {
        context = c;
        songOptionsMenuView = optionsView;
        mainUIFragment = fragment;
        currentPlaylist = playlistChosen;
    }

    // sets the MainUIFragment so that the Music adapter can have access to all of the fields that are needed.
    public void setMainUIFragment(MainUIFragment fragment)
    {
        mainUIFragment = fragment;
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

    // Controls the behavior for when a user wants to do specific options for playlists.
    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
        if(item.getTitle().toString().equalsIgnoreCase("Delete")) // user has decided to delete the playlist completely. This is a very big deal.
        {
            ArrayList<Playlist> playlists = mainUIFragment.getPlaylists(); // get the current list of playlists.

            System.out.println("Is the playlist that we want to work with in the playlistpopupmenu null?" + currentPlaylist);
            System.out.println("Did we grab a null list of playlists? " + playlists);
            System.out.println("When we grab playlists directly from mainUIFragment we get: " + mainUIFragment.getPlaylists());

            // Search through all of the playlists and find the one with the same name as our own, and remove it from the list of all playlists.
            for(int i = 0; i < playlists.size(); i++)
            {
                if(playlists.get(i).name().equalsIgnoreCase(currentPlaylist.name())) // check if the playlists have the exact same name. Very important.
                {
                    playlists.remove(i); // remove the entire playlist from the list of playlists.
                }
            }

            // write the playlists to main memory.
            writePlaylistsToMainMemory(mainUIFragment.getView(), playlists); // write the songs to the mainView.

            mainUIFragment.refreshPlaylistAdapter(); // refresh the adapter to show the new playlists that have been modified.
            mainUIFragment.refreshPlaylistMusicAdapter(); // refresh the music adapter and let it know that we have done something important here.
        }
        else if(item.getTitle().toString().equalsIgnoreCase("Add songs"))
        {
            System.out.println("Is the playlist that we want to work with in the playlistpopupmenu null?" + currentPlaylist.name());
            mainUIFragment.startAddSongsToSinglePlaylistActivity(currentPlaylist); // send the playlist that needs to be used and then start the activity for that playlist, very important.
        }

        return true;
    }
}
