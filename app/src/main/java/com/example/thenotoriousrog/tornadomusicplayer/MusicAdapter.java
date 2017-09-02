package com.example.thenotoriousrog.tornadomusicplayer;

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

import com.example.thenotoriousrog.myapplication.R;

import java.util.List;

/**
 * Created by thenotoriousrog on 5/27/17.
 * This class is in charge of creating a custom adapter that will allow us to display very specific info into ListView.
 *  This will include Song name, artist name, song duration, etc.
 *
 *  I also may need to change MusicAdapter from holding just a string, it may not be able to do that.
 *  I may have to create a pairing class to allow me to pair specific info and the arrayadapter can use that, not quite sure yet honestly.
 */
public class MusicAdapter extends ArrayAdapter<SongInfo> {

    private MainUIFragment mainUIFragment; // holds a copy of the MainUIFragment that is necessary for the app to get access to items that are a little better.

    // constructor
    public MusicAdapter(Context context, int textViewResourceID) {
        super(context, textViewResourceID);
    }

    // constructor
    // this constructor will be able to get the list of songs and we can then be able to extract the music content from them and create a new list to display!
    // or we could do it before, whatever helps.
    public MusicAdapter(Context context, int resource, List<SongInfo> songs) {
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
            v = vi.inflate(R.layout.songlist, null); // this is what expands the items in the list
        }

        final SongInfo info = getItem(position); // set to final because it is needed for the options menu. If it causes problems remove the final modifier and think of a different way to get this.

        if (info != null) // make sure that the SongInfo item exists after adapter gets the item
        {
            TextView songNameText = (TextView) v.findViewById(R.id.SongName); // text view for song name
            TextView artistNameText = (TextView) v.findViewById(R.id.ArtistName); // text view for artist name
            TextView songDurationText = (TextView) v.findViewById(R.id.SongDuration); // text view for the song duration, which we will have to have a count down timer for by the way!
            final ImageView optionsMenu = (ImageView) v.findViewById(R.id.songOptionsMenu); // get the options menu image for the song.

            // Whenever the image is clicked, we want to inflate the options menu and perform specific actions on that specific menu.
            optionsMenu.setOnClickListener(new View.OnClickListener() {

                // When clicked, show the options menu which will be to add song to a playlist or to edit tags so far.
                @Override
                public void onClick(View v)
                {
                    PopupMenu popup = new PopupMenu(getContext(), v); // bind the popup menu with the View that was clicked, which is the imageview on the song itself that was clicked.
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.song_popup_menu, popup.getMenu()); // inflate the menu with all of the options on their

                    SongPopupMenuClickListener songMenuClickListener = new SongPopupMenuClickListener(getContext(), optionsMenu, mainUIFragment, info);
                    popup.setOnMenuItemClickListener(songMenuClickListener); // listener for when a user chooses an option on the popup menu.
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

} // end of Music adapter class.
