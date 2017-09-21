package com.example.thenotoriousrog.tornadomusicplayer;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.thenotoriousrog.tornadomusicplayer.R;;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by thenotoriousrog on 7/12/17.
 * This class is in charge of controlling what happens when the panel is sliding. When a song is changed, it is reflected on the sliding up panel layout from whichever listener sets the song to it.
 * It will also show which song is playing next and will show if Shuffle and/or repeat is engaged on the app itself. This is important because we do not want shuffle/repeat to be turned off randomly.
 * Potential problems: This class is not thread safe. Multiple lists may try to change the song at the same time wreaking havoc on the data that is shown to the user.
 *
 */

public class PanelSlidingListener implements SlidingUpPanelLayout.PanelSlideListener {

    // Variables for the sliding up panel layout views.
    private TextView nowPlayingText; // the view that actually shows the Now Playing... message
    private TextView songText; // the view in the slide that actually shows the song title.
    private TextView artistText; // the view in the slide that shows the artist of the song.
    private ImageView albumArt; // album art per song.
    private TextView upNext; // controls the up next functionality.
    private TextView timerText; // controls the timer of the text of the song being played.

    // Variables for the current song that is playing.
    private String currentSongTitle = ""; // holds the name of the song currently playing
    private String currentArtistName = ""; // holds the name of the artist of the song currently playing.
    private Bitmap currentAlbumArt = null; // holds the album art of the current song Playing.
    private String upNextText = ""; // holds the name of the song that will play next.
    private static CountdownTimer timer; // holds the Countdowntimer of the song that needs to be played. This is used so that we may pause and restart the timer as needed.
    private SongInfo nextSong = null; // holds the actual next Song that will be played. This can be changed by the SelectedSongPlayer(either Songs or folderSongs version can).
    private int currentSongNumber = 0; // this tells us which song was grabbed in the list. This is just like the position in the list that is being grabbed from the SelectedSongPlayer.
    private SongInfo prevSong = null; // holds the prev SongInfo
    private SongInfo currSong = null; // holds the current SongInfo that is playing.

    // Other variables to make the PanelSlidingListener run better.
    private boolean isExpanded = false; // tell other classes whether the panel sliding listener is expanded or not.
    private boolean shuffleState = false; // tells MusicControlListener if the user has activated shuffle or not.
    private static ArrayList<SongInfo> currentSongPaths = null; // holds the song Paths that we are using currently. This is called in one of the SelectedSongPlayers
    private static ShuffleQueue shuffleQueue; // this queue will hold the the shuffle songs and allow us to continue to show the user the next song in the queue.
    private final Activity currentActivity; // holds the activity that was called on. This is important for us to be able to extract the drawable resources and display the defaule album art.
    private msgHandlerService msgHandler = null; // a single instance of the msgHandler that is used to start the notification for when a song is played.
    private MediaPlayer mediaPlayer = null; // The copy of this media player used to help us to determine if songs are playing or not and modify the U.I. as needed.
    private View panelView = null; // this view holds the view of the panel once it has been slid up. It is used mainly for the CountDownTimer since it needs to be able to update the text in the panel.
    private Stack<SongInfo> playedSongs = new Stack<>(); // this stack holds the items in the list to allow for LIFO operation
    private SlidingUpPanelLayout slidingLayout; // allows us to set everything that we need to control sliding layout correctly.
    private SeekBar seekBar; // This is the seek bar that will allow users to be able to control their songs and be able to seek to a specific position.
    Handler handler = new Handler(); // handler to help us be able to control what it is that we are doing in the list

    // Image buttons.
    private ImageView pauseButton;
    private ImageView playButton;
    private ImageView skipButton;
    private ImageView prevButton;

    // Constructor to set the correct Views that we want to be manipulating. If we send the wrong Views, we will not updating them correctly. It is crucial we get this working correctly.
    public PanelSlidingListener(TextView nptext, TextView sText, TextView aText, ImageView aArt, TextView upnext, TextView t, Activity activity, MediaPlayer player, SeekBar seekbar, SlidingUpPanelLayout slidingUpPanelLayout)
    {
        nowPlayingText = nptext;
        songText = sText;
        artistText = aText;
        albumArt = aArt;
        upNext = upnext;
        timerText = t; // don't need this right now.
        currentActivity = activity;
        mediaPlayer = player;
        seekBar = seekbar;
        slidingLayout = slidingUpPanelLayout;


        // TODO: set the seekbar listener in here because we want to make sure that we are able to create it to behave in very specific way.
        SeekBarListener seekBarListener = new SeekBarListener(mediaPlayer, slidingLayout, PanelSlidingListener.this); // create a seekbar listener to send to the sliding layout to allow for the list to be updated properly.
        seekBar.setOnSeekBarChangeListener(seekBarListener); // set the seekbar listener to allow for the mediaplayer to be changed when a user chooses to do so
    }


