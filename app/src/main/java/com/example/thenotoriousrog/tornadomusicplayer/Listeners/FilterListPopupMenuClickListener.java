package com.example.thenotoriousrog.tornadomusicplayer.Listeners;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.PopupMenu;

import com.example.thenotoriousrog.tornadomusicplayer.Fragments.MainUIFragment;
import com.example.thenotoriousrog.tornadomusicplayer.Backend.SongInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by thenotoriousrog on 8/20/17.
 * This class is in control of setting all the different types of sorting and is in charge of updating all of those items as well as storing the data as well as the list options into main memory.
 */

public class FilterListPopupMenuClickListener implements PopupMenu.OnMenuItemClickListener {

    private Context context; // context of the app that we are using.
    private MainUIFragment mainUIFragment; // the mainUIFragment that we are going to be in control of so that we can update everything properly.
    ArrayList<SongInfo> songsList; // holds the list of all the songs.

    public FilterListPopupMenuClickListener(Context c, MainUIFragment fragment, ArrayList<SongInfo> songs)
    {
        context = c;
        mainUIFragment = fragment;
        songsList = songs;
    }

    // sorts the song list by songName
    public void sortBySongName(ArrayList<SongInfo> songs)
    {
        // sorts the orderedList by songname
        Collections.sort(songs, new Comparator<SongInfo>() {

            // sorts the list.
            @Override
            public int compare(SongInfo o1, SongInfo o2) {
                return o1.getSongName().compareTo(o2.getSongName());
            }
        });
    }


    // sorts the songs by artist name.
    public void sortByArtistName(ArrayList<SongInfo> songs)
    {
        // sorts the orderedList by songname
        Collections.sort(songs, new Comparator<SongInfo>() {

            // sorts the list.
            @Override
            public int compare(SongInfo o1, SongInfo o2) {
                return o1.getArtistName().compareTo(o2.getArtistName());
            }
        });
    }

    // sorts all of the songs by duration.
    public void sortByDuration(ArrayList<SongInfo> songs)
    {
        // sorts the orderedList by songname
        Collections.sort(songs, new Comparator<SongInfo>() {

            // sorts the songs by duration.
            @Override
            public int compare(SongInfo o1, SongInfo o2) {
                return o1.getSongDuration().compareTo(o2.getSongDuration());
            }
        });
    }

    // Controls the behavior of the menu items that were clicked and everything. This is very important.
    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
        if(item.getTitle().toString().equalsIgnoreCase("Order by song name..."))
        {
            System.out.println("User has chosen to sort by song name");

            sortBySongName(songsList); // sort the songs.

            // save the chosen filter to main memory.
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit(); // edit the base preferences to help the app know what we are trying to sort our lists on in the future.
            editor.putBoolean("sortBySongName", true);
            editor.putBoolean("sortByArtistName", false);
            editor.putBoolean("sortByDuration", false);
            editor.commit(); // write the filter settings to main memory.

            // set the proper booleans in the main ui fragment.
            mainUIFragment.setSortFilters(true, false, false); // sortby songname true, artistname false, duration false.

            // reload all song adapters and refresh the playlist view.
            mainUIFragment.updateSongsListAdapter(); // reloads the songListAdapter and also forces an update on the viewpager to reflect the current sort.

        }
        else if(item.getTitle().toString().equalsIgnoreCase("Order by artist name..."))
        {
            System.out.println("User has chosen to sort by artist name");

            sortByArtistName(songsList); // sort the songs.

            // save the chosen filter to main memory.
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit(); // edit the base preferences to help the app know what we are trying to sort our lists on in the future.
            editor.putBoolean("sortBySongName", false);
            editor.putBoolean("sortByArtistName", true);
            editor.putBoolean("sortByDuration", false);
            editor.commit(); // write the filter settings to main memory.

            // set the proper booleans in the main ui fragment.
            mainUIFragment.setSortFilters(false, true, false); // sortby songname true, artistname false, duration false.

            // reload all song adapters and refresh the playlist view.
            mainUIFragment.updateSongsListAdapter(); // reloads the songListAdapter and also forces an update on the viewpager to reflect the current sort.
        }
        else if(item.getTitle().toString().equalsIgnoreCase("Order by song duration...."))
        {
            System.out.println("User has chosen to sort by duration");

            sortByDuration(songsList); // sort the songs.

            // save the chosen filter to main memory.
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit(); // edit the base preferences to help the app know what we are trying to sort our lists on in the future.
            editor.putBoolean("sortBySongName", false);
            editor.putBoolean("sortByArtistName", false);
            editor.putBoolean("sortByDuration", true);
            editor.commit(); // write the filter settings to main memory.

            // set the proper booleans in the main ui fragment.
            mainUIFragment.setSortFilters(false, false, true); // sortby songname true, artistname false, duration false.

            // reload all song adapters and refresh the playlist view.
            mainUIFragment.updateSongsListAdapter(); // reloads the songListAdapter and also forces an update on the viewpager to reflect the current sort.
        }

        return true;
    }
}
