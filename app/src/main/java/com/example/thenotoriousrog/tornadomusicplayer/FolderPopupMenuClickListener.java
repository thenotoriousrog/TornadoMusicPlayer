package com.example.thenotoriousrog.tornadomusicplayer;

import android.content.Context;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.example.thenotoriousrog.tornadomusicplayer.R;;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Created by thenotoriousrog on 8/17/17.
 * This is a popup click listener for folders. It behaves in the exact same way as the SongPopupMenuClickListener but allows for different options since we are dealing with Folders.
 */

public class FolderPopupMenuClickListener implements PopupMenu.OnMenuItemClickListener {

    private Context context; // the context that is used for pinning popup menu items to views.
    private ImageView folderOptionsMenuView; // holds the three dot image that we can bind other pop messages too.
    private MainUIFragment mainUIFragment; // the main ui fragment needed in order to get the appropriate data that our users are going to need.
    private String folderPath; // holds the path of the folder that the user selected

    // This constructor may have to take more arguments in order to get all of the options to work correctly.
    public FolderPopupMenuClickListener(Context c, ImageView optionsView, MainUIFragment fragment, String selectedFolder)
    {
        context = c;
        folderOptionsMenuView = optionsView;
        mainUIFragment = fragment;
        folderPath = selectedFolder;
    }

    // Writes the playlists to MainMemory
    public void writePlaylistsToMainMemory(View view, ArrayList<Playlist> playlists)
    {
        // WRITE the playlist object to main memory.
        String ser = SerializeObject.objectToString(playlists); // Trying to serialize the entire Playlist Arraylist, Not sure if it is possible or not yet.
        if(ser != null && !ser.equalsIgnoreCase(""))
        {
            String savedPlaylistFileName = "playlists.dat"; // should be something like "Playlist 1.dat"
            SerializeObject.WriteSettings(view.getContext(), ser, savedPlaylistFileName); // write the item to main memory.
        }
        else // Writing the obeject failed. Think of a better way to handle this if at all.
        {
            System.out.println("WE DID NOT WRITE THE LIST CORRECTLY, SOMETHING BAD HAPPENED.");
            SerializeObject.WriteSettings(view.getContext(), "", "playlists.dat"); // we should be getting this list if we are something bad has happened.
        }

    }

    // this method will find songs within the directory passed into it and return with a File[] full of .mp3 files.
    protected File[] getSongsInDirectory(File dir)
    {
        // filteredSongFiles holds all of the mp3 files that are found
        File[] filteredSongFiles = dir.listFiles(new FilenameFilter() {

            // This will search for songs and ensure that whatever is retrieved is an mp3 and nothing else.
            @Override
            public boolean accept(File dir, String songName) {
                return songName.contains(".mp3");
            }
        });

        return filteredSongFiles; // returns the File[] of filtered songs. It can return 0 if no songs were found.
    }

    // converts a list of songs and their paths into their SongInfo and returns a new list of SongInfo!
    // TODO: warning this is what is slowing the app way down when the app first loads for the very first time!! Find a way to get the app to display some type of loading screen.
    public ArrayList<SongInfo> convertToSongInfoList(ArrayList<String> songsList)
    {
        //System.out.println("Am I failing here?");
        ArrayList<SongInfo> songsInfo = new ArrayList<SongInfo>(); // holds the songs and their info into this list.

        // iterate through the arraylist of song paths and convert each one into a SongInfo, and add to list of SongInfos.
        for(int i = 0; i < songsList.size(); i++)
        {
            SongInfo newSong = new SongInfo(); // create a new SongInfo object.
            newSong.getandSetSongInfo(songsList.get(i)); // get the song path and send it to SongInfo to be parsed into it's song details.

            if(newSong.getSongName() == null || newSong.getSongName().equalsIgnoreCase("")) {
                // do nothing this item is null and should not be included into the list.
            }
            else {
                songsInfo.add(newSong); // add the new SongInfo into list of SongInfos.
            }
        }

        // System.out.println("Did I finish grabbing the info?");
        return songsInfo; // return this list back to caller. All song information has been parsed successfully.
    }

