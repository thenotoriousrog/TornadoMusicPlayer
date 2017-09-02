package com.example.thenotoriousrog.tornadomusicplayer;

import android.app.Activity;
import android.media.MediaPlayer;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by thenotoriousrog on 5/25/17.
 * This class is in charge of playing the songs that are selected by the user and setting up a media player to play those songs!
 */
public class SelectedSongPlayer extends Activity implements AdapterView.OnItemClickListener {

    private MediaPlayer mediaPlayer; // mediaPlayer that we are using to play our songs.
    private static ArrayList<SongInfo> songPaths = null; // paths to different songs, this will be different depending on where the user chooses song be it in all songs or folders.
    private SlidingUpPanelLayout slidingLayout; // layout we are using to slide up the action.
    private static PanelSlidingListener panelSlidingListener; // holds the panel sliding listener to control specific functions for the sliding U.I.
    private SongInfo info;
    private static CountdownTimer songTimer; // used to ensure only one instance of the song timer can be active at a time.
    private boolean musicControlsListenersSet = false; // this boolean will tell us weather or not the app has set the music control listeners yet.


    // the boolean ShufflingNow will help us determine whether or not the SelectedSongPlayer was set to be shuffling or not.
    public SelectedSongPlayer(MediaPlayer mp, ArrayList<SongInfo> songToPick, ArrayList<String> songpaths, SlidingUpPanelLayout layout, MusicPlayer activity, PanelSlidingListener psl, MainUIFragment fragment)
    {
        mediaPlayer = mp; // set the media player we are using, only one should exist period.
        songPaths = songToPick; // set the ArrayList up.
        slidingLayout = layout; // set sliding layout.
        panelSlidingListener = psl; // set the panelSlidingListener.
    }

