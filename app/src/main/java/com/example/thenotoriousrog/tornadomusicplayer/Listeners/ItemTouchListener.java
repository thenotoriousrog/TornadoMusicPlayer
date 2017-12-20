package com.example.thenotoriousrog.tornadomusicplayer.Listeners;

import android.content.ClipData;
import android.view.MotionEvent;
import android.view.View;

import com.example.thenotoriousrog.tornadomusicplayer.Fragments.AddSongsToSinglePlaylistFragmentActivity;
import com.example.thenotoriousrog.tornadomusicplayer.Fragments.ModifyAllPlaylistsFragmentActivity;
import com.example.thenotoriousrog.tornadomusicplayer.Backend.SongInfo;

/**
 * Created by thenotoriousrog on 6/27/17.
 * This class is in charge starting the touch_icon controls for when a user wants to start a drag and drop.
 */

public class ItemTouchListener implements View.OnTouchListener {

    ModifyAllPlaylistsFragmentActivity modifyAllPlaylistsFragmentActivity; // this will let us communicate with the playlist fragment for when we want to get the next item that was modified.
    AddSongsToSinglePlaylistFragmentActivity addSongsToSinglePlaylistFragmentActivity; // a copy of the fragment activity that only modifies one playlist at a time.

    public ItemTouchListener(ModifyAllPlaylistsFragmentActivity fragment)
    {
        modifyAllPlaylistsFragmentActivity = fragment;
    }

    // Create a seperate constructor here that will take in the AddSongsToSinglePlaylistFragmentActivity and change the code to ensure that it only works with the stuff that's sent in.
    public ItemTouchListener(AddSongsToSinglePlaylistFragmentActivity fragment)
    {
        addSongsToSinglePlaylistFragmentActivity = fragment;
    }

    // Controls the behavior for when the user touches a song that they wish to add to the playlist.
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        // if a user drags an item down, then the drag action has started.
        if(event.getActionMasked() == MotionEvent.ACTION_DOWN)
        {
            if(modifyAllPlaylistsFragmentActivity == null) // user is modifying only one playlist at a time.
            {
                // todo: figure out a way to be able to pass the correct song title so that users can see song name on the list itself.
                SongInfo draggedSong = addSongsToSinglePlaylistFragmentActivity.getSelectedSong(); // get the selected song from the user.

                // first string = what the user sees when they drag.
                // second string = what the the actual data is which is the path of the song that we actually want here.
                ClipData data = ClipData.newPlainText(draggedSong.getSongName(), draggedSong.getSongPath()); // nothing for now, but this is what tell the other drag view that something was received.

                System.out.println("clip data that we clipped is " + data);
                View selectedSongItem = addSongsToSinglePlaylistFragmentActivity.getSelectedSongItemView(); // get the song that was selected.
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(selectedSongItem);
                v.startDragAndDrop(data, shadowBuilder, selectedSongItem, 0); // the v might be broken right now. will need to fix this later.
                v.setOnTouchListener(null); // fixes the problem with multiple listeners running at the same time.
                return false;
            }
            else // user is modifying all playlists at once.
            {
                // todo: figure out a way to be able to pass the correct song title so that users can see song name on the list itself.
                SongInfo draggedSong = modifyAllPlaylistsFragmentActivity.getSelectedSong(); // get the selected song from the user.

                // first string = what the user sees when they drag.
                // second string = what the the actual data is which is the path of the song that we actually want here.
                ClipData data = ClipData.newPlainText(draggedSong.getSongName(), draggedSong.getSongPath()); // nothing for now, but this is what tell the other drag view that something was received.

                System.out.println("clip data that we clipped is " + data);
                View selectedSongItem = modifyAllPlaylistsFragmentActivity.getSelectedSongItemView(); // get the song that was selected.
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(selectedSongItem);
                v.startDragAndDrop(data, shadowBuilder, selectedSongItem, 0); // the v might be broken right now. will need to fix this later.
                v.setOnTouchListener(null); // fixes the problem with multiple listeners running at the same time.
                return false;
            }

        }
        else {
            return false;
        }
    }
}
