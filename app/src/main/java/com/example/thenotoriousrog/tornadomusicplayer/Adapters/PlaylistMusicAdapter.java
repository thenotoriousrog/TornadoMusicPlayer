package com.example.thenotoriousrog.tornadomusicplayer.Adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.thenotoriousrog.tornadomusicplayer.Fragments.MainUIFragment;
import com.example.thenotoriousrog.tornadomusicplayer.Backend.Playlist;
import com.example.thenotoriousrog.tornadomusicplayer.Listeners.PlaylistSongPopupMenuClickListener;
import com.example.thenotoriousrog.tornadomusicplayer.R;
import com.example.thenotoriousrog.tornadomusicplayer.Backend.SongInfo;
;

import java.util.List;

/**
 * Created by thenotoriousrog on 8/17/17.
 * This class is suppose to be designed to behave in the exact same way as the MusicAdapter except that it is supposed to perform different actions for the songs in the playlist itself.
 */

public class PlaylistMusicAdapter extends ArrayAdapter<SongInfo> {

    private MainUIFragment mainUIFragment; // holds a copy of the MainUIFragment that is necessary for the app to get access to items that are a little better.
    private Playlist selectedPlaylist; // holds the playlist that was selected by this user.

    // constructor
    public PlaylistMusicAdapter(Context context, int textViewResourceID) {
        super(context, textViewResourceID);
    }

    // constructor
    // this constructor will be able to get the list of songs and we can then be able to extract the music content from them and create a new list to display!
    // or we could do it before, whatever helps.
    public PlaylistMusicAdapter(Context context, int resource, List<SongInfo> songs) {
        super(context, resource, songs);
    }

    // sets the MainUIFragment so that the Music adapter can have access to all of the fields that are needed.
    public void setMainUIFragment(MainUIFragment fragment)
    {
        mainUIFragment = fragment;
    }

    // set the playlist that was selected by the user.
    public void setSelectedPlaylist(Playlist playlist)
    {
        selectedPlaylist = playlist;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView; // view that we are working with.

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(com.example.thenotoriousrog.tornadomusicplayer.R.layout.songlist, null); // this is what expands the items in the list
        }

        final SongInfo info = getItem(position); // set to final because it is needed for the options menu. If it causes problems remove the final modifier and think of a different way to get this.

        if (info != null) // make sure that the SongInfo item exists after adapter gets the item
        {
            TextView songNameText = (TextView) v.findViewById(com.example.thenotoriousrog.tornadomusicplayer.R.id.SongName); // text view for song name
            TextView artistNameText = (TextView) v.findViewById(com.example.thenotoriousrog.tornadomusicplayer.R.id.ArtistName); // text view for artist name
            TextView songDurationText = (TextView) v.findViewById(com.example.thenotoriousrog.tornadomusicplayer.R.id.SongDuration); // text view for the song duration, which we will have to have a count down timer for by the way!
            final ImageView optionsMenu = (ImageView) v.findViewById(com.example.thenotoriousrog.tornadomusicplayer.R.id.songOptionsMenu); // get the options menu image for the song.

            // Whenever the image is clicked, we want to inflate the options menu and perform specific actions on that specific menu.
            optionsMenu.setOnClickListener(new View.OnClickListener() {

                // When clicked, show the options menu which will be to add song to a playlist or to edit tags so far.
                @Override
                public void onClick(View v)
                {
                    PopupMenu popup = new PopupMenu(getContext(), v); // bind the popup menu with the View that was clicked, which is the imageview on the song itself that was clicked.
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.playlist_songs_popup_menu, popup.getMenu()); // inflate the menu with all of the options on their

                    PlaylistSongPopupMenuClickListener playlistSongMenuClickListener = new PlaylistSongPopupMenuClickListener(getContext(), optionsMenu, mainUIFragment, selectedPlaylist, info);
                    popup.setOnMenuItemClickListener(playlistSongMenuClickListener); // listener for when a user chooses an option on the popup menu.
                    popup.show(); // display our popup menu.
                }
            });

            if (songNameText != null)
            {
                String text = info.getSongName();
                String str = "";
                if( text != null && text.length() >= 27)
                {
                    str = text.substring(0,22);
                    songNameText.setText(str + "...");
                    songNameText.setEllipsize(TextUtils.TruncateAt.END);
                }
                else // text is not too long, we can display the whole thing.
                {
                    songNameText.setText(info.getSongName());
                }
            }

            if (info != null)
            {
                artistNameText.setText(info.getArtistName());
            }

            if (songDurationText != null)
            {
                songDurationText.setText(info.getSongTime()); // this should display the actual time of the song.
                //songDurationText.setText(info.getSongDuration()); removed because we are going to display the actual time of the song now.
            }
        }


        // v.setOnClickListener(new SelectedSongPlayer(info)); // have selected Song Player take the song info and do some work with it.
        return v;
    }
}