    // this method is only used by one of the SelectedSongPlayers, this will tell the MusicControlListener what to grab for the next song based on the list that is being set.
    public void setCurrentSongPaths(ArrayList<SongInfo> list, final SlidingUpPanelLayout slidinglayout)
    {
        //System.out.println("Set currentSongPaths IS BEING CALLED!");

        currentSongPaths = list; // sets the list currently being used by the user.
        RelativeLayout draggedView = (RelativeLayout) slidinglayout.findViewById(R.id.draggedView);
        draggedView.setVisibility(View.VISIBLE);


       // slidinglayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED); // This is still running super slowly.

        // Fixme: This is what is causing the fly up panel to be choppy. We want it so be smooth and right now it is not. We need to get this working smoother.
         //This is causing the fly up panel to act all choppy instead of flying up smoothely. Thus, we have to do this only after the list
        if(shuffleState == true) // user has shuffle still turned on.
        {
            resetSongs(); // clear out the old queue.
            shuffleSongs(); // restart the shuffling queue.

            // after redoing the shuffle queue update the data that is being displayed to correctly show the song that is upnext.
            updateSliderData(currSong, shuffleQueue.peekNxtSong()); // keep the same song information, but simply peek the next song that is going to be played.
            refreshSliderLayout(); // force the slider information to update.
        }
        else {} // do nothing, shuffle is not on proceed as normal.


    }

    // this method is called by MusicControlListener to grab the next song and do some work on it.
    public ArrayList<SongInfo> getCurrentSongPaths()
    {
        return currentSongPaths;
    }

    // this class determines what song was picked and the position it was picked in from the list used in one of SelectedSongPlayers (Songs and folderSongs)
    public void setCurrentSongNumber(int songPosition)
    {
        currentSongNumber = songPosition;
    }

    // gets the current song number that is being played from one of the lists.
    public int getCurrentSongNumber()
    {
        return currentSongNumber;
    }

    // this will literally return the next song in the list or queue if shuffle is on.
    public SongInfo getNextSong()
    {
        if(shuffleState == false) // shuffle is not active
        {
            // check to see if the song is at the end of the list or not.
            if(currentSongNumber == currentSongPaths.size() - 1)
            {
                System.out.println("Grabbing the first song in the list"); // for testing.
                setCurrentSongNumber(0); // set the current song number to be zero.
                return currentSongPaths.get(getCurrentSongNumber());
            }
            else // we are not at the end of the list.
            {
                setCurrentSongNumber(currentSongNumber + 1); // increment the next song number
                return currentSongPaths.get(getCurrentSongNumber()); // grab the next song in the list.
            }
        }
        else // shuffle is active, get song from Queue
        {
            return shuffleQueue.popNextSong(); // get the next song in the queue.
        }
    }

    public SongInfo peekNextSong()
    {
        if(shuffleState == false) // shuffle is not active.
        {
            if(currentSongNumber == currentSongPaths.size() -1)
            {
                return currentSongPaths.get(0); // grab the very first item in the list.
            }
            else
            {
                return currentSongPaths.get(currentSongNumber + 1); // return the next song in the list without actually changing the song number.
            }

        }
        else // shuffle is active.
        {
            return shuffleQueue.peekNxtSong(); // simply look at the next song without actually changing anything.
        }
    }

    // this method is called by the MusicControlListener to get the current song that is playing.
    public SongInfo getCurrSong()
    {
        return currSong;
    }

    // returns whether or not shuffle is turned on or not. True = yes, False = no.
    public boolean isShuffleOn()
    {
        return shuffleState;
    }

    // this is called by the MusicControlListener to let is know that the user has engaged the shuffle button.
    public void changeShuffleState(boolean state)
    {
        shuffleState = state;
    }


    // this method is only chosen when it is time to shuffle the current songPaths being used by the PanelSlidingListener. This will keep the list shuffled because it is based on the SelectedSongPlayer calling it.
    public void shuffleSongs()
    {
        // TODO: Make the shuffle queue run as a Thread. Getting the songs in a queue is causing the fly up panel to be choppy. Make use of a background Thread to have this perform just a little better. Very important!
        // Create the shuffleSongQueue here and make sure that it is used.
        shuffleQueue = new ShuffleQueue(currentSongPaths); // send in the current song paths and fill the shuffle queue.
    }



    // this method is called by the MusicControlListener which will do a complete restore of the songs back when user turns shuffling off.
    public void resetSongs()
    {
        shuffleQueue.clear(); // clear the shuffleQueue.
        playedSongs.clear(); // remove all the songs in the played song queue.
    }


    // this method removes the top item from the queue of played songs.
    public SongInfo getPrevSong()
    {
        return playedSongs.pop(); // pop the last item added in the stack.
    }

