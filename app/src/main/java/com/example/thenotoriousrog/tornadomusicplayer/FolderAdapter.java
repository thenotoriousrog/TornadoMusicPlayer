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
 * Created by thenotoriousrog on 5/28/17.
 * This class is in charge of properly setting up the folders for when a user selects a folder.
 */

public class FolderAdapter extends ArrayAdapter<String> {

    private MainUIFragment mainUIFragment; // holds a copy of the MainUIFragment that is necessary for the app to get access to items that are a little better.

    // default contructor
    public FolderAdapter(Context context, int textViewResourceID) {
        super(context, textViewResourceID);
    }

    // this constructor will let us take in an arraylist to be able to apply the information that we need to.
    public FolderAdapter(Context context, int resource, List<String> songs) {
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
            v = vi.inflate(R.layout.folderlist, null); // this is what expands the items in the list
        }

        final String info = getItem(position); // gets the string name of the folder.

        if (info != null) // make sure that the string item exists after adapter gets the item
        {
            final ImageView optionsMenu = (ImageView) v.findViewById(R.id.folderOptionsMenu); // get the options menu image for the folder.

            // When the user clicks this button, it will display the popup menu to allow users to add folders to playlists.
            optionsMenu.setOnClickListener(new View.OnClickListener() {

                // opens up a new popup menu and allows users to either add folders to playlists or delete the folder from the main list entirely.
                @Override
                public void onClick(View v)
                {
                    PopupMenu popup = new PopupMenu(getContext(), v); // bind the popup menu with the View that was clicked, which is the imageview on the song itself that was clicked.
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.folder_popup_menu, popup.getMenu()); // inflate the menu with all of the options on their


                    FolderPopupMenuClickListener folderMenuClickListener = new FolderPopupMenuClickListener(getContext(), optionsMenu, mainUIFragment, info);
                    popup.setOnMenuItemClickListener(folderMenuClickListener); // listener for when a user chooses an option on the popup menu.
                    popup.show(); // display our popup menu.
                }
            });

            // we need to parse info that make sure that the folder name does not include the directory.
            String temp = info;
            String[] arr = temp.split("/"); // split the string by /'s

            int length = arr.length; // get the length of the split string.
            String folderName = arr[length -1]; // get the last split which should hold our folder name.

            TextView folder = (TextView) v.findViewById(R.id.FolderName); // the text view for our folder.
            folder.setText(folderName); // set the name of the folder.

            // todo: add icons into the list so that the users know what needs to be done in order to get the list to perform in the way that I want it too.

        }

        return v;
    }
}
