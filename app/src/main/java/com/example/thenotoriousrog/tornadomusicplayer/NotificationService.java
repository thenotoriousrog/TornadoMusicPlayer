package com.example.thenotoriousrog.tornadomusicplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.example.thenotoriousrog.tornadomusicplayer.R;;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by thenotoriousrog on 6/12/17.
 * Originally from http://www.tutorialsface.com/2015/08/android-custom-notification-tutorial/ but modified to be used for my Application.
 *
 * This class will create and display the song that they are listening too. This is important for users to be able to control the music without having to open the app every time.
 */

public class NotificationService extends Service {

    private final IBinder mIBinder = new LocalBinder(); // this will allow us to set our handler.
    private Handler msgHandler = null; // handler to send messages to the SelectedSongPlayer.
    NotificationCompat.Builder status; // allows us set only one Notification.

    private String songName = "";
    private String artistName = "";
    private String nextSong = "";
    private Bitmap albumArt = null; // gets the album art of the song itself.

    // Using RemoteViews to bind custom layouts into Notification
    RemoteViews views;
    RemoteViews bigViews;
    protected boolean isPausePressed = false; // the app starts out with the pause button. This will tell us if the pause button is pressed or not.


    // sets the name of the song.
    public void setSongName(String str)
    {
        songName = str;
    }

    // sets the artist name of the song.
    public void setArtistName(String str)
    {
        artistName = str;
    }

    // sets the nextSong name
    public void setNextSong(String str)
    {
        nextSong = str;
    }