    // this method will attempt to extract the album art from the current song that is playing.
    private Bitmap extractAlbumArt(SongInfo currentSongPlaying)
    {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever(); // create a new MediaMetaDataRetriever to extract the album art from the current song if one exists.
        mmr.setDataSource(currentSongPlaying.getSongPath()); // send in the song path to attempt to extract the song data items.
        byte[] albumArt = mmr.getEmbeddedPicture();

        if(albumArt != null)
        {
            InputStream stream = new ByteArrayInputStream(mmr.getEmbeddedPicture());
            Bitmap bm = BitmapFactory.decodeStream(stream);
            mmr.release(); // release the mmr to free up resources on the device.
            return bm; // return the bitmap.
        }
        else // the album art is null, return the Bitmap with the default tornado image!
        {
            Bitmap bm = BitmapFactory.decodeResource(currentActivity.getResources(), R.drawable.applogo); // set the default album art for the song.
            mmr.release(); // release the mmr to free up resources on the device.
            return bm; // return the bitmap
        }
    }

    // User selected to skip the song on the notification display.
    public void clickPause()
    {
        pauseButton.performClick(); // programmatically press the pause button.
    }

    // User selected to skip the song on the notification display.
    public void clickSkip()
    {
        skipButton.performClick(); // programmatically press the skip button.
    }

    // User selected to press play again.
    public void clickPlay()
    {
        playButton.performClick();
    }



    public void longClickPrev()
    {
        prevButton.performLongClick(); // this will make the song go back.
    }

    // This method will stop any songs that are playing at the current moment. This will also stop the music player completely.
    public void quit()
    {
        mediaPlayer.stop(); // stop the song as the user has requested.

        // TODO: this is very important, if a user hits quit, we must stop the music player and switch the icon to show the stop icon, this is the only time this icon will show up!
        playButton.setVisibility(View.VISIBLE); // make the play button invisible, however, this should be replaced with the stop button.
        pauseButton.setVisibility(View.INVISIBLE); // make the pause button invisible here because no song is playing.
    }

    // This method is in control of generating and refreshing the notification display. called from within the updateSliderData and refresh layout methods in this class.
    private void refreshNotificationDisplay()
    {
        // send all the information we need to display to the notification:
        MusicPlayer musicPlayer = (MusicPlayer) currentActivity; // casting this to a music player works for whatever reason. Test to make sure it doesn't cause any issues.
        msgHandler = new msgHandlerService(musicPlayer, PanelSlidingListener.this);
        msgHandler.startListening(currentSongTitle, currentArtistName, nextSong.getSongName(), currSong.getSongPath()); // send the song information, test to make sure that the data in this list is working correctly!
    }

    // This is a background thread for the seekbar to tick properly.
    private Runnable tickSeekBar = new Runnable() {
        public void run() {
            int currentDuration = mediaPlayer.getCurrentPosition(); // convert to seconds.

            //System.out.println("Total duration of the current song is: " + totalDuration);
            //System.out.println("current duration of the song is: " + currentDuration);

            // Updating progress bar
            int progress = Integer.parseInt(currSong.getSongDuration()); // extract the song duration for the seekbar.

            seekBar.setProgress(currentDuration); // this is most likely wrong.

            // Running this thread after 100 milliseconds
            handler.postDelayed(this, 1000);
        }
    };

    // This method will be in control of restarting the seekbar with the correct information.
    private void restartSeekBar()
    {
        int songDuration = Integer.parseInt(currSong.getSongDuration()); // extracts the song duration and sets for the users.

        seekBar.setMax(songDuration); // get the duration of the song loaded in the music player.
        handler.postDelayed(tickSeekBar, 0); // begin updating the seekbar immediately
    }

    // this method is called by the two SelectedSongPlayers (Songs and FolderSongs) and updates the data that the panel uses when expanded and collapsed.
    public void updateSliderData(SongInfo currentSongPlaying, SongInfo next)
    {
        playedSongs.push(currSong); // add the current song that is playing into the stack.

        currSong = currentSongPlaying; // set the current song that is playing.
        currentSongTitle = currentSongPlaying.getSongName();
        currentArtistName = currentSongPlaying.getArtistName();
        currentAlbumArt = extractAlbumArt(currentSongPlaying); // get the album for this song.
        upNextText = "Up next: " + next.getSongName();
        nextSong = next; // set the next song.

        // TODO: we need to get the seekbar to be working here.

        restartSeekBar(); // simply restart the seekbar for the new song.
        refreshNotificationDisplay(); // start/update the notification display
    }

    // this method is in control of starting and setting the timer that is
    public void setTimer(CountdownTimer currSongTimer)
    {
        System.out.println("Timer we are setting in PanelSlidingListener is: " + timer);

        timer = currSongTimer;
    }

