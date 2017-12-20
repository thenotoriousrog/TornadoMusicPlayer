package com.example.thenotoriousrog.tornadomusicplayer.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.example.thenotoriousrog.tornadomusicplayer.R;;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

/**
 * Created by thenotoriousrog on 5/28/17.
 * This class will fly up the song playing window from the bottom.
 * It will also allow users to fling the window down.
 */
public class FlyUpSongPlayingWindow extends Activity
{

    boolean isOnBottom = true; // this is talking about the songplayingwindow which is not yet up!
    RelativeLayout rl_main;
    private SlidingUpPanelLayout slidingLayout;

    public void flyUp()
    {
        //Animation slide = null; // no animation is set quite yet.

        //Animation anim = AnimationUtils.loadAnimation(this, R.anim.flyup_animation);


        /*
        slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, -5.0f); // configure the animation

        slide.setDuration(400); // how long the animation should run.
        slide.setFillAfter(true);
        slide.setFillEnabled(true);

        System.out.println("is rlmain still null? " + rl_main);
        rl_main.startAnimation(slide);

        slide.setAnimationListener(new Animation.AnimationListener() {

            // nothing right now.
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                rl_main.clearAnimation(); // stop the animation
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        rl_main.getWidth(), rl_main.getHeight());

                lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                rl_main.setLayoutParams(lp); // set the parameters.
            }

            // nothing right now
            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
        */
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        System.out.println("Am I ready to begin the fly up animation?");
       // rl_main = (RelativeLayout) findViewById(R.id.rl_main); // set the relative layout that we want the animation to apply.

        System.out.println("is rl_main null: " + rl_main);
        // look at open folder and songs and then decide what pieces of information we need to make this display look that much better.

       // Intent intent = getIntent();
        if(isOnBottom == true) // if the layout is on the bottom, we want to fly this animation up.
        {
            flyUp(); // fly up our window, we have selected a new song.
            isOnBottom = false; // no longer on the bottom.
        }
        else // screen is not on the bottom.
        {
            // todo: we need to figure out how to collapse the window and fly it down.

        }

    }


}
