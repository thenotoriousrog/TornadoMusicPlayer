package com.example.thenotoriousrog.tornadomusicplayer;

import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.thenotoriousrog.myapplication.R;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

/**
 * Created by thenotoriousrog on 7/1/17.
 * This class is the most important. This class is in charge of creating the main UI for the music player. It will allow the app to load as well as display a loading screen while the app does work.
 * This class removes most of the work away from MainActivity so that the only thing that the MainActivity has to do is simply show this fragment using a Fragment Transaction.
 */

public class MainUIFragment extends Fragment {

    private static ArrayList<String> songs;  // holds all the raw song paths.
    private ArrayList<String> folders; // holds all the folders.
    private ArrayList<SongInfo> songInfoList = null; // this list holds our songInfo list holds all of the attributes of a song file.
    private SlidingUpPanelLayout slidingLayout; // controls out sliding up layout
    private MediaPlayer mediaPlayer = new MediaPlayer(); // this class will play the songs we want, but we can only have ONE of this class otherwise multiple songs will get played.
    private String playListName = ""; // holds the name of a playlist, but this can be changed multiple times.
    private PlayListAdapter playListAdapter = null; // creates a public playlist adapter so that it is only created once, preventing multiple copies from being created.
    private PlaylistMusicAdapter playlistMusicAdapter = null; // this will allow us to set the playlistMusicAdapter and allow it to be updated once the lists have been saved.
    private ArrayList<Playlist> PlayLists = new ArrayList<>();
    private ArrayList<String> folderSongs = null;//new ArrayList<String>(); // this will hold the songs within the folder itself.
    private ArrayList<SongInfo> alphabetized = null; // this arraylist holds the songs that are organized and are the ones used to ensure that the song the users choose plays correctly.
    private ArrayList<SongInfo> folderSongsCopy = null; // this is a copy of the arraylist songs from the folder that the user chooses to listen to.
    private ArrayList<String> playlistNames = null; // holds the name of all the playlists.
    private int currentTabPosition = 0; // tells us the tab that the user is currently on.
    private MainUIPagerAdapter mainUIPagerAdapter; // this holds the UI pager that we need to be sure that all of these items are updated correctly.
    private ViewPager viewPager; // the main view pager that holds all of the items in the music player.
    FloatingActionButton addPlayListButton; // the playlist button that controls all of the actions by the users.

    // These variables are used when the user selects a song from the folder itself to help prevent the app from greatly slowing down.
    private FolderSelectedSongPlayer folderSelectedSongPlayer; // this SelectedSongPlayer is only used for when users select a song from within the folder itself. Overwriting the older folder that was used.
    private LongSelectedSongListener folderLongSelectedSongListener; // this LongSelectedSongListener is only used when a user selects a song from within the folder itself.

    private PanelSlidingListener panelSlidingListener;

    public final int OPEN_FOLDER_INTENT = 100; // this is the result code that we want to pass back to the intent that way we know to look for specific intent codes.
    public final int CREATE_PLAYLIST_INTENT = 200; // result code we want to send to the open playlist intent.
    public final int MODIFY_PLAYLIST_INTENT = 300; // the result we want to receive after modifying one or all playlists.

    private boolean sortBySongName = true; // tells the app to sort songs by the song name.
    private boolean sortByArtistName = false; // tells us if the user wants the songs to be sorted by Artist name instead.
    private boolean sortByDuration = false; // tells the app to sort songs by the duration of the songs.

    private ListView songsList; // holds the view for songs in the directory passed by the user.
    private ListView foldersList; // holds the view for songs in the directory passed by the user.
    private ListView folderSongsList; // holds all the songs inside a folder.
    private ListView playListList; // holds all of the playlists that a user creates.
    private SeekBar seekBar; // the seekbar that is used to ensure that the songs that are being played are shown and able to be controlled via a seek option.

    // Tells us the the way the user wants to sort their song lists.
    public String getSortByFilter()
    {
        if(sortBySongName == true)
        {
            return "SongName"; // tells the system to sort by songname.
        }
        else if(sortByArtistName == true)
        {
            return "ArtistName"; // tells the system to sort by name of artists.
        }
        else if(sortByDuration == true)
        {
            return "Duration"; // tells the system to sort by duration of songs.
        }

        return null; // this should never happen but if it does then we want to send this with null and handle the error that occurs when null is received.
    }

    // this method will sort the songs list passed in based what the sort filter is set as.
    public void sort(ArrayList<SongInfo> songsToSort)
    {
        String filter = getSortByFilter(); // gets the sort by filter that was set by the users.

        if(filter.equalsIgnoreCase("SongName"))
        {
            sortBySongName(songsToSort); // sort the songs by song name.
        }
        else if(filter.equalsIgnoreCase("ArtistName"))
        {
            sortByArtistName(songsToSort); // sort the songs by artist name.
        }
        else if(filter.equalsIgnoreCase("Duration"))
        {
            sortByDuration(songsToSort); // sort the songs by duration.
        }
    }

    // sorts the songs passed in by songName
    public void sortBySongName(ArrayList<SongInfo> songs)
    {
        // sorts the orderedList by songname
        Collections.sort(songs, new Comparator<SongInfo>() {

            // sorts the list.
            @Override
            public int compare(SongInfo o1, SongInfo o2) {
                return o1.getSongName().compareTo(o2.getSongName());
            }
        });
    }

    // sorts the songs passed in by artist name.
    public void sortByArtistName(ArrayList<SongInfo> songs)
    {
        // sorts the orderedList by songname
        Collections.sort(songs, new Comparator<SongInfo>() {

            // sorts the list.
            @Override
            public int compare(SongInfo o1, SongInfo o2) {
                return o1.getArtistName().compareTo(o2.getArtistName());
            }
        });
    }

    // sorts the songs passed in by duration.
    public void sortByDuration(ArrayList<SongInfo> songs)
    {
        // sorts the orderedList by songname
        Collections.sort(songs, new Comparator<SongInfo>() {

            // sorts the songs by duration.
            @Override
            public int compare(SongInfo o1, SongInfo o2) {
                return o1.getSongDuration().compareTo(o2.getSongDuration());
            }
        });
    }

    // Sorts folders according to their names.
    public void sortFolders(ArrayList<String> folders)
    {
        // sorts the orderedList by songname
        Collections.sort(folders, new Comparator<String>() {

            // sorts the folders
            @Override
            public int compare(String f1, String f2) {
                return f1.compareTo(f2);
            }
        });
    }

    // sorts playlists according to their names.
    public void sortPlaylistsName(ArrayList<Playlist> playlists)
    {
        // sorts the playlists.
        Collections.sort(PlayLists, new Comparator<Playlist>() {

            // sorts the list.
            @Override
            public int compare(Playlist p1, Playlist p2) {
                return p1.name().compareTo(p2.name());
            }
        });
    }


    // sets the correct sort filters and makes sure that one of them is at least true. Otherwise we are going to have bigger problems. We have to make sure that one of them is at least true.
    public void setSortFilters(boolean songName, boolean artistName, boolean duration)
    {
        if(songName == false && artistName == false && duration == false)
        {
            System.out.println("All three filters are false here this should not happen! Figure out where the system has all 3 of these filters as false and why!");
        }

        sortBySongName = songName;
        sortByArtistName = artistName;
        sortByDuration = duration;
    }