    // this method is in control of starting the timer. It is called from other classes in the app.
    // NOTE: This will only work if the Timer has been set using setTimer when called from other apps.
    public void startTimer()
    {
        // may be able to get rid of this if check if it is necessary.
        if(timer != null) {
            timer.start(); // start the timer for the song that is currently playing.
        }
        else {
            System.err.println("THE TIMER RECEIVED IN PANELSLIDINGLISTENER IS NULL IT HAS NOT BEEN SET.");
        }
    }

    // this method just cancels the songTimer, mainly used for pausing a song and used by the MusicControlListener.
    public void pauseTimer()
    {
        timer.cancel();
    }

    // this will resume the timer once the use has selected to do so.
    public void resumeTimer()
    {
        long remainingTime = timer.getRemainingTime(); // get remaining time of the timer.
        String str = Long.toString(remainingTime); // convert the time to a String.
        timer = new CountdownTimer(str, panelView); // create a new timer.
        timer.start(); // start the timer with the time being resumed.
    }

    // this method is in charge of stopping and finishing the current timer that is being played.
    // Note: a timer has to be set using the setTimer method otherwise we will have a null exception.
    public void cancelAndFinishTimer()
    {
        timer.cancel();
        timer.onFinish();
    }

    // this method refreshes the data of the sliding up layout. This is only called from other classes in order to force a refresh on the sliding layout.
    // NOTE: This only works if UpdateSliderData has been called first.
    public void refreshSliderLayout()
    {
        // simply resets the text of all of the views.

        if(isExpanded == true) {
            nowPlayingText.setText("Now playing...");
        }
        else {
            nowPlayingText.setText(currentSongTitle);
        }

        songText.setText(currentSongTitle); // shows the name of current song playing.
        artistText.setText(currentArtistName); // shows the name of the artist of the current song playing.
        albumArt.setImageBitmap(currentAlbumArt); // shows the album art of the current song playing.
        upNext.setText(upNextText); // shows what song is going to be playing next in the list.
        refreshNotificationDisplay(); // update the information on the notification display.
    }

    // this method controls the behavior for when the panel is actively sliding up.
    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        // todo: may want to add an animation like something spinning while the panel is sliding up to make it look cool and like a Tornado.
       // System.out.println("We are sliding!! Here is the offset: " + slideOffset);

    }

    // this method controls the behavior for when the panel is EXPANDED (slid up) and when it is collapsed (slid down).
    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState)
    {
        if( (playButton == null) && (pauseButton == null) && (skipButton == null) && (prevButton == null) ) // if any button is null reset them all.
        {
            // set the buttons that we need to be using.
            playButton = (ImageView) panel.findViewById(R.id.playButton);
            pauseButton = (ImageView) panel.findViewById(R.id.pauseButton);
            skipButton = (ImageView) panel.findViewById(R.id.skipButton);
            prevButton = (ImageView) panel.findViewById(R.id.prevButton);
            panelView = panel; // set the sliding up panel layout view.

            // since every button is null we can safely assume this is the first load up, ensure that the play and pause button works accordingly.
            playButton.setVisibility(View.INVISIBLE); // make the play button invisible
            pauseButton.setVisibility(View.VISIBLE); // make the pause button visible instead.
        }

        if(mediaPlayer.isPlaying()) // a song is playing make sure that the pause button is showing.
        {
            pauseButton.setVisibility(View.VISIBLE); // make pause button visible
            playButton.setVisibility(View.INVISIBLE); // make play button invisible.
        }
        else // a song is not playing make sure that the play button is visible.
        {
            playButton.setVisibility(View.VISIBLE); // make the play button visible.
            pauseButton.setVisibility(View.INVISIBLE); // make the pause button invisible.
        }

        if(newState.compareTo(SlidingUpPanelLayout.PanelState.EXPANDED) == 0) // When expanded, we want to grab the song details and display them
        {
            isExpanded = true; // set true because the expander is expanded.
            nowPlayingText.setText("Now playing..."); // text for when the list is expanded.
            songText.setText(currentSongTitle); // shows the name of current song playing.
            artistText.setText(currentArtistName); // shows the name of the artist of the current song playing.
            albumArt.setImageBitmap(currentAlbumArt); // shows the album art of the current song playing.
            upNext.setText(upNextText); // shows what song is going to be playing next in the list.
        }

        if(newState.compareTo(SlidingUpPanelLayout.PanelState.COLLAPSED) == 0) // when collapsed, just show the title of the current song that is playing.
        {
            isExpanded = false; // set false because the expander is not expanded it is collapsed.
            // todo: make this text look a little better. Seems basic right now.
            nowPlayingText.setText(currentSongTitle); // whatever the title of the song is playing that is what the collapsed window will show.
        }
        // when collapsed, just show the current playing song.
    }
}
