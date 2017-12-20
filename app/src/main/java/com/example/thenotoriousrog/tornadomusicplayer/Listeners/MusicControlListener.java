package com.example.thenotoriousrog.tornadomusicplayer.Listeners;

import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ImageView;

import com.example.thenotoriousrog.tornadomusicplayer.UI.CountdownTimer;
import com.example.thenotoriousrog.tornadomusicplayer.R;
import com.example.thenotoriousrog.tornadomusicplayer.Backend.SongInfo;

import java.io.IOException;
import java.util.Random;

/**
 * Created by thenotoriousrog on 5/29/17.
 *
 * Controls the behavior when a music control is selected. implements both normal click and a long click.
 */

public class MusicControlListener implements ImageView.OnClickListener, View.OnLongClickListener {

    private ImageView action; // controls an action that we need to perform.
    private String actionCode; // tells us what action needs to be performed by this item
    private MediaPlayer mediaPlayer; // allows us to control the music based on an action.
    private View panel; // the main view that we need in order to change up the images.
    private PanelSlidingListener panelSlidingListener; // holds the panelSlidingListener we are using to help keep the data from each users action working smoothely and correctly.
    //private SelectedSongPlayer ssp; // selected song player that we can use to get some information.
    private String prevSongPath = ""; // holds the previous song path.
    private static CountdownTimer songTimer; // songTimer that is used between SelectedSongPlayer and MusicControlListener

    private ObjectAnimator spinEffect; // this is the animation for whenever an item is selected on the control layout.

    // constructor to have some of the important things needed for us to make this click listener.
    public MusicControlListener(ImageView selectedAction, String code, MediaPlayer mp, View p, PanelSlidingListener psl) {
        action = selectedAction;
        actionCode = code;
        mediaPlayer = mp;
        panel = p;
        panelSlidingListener = psl;


        // Set the action for the action when
        spinEffect = ObjectAnimator.ofFloat(action, "rotationY", 0.0f, 720.0f); // spin the icon whenever the list is moving, very important to get this working correctly.
        spinEffect.setDuration(800); // the higher the number the slower the spinning animation
        //spin.setRepeatCount(ObjectAnimator.); // repeat constantly
        spinEffect.reverse(); // reverse the spinning animation
    }

    // starts the spinning animation for the icon that is clicked by the user.
    public void startSpinAnimation()
    {
        spinEffect.start();
    }

    // returns a message to be displayed when a user turns shuffle on
    private String getShuffleOnMessage()
    {
        Random rand = new Random();
        int num = rand.nextInt(10 - 0) + 1; // generates a random number between 1 and 10 to allowing between 1 of 10 different options to choose from.

        switch (num)
        {
            case 1:
                return "Mixing it up!";
            case 2:
                return "Oooooooo I ALSO like to live dangerously " + ("\ud83d\ude09"); // winking face.
            case 3:
                return "See what's up next!"; // maybe put a down arrow here!
            case 4:
                return "Randomizing. pssp pssp pssp pssp";
            case 5:
                return "Got it! Shuffling now!";
            case 6:
                return "Psst, I know what songs will play before you " + ("\ud83d\ude0f"); // smirking face.
            case 7:
                return "Torwald ready for action! " + ("\ud83d\udcaa"); // flexed biceps
            case 8:
                return "Being a tornado makes shuffling easy!";
            case 9:
                return "Did you see that?? It's what's up next";
            case 10:
                return "Is your volume all the way up? 'Cuz I can't hear you!";
            case 11:
                return "You made the shuffle button change colors... AWESOME!";
        }

        return ""; // this will never be used. The randomizer won't allow it
    }

    // returns a message to be displayed when a user turns shuffle off.
    private String getShuffleOffMessage()
    {
        Random rand = new Random();
        int num = rand.nextInt(10 - 0) + 1; // generates a random number between 1 and 10 to allowing between 1 of 10 different options to choose from.

        switch(num)
        {
            case 1:
                return "Going down the line!";
            case 2:
                return "Phew, that was hard mixing the songs up like that";
            case 3:
                return "The next song is by ummm Taylor Swift huh?";
            case 4:
                return "woosh woosh wooosh";
            case 5:
                return "Hey, look how the shuffle button is white now " + ("\ud83d\ude02"); // laughing face with tears.
            case 6:
                return "You just want to know what songs are going to play before me... " + ("\ud83d\ude2d"); // crying face.
            case 7:
                return "RedBull may give you wings, but not like a Tornado can! " + ("\ud83d\udcaf"); // the one hundred symbol
            case 8:
                return "I shuffle by juggling but... I can't juggle";
            case 9:
                return "Made you look!";
            case 10:
                return "Turn it down! Now.... Turn it up!";
            case 11:
                return "I love listening to music with you! " + ("\ud83d\ude01"); // super happy face.
        }

        return ""; // This will never be used, the randomizer won't allow it.
    }