    public void setPlaylists(ArrayList<Playlist> playlists)
    {
        PlayLists = playlists;
    }

    public ArrayList<Playlist> getPlaylists()
    {
        return PlayLists;
    }

    // this method literally resets the songsListAdapter and hopefully will show the songs being updated in the viewpager as well.
    public void updateSongsListAdapter()
    {
        MusicAdapter musicAdapter = new MusicAdapter(getActivity(), R.layout.songlist, songInfoList); // used to just be songInfoList
        musicAdapter.setMainUIFragment(MainUIFragment.this); // set the playlists in the class to be used by the popup menu.
        songsList.setAdapter(musicAdapter); // set out current view.
        musicAdapter.notifyDataSetChanged(); // let the music adapter know that the list is updated and ready to go.

        updateViewPager(viewPager.getCurrentItem());
    }

    // sets the playlistMusicAdapter to be modified later after the app has information that is truly saved. Very important.
    public void setPlaylistMusicAdapter(PlaylistMusicAdapter pma)
    {
        pma.setMainUIFragment(MainUIFragment.this);
        playlistMusicAdapter = pma;
    }

    // simply tells the playlistMusicAdapter to be updated whenever the data has been changed.
    public void refreshPlaylistMusicAdapter()
    {
        playlistMusicAdapter.notifyDataSetChanged();
    }

