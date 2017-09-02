package com.example.thenotoriousrog.tornadomusicplayer;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.Vector;

/**
 * Created by thenotoriousrog on 6/28/17.
 *
 * This class is in charge of setting up and creating the other pages for when a user wants to change to a different playlist to edit.
 */

public class PlaylistPagerAdapter extends PagerAdapter {

    private Context context; // context for the current fragment.
    private Vector<View> playlistPages; // a vector to hold the number of playlists that the user has created.


    // constructor to send the user the number of items that currently exist in the pages themselves.
    public PlaylistPagerAdapter(Context c, Vector<View> pages)
    {
        context = c;
        playlistPages = pages;
    }


    // this method will set up the playlist view and make it show up in the list itself.
    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        System.out.println("We are in instantiate item"); // we see this show up the same number of times as we have playlists because it is calling this method every time.

        View playlist = playlistPages.get(position);
        container.addView(playlist); // add the playlist to the viewgroup.

        return playlist;
    }

    @Override
    public int getCount() {
        return playlistPages.size();
    }

    // tells us if the view itself is from the same object or not. **Probably won't need this method.
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    // this view will let us destroy the view. Might be useful if a user wants to remove a view from the view group anyways.
    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        container.removeView((View) object); // remove this view completely.
    }
}
