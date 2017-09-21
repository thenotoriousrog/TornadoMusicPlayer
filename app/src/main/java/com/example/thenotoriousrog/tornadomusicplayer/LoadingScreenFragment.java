package com.example.thenotoriousrog.tornadomusicplayer;

import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thenotoriousrog.tornadomusicplayer.R;;

/**
 * Created by thenotoriousrog on 7/1/17.
 * This class is simply responsible for displaying and showing the loading screen and that's about it. It does not do any work to the app except keep the user occupied.
 */

public class LoadingScreenFragment extends Fragment {

    // called when this fragment is started. This will also start the spinning performance of the image view.
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    // creates the view for us, no real work is needed however.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(com.example.thenotoriousrog.tornadomusicplayer.R.layout.loading_screen, container, false); // inflate the loading screen. Nothing too fancy here.

        // use object animator to flip image while we are loading.
        ObjectAnimator spin = ObjectAnimator.ofFloat(view.findViewById(R.id.tornado), "rotationY", 0.0f, 720.0f); // spin the tornado image for two full revolutions.
        spin.setDuration(1200); // the higher the number the slower the spinning animation
        spin.setRepeatCount(ObjectAnimator.INFINITE); // repeat constantly
        spin.reverse(); // reverse the spinning animation
        spin.start(); // start the animation.

        return view; // return the created view.
    }
}
