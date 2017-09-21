package com.example.thenotoriousrog.tornadomusicplayer;

import android.app.Dialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.thenotoriousrog.tornadomusicplayer.R;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Created by thenotoriousrog on 8/14/17.
 * This class is used to act as the listener for the Folder for when a user chooses to add a folder to one of the their playlists.
 */

public class LongFolderClickListener implements AdapterView.OnItemLongClickListener {

    private ArrayList<Playlist> Playlists; // holds the list of playlists that the user is going to work with.
    private ArrayList<String> folders; // holds the path of all the folders that the user is working with.
    private ListView playlistList; // the list view that holds the lists of the listviews.
    private View mainView; // the mainUIFragment view to allow our toast messages to be displayed.

    public LongFolderClickListener(ArrayList<Playlist> playlists, ArrayList<String> currFolders, View view)
    {
        Playlists = playlists;
        folders = currFolders;
        mainView = view;
    }

    // This method simply grabs all of the songs in the folder and converts them into the SongInfo list to be added to the current playlist.
    private ArrayList<SongInfo> extractSongsFromFolder(String path)
    {
        ArrayList<String> folderSongs = new ArrayList<>(); // holds the arraylist of the raw songs.
        ArrayList<SongInfo> songs = new ArrayList<>();  // the playlist that we are returning to allow us to add songs to the folder itself.

        File folder = new File(path); // convert the path to a file to allow the system to read the files from it.
        File[] songFiles; // holds all of the approved songs in an array before converting to SongInfo list.

        if(folder.isDirectory()) // ensure that the folder that was grabbed is a directory, otherwise we need to figure something out.
        {
            songFiles = getSongsInDirectory(folder); // grab the songs in the directory.

            // add songs into the arrayList.
            for(int i = 0; i < songFiles.length; i++)
            {
                folderSongs.add(songFiles[i].toString());
            }
        }
        else {
            System.out.println("The path of the file was not a directory. We need to handle this type of situation.");
            // TODO: end the dialog here and tell the user that the file was not added for some odd reason.
            return null;
        }


        songs = convertToSongInfoList(folderSongs); // convert all of the songs here.

        return songs; // change this.
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
    // TODO: Weird for some reason in this class the method that is using this is not slow at all!! I don't understand, adding the songs to the playlist is superfast!
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

    // This method controls what happens when the user has chosen the playlist that they want to add their songs to.
    public void listenForSelectedPlaylist(final Dialog pickPlaylist, final String path)
    {
        playlistList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            ArrayList<SongInfo> folderSongs; // holds the songs in the arraylist.
            Playlist selectedPlaylist; // playlist that the user has chosen to work with.

            // This method will control what happens when a user clicks the playlist that they want to add this folder to.
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Toast.makeText(mainView.getContext(), "Woooooosh adding your songs...", Toast.LENGTH_SHORT).show();
                pickPlaylist.dismiss(); // end the dialog to allow the users to still use the app.

                selectedPlaylist = Playlists.get(position); // gets the playlist that the user has chosen.
                folderSongs = extractSongsFromFolder(path); // extract the songs out of the folder.

                ArrayList<SongInfo> songsInPlaylist = selectedPlaylist.songs(); // gets the songs in the selected playlist.

                // go through all of the songs in the folder and add them to the current playlist. Very important.
                for(int i = 0; i < folderSongs.size(); i++)
                {
                    songsInPlaylist.add(folderSongs.get(i)); // add the songs to the current playlist amazing!
                }

                ArrayList<Playlist> updatedList = Playlists; // make a copy to modify within the actual click listener.
                Playlist updatedPlaylist = new Playlist(selectedPlaylist.name(), songsInPlaylist); // create a new playlist that has all of the old and new songs that were updated.
                //selectedPlaylist.setSongs(songsInPlaylist); // set the new song list into the list of songs. WARNING: I have no idea if this is going to work so I removed it, but I have not tested it yet!!!!

                updatedList.remove(position); // remove this playlist as we are going to replace it with the updated Playlist
                updatedList.add(position, updatedPlaylist); // place the updated playlist into the same position that we have just removed from the arraylist.

                writePlaylistsToMainMemory(view, updatedList); // writes our current playlists back to main memory again after saving the songs.

                Toast.makeText(mainView.getContext(), "I finished adding your songs!", Toast.LENGTH_SHORT).show(); // let the user know that the songs were successively added.
            }
        });
    }


    // This method is in charge when an item is long pressed. This will also send us the path of the folder that we are working with which is very important.
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
    {
        // create a dialogue to show the playlists that the users can choose from.
        Dialog pickPlaylist = new Dialog(view.getContext());
        pickPlaylist.setContentView(R.layout.add_folder_to_playlist); // set the view for the playlist dialogue.

        playlistList = (ListView) pickPlaylist.findViewById(R.id.addFolderPlaylistList); // find our ListView and set it.

        PlayListAdapter playListAdapter = new PlayListAdapter(view.getContext(), R.layout.playlist_list, Playlists); // create a playlist adapter that will let us display the songs properly in the dialoge box.
        playlistList.setAdapter(playListAdapter);
        playListAdapter.notifyDataSetChanged(); // display the playlists and let the adapter know what we have gotten done. Molto importante.

        pickPlaylist.show(); // show the dialog.

        String folderPath = folders.get(position); // gets the folder that the user has chosen.
        listenForSelectedPlaylist(pickPlaylist, folderPath); // have this method take care of listening for certain actions to take place.

        return true; // this will stop the click listener from activating when true is returned from this method.
    }

} // end of LongFolderClickListener