    // This method will extract the songs out of the Folder and convert them into an Arraylist<SongInfo> to be added to the playlist.
    public ArrayList<SongInfo> extractSongsFromFolder()
    {
        ArrayList<String> rawSongs = new ArrayList<>(); // Holds all of the song filepaths.
        ArrayList<SongInfo> folderSongs; // holds all of the songs after being converted into a list of SongInfo objects.

        File folderDirectory = new File(folderPath); // convert the foldername into a file that we can use to open up and get the songs in that folder.
        File[] songs; // will hold all the .mp3 files in this folder.

        if(folderDirectory.isDirectory()) // check to make sure that this is a valid directory before we go searching through it.
        {
            // get the songs in this directory and add it to the arrayList of folderSongs
            songs = getSongsInDirectory(folderDirectory); // get all of the songs in the folder.

            // TODO: check to make sure that their songs in the folder before trying to add them all or the app will crash. I know it works in mine, but it may not for other users.


            // add songs into the arrayList.
            for(int i = 0; i < songs.length; i++)
            {
                rawSongs.add(songs[i].toString()); // adds all the song names into the arraylist of folder songs.
            }
        }
        else
        {
            // TODO: create a toast to let the user know that selecting this folder has failed. Also check if it exists, if it doesn't tell the user that the folder no longer exists.
        }

        folderSongs = convertToSongInfoList(rawSongs); // convert all of the raw songs into an arraylist of SongInfo objects.
        return  folderSongs; // send the arraylist of songs back to the calling function.
    }

    // This method sets the listener for when a user chooses the playlist item that they wanted to add the song to, very important!
    public void listenForSelectedPlaylist(PopupMenu playlistToPickMenu)
    {
        System.out.println("Setting click listeners now");
        playlistToPickMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {


            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                System.out.println("A playlist item was clicked");
                String playlistName = item.getTitle().toString(); // get the name of the playlist that was chosen.

                ArrayList<SongInfo> folderSongs = null; // holds all of the songs of the playlist that we want to add to.
                ArrayList<Playlist> updatedList = mainUIFragment.getPlaylists(); // the list playlist that we are going to update once the user finds the songs that they are looking for.
                int playlistPos = 0; // holds the position of the playlist to ensure that when updated, the playlist is modified in the correct order and saved in the correct order.

                //System.out.println("Playlists that exist are: " + mainUIFragment.getPlaylists().size());

                ArrayList<SongInfo> playlistSongs = new ArrayList<SongInfo>(); // creates an empty arraylist for now but should be overriden by the songs that exist in the playlist already.
                // loop through the playlists and find the playlist with the name of the playlist that we are trying to work on.
                for(int i = 0; i < mainUIFragment.getPlaylists().size(); i++)
                {
                   // System.out.println("looping...");
                    if(mainUIFragment.getPlaylists().get(i).name().equals(playlistName)) // we have found the playlist that we want to work with.
                    {
                        System.out.println("the name of the playlist that the user chose is: " + playlistName);

                        // TODO: Need to extract the songs out of the folder, convert them to SongInfo's add each song into the playlist, and save the playlist.

                        playlistPos = i; // set the playlist position. This is very important otherwise the the first playlist gets erased which is not good.
                        playlistSongs = mainUIFragment.getPlaylists().get(i).songs(); // get the songs of the selected playlist
                        System.out.println("The number of songs in that playlist is: " + playlistSongs.size());
                        folderSongs = extractSongsFromFolder(); // get the songs from the folder and set the arraylist.

                        // loop through all of the folderSongs and add them to the updated list.
                        for(int j = 0; j < folderSongs.size(); j++)
                        {
                            playlistSongs.add(folderSongs.get(j)); // add song to the playlistSongs.
                        }

                    }
                }

                // TODO: have a test to make sure that the folderSongs is not null and if it is null we need to return and quit this action and tell the user why for whatever reason.

                Playlist updatedPlaylist = new Playlist(playlistName, playlistSongs); // update the playlist.

                updatedList.remove(playlistPos); // remove the previous version of the playlist in this position.
                updatedList.add(playlistPos, updatedPlaylist); // readd the playlist in the same position but with the song added this time.

                writePlaylistsToMainMemory(mainUIFragment.getView(), updatedList); // write the updated playlists to main memory again!

                return true; // tell the user now that everything is correct! todo: display a snackbar message to alert the user that their song has been added successfully to their playlist.
            }
        });
    }

    // This method controls what happens when a user selects an option from the popup menu.
    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
        if(item.getTitle().toString().equalsIgnoreCase("Add to playlist..."))
        {
            // generate another popup menu on the options view that lists the playlists.
            PopupMenu playlistToPickMenu = new PopupMenu(context, folderOptionsMenuView); // bind the new options menu to the same menu view.

            // Loop through every playlist and add each playlist name to the new popup playlist item.
            for(int i = 0; i < mainUIFragment.getPlaylists().size(); i++)
            {
                playlistToPickMenu.getMenu().add(mainUIFragment.getPlaylists().get(i).name()); // add the name of the playlist to the menu
            }

            // replace the popup with the popup of the list menu itself.
            MenuInflater inflater = playlistToPickMenu.getMenuInflater();
            inflater.inflate(R.menu.playliststoaddfrom_popup_menu, playlistToPickMenu.getMenu()); // get the menu

            listenForSelectedPlaylist(playlistToPickMenu); // listens for a user to add songs to their playlist and then does that.
            playlistToPickMenu.show(); // show this menu now very important.
        }

        return true;
    }
}
