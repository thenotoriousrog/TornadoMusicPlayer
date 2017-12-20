package com.example.thenotoriousrog.tornadomusicplayer.Listeners;

import android.media.MediaPlayer;
import android.view.View;
import android.widget.SeekBar;

import com.example.thenotoriousrog.tornadomusicplayer.UI.CountdownTimer;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

/**
 * Created by thenotoriousrog on 8/10/17.
 * This class controls the behavior of the seekbar when a user tries to use the slider on it.
 */

public class SeekBarListener implements SeekBar.OnSeekBarChangeListener{

    private MediaPlayer mediaPlayer; // media player used to control songs as they are being played.
    private CountdownTimer timer; // the countdown timer that is used by the sliding layout will be set right here and allowed to be updated by the list, which is very important.
    private View slidingLayout; // a copy of the sliding layout. Used mainly to update the time once a user has changed the time and to ensure that the timer is accurate.
    private PanelSlidingListener panelSlidingListener; // a copy of the panel sliding listener to update the time of the song and be able to control it correctly.

    // Constructor specifically to control the listener in whatever way is necessary.
    public SeekBarListener(MediaPlayer mp, SlidingUpPanelLayout slidinglayout, PanelSlidingListener psl)
    {
        mediaPlayer = mp;
        slidingLayout = slidinglayout;
        panelSlidingListener = psl;
    }

    // updates the timer that we are working with and allows the position of the seek bar to change the time based on the position of the timer.
    public void setTimer(CountdownTimer timerToUpdate)
    {
        timer = timerToUpdate;
    }

    // updates the timer based on when the user is sliding the seek bar.
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        // create the new timer based of the progress that was changed by the user.
        //System.out.println("The progress the seekbar was changed to is: " + progress);
        int remainingTime = mediaPlayer.getDuration(); // gets the duration of the current song that is playing. This is needed to make sure that the correct time is being portrayed to the user as the seek bar is moving.
        String currentTime = Integer.toString(remainingTime - progress); // we want to update the timer in reverse that is the timer should go towards zero as we move to the right.
        CountdownTimer newTimer = new CountdownTimer(currentTime, slidingLayout); // create a new timer so that the users can do the action that is needed.

        if(fromUser) // if this action was done by a user then allow the stuff to be changed.
        {
            mediaPlayer.seekTo(progress); // update the music player to the position that the user selected it to go to.
            panelSlidingListener.cancelAndFinishTimer(); // cancel and finish the current timer in the panelSlidingListener.
            panelSlidingListener.setTimer(newTimer); // set the timer of the current position of the song.
            panelSlidingListener.startTimer(); // start the timer now.
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
