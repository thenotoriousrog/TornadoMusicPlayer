package com.example.thenotoriousrog.tornadomusicplayer.Backend;

/**
 * Created by thenotoriousrog on 6/12/17.
 * Originally from the same website as the code that is used in NotificationService.java. Modified to fit my app.
 */


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.thenotoriousrog.tornadomusicplayer.R;

public class Constants
{
    public interface ACTION
    {
        // may need to change these strings to com.thentoriousrog.myapplication.action.<command>
        public static String MAIN_ACTION = "com.marothiatechs.customnotification.action.main";
        public static String INIT_ACTION = "com.marothiatechs.customnotification.action.init";
        public static String PREV_ACTION = "com.marothiatechs.customnotification.action.prev";
        public static String REPLAY_ACTION = "REPLAY";
        public static String PLAY_ACTION = "com.marothiatechs.customnotification.action.play";
        public static String NEXT_ACTION = "com.marothiatechs.customnotification.action.next";
        public static String STARTFOREGROUND_ACTION = "START_FOREGROUND_ACTION";
        public static String STOPFOREGROUND_ACTION = "com.marothiatechs.customnotification.action.stopforeground";
        public static String OPEN_APP_ACTION = "OPEN_APP_ACTION";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }

    public static Bitmap getDefaultAlbumArt(Context context)
    {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher, options); // todo: change this to be one of the logo's that I have. Not the gif if we can help it.
        } catch (Error ee) {
            // do nothing for this error.
            System.out.println("Constants.java received this error: " + ee.getMessage());
        } catch (Exception e) {
            System.out.println("Constants.java received exception: " + e.getMessage());
        }
        return bm;
    }
}
