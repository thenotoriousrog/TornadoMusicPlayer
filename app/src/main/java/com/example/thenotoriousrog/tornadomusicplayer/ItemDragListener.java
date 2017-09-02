package com.example.thenotoriousrog.tornadomusicplayer;

import android.content.ClipData;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.thenotoriousrog.myapplication.R;

import java.util.ArrayList;

/**
 * Created by thenotoriousrog on 6/27/17.
 * This class controls the behavior for when a drag is in progress and when the user lets go of the drag process.
 */

public class ItemDragListener implements View.OnDragListener {

    private ArrayList<SongInfo> addedSongs = new ArrayList<SongInfo>(); // this arraylist will hold all of the added songs in the list itself.
    private DragSongAdapter dsAdapter; // this adapter will hold the dragged songs and display it in our playlist field. Sweet!!!!!
    private ListView playListView; // this view will control the playlist scrollable view down below.
    private boolean dropExited = false; // lets us know that the user has left the current view i.e. playlists.
    private boolean dropped = false;
    private ModifyAllPlaylistsFragmentActivity modifyAllPlaylistsFragmentActivity;
    private AddSongsToSinglePlaylistFragmentActivity addSongsToSinglePlaylistFragmentActivity;
    private Playlist playlist; // the current playlist that is going to be modified.


    public ItemDragListener(ListView playlistView, ModifyAllPlaylistsFragmentActivity fragment, Playlist playList)
    {
        playListView = playlistView;
        modifyAllPlaylistsFragmentActivity = fragment;

        playlist = playList;
    }

    // this constructor is only used when there is a single playlist
    public ItemDragListener(ListView playlistView, AddSongsToSinglePlaylistFragmentActivity fragment, Playlist playList)
    {
        playListView = playlistView;
        addSongsToSinglePlaylistFragmentActivity = fragment;

        playlist = playList;
    }

    // Create a seperate constructor here that will take in the AddSongsToSinglePlaylistFragmentActivity and change the code to ensure that it only works with the stuff that's sent in.

    // resets the checker flags to help us be able to determine if the user has indeed started a drag and drop event.
    public void resetDropCheckers()
    {
        dropExited = false;
        dropped = true;
    }

    // evaluate what the user has done and see if it qualifies to add the song to the playlist or to ignore it.
    public void evaluateDrop(DragEvent event, ClipData data)
    {
        if(dropExited == true && dropped == true)
        {
            /* not needed, we want the list within the dsAdapter to act as it should and not erase anything.
            if(playlist != null) // we must make sure that the list is not null, if it is null that means no playlists were created and we have to account for that.
            {
                addedSongs = playlist.songs(); // grab the songs in the playlist and set them to the addedSongs to allow the list to be modified.
                System.out.println("The songs that we are setting to the list of current songs is: " + playlist.songs());
                //dsAdapter.notifyDataSetChanged(); // notify that the data has changed now because we do not want the list to flash whenever we are adding songs to the list.
            }
            */

            System.out.println("ItemDragListener we are working with is: " + ItemDragListener.this);
            System.out.println("Clip data we received when something was dropped." + data.toString()); // hope that we get the clip data mang.
            // todo: figure out if we get the actual clip data here. If we do, this is easy, we just populate the list with the song path down below negro!

            String rawClipData = data.toString(); //event.getClipData().toString(); // we got the actual string that we want to work with here.
            String[] splitData = rawClipData.split("\\{"); // split the string by curly brackets.

            //String songPath = rawClipData.substring(50, rawClipData.length()-3); // remove the first 50 characters and the last 3// characters from the clip data to get the full file path.
            String songPath = splitData[2].substring(3, splitData[2].length() - 3); // remove the first 3 characters and remove the last 3 characters.
            System.out.println("Is this a song path? " + songPath);

            // get the song path and convert it into a song info.
            SongInfo draggedSong = new SongInfo();
            draggedSong.getandSetSongInfo(songPath);
            addedSongs.add(draggedSong); // add a new song to the list.

            playListView.setAdapter(dsAdapter);
            dsAdapter.notifyDataSetChanged(); // let the adapter know that the list has changed.

            //playListView.setAdapter(dsAdapter);
            //dsAdapter.notifyDataSetChanged(); // let the adapter know that the list has changed.
        }
        else
        {
            // do nothing, user has performed the correct task.
        }
    }

    // this method simply switches the icons after a drag event has taken place.
    public void revertIcons()
    {
        if(modifyAllPlaylistsFragmentActivity == null) // if this is null, this means we are working with only one playlist not all at once.
        {
            View clickedItem = addSongsToSinglePlaylistFragmentActivity.getClickedItem(); // get the item that was clicked by the user.
            ImageView touchIcon = (ImageView) clickedItem.findViewById(R.id.touchIcon);
            ImageView dragIcon = (ImageView) clickedItem.findViewById(R.id.dragIcon);

            // switch the icons around again
            touchIcon.setVisibility(View.VISIBLE);
            dragIcon.setVisibility(View.INVISIBLE);
        }
        else
        {
            View clickedItem = modifyAllPlaylistsFragmentActivity.getClickedItem(); // get the item that was clicked by the user.
            ImageView touchIcon = (ImageView) clickedItem.findViewById(R.id.touchIcon);
            ImageView dragIcon = (ImageView) clickedItem.findViewById(R.id.dragIcon);

            // switch the icons around again
            touchIcon.setVisibility(View.VISIBLE);
            dragIcon.setVisibility(View.INVISIBLE);
        }
    }

    // behavior for when a drag response has started.
    @Override
    public boolean onDrag(View v, DragEvent event)
    {
        if(playlist != null) // If the list is not null, then we can add the songs into so that the dsAdapter will act accordingly and not erase the list when a drag is started.
        {
            addedSongs = playlist.songs(); // grab the songs in the playlist and set them to the addedSongs to allow the list to be modified.
            System.out.println("The songs that we are setting to the list of current songs is: " + playlist.songs());
            //dsAdapter.notifyDataSetChanged(); // notify that the data has changed now because we do not want the list to flash whenever we are adding songs to the list.
        }

        // todo: Note that is we need to add a playlist fragment there is the ability to set the playlist fragment with the method.
        dsAdapter = new DragSongAdapter(v.getContext(), R.layout.songlist, addedSongs); // create the adapter needed for the playListView. Uses the same song list as my main layout.
       // playListView = (ListView)v.getRootView().findViewById(R.id.playlistList); // get a copy of our list view.
        playListView.setAdapter(dsAdapter);
       // System.out.println("is the playlistView null? " + playListView);

        ClipData grabbedData = null; // this is set later on.

        switch (event.getAction())
        {
            case DragEvent.ACTION_DRAG_STARTED:
                System.out.println("Drag event started");

                resetDropCheckers(); // make all drop checkers false again.
                return true;
            case DragEvent.ACTION_DRAG_ENTERED:
                return true;

            case DragEvent.ACTION_DRAG_EXITED: // may want to add my code for adding items into the view here.
                System.out.println("Drag event exited");
                dropExited = true;
                return true;

            case DragEvent.ACTION_DRAG_ENDED:
                return true;

            case DragEvent.ACTION_DROP: // control the action for when the user releases the song that they wanted to drag down.
                System.out.println("Drop detected");
                grabbedData = event.getClipData();
                evaluateDrop(event, grabbedData);
                System.out.println("is clip data null? " + grabbedData);
                dropped = true;
                revertIcons();
                return true;
        }

        return false;
    }
}
