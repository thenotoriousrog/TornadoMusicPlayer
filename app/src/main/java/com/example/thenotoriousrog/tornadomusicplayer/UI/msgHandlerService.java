package com.example.thenotoriousrog.tornadomusicplayer.UI;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.example.thenotoriousrog.tornadomusicplayer.Backend.Constants;
import com.example.thenotoriousrog.tornadomusicplayer.Listeners.PanelSlidingListener;
import com.example.thenotoriousrog.tornadomusicplayer.Activities.MusicPlayer;

/**
 * Created by thenotoriousrog on 6/16/17.
 *
 * This class will start our handler for the Notification listener and will tell the the SelectedSongPlayer to perform certain actions that the user chooses to do.
 */

public class msgHandlerService
{
    private NotificationService notificationService = null; // a copy of the notificationService so that we can set the handler to receive messages from.
    private Handler songPlayerHandler = null; // this is the handler that will recieve messages from the NotificationService class.
    private MusicPlayer musicPlayer = null; // a copy of the music player activity to speak between them
    private PanelSlidingListener panelSlidingListener = null; // this panel sliding listener is important for controlling actions that the user has selected.
    private boolean listening = true; // this tells us whether or not our program is listening to actions from the NotificationService.

    public msgHandlerService(MusicPlayer activity, PanelSlidingListener PSL)
    {
        // set our fields to do the work that we need.
        musicPlayer = activity;
        panelSlidingListener = PSL;
    }

    // This method is only called once, it will create the service connection which also sets our handler. Very important.
    public void setServiceConnection()
    {

        // create a service connection which will allow us to set our handler.
        ServiceConnection servConn = new ServiceConnection() {

            NotificationService notifyMe = null;

            @Override
            public void onServiceConnected(ComponentName name, IBinder service)
            {
                System.out.println("are we in service connected?");
                notifyMe = ((NotificationService.LocalBinder) service).getInstance(); // get the instance of the notification
                notifyMe.setMsgHandler(songPlayerHandler); // set the handler that is used to receive messages.
            }

            @Override
            public void onServiceDisconnected(ComponentName name)
            {
                // todo: make sure to have this kill the music playing when the user wants to.
            }
        };

        Intent bindIntent = new Intent(musicPlayer, NotificationService.class);
        musicPlayer.bindService(bindIntent, servConn, Context.BIND_AUTO_CREATE); // bind the actual service now.
    }


    // This method will be in charge of listening to the messages being sent from the NotificationService class.
    public void startListening(String song, String artist, String next, final String songPath)
    {
        listening = true; // set it true because we are now listening for a new song.

        System.out.println("we have started the run method");

        // may need to change SelectedSongPlayer.this to mainActivity, but maybe not
        Intent notificationIntent = new Intent(musicPlayer, NotificationService.class);
        notificationIntent.putExtra("songName", song);
        notificationIntent.putExtra("artistName", artist);
        notificationIntent.putExtra("nextSong", "Up Next: " + next);
        System.out.println("song path that we are putting into the intent is: " + songPath);
        notificationIntent.putExtra("songPath", songPath);
        notificationIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);

        musicPlayer.startService(notificationIntent); // start the service.

        setServiceConnection();

        // Run this portion on the U.I. thread.
        musicPlayer.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                System.out.println("are we running the handler now?");
                // the run method will loop here, continuing to listen for actions from the NotificationService.
                songPlayerHandler = new Handler() {

                    // this method should be in control of recieving messages that will be sent by the NotificationService.
                    @Override
                    public void handleMessage(Message msg)
                    {
                        System.out.println("We have received a message from the NotificationService");
                        Bundle notifBundle = msg.getData();
                        String action = notifBundle.getString("msg");

                        if(action.equalsIgnoreCase("pause"))
                        {
                            panelSlidingListener.clickPause(); // no return because we want to keep this Thread alive and listening for actions.
                        }
                        else if(action.equalsIgnoreCase("play"))
                        {
                            panelSlidingListener.clickPlay(); // no return because we want to keep this Thread alive and listening for actions.
                        }
                        else if(action.equalsIgnoreCase("skip"))
                        {
                            System.out.println("skip test");
                            //ssp.printMessage("we have detected a skip action.");
                            //ssp.printMessage("Song path that we want to use is: " + songPath);
                            panelSlidingListener.clickSkip(); // skip to the next song.
                            return; // forces a return, stopping the Thread
                        }
                        else if(action.equalsIgnoreCase("prev"))
                        {
                            panelSlidingListener.longClickPrev(); // go back to previous song.
                            return; // forces a return, stopping the Thread
                        }
                        else if(action.equalsIgnoreCase("quit"))
                        {
                            panelSlidingListener.quit(); // quit the music player here.
                            return; // force a return stopping the Thread
                        }
                    }
                };
            }
        });
    }

}