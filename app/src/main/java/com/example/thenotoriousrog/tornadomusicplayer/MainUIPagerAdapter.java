package com.example.thenotoriousrog.tornadomusicplayer;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.Vector;

/**
 * Created by thenotoriousrog on 8/18/17.
 * This class was created to allow for the pager to be able to be able to swipe and behave like the playlistpager does.
 * This class essentially removes the need for the tab layout to have its own selected listener and may even have the app run a little bit better.
 */

public class MainUIPagerAdapter extends PagerAdapter {

    private Vector<ListView> tabs; // this will hold each of tabs lists above. Meaning that whenever we want to select an item in that list we need to update that list and make the current view go away.
    private ListView songsList; // the listview that holds all of the songs.
    private ListView foldersList; // the listview that holds all of the folders.
    private ListView playlistList; // the listview that holds all of the playlists.

    // constructor to ensure that everything is built correctly.
    public MainUIPagerAdapter(Vector<ListView> tabList, ListView songs, ListView folders, ListView playlists)
    {
        tabs = tabList;
        songsList = songs;
        foldersList = folders;
        playlistList = playlists;

        System.out.println("is the vector that we have set null?" + tabs);
    }

    // This method will replace the current listview being shown to the users and also open up the folder showing the songs.
    public void openFolder(ListView selectedFolder, int currentTabPosition)
    {
        // remove the current list and replace it with the listview of the selected folder.
        tabs.remove(currentTabPosition); // remove the listview of the current tab position.
        tabs.add(currentTabPosition, selectedFolder); // add the selected folder as the list view item.
    }

    // This will replace the current listview with that of the songs of the folder. Pretty important.
    public void closeFolder(int currentTabPosition)
    {
        tabs.remove(currentTabPosition); // remove the listview from the current tabposition.
        tabs.add(currentTabPosition, foldersList); // show the folders again.
    }

    // opens the playlist and shows the songs in it removing the old listview.
    public void openPlaylist(ListView selectedPlaylist, int currentTabPosition)
    {
        tabs.remove(currentTabPosition); // remove the listview of the current tab position.
        tabs.add(currentTabPosition, selectedPlaylist); // add the selected playlist as the list view item.
    }

    // replaces the opened playlist with the list of all of the playlist items.
    public void closePlaylist(int currentTabPosition)
    {
        tabs.remove(currentTabPosition); // remove the listview from the current tabposition.
        tabs.add(currentTabPosition, playlistList); // show the folders again.
    }


    // Gets the number of view that the pager is holding which should only be 3, but we may be able to add more of those updates as time goes on.
    @Override
    public int getCount()
    {
        return tabs.size();
    }

    // this method will set up the playlist view and make it show up in the list itself. This method may also not be used right now either and may be removed.
    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        System.out.println("We are in instantiate item for MainUIPagerAdapter"); // we see this show up the same number of times as we have playlists because it is calling this method every time.

        System.out.println("current tab we are working with is: " + tabs.get(position));
        ListView list = tabs.get(position);
        System.out.println("Is the view that grabbed null? " + list);
        System.out.println("The position we are in now is: " + position);
        container.addView(list); // add the playlist to the viewgroup.

        return list;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        if(position == 0) // songs tab.
        {
            return "Songs";
        }
        else if(position == 1)
        {
            return "Folders";
        }
        else if(position == 2)
        {
            return "Playlists";
        }
        else // not possible, but returns nothing if it were the case.
        {
            return null;
        }
    }

    // this view will let us destroy the view. Might be useful if a user wants to remove a view from the view group anyways.
    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        container.removeView((ListView) object); // remove this view completely.
    }
}