    // returns a message to be displayed when a user turns repeat on.
    private String getRepeatOnMessage()
    {
        Random rand = new Random();
        int num = rand.nextInt(10 - 0) + 1; // generates a random number between 1 and 10 to allowing between 1 of 10 different options to choose from.

        switch(num)
        {
            case 1:
                return "I'm becoming an F5!";
            case 2:
                return "'round and 'round we go... like a tornado";
            case 3:
                return "I love this song! Let's listen to it forever!";
            case 4:
                return "Repeating song forever! No, Five-ever!";
            case 5:
                return "Repeating current song now";
            case 6:
                return "I like the repeat button because it represents spinning heehee " + ("\ud83c\udf00"); // cyclone.
            case 7:
                return "I love Taylor Swift too! No I mean Katy Perry? Ah, I give up";
            case 8:
                return "Repeat this song? That's easy! " + ("\ud83d\ude0e"); // sunglasses face.
            case 9:
                return "Hey the repeat button changed colors!";
            case 10:
                return "Repeat on. Repeat On. Repeat on. Repea...";
            case 11:
                return "Let's tell everyone how awesome this song is!";
        }

        return ""; // This will never be used, the randomizer won't allow it.
    }

    // returns a message to be displayed when a user turns repeat off.
    private String getRepeatOffMessage()
    {
        Random rand = new Random();
        int num = rand.nextInt(10 - 0) + 1; // generates a random number between 1 and 10 to allowing between 1 of 10 different options to choose from.

        switch(num)
        {
            case 1:
                return "Aha! I knew you had more songs to play!";
            case 2:
                return "I could listen to that one forever " + ("\ud83d\ude0a"); // smiling face.
            case 3:
                return "Woah! You made the repeat button turn white again!";
            case 4:
                return "Going back through the list again, how exciting!";
            case 5:
                return "Repeat is off!";
            case 6:
                return "That was one of my favorite songs";
            case 7:
                return "The repeat and I are good friends. We are always spinning!";
            case 8:
                return "Why repeat when you can shuffle right? " + ("\ud83d\ude02"); // laughing with tears.
            case 9:
                return "See what's next! Oh, you already know about that.";
            case 10:
                return "Have no fear, Torwald is here!";
            case 11:
                return "Mr. Repeat can be annoying sometimes I know... he just repeats himself " + ("\ud83d\ude12"); // annoyed face.
        }

        return ""; // This will never be used, the randomizer won't allow it.
    }