    // sets the album art of the current song, if there is any. Converts, the byte array into a bitmap and sets that bitmap to the variable.
    public void setAlbumArt(String songPath)
    {
        System.out.println("We are extracting album art from the song path that is: " + songPath);

        SongInfo currentSong = new SongInfo();
        currentSong.getandSetSongInfo(songPath); // extract the info from the song itself.

        albumArt = extractAlbumArt(currentSong); // extract the album art manually just like it is done in the PanelSlidingListener

        // TODO: this returns null which is not good. We need to have a bit of a more slimmed down version of the logo because it is causing the app to crash.

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
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.tornado_album_art); // set the default album art for the song.
            mmr.release(); // release the mmr to free up resources on the device.
            return bm; // return the bitmap
        }
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    public NotificationService() {}

    // this will allow the notification to be swiped away when a user stops playing music. Very important.
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // this method receives the Intent when we bind the service. This will allow us to recieve data from the SelectedSongPlayer to change information on the notification itself.
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("did we reach the bind method at all?");
        return mIBinder;
    }

    // NEW CLASS
    public class LocalBinder extends Binder
    {
        public NotificationService getInstance()
        {
            return NotificationService.this; // retrieve the instance of this service.
        }
    }

    public void setMsgHandler(Handler handler)
    {
        System.out.println("did my message handler get set? " + handler);
        msgHandler = handler;
    }


    // this method is important for controlling the music player using the notification ticker itself. This is very important.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        System.out.println("We are in onStartCommand");

        // set our song data here.
        setSongName(intent.getStringExtra("songName"));
        setArtistName(intent.getStringExtra("artistName"));
        setNextSong(intent.getStringExtra("nextSong"));

        String songPathToExtract = intent.getStringExtra("songPath");
        System.out.println("songPathToExtract is " + songPathToExtract);

        if(songPathToExtract == null)
        {
            System.out.println("we got null again");
        }
        else
        {
            setAlbumArt(songPathToExtract);
        }

        System.out.println("Current song playing has the name of " + songName);

        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION))
        {
            showNotification();
            isPausePressed = false;

        }
        else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION))
        {
            System.out.println("I'm in the prev action now.");

            System.out.println("prev was pressed.");
            Message msg = new Message();
            Bundle b = new Bundle();
            b.putString("msg", "prev");
            msg.setData(b);
            msgHandler.sendMessage(msg); // sending this message.

        }
        else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) // this controls both pause and play.
        {
              if(isPausePressed == false) // song is playing and user wants to pause it.
              {
                  // pause button was pressed, change button to be play button.
                  isPausePressed = true;

                  views.setImageViewResource(R.id.status_bar_pause, R.drawable.playblack); // ** broken it is not changing the image like we wanted it to.
                  bigViews.setImageViewResource(R.id.status_bar_pause_big, R.drawable.playblack);

                  // send message to handler to change the song.
                  System.out.println("pause was pressed.");
                  Message msg = new Message();
                  Bundle b = new Bundle();
                  b.putString("msg", "pause");
                  msg.setData(b);
                  msgHandler.sendMessage(msg); // sending this message.

                  // update the notification display.
                  status = new NotificationCompat.Builder(this);
                  //status.setCustomContentView(views); // removed because we only want to use the big view.
                  status.setCustomContentView(bigViews); // set the un expanded layout.
                  status.setCustomBigContentView(bigViews); // set this big view.

                  status.setOngoing(true); // keep this going until music has stopped playing.
                  status.setSmallIcon(R.drawable.notification_tornado); // the small tornado for when the notification starts.
                  status.setPriority(Notification.PRIORITY_MAX); // set the priority to be max to ensure that the big display stays the same.

                  // Build the notification
                  Notification notification = status.build();
                  notification.priority = Notification.PRIORITY_MAX; // just to ensure that the priority is set to max I'm setting it here as well.
                  notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR; // creating flags.

                  startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification); // push the notification to the top of the notification list.
              }
              else // paused button has become the play button, and play was pressed.
              {
                  isPausePressed = false; // the play button was pressed, convert back to a pause button.
                  views.setImageViewResource(R.id.status_bar_pause, R.drawable.pauseblack);
                  bigViews.setImageViewResource(R.id.status_bar_pause_big, R.drawable.pauseblack);

                  System.out.println("play was pressed.");
                  Message msg = new Message();
                  Bundle b = new Bundle();
                  b.putString("msg", "play");
                  msg.setData(b);
                  msgHandler.sendMessage(msg); // sending this message.

                  status = new NotificationCompat.Builder(this);
                  //status.setCustomContentView(views); // removed because we only want to use the big view here
                  status.setCustomContentView(bigViews); // set the un expanded layout.
                  status.setCustomBigContentView(bigViews);
                  status.setOngoing(true); // keep this going until music has stopped playing.
                  status.setSmallIcon(R.drawable.notification_tornado); // the small tornado for when the notification starts.
                  status.setPriority(Notification.PRIORITY_MAX);

                  // build the notification.
                  Notification notification = status.build();
                  notification.priority = Notification.PRIORITY_MAX; // just to ensure that the priority is set to max I'm setting it here as well.
                  notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR; // creating flags.

                  startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification); // push the notification to the top of the notification list.
              }
        }
        else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) // user has chosen to skip the song.
        {
            // update the song info that is playing and try to get the current artist and song name that is playing.
            System.out.println("I'm in the skip action now.");

            System.out.println("skip was pressed.");
            Message msg = new Message();
            Bundle b = new Bundle();
            b.putString("msg", "skip");
            msg.setData(b);
            msgHandler.sendMessage(msg); // sending this message.
        }
        else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) // the user has indicated to stop the music player.
        {
            // end the foreground activity.
            stopForeground(true);
            stopSelf();

            Message msg = new Message();
            Bundle b = new Bundle();
            b.putString("msg", "quit"); // this will tell the msgHandlerService to stop the music player.
            msg.setData(b);
            msgHandler.sendMessage(msg);
        }

        return START_STICKY;
    }

    private void showNotification()
    {
        views = new RemoteViews(getPackageName(), R.layout.status_bar); // normal shrunk notification
        bigViews = new RemoteViews(getPackageName(), R.layout.status_bar_expanded); // expanded notification

        // showing default album image
        views.setViewVisibility(R.id.status_bar_icon, View.VISIBLE);

        // check if there is album art.
        if(albumArt != null) // album art is present, set that instead.
        {
            //views.setImageViewBitmap(R.id.status_bar_icon, albumArt);
            bigViews.setImageViewBitmap(R.id.status_bar_album_art_big, albumArt); // get the album art and display it.
        }
        else // no album art is present, set the default images.
        {
            views.setViewVisibility(R.id.status_bar_album_art, View.GONE);
            bigViews.setImageViewBitmap(R.id.status_bar_album_art_big, Constants.getDefaultAlbumArt(this));
        }

        final Intent notificationIntent = new Intent(this, MusicPlayer.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // new flags.

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT); // this intent controls how the app is opened.

        Intent previousIntent = new Intent(this, NotificationService.class);
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0, previousIntent, 0);

        Intent playIntent = new Intent(this, NotificationService.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0, playIntent, 0);

        Intent nextIntent = new Intent(this, NotificationService.class);
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0, nextIntent, 0);

        Intent closeIntent = new Intent(this, NotificationService.class);
        closeIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0, closeIntent, 0);

        views.setOnClickPendingIntent(R.id.status_bar_pause, pplayIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_pause_big, pplayIntent);

        views.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_next_big, pnextIntent);

        views.setOnClickPendingIntent(R.id.status_bar_prev_big, ppreviousIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_prev_big, ppreviousIntent);

        views.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);

        // use to show the play and pause button. Modify as needed to match my app style.
        views.setImageViewResource(R.id.status_bar_pause, R.drawable.pauseblack);
        bigViews.setImageViewResource(R.id.status_bar_pause_big, R.drawable.pauseblack);

        views.setTextViewText(R.id.notifySongName, songName);
        bigViews.setTextViewText(R.id.status_bar_track_name, songName);

        views.setTextViewText(R.id.notifyArtistName, artistName);
        bigViews.setTextViewText(R.id.status_bar_artist_name, artistName);

        bigViews.setTextViewText(R.id.status_bar_album_name, nextSong);

        status = new NotificationCompat.Builder(this);
        //status.setCustomContentView(views); // removed because we only want to use the big view. Very important!
        status.setCustomContentView(bigViews); // set the un expanded layout.
        status.setCustomBigContentView(bigViews); // set the expanded layout.
        status.setOngoing(true); // keep this going until music has stopped playing.
        status.setSmallIcon(R.drawable.notification_tornado); // the small tornado for when the notification starts.
        status.setContentIntent(pendingIntent); // this will let us control the behavior of the music player when a user selects to do so.
        status.setPriority(Notification.PRIORITY_MAX); // set the priority of this notification to be max.

        // builds the notification and tells it to open up the app whenever the notification is clicked.
        Notification notification = status.build();
        notification.priority = Notification.PRIORITY_MAX; // just to ensure that the priority is set to max I'm setting it here as well.
        notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR; // creating flags.

        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification); // push the notification to the user.

    }
}
