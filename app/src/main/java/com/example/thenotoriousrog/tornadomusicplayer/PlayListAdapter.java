package com.example.thenotoriousrog.tornadomusicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.thenotoriousrog.tornadomusicplayer.R;;
import java.util.List;

/**
 * Created by thenotoriousrog on 6/23/17.
 */

public class PlayListAdapter extends ArrayAdapter<Playlist> {

    private MainUIFragment mainUIFragment; // holds a copy of the MainUIFragment that is necessary for the app to get access to items that are a little better.

    // constructor
    public PlayListAdapter(Context context, int textViewResourceID) {
        super(context, textViewResourceID);
    }

    // constructor
    // this constructor will be able to get the list of songs and we can then be able to extract the music content from them and create a new list to display!
    // or we could do it before, whatever helps.
    public PlayListAdapter(Context context, int resource, List<Playlist> songs) {
        super(context, resource, songs);
    }

    // sets the MainUIFragment so that the Music adapter can have access to all of the fields that are needed.
    public void setMainUIFragment(MainUIFragment fragment)
    {
        mainUIFragment = fragment;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView; // view that we are working with.

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(com.example.thenotoriousrog.tornadomusicplayer.R.layout.playlist_list, null); // this is what expands the items in the list
        }

        //SongInfo info = getItem(position);
        //Pair<String, ArrayList<SongInfo>> playListPair = getItem(position); // grabs a single playlist from the list of playlists. old version
        final Playlist playlist = getItem(position); // grab a playlist


        if (playlist.name() != null && playlist.songs() != null) // make sure that the playlist we grabbed is not corrupted.
        {
            TextView playListName = (TextView) v.findViewById(com.example.thenotoriousrog.tornadomusicplayer.R.id.playListName); // allows us to get the name of the playlist.
            final ImageView optionsMenu = (ImageView) v.findViewById(com.example.thenotoriousrog.tornadomusicplayer.R.id.playlistOptionsMenu); // get the options menu item for the playlists.

            // Whenever the image is clicked, we want to inflate the options menu and perform specific actions on that specific menu.
            optionsMenu.setOnClickListener(new View.OnClickListener() {

                // When clicked, show the options menu which will be to add song to a playlist or to edit tags so far.
                @Override
                public void onClick(View v)
                {
                    PopupMenu popup = new PopupMenu(getContext(), v); // bind the popup menu with the View that was clicked, which is the imageview on the song itself that was clicked.
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.playlist_popup_menu, popup.getMenu()); // inflate the menu with all of the options on their

                    PlaylistPopupMenuClickListener playlistPopupMenuClickListener = new PlaylistPopupMenuClickListener(getContext(), optionsMenu, mainUIFragment, playlist);

                    System.out.println("The name of the playlist that the popup menu opened for is: " + playlist.name());
                    popup.setOnMenuItemClickListener(playlistPopupMenuClickListener); // listener for when a user chooses an option on the popup menu.
                    popup.show(); // display our popup menu.
                }
            });


            if(playListName != null) // make sure that the playlist TextView that we just grabbed is not null
            {
                playListName.setText(playlist.name()); // grabs the name of the playlist
            }

            // may be able to do something with the songs here, but it is not likely.
        }


        // v.setOnClickListener(new SelectedSongPlayer(info)); // have selected Song Player take the song info and do some work with it.
        return v;
    }
}