    // controls the behavior of an action when the user selects an action.
    @Override
    public void onClick(View v)
    {
        // todo: fix this so that the PanelSlidingListener accurately pauses.
        // define the user actions.
        if(actionCode.equalsIgnoreCase("pause")) // a specific problem for when an action is clicked. ONLY listener is on Pause, create new one for Play!
        {
            mediaPlayer.pause(); // pause the song.


            action.setVisibility(View.INVISIBLE);

            //songTimer.cancel(); // todo: investigate what happends when we press pause and see how the timer reacts.
            // todo: we want to pause the timer when the song is paused. We can likely create this function in the CountdownTimer class, DO THAT! ALSO MAKE THE PAUSED TIME FLASH!!!

            panelSlidingListener.pauseTimer(); // simply cancel the timer.

            ImageView play = (ImageView) panel.findViewById(R.id.playButton);
            play.setVisibility(View.VISIBLE);

            ObjectAnimator spinEffect = ObjectAnimator.ofFloat(play, "rotationY", 0.0f, 720.0f); // spin the icon whenever the list is moving, very important to get this working correctly.
            spinEffect.setDuration(800); // the higher the number the slower the spinning animation
            //spin.setRepeatCount(ObjectAnimator.); // repeat constantly
            spinEffect.reverse(); // reverse the spinning animation
            spinEffect.start();
        }
        else if(actionCode.equalsIgnoreCase("play")) // this should not be needed anymore we can remove this.
        {
            int timePausedAt = mediaPlayer.getCurrentPosition();

           // user wants to continue the song, grab the current position of the song and have the song get continued.
            //mediaPlayer.reset(); // reset the song
            mediaPlayer.seekTo(timePausedAt); // move up the song to the point where someone has paused the song.
            mediaPlayer.start(); // continue the song that it is located at.

            panelSlidingListener.resumeTimer(); // resume the timer.

            action.setVisibility(View.INVISIBLE); // make the pause button disappear.

            ImageView pause = (ImageView) panel.findViewById(R.id.pauseButton);
            pause.setVisibility(View.VISIBLE); // make the pause button visible again.

            ObjectAnimator spinEffect = ObjectAnimator.ofFloat(pause, "rotationY", 0.0f, 720.0f); // spin the icon whenever the list is moving, very important to get this working correctly.
            spinEffect.setDuration(800); // the higher the number the slower the spinning animation
            //spin.setRepeatCount(ObjectAnimator.); // repeat constantly
            spinEffect.reverse(); // reverse the spinning animation
            spinEffect.start();

        }
        else if(actionCode.equalsIgnoreCase("skip"))
        {
            // make sure that play and pause are in their correct order. After skipping, flip the play button to be pause.
            // todo: change the pause and play flipping to only occur after a check to see if it is visible when it shouldn't be then make it invisible.
            ImageView pause = (ImageView) panel.findViewById(R.id.pauseButton);
            pause.setVisibility(View.VISIBLE);

            ImageView play = (ImageView) panel.findViewById(R.id.playButton);
            play.setVisibility(View.INVISIBLE);

            startSpinAnimation(); // make the skip icon start spinning.

            try {

                if(panelSlidingListener.isShuffleOn() == true ) // need to randomly choose a song from songPaths.
                {
                    SongInfo newSong = panelSlidingListener.getNextSong(); // get the next song in the queue.
                    System.out.println("shuffle is on while we are skipping!");

                    // play next song.
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(newSong.getSongPath());
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                    CountdownTimer t = new CountdownTimer(newSong.getSongDuration(), panel);

                    panelSlidingListener.cancelAndFinishTimer();
                    panelSlidingListener.setTimer(t); // set the new timer.
                    panelSlidingListener.startTimer(); // start the new timer.

                    SongInfo nextSong = panelSlidingListener.peekNextSong(); // get the next song in the queue again so that the panel slide listener knows what is going on.

                    System.out.println("The new song we are going to play is: " + newSong);
                    System.out.println("Next song should be after we skip is: " + nextSong);

                    panelSlidingListener.updateSliderData(newSong, nextSong); // updates the panelSlidingListener data

                    // this is likely broken below so we will have to fix this when we turn shuffle off it is not going to work correctly.
                    // may need to change the position to be randSongPos
                    int nextSongPosition = panelSlidingListener.getCurrentSongNumber() +1; // grabs the current song number and adds 1 to it. This is needed to keep the current song number in the list accurate.
                    panelSlidingListener.setCurrentSongNumber(nextSongPosition); // set the updated song position.
                    panelSlidingListener.refreshSliderLayout(); // refresh the layout showing the next song immediately.

                }
                else // shuffle is off, we need to to just select the next song in the list. NOTE: this part of the new setup is working correctly! Do not modify.
                {

                    System.out.println("shuffle is off while we are skipping.");

                    //String nextSongPath = panelSlidingListener.getNextSong().getSongPath(); // get next song.

                    SongInfo newSong = panelSlidingListener.getNextSong(); // get the next song to play

                    // play next song.
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(newSong.getSongPath());
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                    CountdownTimer t = new CountdownTimer(newSong.getSongDuration(), panel);

                    panelSlidingListener.cancelAndFinishTimer(); // cancel and finish current timer.
                    panelSlidingListener.setTimer(t);
                    panelSlidingListener.startTimer();

                    SongInfo nextSong = panelSlidingListener.peekNextSong(); // peeks the next song so that we get the next without changing the current song position or anything else of the like.

                    panelSlidingListener.updateSliderData(newSong, nextSong); // update the PanelSliding data.

                    System.out.println("Current song number = " + panelSlidingListener.getCurrentSongNumber());
                    panelSlidingListener.refreshSliderLayout(); // refresh the current sliding views to ensure that the
                }
            }
            catch (IOException ex) {
                System.out.println("problem happened while playing song." + ex.getMessage());
                // TODO: tell the user that their song cannot be played.
            }
        }
        else if(actionCode.equalsIgnoreCase("replay")) // user pressed the back button once, causing the current song to be replayed.
        {
           // todo: update the song an album titles in music controls whenever we go to the previous song.

            System.out.println("Replay was pressed (previous)");

            // make sure that play and pause are in their correct order. After skipping, flip the play button to be pause.
            ImageView pause = (ImageView) panel.findViewById(R.id.pauseButton);
            pause.setVisibility(View.VISIBLE);

            ImageView play = (ImageView) panel.findViewById(R.id.playButton);
            play.setVisibility(View.INVISIBLE);

            startSpinAnimation(); // have the animation start spinning.

            // restart the current song and replay it.
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            mediaPlayer.start();

            // reset countdown timer
            SongInfo currSong = panelSlidingListener.getCurrSong();
            CountdownTimer t = new CountdownTimer(currSong.getSongDuration(), panel); // get current song timer
            panelSlidingListener.cancelAndFinishTimer();
            panelSlidingListener.setTimer(t);
            panelSlidingListener.startTimer();

        }
        else if(actionCode.equalsIgnoreCase("shuffleOff")) // turning on shuffle, this stuff below seems to be working fine.
        {

            // WARNING** this appears to be working correctly now, do not modify unless sure that this is the problem!!!

            // todo: may be able to have PanelSlidingListener take care of this! Decide if we need to!
            action.setVisibility(View.INVISIBLE); // make the black shuffle button invisible.
            ImageView shuffleOn = (ImageView) panel.findViewById(R.id.shuffleOn);
            shuffleOn.setVisibility(View.VISIBLE);

            panelSlidingListener.shuffleSongs(); // begin shuffling the songs that we are currently using.
            panelSlidingListener.changeShuffleState(true); // shuffling has been activated.

            SongInfo nextSong = panelSlidingListener.peekNextSong(); // get the next song (shuffled)

            //may not need this   panelSlidingListener.setCurrentSongNumber(nextSongPos); // set the next song position in the list.
            SongInfo currentSongPlaying = panelSlidingListener.getCurrSong(); // get the current song playing.
            panelSlidingListener.updateSliderData(currentSongPlaying, nextSong); // update the data and grab next song.
            panelSlidingListener.refreshSliderLayout(); // force all of the views to be reset. WARNING** this may cause the layout to look funny if the data we are setting is not accurate.

            CoordinatorLayout snackBarLocation = (CoordinatorLayout) panel.findViewById(R.id.snackbarlocation); // grab the snackbarlocation we wish to use.
            Snackbar.make(snackBarLocation, getShuffleOnMessage(), Snackbar.LENGTH_SHORT).show(); // shuffle is turned on, display appropriate message.

        }
        else if(actionCode.equalsIgnoreCase("shuffleOn")) // turning shuffle off.
        {
            // todo: may be able to have PanelSlidingListener take care of this! Decide if we need to!
            action.setVisibility(View.INVISIBLE); // make the blue shuffle button invisible
            ImageView shuffleOff = (ImageView) panel.findViewById(R.id.shuffleOff);
            shuffleOff.setVisibility(View.VISIBLE); // make the black shuffle button invisible

            SongInfo currSong = panelSlidingListener.getCurrSong(); // get the current song that is playing.

            // todo: consider this for why the app seems to be running a bit choppy on the fly up animation. This may be an area to greatly reduce some stress on the UI Thread by having background threads.

            panelSlidingListener.resetSongs(); // resetting clears the shuffleQueue
            panelSlidingListener.changeShuffleState(false); // let the system know that we have changed the shuffling of the list.

            int currentSongNum = 0; // holds the exact position of the song currently playing.

            // get the current song and find the current song number of the song that is playing.
            for(int i = 0; i < panelSlidingListener.getCurrentSongPaths().size(); i++)
            {
                if(currSong.getSongName().equalsIgnoreCase(panelSlidingListener.getCurrentSongPaths().get(i).getSongName())) // if the songs have the same name
                {
                    currentSongNum = i; // grab the position of the song playing in the list.
                }
            }

            panelSlidingListener.setCurrentSongNumber(currentSongNum); // resets the current song number to ensure that songs are correctly playing.

            SongInfo nextSong = panelSlidingListener.getNextSong(); // get the next song from the normal list.
            panelSlidingListener.setCurrentSongNumber(panelSlidingListener.getCurrentSongNumber() - 1); // decrement by once to ensure that the next song grabbed plays correctly.

            // update info and set the correct song to be played.
            panelSlidingListener.updateSliderData(currSong, nextSong);
            panelSlidingListener.refreshSliderLayout(); // force the data to refresh immediately on the slider layout.

            CoordinatorLayout snackBarLocation = (CoordinatorLayout) panel.findViewById(R.id.snackbarlocation); // grab the snackbarlocation we wish to use.
            Snackbar.make(snackBarLocation, getShuffleOffMessage(), Snackbar.LENGTH_SHORT).show(); // shuffle is turned off, display appropriate message
        }
        else if(actionCode.equalsIgnoreCase("repeatOff")) // turning on repeat
        {
            // todo: may be able to have PanelSlidingListener take care of this! Decide if we need to!
            action.setVisibility(View.INVISIBLE); // make this turn blue.
            ImageView repeatOn = (ImageView) panel.findViewById(R.id.repeatOn);
            repeatOn.setVisibility(View.VISIBLE); // make the blue repeat button turn on.

            mediaPlayer.setLooping(true); // start looping the that is playing.

            CoordinatorLayout snackBarLocation = (CoordinatorLayout) panel.findViewById(R.id.snackbarlocation); // grab the snackbarlocation we wish to use.
            Snackbar.make(snackBarLocation, getRepeatOnMessage(), Snackbar.LENGTH_SHORT).show(); // repeat is turned on, show appropriate message.

        }
        else if(actionCode.equalsIgnoreCase("repeatOn")) // turning off repeat
        {
            // todo: may be able to have PanelSlidingListener take care of this! Decide if we need to!
            action.setVisibility(View.INVISIBLE); // make this turn black.
            ImageView repeatOff = (ImageView) panel.findViewById(R.id.repeatOff);
            repeatOff.setVisibility(View.VISIBLE); // make the blue repeat button turn on.

            mediaPlayer.setLooping(false); // stop looping the song.

            CoordinatorLayout snackBarLocation = (CoordinatorLayout) panel.findViewById(R.id.snackbarlocation); // grab the snackbarlocation we wish to use.
            Snackbar.make(snackBarLocation, getRepeatOffMessage(), Snackbar.LENGTH_SHORT).show(); // repeat is turned off, show appropriate message.
        }
        else {} // do nothing, it isn't an action we can do.
    }

