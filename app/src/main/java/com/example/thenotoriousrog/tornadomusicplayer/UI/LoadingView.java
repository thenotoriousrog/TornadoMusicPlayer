package com.example.thenotoriousrog.tornadomusicplayer.UI;

/*
      Created by YouTuber, Indragni Soft Solutions, modified by thenotoriousrog to fit the program.
      This class is in charge of putting our gif into the slot so that it can be viewed by our users while the program loads.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import com.example.thenotoriousrog.tornadomusicplayer.R;;

import java.io.InputStream;

public class LoadingView extends View{

    private InputStream gifInputStream;
    private Movie gifMovie;
    private int movieWidth, movieHeight;
    private long movieDuration;
    private long movieStart;


    public LoadingView(Context context) {
        super(context);
        init(context);
    }


    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setFocusable(true);
        gifInputStream = context.getResources().openRawResource(+R.drawable.loadingtornado);

        gifMovie = Movie.decodeStream(gifInputStream);
        movieWidth = gifMovie.width();
        movieHeight = gifMovie.height();
        movieDuration = gifMovie.duration();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(movieWidth, movieHeight);
    }

    public int getMovieWidth() {
        return movieWidth;
    }

    public int getMovieHeight() {
        return movieHeight;
    }

    public long getMovieDuration() {
        return movieDuration;
    }

    @Override
    protected void onDraw(Canvas canvas) {

       // System.out.println("are we drawing yet?");
        long now = SystemClock.uptimeMillis();

        if(movieStart == 0) {
            movieStart = now;
        }

        if(gifMovie != null) {

            int dur = gifMovie.duration();
            if(dur == 0) {
                dur = 1000;
            }

            int relTime = (int)((now - movieStart) % dur);

            gifMovie.setTime(relTime);

            gifMovie.draw(canvas, 100, 100);
            invalidate();
        }
    }
}