    // todo: decide if this needs to be removed or not. I don't believe it is being used or is needed
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo contextInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle("Select Action");
        menu.add(Menu.NONE, v.getId(), 0, "Edit Tags");
        menu.add(Menu.NONE, v.getId(), 0, "Add to Playlist");
    }

    // this method is in control of the behavior for when a user clicks on a song that they want to play.
    // @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        try // attempt to play a song and get the song path, catch any failures if any exist.
        {
            info = songPaths.get(position); // we cannot use panelSlidingLayout for this because we have not set the very first timer, so this is ok to use like this for the first time only however.

            if(songTimer != null) // songTimer is set and ready to be changed.
            {
                CountdownTimer t = new CountdownTimer(info.getSongDuration(), slidingLayout);

                panelSlidingListener.cancelAndFinishTimer(); // cancel and finish the timer that we are using on the panelSlideListener.
                panelSlidingListener.setTimer(t); // set the new countdownTimer
                panelSlidingListener.startTimer(); // start the timer.
            }
            else // songTimer is null right here.
            {
                songTimer = new CountdownTimer(info.getSongDuration(), slidingLayout);

                // if the mediaplayer is playing then we know that there is a timer and we need to cancel and finish it, this will also prevent the FolderSelectedSongPlayer from crashing when nothing is playing.
                if(mediaPlayer.isPlaying())
                {
                    panelSlidingListener.cancelAndFinishTimer();
                }

                panelSlidingListener.setTimer(songTimer); // set the new countdownTimer

            }

            // check to see if the music control listeners have been set or not yet.
            if(musicControlsListenersSet == false)
            {
                musicControlsListenersSet = true;
            }
            else {
                System.out.println("musicControlListeners have been created");
            } // do nothing, the music control listeners are already started.

            // create the song completion listener after a song has completed.
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                // behavior for when a song has completed. It will just go to the next one.
                @Override
                public void onCompletion(MediaPlayer mp)
                {

                    try // attempt to set up the next song for when the current one finishes playing.
                    {

                        SongInfo newSong = panelSlidingListener.getNextSong(); // grab the current song that is playing.
                        SongInfo nextSong = panelSlidingListener.peekNextSong(); // grab the next song that is playing.

                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(newSong.getSongPath()); // pass in the song path for when this thing has finished correctly.
                        mediaPlayer.prepare();

                        CountdownTimer t = new CountdownTimer(nextSong.getSongDuration(), slidingLayout);

                        panelSlidingListener.cancelAndFinishTimer(); // finish the current timer.
                        panelSlidingListener.setTimer(t); // set the new timer.
                        panelSlidingListener.startTimer(); // start the new timer.

                        mediaPlayer.start(); // start playing the song.

                        panelSlidingListener.updateSliderData(newSong, nextSong);
                        panelSlidingListener.refreshSliderLayout(); // force the app to refresh when the next song is playing.


                    } catch(IOException ex) {
                        System.out.println("caught error while trying to play song in SelectedSongPlayer");
                        ex.printStackTrace();
                    }
                }
            });

            // get the next song prepared to be displayed.
            //SongInfo nextSong = songPaths.get(position +1); // because we have not sent in the song list to the PanelSlidingListener yet it is ok to do this as it currently is.
            SongInfo nextSong = null;

            System.out.println("Position of the song that was selected is: " + position);
            if(position == songPaths.size() - 1)
            {
                nextSong = songPaths.get(0); // get the first song instead of the last song.
            }
            else
            {
                nextSong = songPaths.get(position + 1);
            }


            // NOTE: this is where we will want to update our SlidingUpData with all of the information of the new song.
            panelSlidingListener.updateSliderData(info, nextSong); // send it the songInfo of the current song to extract data, and send in the song name of the next song that will be played.
            panelSlidingListener.setCurrentSongPaths(songPaths, slidingLayout); // always send in the list after updating the slider information to ensure that the correct song is being displayed.
            panelSlidingListener.setCurrentSongNumber(position); // set the current song position when user selects a song.


            // Selected song is processed and played here.
            if(mediaPlayer != null) // make sure that the media player is not not null so we can play songs.
            {
                String songPath = songPaths.get(position).getSongPath(); // get the song that was chosen based on the position of its pick.

                if(mediaPlayer.isPlaying())
                {
                    // stop the current song from playing.
                    mediaPlayer.stop();
                    mediaPlayer.reset();

                    mediaPlayer.setDataSource(songPath); // set the path of the selected song so that it can be sent to the android background to play.
                    mediaPlayer.prepare();

                    CountdownTimer t = new CountdownTimer(info.getSongDuration(), slidingLayout);

                    // have the panelSlideListener restart the timer.
                    panelSlidingListener.cancelAndFinishTimer(); // cancels and finishes the currently playing timer.
                    panelSlidingListener.setTimer(t); // set the new timer.
                    panelSlidingListener.startTimer(); // start the new timer.

                    mediaPlayer.start();
                }
                else // the mediaPlayer is not playing, so just play the song.
                {
                    mediaPlayer.reset(); // this should let us clear where we are at if a song if a song is paused so we may select a new one.
                    mediaPlayer.setDataSource(songPath); // set the path of the selected song so that it can be sent to the android background to play.
                    mediaPlayer.prepare();  // prepare the song to be played.

                    CountdownTimer t = new CountdownTimer(info.getSongDuration(), slidingLayout);

                    // have the panelSlideListener restart the timer.
                    panelSlidingListener.cancelAndFinishTimer(); // cancels and finishes the currently playing timer.
                    panelSlidingListener.setTimer(t); // set the new timer.
                    panelSlidingListener.startTimer(); // start the new timer.

                    mediaPlayer.start(); // play the song

                }
            } // a song is now playing.

            // removed the below line because we want the sliding listener to do all of that for us.
           // slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED); // Automatically expands the slidingLayout whenever the user selects a song, flies up after all data has been populated.
        }
        catch(IOException ex) {
            System.out.println("We got this error while trying to get song path: " + ex.getMessage());
            Toast.makeText(getApplicationContext(), "I couldn't find your song :(", Toast.LENGTH_SHORT).show(); // tell user that songs were found.
        }

        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED); // have the panel fly up.
    }

}
