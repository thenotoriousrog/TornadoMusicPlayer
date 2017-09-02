package com.example.thenotoriousrog.tornadomusicplayer;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.ListView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

/**
 * Created by thenotoriousrog on 5/23/17.
 * This class is in charge of determining the behaviors associated when tabs are selected, unselected, and reselected.
 */
// todo: This class is no longer used, we can remove it from the app completely!!!
public class TabSelectedListener extends MainActivity implements TabLayout.OnTabSelectedListener {

    private ListView songsList = null; // this is the view for when we select the "Songs" tab
    private ListView foldersList = null; // this is the view for when we select the "Folders" tab
    private ListView folderSongsList = null; // this is the view for when we select a folder in the "Folders" tab.
    private ListView playListList = null; // to show the list of the playlists on the app.
    private ListView playlistSongView = null; // this shows the song in the playlist here.
    private FloatingActionButton addPlaylistButton = null; // allows a user to add a playlist with the button.
    private SlidingUpPanelLayout slidingLayout; // a copy of the sliding layout mainly to stop shuffling whenever we go to another list and have the users select to shuffle the songs again manually.
    private MainUIFragment mainUIFragment; // holds the main UI fragment to let us change which tab that the user is in for better setting of current lists in the SelectedSongListeners.

    public TabSelectedListener(ListView songs, ListView folders, ListView songsinfolder, ListView playlists, FloatingActionButton button, SlidingUpPanelLayout layout, MainUIFragment fragment) // default contsructor that sets everything up to be used for tab behvaior
    {
        songsList = songs;
        foldersList = folders;
        folderSongsList = songsinfolder; // in charge of the list of songs in a folder.
        playListList = playlists;
        addPlaylistButton = button;
        slidingLayout = layout; // todo: remove this as it is not needed for this class.
        mainUIFragment = fragment;

       // playlistSongView = (ListView) slidingLayout.findViewById(R.id.openPlaylistSongsView); // get the open songs play list view.
    }

    // controls the action for when a tab is selected for the first time
    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        int tabPosition = tab.getPosition(); // tells us which tab was selected.

        if(tabPosition == 0) // Songs tab
        {
            System.out.println("We found the songs tab!!");
            System.out.println("count for this view is: " + songsList.getCount());
            songsList.setVisibility(View.VISIBLE); // make the list visible again.
            songsList.startLayoutAnimation(); // start the layout animation for when this tab is selected.

           // mainUIFragment.setCurrentTabPosition(0); // we are in songs tab.
        }
        else if(tabPosition == 1) // Folders tab
        {
            System.out.println("count for folders view is: " + foldersList.getCount());

            foldersList.setVisibility(View.VISIBLE); // make the folderList visibile here.
            foldersList.startLayoutAnimation(); // playlist the layout animation for this list.
            foldersList.setNestedScrollingEnabled(true);

           // mainUIFragment.setCurrentTabPosition(1); // we are in folders tab.
            // folderSongsList.setVisibility(View.GONE);

            // **NOTE: we do not want to include the folderSongsList because we want the users to go back to the Folders list itself.
        }
        else // Playlists tab
        {
            playListList.setVisibility(View.VISIBLE);
            playListList.setNestedScrollingEnabled(true);
            playListList.startLayoutAnimation(); // start the fade in animation for when the app is started for the very first time.
            addPlaylistButton.setVisibility(View.VISIBLE);

           // mainUIFragment.setCurrentTabPosition(2); // we are in playlists tab.
        }

    }

    // controls the behavior when a tab is unselected
    @Override
    public void onTabUnselected(TabLayout.Tab tab)
    {
        // do nothing for now.
        // will be in charge of making sure that certain listViews become "gone" or "invisible" when needed.
        if(tab.getPosition() == 0) // Songs tab is unselected
        {
            songsList.setVisibility(View.GONE); // removes the list temporarily.
        }
        else if(tab.getPosition() == 1) // Folders tab is unselected.
        {
            foldersList.setVisibility(View.GONE); // removes the list temporarily.
            folderSongsList.setVisibility(View.GONE); // removes the list of songs in the folder as well so they do not lay on top of each other.
        }
        else // Playlists tab is unselected
        {
            addPlaylistButton.setVisibility(View.GONE); // make the button visible for users to interact with.
            playListList.setVisibility(View.GONE); // make the playlist list go away
            playlistSongView.setVisibility(View.GONE); // make the songs in the playlist gone.
        }
    }

    // todo: may need to change this as we only want the mainUI fragment to show that we have been unselected and reselected. This is important.
    // controls the behavior when a tab is reselected.
    @Override
    public void onTabReselected(TabLayout.Tab tab)
    {
        int tabPosition = tab.getPosition(); // get the current tab we are working with.

        if(tabPosition == 0) // Songs tab
        {
            System.out.println("reselected tab 0");
            songsList.setVisibility(View.VISIBLE); // make this tab visible again.
            songsList.startLayoutAnimation(); // start the layout animation when this happens.
            System.out.println("The count for this view is: " + songsList.getCount());
        }
        else if(tabPosition == 1) // Folders tab
        {
            System.out.println("reselected tab 1");
            System.out.println("count for folders is: " + foldersList.getCount());

            // add method to have action here.
            foldersList.setVisibility(View.VISIBLE); // make this tab visible again
            foldersList.startLayoutAnimation(); // play the fading animation when a user selects this tab.
            folderSongsList.setVisibility(View.GONE); // make the previous folder list of songs go away as we are going back to the list of Folders.
        }
        else if(tabPosition == 2) // Playlists tab
        {
            System.out.println("reselected tab 2");
            playListList.setVisibility(View.VISIBLE); // make playlist list visible again
            playListList.startLayoutAnimation(); // play the fading animation on the playlists.
            playlistSongView.setVisibility(View.GONE); // make this list gone.
            addPlaylistButton.setVisibility(View.VISIBLE); // make button visible again to add another playlist.


            // todo: this may cause problems as the users may want to look at their playlists but not startplaying in them. Will need to think of a fix for this.
            //mainUIFragment.setCurrentTab(2); // we are in playlists tab.
        }
        else {
            // do nothing.
        }
    }
}
