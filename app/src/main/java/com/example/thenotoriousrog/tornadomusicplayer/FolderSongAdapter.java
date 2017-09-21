package com.example.thenotoriousrog.tornadomusicplayer;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.thenotoriousrog.tornadomusicplayer.R;

import java.util.List;

/**
 * Created by thenotoriousrog on 8/3/17.
 * This class is used to show the songs in the folder that was selected by the user. It is used for the pager in showing these items.
 * This class is a mix between the DragSongAdapter and the FolderSongAdapter.
 */

public class FolderSongAdapter extends ArrayAdapter<SongInfo> {

    // default contructor
    public FolderSongAdapter(Context context, int textViewResourceID) {
        super(context, textViewResourceID);
    }

    // this constructor will let us take in an arraylist to be able to apply the information that we need to.
    public FolderSongAdapter(Context context, int resource, List<SongInfo> songs) {
        super(context, resource, songs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView; // view that we are working with.

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(com.example.thenotoriousrog.tornadomusicplayer.R.layout.playlist_songlist, null); // inflate the song list with the image of the item that we want to see.
        }

        SongInfo info = getItem(position);

        if (info != null) // make sure that the SongInfo item exists after adapter gets the item
        {
            TextView songNameText = (TextView) v.findViewById(com.example.thenotoriousrog.tornadomusicplayer.R.id.SongName); // text view for song name
            TextView artistNameText = (TextView) v.findViewById(com.example.thenotoriousrog.tornadomusicplayer.R.id.ArtistName); // text view for artist name
            TextView songDurationText = (TextView) v.findViewById(R.id.SongDuration); // text view for the song duration, which we will have to have a count down timer for by the way!

            if (songNameText != null) {
                String text = info.getSongName();
                String str = "";
                if (text != null && text.length() >= 27) {
                    str = text.substring(0, 22);
                    songNameText.setText(str + "...");
                    songNameText.setEllipsize(TextUtils.TruncateAt.END);
                } else // text is not too long, we can display the whole thing.
                {
                    songNameText.setText(info.getSongName());
                }
            }

            if (info != null) {
                artistNameText.setText(info.getArtistName());
            }


            if (songDurationText != null) {
                songDurationText.setText(info.getSongTime()); // this should display the actual time of the song.
                //songDurationText.setText(info.getSongDuration()); removed because we are going to display the actual time of the song now.
                songDurationText.setVisibility(View.GONE); // make this invisible, users do not need to see this nor does it actually matter.
            }

        }

        return v;
    }
}