    // This method will extract the sort filters out of main memory and restore them in the class to ensure that the lists are being displayed properly.
    public void extractAndSetSortFilters()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext()); // get the context that we need in order to sort these lists.
        boolean songNameSort = preferences.getBoolean("sortBySongName", true); // If someone does not get grabbed by main memory, we are defaulting this to be the sort that we want to use which is by songName
        boolean artistNameSort = preferences.getBoolean("sortByArtistName", false);
        boolean durationSort = preferences.getBoolean("sortByDuration", false);

        setSortFilters(songNameSort, artistNameSort, durationSort); // set the sort filters chosen by the users.
    }


    // This is the first method that is called when the app is ready to start loading the music UI.
    // This method will take in items from the class and begin loading the songs and preparing the adapters and listeners to be set.
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // todo: fix this app so that when a user rotates the screen it will just show the music app and not the loading screen.

        System.out.println("Is saved instance state null? " + savedInstanceState);

        System.out.println("WE ARE IN ONCREATE IN THE MAINUIFRAGMENT! IT IS DOING THE WORK WE NEED IT TO!");

        // For testing if everything is null or not...
        System.out.println("Is songs in MainUIFragment null: " + songs);
        System.out.println("Is folders in MainUIFragment null: " + folders);
        System.out.println("Is the SongInfoList null: " + songInfoList);

        extractAndSetSortFilters(); // have the app extract and set the sort filters needed for the app to function properly.

        Bundle args = getArguments(); // todo: make sure in MainActivity we actually send the correct arguments otherwise this class will crash.
        songs = args.getStringArrayList("songs"); // get the raw song paths from the MainActivity.

        // TODO: sort folders by their name alphabetically!
        folders = args.getStringArrayList("folders"); // get the raw folder paths from the MainActivity.
        sortFolders(folders); // sorts the folders by their names.

        songInfoList = args.getParcelableArrayList("songInfoList"); // get the songInfoList from the mainActivity to begin displaying the songs in the correct order.
        sort(songInfoList); // this will sort the songInfoList based on the user's filter preference.

        String musicFolderPath = args.getString("musicFolderPath"); // get the music folder path that the user has chosen.

        // TODO: sort playlists by their name alphabetically.
        playlistNames = args.getStringArrayList("playlistNames"); // get the arraylist of all playlists.

        // simply for testing whether or not a playlist was parcelized and sent off correctly or not yet.
        System.out.println("LOOK HERE FOR TESTING TO SEE IF THIS WORKED OUT CORRECTLY!!");
        System.out.println("Did we end up receiving our Playlist arraylist?: " + args.getParcelableArrayList("playlists"));


        ArrayList<Playlist> playlists = args.getParcelableArrayList("playlists"); // retrieve the playlists from MusicPlayer class.
        System.out.println("Is the playlists we retrieved from MusicPlayer null?" + playlists);

        if(playlists != null) // if the playlists exist, then set the new Playlists to be used for the entire class.
        {
            System.out.println("The Playlist Arraylist has been set!!");

            sortPlaylistsName(playlists); // sort the playlists that we have received making the list more orderly.
            setPlaylists(playlists); // set the playlists that was retrieved from MusicPlayer class.
        }

        File musicDirectory = new File(musicFolderPath); // convert the music folder into a path.

        // todo: somehow implement a loading screen until this the app finishes loading up all of the song information. Important.
        evaluateAndCreateLists(musicDirectory); // check songs and folders and make sure it is correct and then create the appropriate lists which will be used in TabLayout later!

    }

    // starts the music control listeners after the sliding layout has been created.
    public void startMusicControlListeners(PanelSlidingListener panelSlidingListener)
    {
        System.out.println("Creating Music Control listeners.....");

        // Image buttons.
        ImageView pauseButton;
        ImageView playButton;
        ImageView skipButton;
        ImageView prevButton;
        ImageView shuffleOnButton;
        ImageView shuffleOffButton;
        ImageView repeatOnButton;
        ImageView repeatOffButton;

        // Action codes.
        String pauseCode = "pause";
        String playCode = "play";
        String skipCode = "skip";
        String replayCode = "replay"; // the back button was pressed.
        String prevCode = "prev"; // a long click was was pressed on the back button.
        String shuffleOn = "shuffleOn";
        String shuffleOff = "shuffleOff";
        String repeatOn = "repeatOn";
        String repeatOff = "repeatOff";

        // todo: set these images buttons when we create the sliding layout in MainUIFragment
        // Image buttons.
        pauseButton = (ImageView) slidingLayout.findViewById(R.id.pauseButton);
        playButton = (ImageView) slidingLayout.findViewById(R.id.playButton);
        skipButton = (ImageView) slidingLayout.findViewById(R.id.skipButton);
        prevButton = (ImageView) slidingLayout.findViewById(R.id.prevButton); // normal press == replay, longpress == previous.
        shuffleOnButton = (ImageView) slidingLayout.findViewById(R.id.shuffleOn);
        shuffleOffButton = (ImageView) slidingLayout.findViewById(R.id.shuffleOff);
        repeatOnButton = (ImageView) slidingLayout.findViewById(R.id.repeatOn);
        repeatOffButton = (ImageView) slidingLayout.findViewById(R.id.repeatOff);


        // todo: may be able to set these OnClickListeners using the PanelSlidingListener class since the only need to have the SelectedSongPlayer class is to tell us the info for the song during actions.
        // todo: (cont.) investigate the purpose of sending in the SelectedSongPlayer as we may be able to just use PanelSlidingListener instead! This is very important!
        // click listeners for each button.
        pauseButton.setOnClickListener(new MusicControlListener(pauseButton, pauseCode, mediaPlayer, slidingLayout, panelSlidingListener));
        playButton.setOnClickListener(new MusicControlListener(playButton, playCode, mediaPlayer, slidingLayout, panelSlidingListener));
        skipButton.setOnClickListener(new MusicControlListener(skipButton, skipCode, mediaPlayer, slidingLayout, panelSlidingListener));
        prevButton.setOnClickListener(new MusicControlListener(prevButton, replayCode, mediaPlayer, slidingLayout, panelSlidingListener));
        prevButton.setOnLongClickListener(new MusicControlListener(prevButton, prevCode, mediaPlayer, slidingLayout, panelSlidingListener));
        shuffleOnButton.setOnClickListener(new MusicControlListener(shuffleOnButton, shuffleOn, mediaPlayer, slidingLayout, panelSlidingListener));
        shuffleOffButton.setOnClickListener(new MusicControlListener(shuffleOffButton, shuffleOff, mediaPlayer, slidingLayout, panelSlidingListener));
        repeatOnButton.setOnClickListener(new MusicControlListener(repeatOnButton, repeatOn, mediaPlayer, slidingLayout, panelSlidingListener));
        repeatOffButton.setOnClickListener(new MusicControlListener(repeatOffButton, repeatOff, mediaPlayer, slidingLayout, panelSlidingListener));
    }

    /* removed because it is no longer needed, but not deleted just in case.
    // Takes a list of SongInfo and alphabetizes them according to their song name
    public ArrayList<SongInfo> alphabetizeSongs(ArrayList<SongInfo> unorderedList)
    {
        ArrayList<SongInfo> orderedList = unorderedList; // holds the newly organized song ArrayList.

        // sorts the orderedList by songname
        Collections.sort(orderedList, new Comparator<SongInfo>() {

            // controls the behavior for how the list is organized which is based upon song name.
            @Override
            public int compare(SongInfo o1, SongInfo o2) {
                return o1.getSongName().compareTo(o2.getSongName());
            }
        });

        return orderedList; // return the alphabetized songinfo list.
    }
    */

    // This method is in charge of creating as well as setting all the adapters and listeners of the main U.I. itself.
    // This method is what will make the U.I. become displayed. Essentially when MainActivity creates this fragment, everything will be already loaded and ready to be displayed. Awesome!
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View mainView = inflater.inflate(R.layout.activity_main, container, false); // inflate the main layout.

        slidingLayout = (SlidingUpPanelLayout) mainView.findViewById(R.id.sliding_layout); // find and set the sliding layout

        RelativeLayout draggedView = (RelativeLayout) slidingLayout.findViewById(R.id.draggedView); // get the dragged view of this layout.
        draggedView.setVisibility(View.GONE); // make this layout gone when the app loads until a song is played. We do not want this to block the view of the app.

        TextView nowPlayingText = (TextView) slidingLayout.findViewById(R.id.nowPlaying); // the view that actually shows the Now Playing... message
        TextView songText = (TextView) slidingLayout.findViewById(R.id.songText); // the view in the slide that actually shows the song title.
        TextView artistText = (TextView) slidingLayout.findViewById(R.id.artistText); // the view in the slide that shows the artist of the song.
        ImageView albumArt = (ImageView) slidingLayout.findViewById(R.id.AlbumArt); // album art on the sliding layout.

        TextView upNext = (TextView) slidingLayout.findViewById(R.id.upNextText); // upnext text on the sliding layout.
        final TextView timerText = (TextView) slidingLayout.findViewById(R.id.songTimeText); // timer on the sliding layout.

        ImageView filterOptions = (ImageView) slidingLayout.findViewById(R.id.filter_list); // gets the filter list image. We will need to create a popup menu for this list when this happens.
        filterOptions.setOnClickListener(new View.OnClickListener() {

            // When clicked open up the options menu and make sure that the menu is in fact working correctly.
            @Override
            public void onClick(View v)
            {
                PopupMenu filterOptions = new PopupMenu(getContext(), v); // grab the main view that we want to be modifying here.
                MenuInflater inflater = filterOptions.getMenuInflater();
                inflater.inflate(R.menu.filter_list_options, filterOptions.getMenu()); // inflate the menu we are using for the filter_list options.

                // TODO: create a menuClickListener for the filter_options area and make sure that the entire list is able to be searched. This is very important!!!!
                FilterListPopupMenuClickListener filterPopupMenuClkListener = new FilterListPopupMenuClickListener(getContext(), MainUIFragment.this, songInfoList); // the click listener that is needed for this here.
                filterOptions.setOnMenuItemClickListener(filterPopupMenuClkListener); // set the click listener the filter options popup.

                filterOptions.show();
            }
        });

        ImageView options = (ImageView) slidingLayout.findViewById(R.id.extraOptionsMenu); // grab the extra options menu on the actual U.I.
        options.setOnClickListener(new View.OnClickListener() {

            // When clicked open up the popup menu and listen for actions after the menu is opened.
            @Override
            public void onClick(View v)
            {
                PopupMenu extraOptions = new PopupMenu(getContext(), v); // grab the main view that we want to be modifying here.
                MenuInflater inflater = extraOptions.getMenuInflater();
                inflater.inflate(R.menu.main_ui_options, extraOptions.getMenu()); // inflate the extra options menu, this is very important.

                // TODO: create a menuClick listener for the extra options area and create a fragment that will show the fragment that we are trying to write over.

                extraOptions.show();
            }
        });

        seekBar = (SeekBar) slidingLayout.findViewById(R.id.seekBar); // grab the seek bar that is being used by the sliding layout.

        panelSlidingListener = new PanelSlidingListener(nowPlayingText, songText, artistText, albumArt, upNext, timerText, getActivity(), mediaPlayer, seekBar, slidingLayout); // set the panel sliding listener here.
        slidingLayout.addPanelSlideListener(panelSlidingListener); // set the listener that we are using for the sliding layout. There is only one listener, another will not be created.

            // WARNING: may need to call this method before the slidingLayout.appPanelSlideListener if there are any issues involving any music controls.
        startMusicControlListeners(panelSlidingListener); // start the Music control listeners which will allow the MusicControlListner to behave accurately.

        // create the listener for when a song is selected.
        //songsList = (ListView) mainView.findViewById(R.id.songsListView);

        // create the new songList and change of the styling of list items.
        songsList = new ListView(getContext()); // recreate the new list item
        songsList.setDivider(null); // remove the divider completely.
        songsList.setDividerHeight(80); // 80 seems to be the perfect height for the divider it is crucial that we are getting it to perform a little better.
        songsList.setNestedScrollingEnabled(true); // set the list of the items in order to help make sure that the items are working correctly.

        // todo: if the app is breaking in the normal songs field it is because I have removed this line below and sent in a list that can be changed by the entire list. Change it back to fix it.
        //ArrayList<SongInfo> copy = new ArrayList<>(songInfoList); // a copy to allow it to be manipulated by the SelectedSongPlayer without changing the list of the songs themselves.
        MusicPlayer musicPlayer = (MusicPlayer) getActivity(); // somehow casting this to the MainActivity works! The selected song player behaves properly for whatever reason.

        songsList.setOnItemClickListener(new SelectedSongPlayer(mediaPlayer, songInfoList, songs, slidingLayout, musicPlayer, panelSlidingListener, MainUIFragment.this)); // start a SelectedSongPlayer
        //songsList.setOnItemLongClickListener(new LongSelectedSongListener(copy)); // start a LongSelectedSongListener. Pass in songs list that can be chosen by user.

        // TODO: Sort the folders list alphabetically because it just looks better.

        // create the listener for when a folder is selected.
        //foldersList = (ListView) mainView.findVsuper.onBackPressed();iewById(R.id.foldersListView);
        foldersList = new ListView(getContext()); // set the new listview here.
        foldersList.setDivider(null); // remove the divider.
        foldersList.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getContext(), R.anim.folderlistandplaylistlistanim)); // set the layout animation for when we load folders again.
        foldersList.setNestedScrollingEnabled(true);
        //foldersList.setDividerHeight(); // we can change the height of the divider here if we choose to do so.

        // when the user chooses to select a folder, have that folder open up to show all the songs in the folders themselves.
        foldersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            // when a folder is selected, create a new intent and show the songs in the folder.
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent folderSelectedIntent = new Intent(getActivity(), OpenFolderandSongs.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("FoldersList", folders);
                bundle.putInt("SelectedFolderPosition", position);
                System.out.println("item selected posoition: " + parent.getSelectedItemPosition());
                System.out.println("position of the folder that was selected is: " + position);
                folderSelectedIntent.putExtra("FolderListBundle", bundle);
                startActivityForResult(folderSelectedIntent, OPEN_FOLDER_INTENT); // start the activity.
            }
        });

        //LongFolderClickListener lfcl = new LongFolderClickListener(PlayLists, folders, mainView); // pass in the Playlists and folders to be used by the LongFolderClickListener.
        //foldersList.setOnItemLongClickListener(lfcl); // sets the long selected listener to allow adding folders to playlists.

       // folderSongsList = (ListView) mainView.findViewById(R.id.openFolderSongsView); // should NOT need a listener for this.
        folderSongsList = new ListView(getContext()); // the new way we are doing the listview.
        folderSongsList.setNestedScrollingEnabled(true);
        folderSongsList.setDivider(null); // remove the divider.
        folderSongsList.setDividerHeight(80); // height of the divider.
        folderSongsList.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getContext(), R.anim.folderlistandplaylistlistanim));
        //folderSongsList.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in)); // set the animation for when the user wants to select something in the list.

        //playListList = (ListView) mainView.findViewById(R.id.playlistList); // get the list of playlists.
        playListList = new ListView(getContext());
        playListList.setDivider(null); // remove the divider
        playListList.setNestedScrollingEnabled(true);
        playListList.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getContext(), R.anim.folderlistandplaylistlistanim)); // set the animation for when we see the items again on the list. This is very important.

        //playListList.setDividerHeight(80); // removed because they are already spaced correctly.

        // Clicking a playlist will show the songs in the playlist and behaves similarly to when the user clicks on a folder.
        playListList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            // When a playlist is selected simply update the adapter and display the songs like the Folder does except we do not have to grab all of the songs anymore.
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //ListView playlistSongsList = (ListView) mainView.findViewById(R.id.openPlaylistSongsView); // grab the view of the playlist songs right here.

                // create the playlistSongList and set the style of the listitems themselves.
                ListView playlistSongsList = new ListView(getContext()); // create a new ListView here.
                playlistSongsList.setDivider(null); // remove the divider height.
                playlistSongsList.setDividerHeight(80); // set the height of the divider between items.
                playlistSongsList.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getContext(), R.anim.folderlistandplaylistlistanim)); // set the animation for when we open a playlist.

                //MusicAdapter musicAdapter = new MusicAdapter(getActivity(), R.layout.songlist, PlayLists.get(position).getSongs()); // set the music adapter old version
               // MusicAdapter musicAdapter = new MusicAdapter(getActivity(), R.layout.songlist, PlayLists.get(position).songs()); // set the music adapter
                PlaylistMusicAdapter playlistMusicAdapter = new PlaylistMusicAdapter(getActivity(), R.layout.songlist, PlayLists.get(position).songs());
                playlistMusicAdapter.setMainUIFragment(MainUIFragment.this);
                playlistMusicAdapter.setSelectedPlaylist(PlayLists.get(position)); // set the playlist that was selected by the user.


                // TODO: In here call a method that will sort the playlists by style that the users want to sort the playlists. This is very important.

                playlistSongsList.setAdapter(playlistMusicAdapter);
                playlistMusicAdapter.notifyDataSetChanged(); // let the music player know that the list has indeed changed.
                setPlaylistMusicAdapter(playlistMusicAdapter); // set the music adapter that we are working with here.
                //ArrayList<SongInfo> alphabetical = alphabetizeSongs(PlayLists.get(position).songs()); // send the songs to be alphabetized.
                sort(PlayLists.get(position).songs()); // sort the songs based on the list that is being grabbed right now.

                // sending int the playlist that was selected will get us our list that we need to see so desperately.
                //playlistSongsList.setOnItemClickListener(new PlaylistSelectedSongPlayer(mediaPlayer, PlayLists.get(position).getSongs(), slidingLayout, panelSlidingListener)); old version
                playlistSongsList.setOnItemClickListener(new PlaylistSelectedSongPlayer(mediaPlayer, PlayLists.get(position).songs(), slidingLayout, panelSlidingListener)); // TODO: removed using alphabetical so change if this is needed.
                openPlaylist(playlistSongsList); // tell the mainUIFragment to open the playlist.
            }
        });


        // TODO: This is where we are sending in the items that we need for the MusicAdapter to perform a little better. I may have to copy these updates acroll other uses of this adapter!!
        // set the music adapter for the song list view
        MusicAdapter musicAdapter = new MusicAdapter(getActivity(), R.layout.songlist, songInfoList); // used to just be songInfoList
        musicAdapter.setMainUIFragment(MainUIFragment.this); // set the playlists in the class to be used by the popup menu.

        songsList.setAdapter(musicAdapter); // set out current view.
        musicAdapter.notifyDataSetChanged(); // let the music adapter know that the list is updated and ready to go.

        // set the folder adapter for the list view.
        FolderAdapter folderAdapter = new FolderAdapter(getActivity(), R.layout.folderlist, folders);
        folderAdapter.setMainUIFragment(MainUIFragment.this); // set the MainUIFragment that we need for the application.
        foldersList.setAdapter(folderAdapter); // set the new adapter.
        folderAdapter.notifyDataSetChanged();

        addPlayListButton = (FloatingActionButton) mainView.findViewById(R.id.addPlaylistButton); // grab in our main activity.

        // click listener for add playlist button.
        AddPlaylistButtonClickListener addPlaylistButtonClickListener = new AddPlaylistButtonClickListener(MainUIFragment.this, PlayLists, songs, songInfoList, folders);
        addPlayListButton.setOnClickListener(addPlaylistButtonClickListener); // set the click listener.

        /* New click listener works!! This entire section of code may be removed once you feel comfortable with the performance of the class.
        // IMPORTANT set the behavior for when a user wants to add a new playlist.
        addPlayListButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ArrayList<String> songsInPlaylist = new ArrayList<String>(); // create a new ArrayList of songs in the playlist.

                // TODO: create the new playlist_plusbutton_dialog here and ask users for their response.
                // TODO: BUT FIRST CREATE A NEW CLASS FOR THIS ENTIRE ACTION FOR THE ADDPLAYLISTBUTTON BECAUSE THIS METHOD IS JUST WAAAYYYY TOO LONG!
                final Dialog playListCreationDialog = new Dialog(v.getContext());
                playListCreationDialog.setContentView(R.layout.new_playlist_dialog); // set the dialog for a user to create their playlist.
                playListCreationDialog.setTitle("Creating a playlist...");
                playListCreationDialog.show(); // show the dialog box!

                final EditText enterPlaylistNameField = (EditText) playListCreationDialog.findViewById(R.id.playlistEnterField); // set the field that the user will enter their playlist name.
                Button playListConfirmButton = (Button) playListCreationDialog.findViewById(R.id.playlistConfirmButton); // Confirm button for when a user Finishes entering the name of their playlist.

                // set behavior for when a user hits the confirm button.

                playListConfirmButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        String str = enterPlaylistNameField.getText().toString(); // grab what the user enters for their playlist.

                        if (str.length() == 0) {
                            // TODO: decide if it is important to force a user to enter a name or let the app create one on its own. A small detail but an important one nonetheless.
                            str = "New Playlist"; // name the generic playlist for the user.
                        }

                        setPlayListName(str); // set the playlist name chosen by the user.
                        playListCreationDialog.dismiss(); // close the dialog after the user enters a name of the arraylist.

                        // todo: create another popup here asking if users want to add songs to the playlist. Once that is selected go from there.
                        // if they say yes, the user will drag and drop songs into the playlist.
                        // if they say no, an empty playlist will be created for them, it is crucial to get this one correct.


                        final Dialog addSongsDialog = new Dialog((v.getContext()));
                        addSongsDialog.setContentView(R.layout.addsongsdialog); // set the view for this dialog.
                        addSongsDialog.setTitle("Do you want to add songs to your playlist?");
                        Button yesButton = (Button) addSongsDialog.findViewById(R.id.yesButton); // grab yes button.
                        Button noButton = (Button) addSongsDialog.findViewById(R.id.noButton); // grab no button.
                        addSongsDialog.show();

                        // set behavior for when the user presses yes.
                        yesButton.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                System.out.println("Yes was selected, we need to add songs to this playlist with the view that we want to have.");
                                addSongsDialog.dismiss(); // close the dialog after user says yes.

                                addEmptyPlaylist(); // todo: REMOVE THIS, but for now we have it here to allow users to be able to be able to see the playlist that they just created.
                                updatePlaylistAdapter(); // todo: this should also be done after the user adds songs to the playlist as well.

                                // TODO: We will want to create an Activity that will allow the creation of the playlist and will also allow us to send back the playlist once it is has been successfully completed.

                                // We will want to create a seperate activity that will take care of this for us.

                                Intent addPlaylistActivity = new Intent(getContext(), ModifyAllPlaylistsFragmentActivity.class); // create the activity to allow us to have the activity do what it is supposed to.
                                Bundle playlistIntentBundle = new Bundle();
                                playlistIntentBundle.putStringArrayList("songs", songs); // send in the songs to be used by the modifyAllPlaylistsFragmentActivity
                                playlistIntentBundle.putStringArrayList("playlistNames", getPlaylistNames()); // send in the playlist names.
                                playlistIntentBundle.putParcelableArrayList("songInfos", songInfoList); // send in the songInfoList
                                playlistIntentBundle.putStringArrayList("folders", folders); // send in the folders.
                                playlistIntentBundle.putParcelableArrayList("playlists", PlayLists); // send in the playlists NOTE: remove this if we cannot do it this way as it could be causing problems.

                                addPlaylistActivity.putExtra("args", playlistIntentBundle); // send in the intent to grab the items for the activity.
                                startActivityForResult(addPlaylistActivity, CREATE_PLAYLIST_INTENT); // start the activity for the result of the songs in the playlist.

                                refreshPlaylistAdapter();
                                updateViewPager(viewPager.getCurrentItem()); // update the view pager after the playlist was updated. Very important.
                                // also want to update the playlist adapter here using that updatePlaylistAdapter() method.

                            }
                        });

                        // set behavior for when a user presses no.
                        noButton.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                System.out.println("No was selected, create an empty playlist");
                                // dismisses the dialog box and creates an empty arraylist of songs.
                                addSongsDialog.dismiss();
                                addEmptyPlaylist(); // creates an empty playlist with the name that was created.

                                refreshPlaylistAdapter();
                                updatePlaylistAdapter(); // force the playlist adapter to be updated and show hopefully it will allow us to keep track of the songs in the correct way.
                            }
                        });

                    }
                });



            }
        }); // end of click listener.
        */

        // Create our own custom playlist adapter to ensure that the songs will all be the same, very important.
        playListAdapter = new PlayListAdapter(getActivity(), R.layout.playlist_list, PlayLists);
        playListAdapter.setMainUIFragment(MainUIFragment.this); // set the mainUIFragment that is needed for this action.
        playListList.setAdapter(playListAdapter);
        playListAdapter.notifyDataSetChanged(); // update the playlist again in case their is some data that needs to be updated for the list to be seen.

        // TODO: grab the view pager that we want to work with and update the fields within the view pager itself.
        viewPager = (ViewPager) slidingLayout.findViewById(R.id.tabsViewPager); // grab the view pager that we want to work with. This is very important.

        // create the variables needed for the ViewPager
        Vector<ListView> tabLists = new Vector<>(); // this vector will hold of the lists that we will need.
        tabLists.add(songsList); // add the songs listview to the view pager. .
        tabLists.add(foldersList); // add the folders listview to the view pager.
        tabLists.add(playListList); // add the playlist listview to the view pager.


        // Set the MainUIPagerAdapter to ensure that everything is being displayed correctly.
        mainUIPagerAdapter = new MainUIPagerAdapter(tabLists, songsList, foldersList, playListList); // create mainUIPagerAdapter that is needed for us to keep track of how the app is working.
        //PlaylistPagerAdapter playlistPagerAdapter = new PlaylistPagerAdapter(getContext(), tabLists);
        mainUIPagerAdapter.notifyDataSetChanged();
        viewPager.setAdapter(mainUIPagerAdapter);

        // set the behavior for when a user changes the pages on the layout
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
               // System.out.println("we are in position: " + position);
                // have have the button slowly disappear as the user starts to add animations to the list.

            }

            @Override
            public void onPageSelected(int position)
            {
                if(position == 2) //
                {
                    addPlayListButton.setVisibility(View.VISIBLE); // make the button visible again.

                    // use object animator to flip image while we are loading.
                    ObjectAnimator spin = ObjectAnimator.ofFloat(addPlayListButton, "rotationY", 0.0f, 360.0f); // spin the tornado image for two full revolutions.
                    spin.setDuration(400); // the higher the number the slower the spinning animation
                    spin.reverse(); // reverse the spinning animation
                    spin.start(); // start the animation.


                }
                else // have the button appear only when when we are in the playlist position.
                {
                    addPlayListButton.setVisibility(View.GONE); // make the playlistbutton gone.
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {} // do nothing for this method.
        });

        // create the listener for when a tab is selected.
        TabLayout tabLayout = (TabLayout) mainView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        // We want to be able to select the setup with view pager thingy that is built into the TabLayout method.
       // tabLayout.addOnTabSelectedListener(new TabSelectedListener(songsList, foldersList, folderSongsList, playListList, addPlayListButton, slidingLayout, MainUIFragment.this)); // listener on our tabs.

        // The main view has been created at this point, we can display our welcome message now.
        Snackbar.make(mainView, grabWelcomeMessage(), Snackbar.LENGTH_SHORT).show(); // show the snackbar welcome message for a short time.
        return mainView;
    }

    // this method will simply begin the transaction to add songs to a single playlist very important.
    public void startAddSongsToSinglePlaylistActivity(Playlist playlistChosen)
    {
        int chosenPlaylistPos = 0;
        String playlistName = "";
        for(int i = 0; i < PlayLists.size(); i++)
        {
            // TODO: if this breaks we need to fix this to only search for playlist name since that is the most important thing here.
            if(PlayLists.get(i).name().equals(playlistChosen.name()))
            {
                playlistName = PlayLists.get(i).name(); // grab the name of the playlist.
            }
        }

        Intent addSongsToPlaylistActivity = new Intent(getContext(), AddSongsToSinglePlaylistFragmentActivity.class); // activity for adding songs to a single playlist.
        Bundle playlistIntentBundle = new Bundle();
        playlistIntentBundle.putStringArrayList("songs", songs); // send in the songs to be used by the modifyAllPlaylistsFragmentActivity
        playlistIntentBundle.putStringArrayList("playlistNames", getPlaylistNames()); // send in the playlist names.
        playlistIntentBundle.putParcelableArrayList("songInfos", songInfoList); // send in the songInfoList
        playlistIntentBundle.putStringArrayList("folders", folders); // send in the folders.
        playlistIntentBundle.putParcelableArrayList("playlists", PlayLists); // send in the playlists NOTE: remove this if we cannot do it this way as it could be causing problems.
        //playlistIntentBundle.putInt("PlaylistPosition", chosenPlaylistPos); // send in the last playlist because that is the one we just added muahaha
        playlistIntentBundle.putString("ChosenPlaylistName", playlistName); // send in the name of the playlist that we want to work with.
        addSongsToPlaylistActivity.putExtra("args", playlistIntentBundle); // send the arguments to the fragmentActivity.
        startActivityForResult(addSongsToPlaylistActivity, MODIFY_PLAYLIST_INTENT); // start the activity for the result of the songs in the playlist.

        refreshPlaylistAdapter();
        updatePlaylistAdapter(); // this forces a sort making the list behave much better.

        // Warning: if issues arise just uncomment the line below and pass in the viewPager via the constructor of this class from the mainUIFragment.
        // mainUIFragment.updateViewPager(viewPager.getCurrentItem()); // update the view pager after the playlist was updated.

        updateViewPager(getCurrentPageItem()); // this should work if not see the warning comment 3 lines up.
    }

    // This will read whatever is written into main memory and grab our playlists arraylist.
    private ArrayList<Playlist> readPlaylistsFromMainMemory()
    {
        // READ the playlist data object from main memory if it exists.
        ArrayList<Playlist> playlists = new ArrayList<>();
        String ser1 = SerializeObject.ReadSettings(getActivity().getBaseContext(), "playlists.dat"); // attempt to find this in main memory.
        if(ser1 != null && !ser1.equalsIgnoreCase(""))
        {
            Object obj = SerializeObject.stringToObject(ser1); // grab the object that was read in main memory.

            // cast the object to the correct type of arraylist.
            if(obj instanceof ArrayList)
            {
                playlists = (ArrayList<Playlist>) obj; // set our songInfo list.
            }
        }

        sortPlaylistsName(playlists); // sorts the playlists by their name to keep the app looking uniform.
        return playlists; // todo: make sure that this is not null
    }

    // tells the caller of this method if the slidinglayout is expanded or not.
    public boolean isSlidingPanelExpanded()
    {
        if(slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    // this method will force the sliding panel to fly down when the user hits the back button mainly.
    public void slidePanelDown()
    {
        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED); // set the panel to be collapsed.
    }

    // this method returns the current item from the view pager which helps with deciding what to do for the music player.
    public int getCurrentPageItem()
    {
        return viewPager.getCurrentItem();
    }

    // Opens but the folder when the user wishes to do so.
    public void openFolder(ListView folderSongsList)
    {
        int tempListItem = viewPager.getCurrentItem();
        mainUIPagerAdapter.openFolder(folderSongsList, viewPager.getCurrentItem()); // open the folder of the current view that we are working with.
        mainUIPagerAdapter.notifyDataSetChanged();

        viewPager.setAdapter(mainUIPagerAdapter);
        viewPager.setCurrentItem(tempListItem);

        folderSongsList.startLayoutAnimation(); // start the fading animation
    }

    // closes the folder and shows the list of folders to begin with which is a pretty big deal.
    public void closeFolder()
    {
        int tempListItem = viewPager.getCurrentItem();

        mainUIPagerAdapter.closeFolder(tempListItem); // send in the original folderlist to reset the view pager.
        mainUIPagerAdapter.notifyDataSetChanged();

        viewPager.setAdapter(mainUIPagerAdapter);
        viewPager.setCurrentItem(tempListItem);
        foldersList.startLayoutAnimation(); // start the fading animation.
    }

    // this method tells the mainUIPager to open the playlist and display the songs within it.
    public void openPlaylist(ListView playListList)
    {
        int tempListItem = viewPager.getCurrentItem();
        mainUIPagerAdapter.openPlaylist(playListList, viewPager.getCurrentItem()); // open the folder of the current view that we are working with.
        mainUIPagerAdapter.notifyDataSetChanged();

        viewPager.setAdapter(mainUIPagerAdapter);
        viewPager.setCurrentItem(tempListItem);

        playListList.startLayoutAnimation(); // start the fading animation.
    }

    // this method will close the folder in the playlist and update the items within that playlist. Very important.
    public void closePlaylist()
    {
        int tempListItem = viewPager.getCurrentItem();
        mainUIPagerAdapter.closePlaylist(tempListItem); // send in the original folderlist to reset the view pager.
        mainUIPagerAdapter.notifyDataSetChanged();

        viewPager.setAdapter(mainUIPagerAdapter);
        viewPager.setCurrentItem(tempListItem);

        playListList.startLayoutAnimation(); // start the fading animation.
    }

    // this causes the view pager to be updated and snap automatically to the page that was modified on.
    public void updateViewPager(int currentPageItem)
    {
        mainUIPagerAdapter.notifyDataSetChanged();
        viewPager.setAdapter(mainUIPagerAdapter);
        viewPager.setCurrentItem(currentPageItem);
    }

    // This method is in charge of retrieving any result from an activity. Different codes tell us where the result is coming from!
    // Essentially the only time that this item is going to be called is when the user grabs a folder, this will let us open the folder and grab all of the songs and put them into a folder.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intentResult) {
        System.out.println("we are in activity result now");
        super.onActivityResult(requestCode, resultCode, intentResult);

        if(requestCode == OPEN_FOLDER_INTENT) // this is the result that we wanted.
        {
            System.out.println("We have received the folder that we were trying to work with.");

            // update the list that we have received from openFoldersandSongs class.
            Bundle resultBundle; // = new Bundle();
            resultBundle = intentResult.getBundleExtra("resultBundle"); // get the bundle that holds our updated arraylist of folder songs.

            folderSongs = resultBundle.getStringArrayList("SongsInFolder"); // get the arraylist of songs in the folder from the openFolderandDisplaySongs activity.

            //songInfoList = convertToSongInfoList(folderSongs);
            // note: the arraylist foldersongs is used instead of songInfoList to ensure that the songInfoList is not messed with in any way shape or form.
            ArrayList<SongInfo> foldersongs = convertToSongInfoList(folderSongs); // convert the folder songs into their own arraylist which is created in itself.

            //MusicAdapter musicAdapter = new MusicAdapter(getActivity(), R.layout.songlist, foldersongs); // set the music adapter
            MusicAdapter musicAdapter = new MusicAdapter(getActivity(), R.layout.songlist, foldersongs); // set the music adapter
            musicAdapter.setMainUIFragment(MainUIFragment.this); // set the playlists in the class to be used by the popup menu.

            // TODO: Sort the folder songs list by the style that the users wish to sort them.

            folderSongsList.setAdapter(musicAdapter); // set out current view.
            musicAdapter.notifyDataSetChanged(); // let the music adapter know that the list is updated and ready to go.

            //foldersList.setVisibility(View.GONE); // make the list of the folder gone when a user clicks a folder.
           // folderSongsList.setVisibility(View.VISIBLE); // make the songs within the folder itself visible now.
            folderSongsList.startLayoutAnimation(); // start the animation that we want to see!
            //MusicPlayer musicPlayer = (MusicPlayer) getActivity();



            // todo: get the alphabetized list to be shown since it is causing problems for some reason.
           // alphabetized = new ArrayList<>(alphabetizeSongs(foldersongs)); // take the SongInfo list that was just created and reset it. REMOVED BECAUSE IT WAS CAUSING PROBLEMS FOR SOME REASON.
           // folderSongsCopy = new ArrayList<>(foldersongs); // create a copy of the folder songs to be used by the application.

            // Overwrite current listeners with new data

            // NOTE: ** I removed the selectedSongInFolderPlayer because we are going to have another player that is only for the songs in the folder not using SelectedSongPlayer.
            // selectedSongInFolderPlayer = new SelectedSongPlayer(mediaPlayer, alphabetized, slidingLayout, musicPlayer); // overwrite current selectedSongInFolderPlayer.
            //folderLongSelectedSongListener = new LongSelectedSongListener(folderSongsCopy); // overwrite current LongSelectedListener.

            // *NOTE: settings the listeners in this way should prevent the many copies from showing up which is what is slowing down the app like crazy.

            sort(foldersongs); // sort the folder songs according to the chosen filter by the user.

            // TODO: if the app breaks because of this then we need to remove the sort from above and also uncomment the alphabetized section of code.
            final ArrayList<SongInfo> copy = new ArrayList<>(foldersongs); // a copy to allow it to be manipulated by the SelectedSongPlayer without changing the list of the songs themselves.
            MusicPlayer mPlayer = (MusicPlayer) getActivity(); // somehow casting this to the MainActivity works! The selected song player behaves properly for whatever reason.

            // creates a new FolderSelectedSongPlayer to ensure that the songs in that folder is used in the panelSlidingListener.
            folderSelectedSongPlayer = new FolderSelectedSongPlayer(mediaPlayer, copy, folderSongs, slidingLayout, mPlayer, panelSlidingListener, MainUIFragment.this);

            folderSongsList.setOnItemClickListener(folderSelectedSongPlayer);
            folderSongsList.setOnItemLongClickListener(folderLongSelectedSongListener);


            openFolder(folderSongsList); // have the MainUIFragament open the folder and display the items correctly in the view pager.

            // Todo: We need to add the behavior for when a user hits the back button so that the list of folders become displayed again.

        }
        else if(requestCode == CREATE_PLAYLIST_INTENT) // user has chosen to create a playlist.
        {
            Bundle resultBundle = intentResult.getExtras();

            ArrayList<Playlist> playlistsReceived = new ArrayList<>(); // list of all the playlists.
            playlistsReceived = readPlaylistsFromMainMemory(); // grab the playlists that were written from main memory.

            // see if the we can send the a parcelable data structure here and see if that helps at all.
            System.out.println("The playlist arraylist received from main memory is: " + playlistsReceived);

            ArrayList<String> playlistNames = resultBundle.getStringArrayList("playlistNames");

            setPlaylists(playlistsReceived); // set the playlists to be used by the application.

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
            Set<String> playListNames = new HashSet<>(playlistNames); // convert all of the playlist names to a set to be saved to main memory.

            // save details into main memory.
            SharedPreferences.Editor editor = preferences.edit(); // create an editor to make changes to what is saved to main memory.
            editor.putStringSet("playlistNames", playListNames); // save the set to main memory.
            editor.commit(); // commit the changes to the application so that the app can reload properl

            // Reset the the playlist button click listener as it will need updated values.
            addPlayListButton.setOnClickListener(null); // remove the old click listener to prevent the lists from creating brand new ones. Very important!
            AddPlaylistButtonClickListener addPlaylistButtonClickListener = new AddPlaylistButtonClickListener(MainUIFragment.this, PlayLists, songs, songInfoList, folders);
            addPlayListButton.setOnClickListener(addPlaylistButtonClickListener); // set the click listener.

            updatePlaylistAdapter(); // update the playlist so users can see their playlists immediately once they have been saved.
            updateViewPager(viewPager.getCurrentItem());


        }
        else if(requestCode == MODIFY_PLAYLIST_INTENT)
        {
            System.out.println("We are back in MainUIFragment after modifying one or more playlists. ");
            Bundle resultBundle = intentResult.getExtras();

            ArrayList<Playlist> playlistsReceived = new ArrayList<>(); // list of all the playlists.
            playlistsReceived = readPlaylistsFromMainMemory(); // grab the playlists that were written from main memory.

            // see if the we can send the a parcelable data structure here and see if that helps at all.
           /// System.out.println("The playlist arraylist received from main memory is: " + playlistsReceived);
            System.out.println("The size of the number of playlists read from main memory is: " + playlistsReceived.size());

            ArrayList<String> playlistNames = resultBundle.getStringArrayList("playlistNames");

            setPlaylists(playlistsReceived); // set the playlists to be used by the application.

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
            Set<String> playListNames = new HashSet<>(playlistNames); // convert all of the playlist names to a set to be saved to main memory.

            // save details into main memory.
            SharedPreferences.Editor editor = preferences.edit(); // create an editor to make changes to what is saved to main memory.
            editor.putStringSet("playlistNames", playListNames); // save the set to main memory.
            editor.commit(); // commit the changes to the application so that the app can reload properl

            // Reset the click listener because we are going to need to have updated values.
            addPlayListButton.setOnClickListener(null); // remove the old click listener to prevent the lists from creating copies of the playlists.
            AddPlaylistButtonClickListener addPlaylistButtonClickListener = new AddPlaylistButtonClickListener(MainUIFragment.this, PlayLists, songs, songInfoList, folders);
            addPlayListButton.setOnClickListener(addPlaylistButtonClickListener); // set the click listener.

            updatePlaylistAdapter(); // update the playlist so users can see their playlists immediately once they have been saved.
            updateViewPager(viewPager.getCurrentItem());
        }
    }

    // refreshes the playlist adapter showing any changes made to the playlists.
    public void refreshPlaylistAdapter()
    {
        sortPlaylistsName(PlayLists); // resorts the playlists according to their names
        playListAdapter.notifyDataSetChanged();
    }

    // resets the playListList to ensure that the adapter is accurate. Called only by after the user has created a new playlist and saved it.
    public void updatePlaylistAdapter()
    {
        sortPlaylistsName(PlayLists); // resorts the playlists according to their names
        playListAdapter = new PlayListAdapter(getActivity(), R.layout.playlist_list, PlayLists);
        playListAdapter.setMainUIFragment(MainUIFragment.this); // very important and greatly needed.
        playListList.setAdapter(playListAdapter);
        playListAdapter.notifyDataSetChanged();

    }


    // Adds an empty playlist to the list of our playlists.
    public void addEmptyPlaylist()
    {
        String playlistName = getPlayListName(); // get the name of the newly created playlist.
        ArrayList<SongInfo> emptySongList = new ArrayList<SongInfo>(); // an empty playlist that has no songs in it.
        //Pair<String, ArrayList<SongInfo>> newPair = new Pair<String, ArrayList<SongInfo>>(playlistName, emptySongList); // create a new empty pair. old version
        Playlist emptyPlaylist = new Playlist(playlistName, emptySongList);
        PlayLists.add(emptyPlaylist);
    }

    // returns the names of all playlists
    public ArrayList<String> getPlaylistNames()
    {
        ArrayList<String> playlistNames = new ArrayList<>();
        for(int i = 0; i < PlayLists.size(); i++)
        {
            //playlistNames.add(PlayLists.get(i).getName()); // add the name into the arraylist. old version
            playlistNames.add(PlayLists.get(i).name());
        }

        return playlistNames;
    }

    public void setPlayListName(String name)
    {
        playListName = name;
    }

    public String getPlayListName()
    {
        return playListName;
    }

    // this method will do similar behavior as addSongsToList except it will only add the folders into the list.
    private void addFoldersToList(File[] foldersToAdd)
    {
        // loop through each file and add to the songs arrayList
        for(int i = 0; i < foldersToAdd.length; i++)
        {
            folders.add(foldersToAdd[i].toString()); // add the filename to the arraylist 'songs'. We will want to Parse the name to make it look better as we develop the app as we go. It's ugly right now.
        }
    }


    // this method will add songs to the list associated with listing songs. This is the tricky part because we need to make sure it's the correct list we are adding it too. Look at the i.d.
    protected void addSongsToList(File[] songsToAdd)
    {
        // loop through each file and add to the songs arrayList
        for(int i = 0; i < songsToAdd.length; i++)
        {
            songs.add(songsToAdd[i].toString()); // add the filename to the arraylist 'songs'. We will want to Parse the name to make it look better as we develop the app as we go. It's ugly right now.
        }
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

    // generates a random number and loads up a random message to be displayed to the user when the app is loaded.
    public String grabWelcomeMessage()
    {
        Random rand = new Random();
        int num = rand.nextInt(10 - 0) + 1; // generates a random number between 1 and 10 to allowing between 1 of 10 different options to choose from.

        // WARNING: I've never seen the last message saying "WOOOOOOOOOOSH" This is very odd indeed. Need to figure out how why this is.

        // TODO: add more welcome messages for the user to see, this will keep the app refreshing!
        switch (num)
        {
            case 1:
                return "I'm so happy to see you! " + ("\ud83d\ude01"); // super happy face
            case 2:
                return "Play me your favorite song! " + ("\ud83c\udfb6"); // multiple music notes
            case 3:
                return "What shall we listen to today hmmm?";
            case 4:
                return "You survived! I'm a small tornado " + ("\ud83d\ude1d"); // tongue out face
            case 5:
                return "Music can never be too loud!";
            case 6:
                return "My name is Torwald! Have we met before?";
            case 7:
                return "Turn up the volume! " + ("\ud83d\udd0a"); // speaker
            case 8:
                return "I love music sooooooo much.... and you! " + ("\ud83d\udc95"); // hearts
            case 9:
                return "Your songs are fire " + ("\ud83d\udd25"); // fire
            case 10:
                return "You have a great taste in music! " + ("\ud83d\ude0a"); // smiling face
            case 11:
                return "Wooooooooooooooosh!";
        }

        return ""; // this message should never, ever be displayed because the randomizer will ensure that this message is never reached. We can leave it blank.
    }


    // TODO: Important to create the lists correctly and with the Activity in mind. This is very important, but it should also work correctly.
    // This class is in charge of taking input from the user and making sure that the songs actually exist in the folder. It will also create the lists that for each of the items.
    // If they do not, we need to do something to reset the main U.i. we may want to have MainActivity do this first.
    public void evaluateAndCreateLists(File musicDirectory)
    {

       // TODO: We need to add the ability to have the app just search for all of the songs available on the device itself without having to pass in some kind of folder or path, Users will want this!!

       // CoordinatorLayout mainLayout = (CoordinatorLayout) getActivity().findViewById(R.id.mainLayout); // get the parent view that we want the snackbar message to be displayed at.

        if(songs == null && folders == null) // no songs were found.
        {
            // TODO: tell the user that we could not find any songs, use Toast
        }
        else if(songs != null && folders == null) // we only found songs, no folders were selected.
        {
            // TODO: this breaks we need to be passing in songs and folders to the application, it just force quits right now.
        }
        else if(songs != null && folders != null) // we have songs and folder found.
        {
            // TODO: This breaks we need to be passing in the folders and songs to the application it just force quits right now.

        }
        else if(songs == null && folders != null) // we did not find songs, but found only folders
        {
            File[] songFiles = musicDirectory.listFiles(); // get either song files or regular files.

            // Loop through each folder and search for songs in it.
            for(int i = 0; i < songFiles.length; i++)
            {
                File[] folderSongs = getSongsInDirectory(songFiles[i]); // songFiles[i] is one of the directories, thus, we need to check for songs in it.

                if(folderSongs.length == 0) {
                 // do nothing, nothing is in the folder.
                }

                if(folderSongs.length > 0) // we found songs in this folder! Now we can add to our list.
                {
                    addSongsToList(folderSongs); // adds songs to the songs arraylist.
                }
            }

            addFoldersToList(songFiles); // find all of the folders and add them into the folders list.

        }
        else // unknown result
        {
            // TODO: handle this type of condition if it ever occurs.
        }
    }

}