    // This option means that the user has decided to long click the previous button. This will cause the previous song to be played.
    @Override
    public boolean onLongClick(View v)
    {
        if(actionCode.equalsIgnoreCase("prev")) // previous button was selected.
        {
            SongInfo currentSong = panelSlidingListener.getCurrSong(); // get the current song and hold it to show the users what the next song is going to be.
            SongInfo prevSong = panelSlidingListener.getPrevSong(); // get the previous song in the list.

            System.out.println("Previous button was selected (long click).");

            startSpinAnimation(); // start the spinning icon after the user chooses to select the previous icon.

            // NOTE: prevSong, is going to be the current song, and the currentSong is going to be the song that we are going to show next.
            if(prevSong != null)
            {
                panelSlidingListener.setCurrentSongNumber(panelSlidingListener.getCurrentSongNumber() - 1); // remove 1 from the current song number since we are going back a song.

                if(panelSlidingListener.isShuffleOn() == false) // if shuffle is off than we want to make sure that we show the next song when they go back.
                {
                    panelSlidingListener.updateSliderData(prevSong, currentSong); // prevSng will be shown and played, and the current song that is playing now we be shown to be next.
                    panelSlidingListener.refreshSliderLayout(); // force fields to update.
                }
                else // shuffle is on.
                {
                    SongInfo nextSong = panelSlidingListener.peekNextSong(); // this will allow us to see the next song to be played when on shuffle this is important.
                    panelSlidingListener.updateSliderData(prevSong, nextSong);
                    panelSlidingListener.refreshSliderLayout(); // force fields to update.
                }

                try
                {
                    // stop current song and begin to play the next song.
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(prevSong.getSongPath());
                    mediaPlayer.prepare();

                    mediaPlayer.start();

                } catch (IOException e) {
                    System.out.println("Error while trying to play previous song.");
                    e.printStackTrace();
                }

            }
            else // prevSong is null, we are at the start of the list.
            {
                // TODO: let the user know that they cannot go back because they are at the beginning of the list.
                System.out.println("We are at the start of the list, we cannot go back to anything.");
            }
        }

        return false;
    }
}
