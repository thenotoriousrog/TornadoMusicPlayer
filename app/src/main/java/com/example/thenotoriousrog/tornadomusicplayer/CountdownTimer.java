package com.example.thenotoriousrog.tornadomusicplayer;


import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import com.example.thenotoriousrog.myapplication.R;

import java.util.concurrent.TimeUnit;

/**
 * Created by thenotoriousrog on 6/6/17.
 *
 * This class will act as the countdown timer that we wish to use
 */
public class CountdownTimer extends CountDownTimer {

    private String songInMilliseconds = ""; // holds the song in milliseconds.
    private String songActualTime = ""; // holds the song in minute:second format
    private View panel = null; // holds our sliding layout instance.
    private static TextView songTimer; // timer to be shown while our song is playing.
    private static CountdownTimer currTimer; // a copy of the current timer being used. Used to get past the problem with selecting a song in the folder.
    private static long remaining; // holds the remaining milliseconds of the timer itself.

    public CountdownTimer(String milliseconds, View p)
    {
        super(Long.parseLong(milliseconds), 1000); // tell the CountDownTimer to read the milliseconds and to count down by 1 second at a time.
        songInMilliseconds = milliseconds;
       // songActualTime = actualTime;
        panel = p;
        songTimer = (TextView) panel.findViewById(R.id.songTimeText); // find the song timer and reduce the time according to the countdown timer.
    }

    // returns the remaining time in milliseconds until the timer is finished.
    public long getRemainingTime()
    {
        return remaining;
    }

    // this method simply saves the time of the milliseconds that are remaining on every tick of the time.
    private void updateRemainingTime(long milliseconds)
    {
        remaining = milliseconds;
    }

    // this method is in control of lowering the time of the song as it is playing.
    @Override
    public void onTick(long millisUntilFinished)
    {
       // songTimer = (TextView) panel.findViewById(R.id.songTimeText);

        String ms = String.format("%2d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

        songTimer.setText(ms); // set the reduced time of the song on each tick, should go down by 1 second at a time.
        updateRemainingTime(millisUntilFinished); // update the remaining time.
    }

    // this should do nothing, although we may be able to get this to change to a new song if we wanted to.
    @Override
    public void onFinish() {
        System.out.println("Did the timer finish?"); // to test if the timer behaves properly or not.
        // do nothing.
    }
}
