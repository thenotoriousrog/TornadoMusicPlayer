package com.example.thenotoriousrog.tornadomusicplayer.Adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by thenotoriousrog on 8/21/17.
 * This class behaves exactly like the playlistPagerAdapter except that it only takes a single view instead of a vector of views.
 */

public class SinglePlaylistPagerAdapter extends PagerAdapter {

    private Context context; // context for the current fragment.
    private View playlistPage; // a vector to hold the number of playlists that the user has created.


    // constructor to send the user the number of items that currently exist in the pages themselves.
    public SinglePlaylistPagerAdapter(Context c, View page)
    {
        context = c;
        playlistPage = page;
    }


    // this method will set up the playlist view and make it show up in the list itself.
    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        System.out.println("We are in instantiate item"); // we see this show up the same number of times as we have playlists because it is calling this method every time.

        View playlist = playlistPage;
        container.addView(playlist); // add the playlist to the viewgroup.

        return playlist;
    }

    @Override
    public int getCount() {
        return 1; // only need to return 1 because this is the only page that is going to be created.
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
